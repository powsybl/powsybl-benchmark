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

Load flow benchmark has been done using [JMH](https://github.com/openjdk/jmh) framework and [Open Load Flow v2.1.1](https://github.com/powsybl/powsybl-open-loadflow/releases/tag/v2.1.1). More load flow engines will be added later.

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
| IEEE 14  | 151 µs           | 152 µs              | 58 µs                                                  |
| IEEE 118 | 1.09 ms          | 1.53 ms             | 411 µs                                                 |
| IEEE 300 | 2.91 ms          | 4.88 ms             | 1.05 ms                                                |
| RTE 1888 | 21.5 ms          | 26.6 ms             | 9.7 ms                                                 |
| RTE 6515 | 98.4 ms          | 177.8 ms            | 49.1 ms                                                |
| RealGrid | 103 ms           | 168 ms              | 63.1 ms                                                |

_Note: those results are for the v2025.3.2 version_

## Security analysis benchmark

### Manual security analysis benchmark (deprecated)

Security analysis benchmark has been done with RTE 1888 buses and RTE 6515 buses. The same basic and standard load flow 
parameters sets as for load flow benchmark have been used. 1000 contingencies have been sequentially simulated for each of the analyses 
(taking the first 1000 lines of the network).

| Network  | Basic parameters    | Standard parameters |
|----------|---------------------|---------------------|
| RTE 1888 | 5 ms / contingency  | 8 ms / contingency  |
| RTE 6515 | 18 ms / contingency | 29 ms / contingency |

Another run has been done using i7-10610U CPU, and 32 Go RAM. CGMES Real grid 6051 network with basic parameter has been added to the run.

| Network       | Basic parameters    | Standard parameters  |
|---------------|---------------------|----------------------|
| RTE 1888      | 18 ms / contingency | 23 ms / contingency  |
| RTE 6515      | 74 ms / contingency | 111 ms / contingency |
| RealGrid 6051 | 71 ms / contingency | -                    |

_Note: those results are for the v2025.3.2 version_

### Mono-thread security analysis benchmark

Security analysis benchmark has been done with the IEEE networks, the RTE 1888 buses and RTE 6515 buses networks, and
ENTSOE RealGrid network. The same load flow parameters sets as for load flow benchmark have been used. At most, 1000 
contingencies have been sequentially simulated for each of the analyses (taking the first 1000 existing lines of the network).

The results here are the duration per contingency.

| Network  | Contincencies | Basic parameters | Standard parameters | Standard parameters <br/>with reactive limits not used |
|----------|---------------|------------------|---------------------|--------------------------------------------------------|
| IEEE 14  | 17            | 49 µs            | 83 µs               | 16 µs                                                  |
| IEEE 118 | 177           | 221 µs           | 468 µs              | 43 µs                                                  |
| IEEE 300 | 304           | 851 µs           | 1.9 ms              | 141 µs                                                 |
| RTE 1888 | 1000          | 5 ms             | 8 ms                | 1.2 ms                                                 |
| RTE 6515 | 1000          | 18 ms            | 29.85 ms            | 7 ms                                                   |
| RealGrid | 1000          | 704 ms           | 696 ms              | 626 ms                                                 |

_Note: those results are for the v2025.3.2 version_

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
| IEEE 14  | 17            | 68 µs            | 102 µs              | 19 µs                                                  |
| IEEE 118 | 177           | 3.4 ms           | 3.8 ms              | 2.9 ms                                                 |
| IEEE 300 | 304           | 3.9 ms           | 5.0 ms              | 2.7 ms                                                 |
| RTE 1888 | 1000          | 9.3 ms           | 12.2 ms             | 3.7 ms                                                 |
| RTE 6515 | 1000          | 25.0 ms          | 35.4 ms             | 4.9 ms                                                 |
| RealGrid | 1000          | 23.2 ms          | 27.1 ms             | 5.1 ms                                                 |

_Note: those results are for the v2025.3.2 version_

## Serialization benchmark

### Network serialization benchmark

Network serialization benchmark has been done with RTE 6515 buses network and the 
[ENTSOE RealGrid network v3.0.3](https://www.entsoe.eu/Documents/CIM_documents/Grid_Model_CIM/CGMES_ConformityAssessmentScheme_TestConfigurations_v3-0-3.zip).

For the RTE 6515 buses network:

| Benchmark Operation    | XML (XIIDM) | JSON (JIIDM) | Binary (BIIDM) | CGMES   |
|------------------------|-------------|--------------|----------------|---------|
| Deserialization        | 73.26       | 48.36        | 39.63          | 1644.27 |
| Stream serialization   | 95.37       | 88.69        | 78.93          | —       |
| File serialization     | 182.18      | 94.46        | 75.23          | 700.93  |
| Copy                   | 355.47      | 213.81       | 145.36         | —       |

For the ENTSOE RealGrid network:

| Benchmark Operation    | XML (XIIDM) | JSON (JIIDM) | Binary (BIIDM) | CGMES   |
|------------------------|-------------|--------------|----------------|---------|
| Deserialization        | 273.87      | 166.77       | 124.26         | 3649.50 |
| Stream serialization   | 306.92      | 247.26       | 200.09         | —       |
| File serialization     | 757.48      | 273.10       | 216.81         | 1959.44 |
| Copy                   | 1275.75     | 854.35       | 437.56         | —       |

_Note: those results are for the v2025.3.2 version_

### Contingency serialization benchmark

Contingency serialization benchmark has been done using RTE 6515 buses network. A list of 1000 contingencies has been 
generated by using the first 1000 lines of the network.

| Benchmark Operation | Time (ms/op) |
|---------------------|--------------|
| Parsing             | 0.836        |
| Parsing from bytes  | 0.649        |
| Just reading        | 0.104        |
| Reading to string   | 0.107        |
| Writing             | 0.188        |
| Buffered writing    | 0.167        |

_Note: those results are for the v2025.3.2 version_

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

### Deprecated benchmarks (to be removed)
To run the manual Security Analysis benchmark, use the following command:

```
java -cp target/benchmark.jar com.powsybl.benchmark.security.ManualSecurityAnalysisBenchmark
```
