# PowSyBl benchmark

## Benchmark configuration

All the benchmark results presented here were obtained on the same hardware and software configuration:

| Component      | Specification                        |
|----------------|--------------------------------------|
| Hardware model | Dell Precision 5680                  |
| Processor      | 13th Gen Intel(R) Core(TM) i7-13700H |
| RAM            | 32 Go                                |
| OS             | Ubuntu 22.04 LTS                     |

Execution is done on a single core, there is no code parallelization.

## Load flow benchmark

Load flow benchmark has been done using [JMH](https://github.com/openjdk/jmh) framework and [Open Load Flow v1.3.0](https://github.com/powsybl/powsybl-open-loadflow/releases/tag/v1.3.0). More load flow engines will be added later.

Five networks of various sizes have been used: 

- 3 classical IEEE networks: 14, 118 and 300 buses.
- 2 networks coming from [Matpower toolbox](https://matpower.org/): RTE 1888 buses (EHV French system) and RTE 6515 buses (full EVH + HV French system).

Two different load flow parameters sets have been tested:

- a basic one: this is the most basic configuration we can use for a load flow, so just a Newton-Raphson run without any outer loop.
- a standard one: slack bus is distributed and generator reactive limits are taken into account.

| Network  | Basic parameters | Standard parameters |
|----------|------------------|---------------------|
| IEEE 14  | 105 µs           | 108 µs              |
| IEEE 118 | 1.14 ms          | 1.61 ms             |
| IEEE 300 | 3.2 ms           | 4.7 ms              |
| RTE 1888 | 21.3 ms          | 28.8 ms             |
| RTE 6515 | 126 ms           | 248 ms              |


## Security analysis benchmark

Security analysis benchmark has been done with RTE 1888 buses and RTE 6515 buses. The same basic and standard load flow 
parameters sets as for load low benchmark have been used. 1000 contingencies have been sequentially simulated for each of the analyses 
(taking the first 1000 lines of the network).

| Network  | Basic parameters    | Standard parameters |
|----------|---------------------|---------------------|
| RTE 1888 | 6 ms / contingency  | 7 ms / contingency  |
| RTE 6515 | 28 ms / contingency | 39 ms / contingency |

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
java -cp target/benchmark.jar com.powsybl.benchmark.SecurityAnalysisBenchmark
```
