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

## Time series benchmark

Time series benchmark has been done using the [JMH](https://github.com/openjdk/jmh) framework.

### Node printing benchmark

This benchmark measures `NodeCalcPrinter.print(...)`, which traverses all the tree to
render it as an expression string. The `size` parameter is the number of nodes.

| Operation     | 1000              | 2000              | 4000              | 8000              |
|---------------|-------------------|-------------------|-------------------|-------------------|
| Node printing | 0,03 ms/op (-86%) | 0,08 ms/op (-91%) | 0,16 ms/op (-95%) | 0,35 ms/op (-97%) |

_Note: those results are for the v2026.2.0 version_

### Split benchmark
This benchmark measures `TimeSeries.split(list, newChunkSize)`, which slices a stored double time series
into fixed-size chunks (use case: splitting a series)

The benchmark isolates the two factors that actually drive the cost:

- `size` (n): the number of points in the series.
- `newChunkSize` (k): the target size of each output chunk.

**Layout**: the input chunk topology.
- `SINGLE_CHUNK`: one large uncompressed chunk
- `FRAGMENTED`: many single uncompressed chunks

| Layout       | newChunkSize (k) | size = 1000       | size = 10000       | size = 100000      |
|--------------|------------------|-------------------|--------------------|--------------------|
| SINGLE_CHUNK | 100              | 0,003 ms/op (-4%) | 0,265 ms/op (-4%)  | 30,814 ms/op (-6%) |
| SINGLE_CHUNK | 1000             | 0,000 ms/op (-5%) | 0,028 ms/op (-5%)  | 3,059 ms/op (-7%)  |
| FRAGMENTED   | 100              | 0,040 ms/op (-5%) | 0,403 ms/op (-6%)  | 4,185 ms/op (-15%) |
| FRAGMENTED   | 1000             | 0,307 ms/op (-8%) | 3,024 ms/op (-10%) | 31,150 ms/op (-8%) |

_Note: those results are for the v2026.2.0 version_

The algorithmic complexity of the `split` method is O(n² / k) for a single chunk and O(n * k) for fragmented chunks.