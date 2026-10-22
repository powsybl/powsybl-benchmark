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

| Network  | Basic parameters  | Standard parameters | Standard parameters <br/>with reactive limits not used |
|----------|-------------------|---------------------|--------------------------------------------------------|
| IEEE 14  | 0,13 ms/op (-11%) | 0,14 ms/op (-10%)   | 0,14 ms/op (-6%)                                       |
| IEEE 118 | 1,10 ms/op (+1%)  | 1,56 ms/op (+7%)    | 1,21 ms/op (+2%)                                       |
| IEEE 300 | 2,64 ms/op (+6%)  | 4,65 ms/op (+2%)    | 3,06 ms/op (-4%)                                       |
| RTE 1888 | 18,84 ms/op (-7%) | 24,07 ms/op (+3%)   | 20,24 ms/op (-2%)                                      |
| RTE 6515 | 90,41 ms/op (+2%) | 165,30 ms/op (+5%)  | 102,58 ms/op (-1%)                                     |
| RealGrid | 93,83 ms/op (+5%) | 150,36 ms/op (+9%)  | 108,75 ms/op (+6%)                                     |

_Note: those results are for the v2026.2.0 version_

## Security analysis benchmark

### Mono-thread security analysis benchmark

Security analysis benchmark has been done with the IEEE networks, the RTE 1888 buses and RTE 6515 buses networks, and
ENTSOE RealGrid network. The same load flow parameters sets as for load flow benchmark have been used. At most, 1000 
contingencies have been sequentially simulated for each of the analyses (taking the first 1000 existing lines of the network).

The results here are the duration per contingency.

| Network  | Contingencies | Basic parameters  | Standard parameters | Standard parameters <br/>with reactive limits not used |
|----------|---------------|-------------------|---------------------|--------------------------------------------------------|
| IEEE 14  | 17            | 0,05 ms/op (0%)   | 0,11 ms/op (+34%)   | 0,08 ms/op (+40%)                                      |
| IEEE 118 | 177           | 0,20 ms/op (0%)   | 0,47 ms/op (+20%)   | 0,29 ms/op (+13%)                                      |
| IEEE 300 | 304           | 0,72 ms/op (-1%)  | 1,77 ms/op (+24%)   | 0,99 ms/op (+21%)                                      |
| RTE 1888 | 1000          | 4,17 ms/op (+25%) | 6,94 ms/op (+30%)   | 5,13 ms/op (+10%)                                      |
| RTE 6515 | 1000          | 16,61 ms/op (+5%) | 25,69 ms/op (+9%)   | 18,60 ms/op (+31%)                                     |

_Note: those results are for the v2026.2.0 version_

_Note: there is a performance issue in the security analysis, especially in the standard parameters, this is under investigation._

In the current version, the security analysis is unexpectedly slow for the RealGrid network. This is to be investigated.

| Network  | Contingencies | Basic parameters | Standard parameters | Standard parameters <br/>with reactive limits not used |
|----------|---------------|------------------|---------------------|--------------------------------------------------------|
| RealGrid | 1000          | 635,96 ms/op     | 547,45 ms/op        | 547,05 ms/op                                           |

_Note: those results are for the v2026.2.0 version_

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

| Network  | Contingencies | Basic parameters   | Standard parameters | Standard parameters <br/>with reactive limits not used |
|----------|---------------|--------------------|---------------------|--------------------------------------------------------|
| IEEE 14  | 17            | 0,06 ms/op (0%)    | 0,13 ms/op (+37%)   | 0,10 ms/op (+32%)                                      |
| IEEE 118 | 177           | 3,10 ms/op (-4%)   | 3,48 ms/op (+1%)    | 3,32 ms/op (-1%)                                       |
| IEEE 300 | 304           | 3,73 ms/op (+2%)   | 4,77 ms/op (+1%)    | 3,93 ms/op (-5%)                                       |
| RTE 1888 | 1000          | 8,71 ms/op (+2%)   | 11,59 ms/op (+2%)   | 9,82 ms/op (+4%)                                       |
| RTE 6515 | 1000          | 23,43 ms/op (+20%) | 33,31 ms/op (+4%)   | 25,88 ms/op (+22%)                                     |
| RealGrid | 1000          | 21,79 ms/op (+1%)  | 26,23 ms/op (+3%)   | 23,86 ms/op (+15%)                                     |

_Note: those results are for the v2026.2.0 version_

_Note: there is a performance issue in the sensitivity analysis, especially in the standard parameters, this is under investigation._

## Serialization benchmark

### Network serialization benchmark

Network serialization benchmark has been done with RTE 6515 buses network and the 
[ENTSOE RealGrid network v3.0.3](https://www.entsoe.eu/Documents/CIM_documents/Grid_Model_CIM/CGMES_ConformityAssessmentScheme_TestConfigurations_v3-0-3.zip).
The results presented here are the average time per operation, given in ms/op.

For the RTE 6515 buses network:

| Benchmark Operation  | XML (XIIDM)   | JSON (JIIDM)  | Binary (BIIDM) | CGMES         |
|----------------------|---------------|---------------|----------------|---------------|
| Deserialization      | 72,55 (-5%)   | 48,94 (-10%)  | 32,07 (-9%)    | 1370,36 (-4%) |
| Stream Serialization | 73,15 (-11%)  | 66,64 (-12%)  | 57,96 (-9%)    | —             |
| File Serialization   | 190,50 (+13%) | 104,84 (+36%) | 78,07 (+21%)   | 688,29 (-2%)  |
| Copy                 | 172,08 (-95%) | 84,43 (-6%)   | 88,98 (-16%)   | —             |

For the ENTSOE RealGrid network:

| Benchmark Operation  | XML (XIIDM)   | JSON (JIIDM)  | Binary (BIIDM) | CGMES         |
|----------------------|---------------|---------------|----------------|---------------|
| Deserialization      | 284,70 (-2%)  | 162,84 (-9%)  | 89,92 (-8%)    | 3213,83 (+2%) |
| Stream Serialization | 241,68 (-10%) | 196,61 (0%)   | 152,85 (-5%)   | —             |
| File Serialization   | 745,39 (+11%) | 293,41 (+41%) | 247,78 (+54%)  | 1889,08 (0%)  |
| Copy                 | 698,47 (-96%) | 229,91 (-11%) | 234,06 (-6%)   | —             |

_Note: those results are for the v2026.2.0 version_

**_Note: There was a performance issue in the previous release for the XML copy operation, which has been fixed in the v7.4.0 release
of powsybl-core, hence the huge improvement._**

**_Note 2: There is now a performance issue in the file serialization, which will be fixed in the next release of powsybl-core._**

### Contingency serialization benchmark

Contingency serialization benchmark has been done using RTE 6515 buses network. A list of 1000 contingencies has been 
generated by using the first 1000 lines of the network.

| Benchmark Operation | Time (ms/op) |
|---------------------|--------------|
| Parsing             | 0,779 (+11%) |
| Parsing from bytes  | 0,536 (+1%)  |
| Just reading        | 0,089 (-8%)  |
| Reading to string   | 0,090 (-7%)  |
| Writing             | 0,142 (-1%)  |
| Buffered writing    | 0,162 (0%)   |

_Note: those results are for the v2026.2.0 version_

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
