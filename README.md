# PowSyBl benchmark

## Benchmark configuration

All the benchmark results presented here were obtained on the same hardware and software configuration:

| Component      | Specification         |
|----------------|-----------------------|
| Hardware model | Dell Precision 5690   |
| Processor      | Intel(R) Ultra 7 155H |
| RAM            | 32 Go                 |
| OS             | Ubuntu 22.04 LTS      |

Execution is done on a single core, there is no code parallelization, and the results are in `ms/op` unless explicitly stated.
> [!NOTE]
> When changing hardware configuration, the benchmarks are run for both the previous version and the new one.
> This allows creating a baseline for comparison.
>
> Therefore, numeric results might be different, but the relative performance remains relevant.

## Load flow benchmark

Load flow benchmark has been done using [JMH](https://github.com/openjdk/jmh) framework and [Open Load Flow v2.2.1](https://github.com/powsybl/powsybl-open-loadflow/releases/tag/v2.2.1). 
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
| IEEE 14  | 0,23 (0%)        | 0,23 (-4%)          | 0,23 (0%)                                              |
| IEEE 118 | 1,41 (+3%)       | 1,78 (-12%)         | 1,43 (-5%)                                             |
| IEEE 300 | 3,40 (-1%)       | 5,54 (-9%)          | 3,94 (-5%)                                             |
| RTE 1888 | 24,83 (-2%)      | 30,21 (-2%)         | 26,11 (0%)                                             |
| RTE 6515 | 122,94 (-7%)     | 206,73 (-1%)        | 129,20 (-8%)                                           |
| RealGrid | 125,48 (-1%)     | 193,99 (+6%)        | 143,61 (-8%)                                           |

_Note: those results are for the v2026.1.0 version_

## Security analysis benchmark

### Mono-thread security analysis benchmark

Security analysis benchmark has been done with the IEEE networks, the RTE 1888 buses and RTE 6515 buses networks, and
ENTSOE RealGrid network. The same load flow parameters sets as for load flow benchmark have been used. At most, 1000 
contingencies have been sequentially simulated for each of the analyses (taking the first 1000 existing lines of the network).

The results here are the duration per contingency.

| Network  | Contingencies | Basic parameters   | Standard parameters | Standard parameters <br/>with reactive limits not used |
|----------|---------------|--------------------|---------------------|--------------------------------------------------------|
| IEEE 14  | 85            | 0,01 ms/op (0%)    | 0,02 ms/op (0%)     | 0,01 ms/op (0%)                                        |
| IEEE 118 | 885           | 0,05 ms/op (0%)    | 0,10 ms/op (0%)     | 0,05 ms/op (0%)                                        |
| IEEE 300 | 1520          | 0,15 ms/op (-26%)  | 0,33 ms/op (-30%)   | 0,19 ms/op (-16%)                                      |
| RTE 1888 | 5000          | 0,81 ms/op (-4%)   | 1,51 ms/op (+9%)    | 1,12 ms/op (-7%)                                       |
| RTE 6515 | 5000          | 3,90 ms/op (-4%)   | 5,69 ms/op (-4%)    | 3,48 ms/op (-4%)                                       |
| RealGrid | 5000          | 150,23 ms/op (+4%) | 144,05 ms/op (-1%)  | 153,22 ms/op (+7%)                                     |

_Note: those results are for the v2026.1.0 version_

In the current version, the security analysis is unexpectedly slow for the RealGrid network. This is to be investigated.

### Multi-thread security analysis benchmark

Security analysis benchmark has been done with the RTE 6515 buses network, the standard load flow parameters set and limited to 500
contingencies.

| Network  | 1 thread                    | 2 threads                   | 4 threads                   | 8 threads                  |
|----------|-----------------------------|-----------------------------|-----------------------------|----------------------------|
| RTE 6515 | 21302,09 ms/op (1,00) (-9%) | 12854,87 ms/op (0,83) (-2%) | 9396,80 ms/op (0,57) (+18%) | 5478,87 ms/op (0,49) (-3%) |

_Note: those results are for the v2026.1.0 version_

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

| Network  | Contingencies | Basic parameters  | Standard parameters | Standard parameters <br/>with reactive limits not used |
|----------|---------------|-------------------|---------------------|--------------------------------------------------------|
| IEEE 14  | 85            | 0,02 ms/op (0%)   | 0,02 ms/op (0%)     | 0,02 ms/op (0%)                                        |
| IEEE 118 | 885           | 0,86 ms/op (0%)   | 0,95 ms/op (0%)     | 0,91 ms/op (0%)                                        |
| IEEE 300 | 1520          | 0,98 ms/op (-4%)  | 1,25 ms/op (-5%)    | 1,07 ms/op (-2%)                                       |
| RTE 1888 | 5000          | 2,21 ms/op (-2%)  | 2,56 ms/op (-5%)    | 2,38 ms/op (0%)                                        |
| RTE 6515 | 5000          | 4,94 ms/op (-24%) | 7,72 ms/op (+12%)   | 5,49 ms/op (-23%)                                      |
| RealGrid | 5000          | 5,37 ms/op (+9%)  | 6,29 ms/op (-4%)    | 5,71 ms/op (+6%)                                       |

_Note: those results are for the v2026.1.0 version_

## Serialization benchmark

### Network serialization benchmark

Network serialization benchmark has been done with RTE 6515 buses network and the 
[ENTSOE RealGrid network v3.0.3](https://www.entsoe.eu/Documents/CIM_documents/Grid_Model_CIM/CGMES_ConformityAssessmentScheme_TestConfigurations_v3-0-3.zip).
The results presented here are the average time per operation, given in ms/op.

For the RTE 6515 buses network:

| Benchmark Operation  | XML (XIIDM)      | JSON (JIIDM)  | Binary (BIIDM) | CGMES          |
|----------------------|------------------|---------------|----------------|----------------|
| Deserialization      | 98,77 (0%)       | 59,49 (0%)    | 43,01 (-6%)    | 1620,21 (-10%) |
| Stream Serialization | 117,94 (+3%)     | 106,59 (0%)   | 90,92 (-2%)    | —              |
| File Serialization   | 197,16 (-2%)     | 110,03 (0%)   | 94,48 (+1%)    | 682,82 (0%)    |
| Copy                 | 4052,79 (+1120%) | 124,85 (-48%) | 134,45 (-18%)  | —              |

For the ENTSOE RealGrid network:

| Benchmark Operation  | XML (XIIDM)       | JSON (JIIDM)  | Binary (BIIDM) | CGMES         |
|----------------------|-------------------|---------------|----------------|---------------|
| Deserialization      | 353,66 (-6%)      | 194,87 (0%)   | 118,93 (-11%)  | 3820,13 (-7%) |
| Stream Serialization | 345,67 (+8%)      | 287,14 (+2%)  | 205,91 (-1%)   | —             |
| File Serialization   | 737,14 (-2%)      | 298,55 (+3%)  | 212,74 (-3%)   | 1966,39 (-1%) |
| Copy                 | 19918,36 (+1412%) | 373,43 (-56%) | 312,46 (-34%)  | —             |

_Note: those results are for the v2026.1.0 version_

There is a big degradation in performance for the network copy in XML. A fix is currently underway.

Note that by default, the network copy is in JSON format, so this should not have too much of an impact.
JSON also got twice as fast as the previous version for the network copy.
Binary has gone from beta to stable and has also seen a significant performance improvement.

### Contingency serialization benchmark

Contingency serialization benchmark has been done using RTE 6515 buses network. A list of 1000 contingencies has been 
generated by using the first 1000 lines of the network.

| Benchmark Operation | Time (ms/op) |
|---------------------|--------------|
| Parsing             | 0,84 (0%)    |
| Parsing from bytes  | 0,64 (-8%)   |
| Just reading        | 0,10 (0%)    |
| Reading to string   | 0,10 (0%)    |
| Writing             | 0,24 (-8%)   |
| Buffered writing    | 0,19 (0%)    |

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
