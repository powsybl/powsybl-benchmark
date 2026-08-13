#!/usr/bin/env bash

##
# Copyright (c) 2026, RTE (https://www.rte-france.com)
# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this
# file, You can obtain one at http://mozilla.org/MPL/2.0/.
# SPDX-License-Identifier: MPL-2.0
#
runBench () {
 git checkout "$1"
 mvn clean verify
 java -jar target/benchmark.jar LoadFlowBenchmark.benchmarkLoadFlow \
                                 MonoThreadSecurityAnalysisBenchmark.benchmarkMonoThreadSecurityAnalysisWithRealGrid \
                                 MultiThreadSecurityAnalysisBenchmark.benchmarkMultiThreadsSecurityAnalysis \
                                 SensitivityAnalysisBenchmark.benchmarkSensitivityAnalysis \
                                 ContingencySerializationBenchmark.benchmark1Parsing \
                                 ContingencySerializationBenchmark.benchmark2ParsingFromBytes \
                                 ContingencySerializationBenchmark.benchmark3JustReading \
                                 ContingencySerializationBenchmark.benchmark4ReadingToString \
                                 ContingencySerializationBenchmark.benchmark5Writing \
                                 ContingencySerializationBenchmark.benchmark6BufferedWriting \
                                 NetworkSerializationBenchmark.benchmark1NetworkDeserialization \
                                 NetworkSerializationBenchmark.benchmark2NetworkStreamSerialization \
                                 NetworkSerializationBenchmark.benchmark3NetworkFileSerialization \
                                 NetworkSerializationBenchmark.benchmark4NetworkCopy
 mkdir -p "markdown/$1"
 mv benchmark_reports/*.md "markdown/$1"
}

rm benchmark_reports/*.md
runBench "$1"
runBench "$2"
