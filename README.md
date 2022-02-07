# PowSyBl benchmark



## Load flow benchmark



Load flow benchmark has been done using [JMH](https://github.com/openjdk/jmh) framework and [Open Load Flow v0.16.0](https://github.com/powsybl/powsybl-open-loadflow/releases/tag/v0.16.0). More load flow engines will be added later.

Five networks of various sizes have been used: 

- 3 classical IEEE networks: 14, 118 and 300 buses 
- 2 networks coming from [Matpower toolbox](https://matpower.org/): RTE 6515 buses (full French TSO EVH + HV network) and Pegase 13659 buses network (a pan European network).

Two differents load flow parameters sets have been tested:

- a basic one: this a the most basic configuration we can use for a load flow so just a Newton-Raphson run without any outer loop.
- a standard one: slack bus is distributed and generator reactive limits are taken into account.



This table reports the average time execution for all network and parameters sets using a MacBookPro 2021 M1 Max, 16Go. Execution is done on a single core , there is no code parallelisation.

| Network      | Basic parameters | Standard parameters |
| ------------ | ---------------- | ------------------- |
| IEEE 14      | 149 &#181;s      | 222 &#181;s         |
| IEEE 118     | 1,26 ms          | 2,35 ms             |
| IEEE 300     | 10,7 ms          | 10,7 ms             |
| RTE 6515     | 147 ms           | 274 ms              |
| Pegase 13659 | 510 ms           | 1.08 s              |



## Security analysis benchmark

TODO



## Security analysis benchmark

TODO
