# PowSyBl benchmark

## Benchmark configuration

All the benchmark results presented here were obtained on the same hardware and software configuration:

| Component      | Specification                        |
|----------------|--------------------------------------|
| Hardware model | Dell Precision 5680                  |
| Processor      | 13th Gen Intel(R) Core(TM) i7-13700H |
| RAM            | 32 Go                                |
| OS             | Ubuntu 22.04 LTS                     |

Execution is done on a single core, there is no code parallelization unless explicitly stated.

## Load flow benchmark

Load flow benchmark has been done using [JMH](https://github.com/openjdk/jmh) framework and [Open Load Flow v2.1.1](https://github.com/powsybl/powsybl-open-loadflow/releases/tag/v2.1.1). More load flow engines will be added later.

Five networks of various sizes have been used: 

- 3 classical IEEE networks: 14, 118 and 300 buses.
- 2 networks coming from [Matpower toolbox](https://matpower.org/): RTE 1888 buses (EHV French system) and RTE 6515 buses (full EVH + HV French system).

Two different load flow parameters sets have been tested:

- a basic one: this is the most basic configuration we can use for a load flow, so just a Newton-Raphson run without any outer loop.
- a standard one: slack bus is distributed and generator reactive limits are taken into account.

| Network  | Basic parameters | Standard parameters |
|----------|------------------|---------------------|
| IEEE 14  | 179 µs           | 188 µs              |
| IEEE 118 | 1.37 ms          | 1.88 ms             |
| IEEE 300 | 3.5 ms           | 5.9 ms              |
| RTE 1888 | 24.7 ms          | 30.7 ms             |
| RTE 6515 | 118 ms           | 191 ms              |


## Security analysis benchmark

### Manual security analysis benchmark (deprecated)

Security analysis benchmark has been done with RTE 1888 buses and RTE 6515 buses. The same basic and standard load flow 
parameters sets as for load low benchmark have been used. 1000 contingencies have been sequentially simulated for each of the analyses 
(taking the first 1000 lines of the network).

| Network  | Basic parameters    | Standard parameters |
|----------|---------------------|---------------------|
| RTE 1888 | 5 ms / contingency  | 8 ms / contingency  |
| RTE 6515 | 18 ms / contingency | 29 ms / contingency |

### Mono-thread security analysis benchmark

Security analysis benchmark has been done with RTE 1888 buses and RTE 6515 buses. The same basic and standard load flow 
parameters sets as for load low benchmark have been used. 1000 contingencies have been sequentially simulated for each of the analyses 
(taking the first 1000 lines of the network).

| Network  | Basic parameters    | Standard parameters |
|----------|---------------------|---------------------|
| RTE 1888 | 5 ms / contingency  | 8 ms / contingency  |
| RTE 6515 | 19 ms / contingency | 30 ms / contingency |

### Multi-thread security analysis benchmark

Security analysis benchmark has been done with RTE 6515 buses, the standard load flow parameters set and limited to 500
contingencies.

| Network  | 1 thread        | 2 threads           | 4 threads           | 8 threads           |
|----------|-----------------|---------------------|---------------------|---------------------|
| RTE 6515 | 14.60 (1.00)    | 8.23 (0.89)         | 5.03 (0.73)         | 4.00 (0.46)         |


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

## Sensitivity analysis benchmark

TODO


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
java -jar target/benchmark.jar "iidmSerializationBenchmark"

# This will only run the Xmx1G benchmark
java -jar target/benchmark.jar XmxSecurityAnalysisBenchmark.runXmx1G
```

### Deprecated benchmarks (to be removed)
To run the manual Security Analysis benchmark, use the following command:

```
java -cp target/benchmark.jar com.powsybl.benchmark.ManualSecurityAnalysisBenchmark
```
