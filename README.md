# PowSyBl benchmark



## Load flow benchmark



Load flow benchmark has been done using [JMH](https://github.com/openjdk/jmh) framework and [Open Load Flow v1.3.0](https://github.com/powsybl/powsybl-open-loadflow/releases/tag/v1.3.0). More load flow engines will be added later.

Five networks of various sizes have been used: 

- 3 classical IEEE networks: 14, 118 and 300 buses.
- 2 networks coming from [Matpower toolbox](https://matpower.org/): RTE 1888 buses (EHV French system) and RTE 6515 buses (full EVH + HV French system).

Two differents load flow parameters sets have been tested:

- a basic one: this a the most basic configuration we can use for a load flow so just a Newton-Raphson run without any outer loop.
- a standard one: slack bus is distributed and generator reactive limits are taken into account.

This table reports the average time execution for all networks and parameters sets using a MacBook Pro 2021, processor M1 Pro, 16 Go. Execution is done on a single core, there is no code parallelization.

| Network  | Basic parameters | Standard parameters |
| -------- |------------------|---------------------|
| IEEE 14  | 140 &#181;s      | 140 &#181;s         |
| IEEE 118 | 1,05 ms          | 1.54 ms             |
| IEEE 300 | 3.2 ms           | 4.8 ms              |
| RTE 1888 | 22,5 ms          | 31.3 ms             |
| RTE 6515 | 123 ms           | 250 ms              |



## Security analysis benchmark

Security analysis benchmark has been done with RTE 1888 buses and RTE 6515 buses. Same basic and standard load flow parameters sets as for load low benchmark have been used. 1000 contingencies have been simulated for each of the analysis (taking first 1000 lines of the network).

This table reports the average time execution per contingency for all networks and parameters sets using a MacBook Pro 2021, processor M1 Pro, 16 Go. Execution is done on a single core, there is no code parallelization, contingencies are sequentially simulated.

| Network  | Basic parameters    | Standard parameters |
| -------- |---------------------|---------------------|
| RTE 1888 | 7 ms / contingency  | 9 ms / contingency  |
| RTE 6515 | 31 ms / contingency | 41 ms / contingency |



## Sensitivity analysis benchmark

TODO


## Running the benchmarks

Build the project using Maven:

```
mvn clean verify
```

Use the self-contained executable JAR which holds the benchmarks and all essential JMH infrastructure code:

```
java -jar target/benchmark.jar
```
