#!/usr/bin/env bash
#
# Copyright (c) 2026, RTE (https://www.rte-france.com)
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
# SPDX-License-Identifier: MPL-2.0
#
# Run the same benchmarks on two git refs (branches, tags or commits) and compare the results.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

usage() {
    cat <<EOF
Usage: $(basename "$0") --baseline <ref> --candidate <ref> [options] -- <benchmark args>

Build the benchmark JAR from two git refs (branches, tags or commits), run the baseline then the
candidate, and compare the candidate results with the baseline ones.
Each ref is built in a temporary git worktree: the current checkout is left untouched.

Options:
  --baseline <ref>            Git ref used as baseline
  --candidate <ref>           Git ref compared to the baseline
  --output-dir <dir>          Output directory (default: version_comparison/<timestamp>)
  --skip-tests                Skip unit tests when building the JARs
  --pause <seconds>           Cool-down pause between the two runs (default: 30)
  -h, --help                  Show this help and exit

Everything after "--" is passed to the benchmark JAR (benchmark names, --release, JMH options...).

Example:
  $(basename "$0") --baseline main --candidate my-fix-branch \\
      --skip-tests -- LoadFlowBenchmark -f 1 -wi 1 -i 1
EOF
}

die() {
    echo "Error: $*" >&2
    exit 1
}

# Check that required commands are available
command -v git >/dev/null || die "git not found in PATH"
command -v mvn >/dev/null || die "mvn not found in PATH"
command -v java >/dev/null || die "java not found in PATH"

BASELINE_REF=""
CANDIDATE_REF=""
OUTPUT_DIR=""
SKIP_TESTS=false
PAUSE=30
BENCHMARK_ARGS=()

while [[ $# -gt 0 ]]; do
    case "$1" in
        --baseline)
            [[ $# -ge 2 ]] || die "--baseline requires a value"
            BASELINE_REF="$2"
            shift 2
            ;;
        --candidate)
            [[ $# -ge 2 ]] || die "--candidate requires a value"
            CANDIDATE_REF="$2"
            shift 2
            ;;
        --output-dir)
            [[ $# -ge 2 ]] || die "--output-dir requires a value"
            OUTPUT_DIR="$2"
            shift 2
            ;;
        --skip-tests)
            SKIP_TESTS=true
            shift
            ;;
        --pause)
            [[ $# -ge 2 ]] || die "--pause requires a value"
            [[ "$2" =~ ^[0-9]+$ ]] || die "--pause value must be a non-negative integer"
            PAUSE="$2"
            shift 2
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        --)
            shift
            BENCHMARK_ARGS=("$@")
            break
            ;;
        *)
            usage >&2
            die "unknown option '$1' (benchmark arguments must be given after '--')"
            ;;
    esac
done

[[ -n "$BASELINE_REF" ]] || die "--baseline ref is required"
[[ -n "$CANDIDATE_REF" ]] || die "--candidate ref is required"
[[ ${#BENCHMARK_ARGS[@]} -gt 0 ]] || die "no benchmark arguments given after '--'"

cd "$SCRIPT_DIR"

resolve_ref() {
  local ref="$1"
    git rev-parse --verify --quiet "$ref^{commit}" || die "unknown git ref '$ref'"
}
BASELINE_SHA="$(resolve_ref "$BASELINE_REF")"
CANDIDATE_SHA="$(resolve_ref "$CANDIDATE_REF")"

if [[ -z "$OUTPUT_DIR" ]]; then
    OUTPUT_DIR="version_comparison/$(date +%Y%m%d_%H%M%S)"
fi
[[ ! -e "$OUTPUT_DIR" ]] || die "output directory '$OUTPUT_DIR' already exists"
mkdir -p "$OUTPUT_DIR/baseline" "$OUTPUT_DIR/candidate" "$OUTPUT_DIR/worktrees"
OUTPUT_DIR="$(cd "$OUTPUT_DIR" && pwd)"
WORKTREES_DIR="$OUTPUT_DIR/worktrees"

# Worktrees are removed as soon as their JAR is copied; the EXIT trap only handles failures mid-build
CURRENT_WORKTREE=""
remove_worktrees() {
    if [[ -n "$CURRENT_WORKTREE" ]]; then
        git -C "$SCRIPT_DIR" worktree remove --force "$CURRENT_WORKTREE" >/dev/null 2>&1 || true
        CURRENT_WORKTREE=""
    fi
    git -C "$SCRIPT_DIR" worktree prune >/dev/null 2>&1 || true
    rm -rf "$WORKTREES_DIR"
}
trap remove_worktrees EXIT

# Build the benchmark JAR of the given commit in a temporary worktree and copy it to the given directory
build_jar() {
    local label="$1" ref="$2" sha="$3" target_dir="$4"
    local worktree="$WORKTREES_DIR/$label"
    echo "==> Creating $label worktree for $ref (${sha:0:10})"
    git worktree add --quiet --detach "$worktree" "$sha"
    CURRENT_WORKTREE="$worktree"
    local mvn_args=(-B -q -f "$worktree/pom.xml" clean package)
    if [[ "$SKIP_TESTS" == true ]]; then
        mvn_args+=(-DskipTests)
    fi
    echo "==> Building $label JAR: mvn ${mvn_args[*]}"
    mvn "${mvn_args[@]}" || die "$label build failed for ref '$ref'"
    cp "$worktree/target/benchmark.jar" "$target_dir/benchmark.jar"
    echo "==> Removing $label worktree"
    remove_worktrees
}

# Both builds are done before running any benchmark so that a build failure is detected early
build_jar "baseline" "$BASELINE_REF" "$BASELINE_SHA" "$OUTPUT_DIR/baseline"
build_jar "candidate" "$CANDIDATE_REF" "$CANDIDATE_SHA" "$OUTPUT_DIR/candidate"

echo "==> Running baseline benchmarks ($BASELINE_REF)"
java -jar "$OUTPUT_DIR/baseline/benchmark.jar" --serde-path "$OUTPUT_DIR/baseline" "${BENCHMARK_ARGS[@]}"

if [[ "$PAUSE" -gt 0 ]]; then
    echo "==> Pausing ${PAUSE}s before running candidate benchmarks"
    sleep "$PAUSE"
fi

echo "==> Running candidate benchmarks ($CANDIDATE_REF) compared to baseline"
java -jar "$OUTPUT_DIR/candidate/benchmark.jar" --serde-path "$OUTPUT_DIR/candidate" -C "$OUTPUT_DIR/baseline" "${BENCHMARK_ARGS[@]}"

echo
echo "==> Comparison done"
echo "    Baseline:  $BASELINE_REF (${BASELINE_SHA:0:10})"
echo "    Candidate: $CANDIDATE_REF (${CANDIDATE_SHA:0:10})"
echo "    Baseline results:   $OUTPUT_DIR/baseline"
echo "    Comparison results: $OUTPUT_DIR/candidate/*.md"
