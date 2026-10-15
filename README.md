# PowSyBl benchmark

## Benchmark configuration

All the benchmark results presented here were obtained on the same hardware and software configuration:

| Component      | Specification                        |
|----------------|--------------------------------------|
| Hardware model | Dell Precision 5680                  |
| Processor      | 13th Gen Intel(R) Core(TM) i7-13700H |
| RAM            | 32 Go                                |
| OS             | Ubuntu 22.04 LTS                     |

Execution is done on a single core, there is no code parallelization, and the results are in `ms/op` unless explicitly stated.

## Load flow benchmark

Load flow benchmark has been done using [JMH](https://github.com/openjdk/jmh) framework and [Open Load Flow v2.3.0](https://github.com/powsybl/powsybl-open-loadflow/releases/tag/v2.3.0). 
More load flow engines will be added later.

Six networks of various sizes have been used: 

- 3 classical IEEE networks: 14, 118, and 300 buses.
- 2 networks coming from [Matpower toolbox](https://matpower.org/): RTE 1888 buses (EHV French system) and RTE 6515 buses (full EVH + HV French system).
- ENTSOE [RealGrid network v3.0.3](https://www.entsoe.eu/Documents/CIM_documents/Grid_Model_CIM/CGMES_ConformityAssessmentScheme_TestConfigurations_v3-0-3.zip).

Three different load flow parameters sets have been tested:

- a basic one: this is the most basic configuration we can use for a load flow, so just a Newton-Raphson run without any outer loop.
- a standard one: slack bus is distributed and generator reactive limits are taken into account.
- a standard one with the reactive limits not used

| Network  | Basic parameters | Standard parameters | Standard parameters <br/>with reactive limits not used |
|----------|------------------|---------------------|--------------------------------------------------------|
| IEEE 14  | 0,15 ms/op       | 0,16 ms/op          | 0,15 ms/op                                             |
| IEEE 118 | 1,09 ms/op       | 1,46 ms/op          | 1,18 ms/op                                             |
| IEEE 300 | 2,50 ms/op       | 4,57 ms/op          | 3,17 ms/op                                             |
| RTE 1888 | 20,31 ms/op      | 23,36 ms/op         | 20,62 ms/op                                            |
| RTE 6515 | 88,71 ms/op      | 158,02 ms/op        | 103,21 ms/op                                           |
| RealGrid | 89,55 ms/op      | 138,05 ms/op        | 103,06 ms/op                                           |

_Note: those results are for the v2026.1.0 version_

## Security analysis benchmark

### Mono-thread security analysis benchmark

Security analysis benchmark has been done with the IEEE networks, the RTE 1888 buses and RTE 6515 buses networks, and
ENTSOE RealGrid network. The same load flow parameters sets as for load flow benchmark have been used. At most, 1000 
contingencies have been sequentially simulated for each of the analyses (taking the first 1000 existing lines of the network).

The results here are the duration per contingency.

| Network  | Contingencies | Basic parameters | Standard parameters | Standard parameters <br/>with reactive limits not used |
|----------|---------------|------------------|---------------------|--------------------------------------------------------|
| IEEE 14  | 17            | 45 µs            | 80 µs               | 53 µs                                                  |
| IEEE 118 | 177           | 199 µs           | 389 µs              | 253 µs                                                 |
| IEEE 300 | 304           | 728 µs           | 1.42 ms             | 819 µs                                                 |
| RTE 1888 | 1000          | 3.33 ms          | 5.33 ms             | 4.67 ms                                                |
| RTE 6515 | 1000          | 15.8 ms          | 23.5 ms             | 14.2 ms                                                |

_Note: those results are for the v2026.1.0 version_

In the current version, the security analysis is unexpectedly slow for the RealGrid network. This is to be investigated.

| Network  | Contingencies | Basic parameters | Standard parameters | Standard parameters <br/>with reactive limits not used |
|----------|---------------|------------------|---------------------|--------------------------------------------------------|
| RealGrid | 1000          | 719 ms           | 636 ms              | 628 ms                                                 |

_Note: those results are for the v2025.3.3 version_

### Multi-thread security analysis benchmark

Security analysis benchmark has been done with the RTE 6515 buses network, the standard load flow parameters set and limited to 500
contingencies.

| Network  | 1 thread        | 2 threads           | 4 threads           | 8 threads           |
|----------|-----------------|---------------------|---------------------|---------------------|
| RTE 6515 | 14.60 (1.00)    | 8.23 (0.89)         | 5.03 (0.73)         | 4.00 (0.46)         |

_Note: those results are for the v2025.3.2 version_

### Influence of the `-Xmx` parameter

Using the same configuration as the multi-thread benchmark, the influence of the `-Xmx` parameter on the performance has 
been measured.

The following table shows the performance of the multi-thread benchmark with different values of `-Xmx` (given in s/op),
and the parallelization efficiency (equals to `(T₁ / Tₙ) / n)`.

| Xmx value | 1 thread      | 2 threads     | 4 threads    | 8 threads    |
|-----------|---------------|---------------|--------------|--------------|
| 128M      | Failed        | Failed        | Failed       | Failed       |
| 256M      | 14.33 (1.00)  | 9.07 (0.79)   | Failed       | Failed       |
| 512M      | 14.57 (1.00)  | 8.35 (0.87)   | 5.70 (0.64)  | Failed       |
| 1G        | 14.12 (1.00)  | 8.64 (0.82)   | 5.22 (0.68)  | 4.42 (0.40)  |
| 2G        | 15.26 (1.00)  | 8.75 (0.87)   | 5.25 (0.73)  | 4.25 (0.45)  |
| 4G        | 14.96 (1.00)  | 8.89 (0.84)   | 5.06 (0.74)  | 4.02 (0.47)  |
| 8G        | 15.12 (1.00)  | 8.73 (0.87)   | 5.05 (0.75)  | 4.07 (0.47)  |
| Undefined | 15.17 (1.00)  | 8.70 (0.87)   | 5.07 (0.75)  | 4.15 (0.46)  |

The failures are due to insufficient memory (Out-of-memory error).

_Note: those results are for the v2025.3.2 version_

## Sensitivity analysis benchmark

Sensitivity analysis benchmark has been done with the IEEE networks, the RTE 1888 buses and RTE 6515 buses networks, and
ENTSOE RealGrid network. The same load flow parameters sets as for load flow benchmark have been used.

At most, 1000 contingencies have been simulated for each of the analyses (taking the first 1000 lines of the network).
For each contingency, at most 10,000 factors are computed. Factors computed are the branch flow per injection increase. All 
permutations are computed and only the first 10,000 are selected.

This table presents the average execution time per contingency and factors for all networks and parameters sets.

| Network  | Contingencies | Basic parameters | Standard parameters | Standard parameters <br/>with reactive limits not used |
|----------|---------------|------------------|---------------------|--------------------------------------------------------|
| IEEE 14  | 17            | 65 µs            | 95 µs               | 73 µs                                                  |
| IEEE 118 | 177           | 3.2 ms           | 3.4 ms              | 3.4 ms                                                 |
| IEEE 300 | 304           | 3.7 ms           | 4.7 ms              | 4.1 ms                                                 |
| RTE 1888 | 1000          | 8.5 ms           | 11.3 ms             | 9.4 ms                                                 |
| RTE 6515 | 1000          | 19.5 ms          | 32.2 ms             | 21.3 ms                                                |
| RealGrid | 1000          | 21.7 ms          | 25.5 ms             | 20.7 ms                                                |

_Note: those results are for the v2026.1.0 version_

## Serialization benchmark

### Network serialization benchmark

Network serialization benchmark has been done with RTE 6515 buses network and the 
[ENTSOE RealGrid network v3.0.3](https://www.entsoe.eu/Documents/CIM_documents/Grid_Model_CIM/CGMES_ConformityAssessmentScheme_TestConfigurations_v3-0-3.zip).
The results presented here are the average time per operation, given in ms/op.

For the RTE 6515 buses network:

| Benchmark Operation  | XML (XIIDM) | JSON (JIIDM) | Binary (BIIDM) | CGMES   |
|----------------------|-------------|--------------|----------------|---------|
| Deserialization      | 76,07       | 54,14        | 35,11          | 1428,34 |
| Stream Serialization | 81,85       | 75,86        | 63,73          | —       |
| File Serialization   | 168,06      | 77,22        | 64,56          | 700,12  |
| Copy                 | 3537,74     | 89,65        | 106,04         | —       |

For the ENTSOE RealGrid network:

| Benchmark Operation  | XML (XIIDM) | JSON (JIIDM) | Binary (BIIDM) | CGMES   |
|----------------------|-------------|--------------|----------------|---------|
| Deserialization      | 290,50      | 179,81       | 97,53          | 3166,03 |
| Stream Serialization | 269,29      | 196,65       | 160,36         | —       |
| File Serialization   | 670,42      | 207,65       | 161,40         | 1898,05 |
| Copy                 | 17307,93    | 258,38       | 249,94         | —       |

_Note: those results are for the v2026.1.0 version_

**_Note: There is a performance issue in this release for the XML copy operation, which has been fixed in the v7.4.0 release
of powsybl-core._**

### Contingency serialization benchmark

Contingency serialization benchmark has been done using RTE 6515 buses network. A list of 1000 contingencies has been 
generated by using the first 1000 lines of the network.

| Benchmark Operation | Time (ms/op) |
|---------------------|--------------|
| Parsing             | 0,700        |
| Parsing from bytes  | 0,528        |
| Just reading        | 0,097        |
| Reading to string   | 0,097        |
| Writing             | 0,143        |
| Buffered writing    | 0,162        |

_Note: those results are for the v2026.1.0 version_

## Running the benchmarks

Build the project using Maven:

```
mvn clean verify
```

Use the self-contained executable JAR, which holds the benchmarks and all essential JMH infrastructure code:

```
java -jar target/benchmark.jar
```

To run specific benchmarks, you can:
- add the benchmark(s) class name as a parameter to the JAR command line
- add the benchmark(s) method name as a parameter to the JAR command line
- add a regex as a parameter to the JAR command line

**Examples:**
```
# Run everything in the MultiThreadSecurityAnalysisBenchmark class
java -jar target/benchmark.jar MultiThreadSecurityAnalysisBenchmark

# This will run only benchmarks that contain "1G" or "2G" in their name
java -jar target/benchmark.jar "1G|2G"

# This will run all the IIDM serialization benchmarks (XML, JSON and Binary)
java -jar target/benchmark.jar NetworkSerializationBenchmark

# This will only run the Xmx1G benchmark
java -jar target/benchmark.jar XmxSecurityAnalysisBenchmark.runXmx1G
```

To run only the benchmarks used for the release, use the following command:

```
java -jar target/benchmark.jar --release
```

To get a list of all available flags, run:
```
java -jar target/benchmark.jar --help
```
