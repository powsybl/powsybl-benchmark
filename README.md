# PowSyBl benchmark



## Load flow benchmark



Load flow benchmark has been done using [JMH](https://github.com/openjdk/jmh) framework and [Open Load Flow v0.18.0](https://github.com/powsybl/powsybl-open-loadflow/releases/tag/v0.18.0). More load flow engines will be added later.

Five networks of various sizes have been used: 

- 3 classical IEEE networks: 14, 118 and 300 buses.
- 2 networks coming from [Matpower toolbox](https://matpower.org/): RTE 1888 buses (EHV French system) and RTE 6515 buses (full EVH + HV French system).

Two differents load flow parameters sets have been tested:

- a basic one: this a the most basic configuration we can use for a load flow so just a Newton-Raphson run without any outer loop.
- a standard one: slack bus is distributed and generator reactive limits are taken into account.

This table reports the average time execution for all networks and parameters sets using a MacBook Pro 2021, processor M1 Pro, 16 Go. Execution is done on a single core, there is no code parallelization.

| Network  | Basic parameters | Standard parameters |
| -------- |------------------|---------------------|
| IEEE 14  | 146 &#181;s      | 223 &#181;s         |
| IEEE 118 | 1,25 ms          | 2,29 ms             |
| IEEE 300 | 10,7 ms          | 10,7 ms             |
| RTE 1888 | 28,6 ms          | 36.9 ms             |
| RTE 6515 | 146 ms           | 255 ms              |



## Security analysis benchmark

Security analysis benchmark has been done with RTE 1888 buses and RTE 6515 buses. Same basic and standard load flow parameters sets as for load low benchmark have been used. 1000 contingencies have been simulated for each of the analysis (taking first 1000 lines of the network).

This table reports the average time execution per contingency for all networks and parameters sets using a MacBook Pro 2021, processor M1 Pro, 16 Go. Execution is done on a single core, there is no code parallelization, contingencies are sequentially simulated.

| Network  | Basic parameters    | Standard parameters |
| -------- |---------------------|---------------------|
| RTE 1888 | 7 ms / contingency  | 10 ms / contingency |
| RTE 6515 | 34 ms / contingency | 46 ms / contingency |

Another run has been done using i7-10610U CPU, and 32 Go RAM. CGMES Real grid 6051 network with basic parameter has been added to the run.

| Network       | Basic parameters    | Standard parameters |
| ------------- | ------------------- | ------------------- |
| RTE 1888      | 18 ms / contingency | 23 ms / contingency |
| RTE 6515      | 74 ms / contingency | 111 ms / contingency |
| RealGrid 6051 | 71 ms / contingency | - |


## Sensitivity analysis benchmark

Sensitivity analysis benchmark has been done with RTE 1888 buses and RTE 6515 buses. Same basic and standard load flow parameters sets as for load low benchmark have been used. 1000 contingencies have been simulated for each of the analysis (taking first 1000 lines of the network).
For each contingency 10000 factors are computed. Factors computed are the branch flow per injection increase. All permutation are computed and only the first 10000 are selected.

This table reports the average time execution per contingency and factors for all networks and parameters sets using i7-10610U CPU, and 32 Go RAM. Execution is done on a single core, there is no code parallelization, contingencies are sequentially simulated.

| Network       | Basic parameters                               | Standard parameters                            |
| ------------- | ---------------------------------------------- | ---------------------------------------------  |
| RTE 1888      | 18 ms / contingency,  555555 factors / second  | 26 ms / contingency,  384615 factors / second  |
| RTE 6515      | 73 ms / contingency,  136986 factors / second  | 101 ms / contingency,  99009 factors / second  |
| RealGrid 6051 | 64 ms / contingency,  156250 factors / second  | -                                              |
