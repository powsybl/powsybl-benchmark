#!/usr/bin/env bash
#
# Copyright (c) 2026, RTE (https://www.rte-france.com)
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
# SPDX-License-Identifier: MPL-2.0
#
# Run the same benchmarks against two sets of dependency versions and compare the results.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

usage() {
    cat <<EOF
Usage: $(basename "$0") --baseline <property>=<version> --candidate <property>=<version> [options] -- <benchmark args>

Build the benchmark JAR twice with different Maven property values (e.g. dependency versions),
run the baseline then the candidate, and compare the candidate results with the baseline ones.

Options:
  --baseline <prop>=<value>   Maven property override for the baseline build (repeatable)
  --candidate <prop>=<value>  Maven property override for the candidate build (repeatable)
  --output-dir <dir>          Output directory (default: version_comparison/<timestamp>)
  --skip-tests                Skip unit tests when building the JARs
  --pause <seconds>           Cool-down pause between the two runs (default: 30)
  -h, --help                  Show this help and exit

Everything after "--" is passed to the benchmark JAR (benchmark names, --release, JMH options...).

Example:
  $(basename "$0") --baseline powsybl-core.version=7.4.0 --candidate powsybl-core.version=7.4.3 \\
      --skip-tests -- LoadFlowBenchmark -f 1 -wi 1 -i 1
EOF
}

die() {
    echo "Error: $*" >&2
    exit 1
}

BASELINE_PROPS=()
CANDIDATE_PROPS=()
OUTPUT_DIR=""
SKIP_TESTS=false
PAUSE=30
BENCHMARK_ARGS=()

check_property() {
  local prop="$1"
    [[ "$prop" =~ ^[A-Za-z0-9_.-]+=.+$ ]] || die "invalid property '$prop', expected <property>=<value>"
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --baseline)
            [[ $# -ge 2 ]] || die "--baseline requires a value"
            check_property "$2"
            BASELINE_PROPS+=("$2")
            shift 2
            ;;
        --candidate)
            [[ $# -ge 2 ]] || die "--candidate requires a value"
            check_property "$2"
            CANDIDATE_PROPS+=("$2")
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

[[ ${#BASELINE_PROPS[@]} -gt 0 ]] || die "at least one --baseline property is required"
[[ ${#CANDIDATE_PROPS[@]} -gt 0 ]] || die "at least one --candidate property is required"
[[ ${#BENCHMARK_ARGS[@]} -gt 0 ]] || die "no benchmark arguments given after '--'"
command -v mvn >/dev/null || die "mvn not found in PATH"
command -v java >/dev/null || die "java not found in PATH"

cd "$SCRIPT_DIR"

if [[ -z "$OUTPUT_DIR" ]]; then
    OUTPUT_DIR="version_comparison/$(date +%Y%m%d_%H%M%S)"
fi
[[ ! -e "$OUTPUT_DIR" ]] || die "output directory '$OUTPUT_DIR' already exists"
mkdir -p "$OUTPUT_DIR/baseline" "$OUTPUT_DIR/candidate"
OUTPUT_DIR="$(cd "$OUTPUT_DIR" && pwd)"

# Build the benchmark JAR with the given property overrides and copy it to the given directory
build_jar() {
    local label="$1" target_dir="$2"
    shift 2
    local mvn_args=(-B -q clean package)
    local prop
    for prop in "$@"; do
        mvn_args+=("-D$prop")
    done
    if [[ "$SKIP_TESTS" == true ]]; then
        mvn_args+=(-DskipTests)
    fi
    echo "==> Building $label JAR: mvn ${mvn_args[*]}"
    mvn "${mvn_args[@]}" || die "$label build failed (are the versions compatible with the benchmark sources?)"
    cp target/benchmark.jar "$target_dir/benchmark.jar"
}

# Both builds are done before running any benchmark so that a build failure is detected early
build_jar "baseline" "$OUTPUT_DIR/baseline" "${BASELINE_PROPS[@]}"
build_jar "candidate" "$OUTPUT_DIR/candidate" "${CANDIDATE_PROPS[@]}"

echo "==> Running baseline benchmarks (${BASELINE_PROPS[*]})"
java -jar "$OUTPUT_DIR/baseline/benchmark.jar" --serde-path "$OUTPUT_DIR/baseline" "${BENCHMARK_ARGS[@]}"

if [[ "$PAUSE" -gt 0 ]]; then
    echo "==> Pausing ${PAUSE}s before running candidate benchmarks"
    sleep "$PAUSE"
fi

echo "==> Running candidate benchmarks (${CANDIDATE_PROPS[*]}) compared to baseline"
java -jar "$OUTPUT_DIR/candidate/benchmark.jar" --serde-path "$OUTPUT_DIR/candidate" -C "$OUTPUT_DIR/baseline" "${BENCHMARK_ARGS[@]}"

echo
echo "==> Comparison done"
echo "    Baseline:  ${BASELINE_PROPS[*]}"
echo "    Candidate: ${CANDIDATE_PROPS[*]}"
echo "    Baseline results:   $OUTPUT_DIR/baseline"
echo "    Comparison results: $OUTPUT_DIR/candidate/*.md"
