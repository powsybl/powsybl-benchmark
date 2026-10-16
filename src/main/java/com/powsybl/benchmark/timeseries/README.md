# PowSyBl benchmark

## Benchmark configuration

All the benchmark results presented here were obtained on the same hardware and software configuration:

| Component      | Specification               |
|----------------|-----------------------------|
| Hardware model | Dell Inc. Precision 5520    |
| Processor      | Intel(R) Core(TM) i7-6820HQ |
| RAM            | 32 Go                       |
| OS             | Ubuntu 22.04.5 LTS          |

Execution is done on a single core, there is no code parallelization, and the results are in `ms/op` unless explicitly stated.

## Time series benchmark

Time series benchmark has been done using the [JMH](https://github.com/openjdk/jmh) framework.

### Node printing benchmark

This benchmark measures `NodeCalcPrinter.print(...)`, which traverses all the tree to
render it as an expression string. The `size` parameter is the number of nodes.

| Operation     | 1000       | 2000       | 4000       | 8000        |
|---------------|------------|------------|------------|-------------|
| Node printing | 0,22 ms/op | 0,85 ms/op | 3,38 ms/op | 13,73 ms/op |

_Note: those results are for the v2026.0.0 version_

### Split benchmark
This benchmark measures `TimeSeries.split(list, newChunkSize)`, which slices a stored double time series
into fixed-size chunks (use case: splitting a series)

The benchmark isolates the two factors that actually drive the cost:

- `size` (n): the number of points in the series.
- `newChunkSize` (k): the target size of each output chunk.

**Layout**: the input chunk topology.
- `SINGLE_CHUNK`: one large uncompressed chunk
- `FRAGMENTED`: many single uncompressed chunks

| Layout       | newChunkSize (k) | size = 1000 | size = 10000 | size = 100000 |
|--------------|------------------|-------------|--------------|---------------|
| SINGLE_CHUNK | 100              | 0,003 ms/op | 0,275 ms/op  | 32,950 ms/op  |
| SINGLE_CHUNK | 1000             | 0,000 ms/op | 0,029 ms/op  | 3,295 ms/op   |
| FRAGMENTED   | 100              | 0,042 ms/op | 0,430 ms/op  | 4,900 ms/op   |
| FRAGMENTED   | 1000             | 0,335 ms/op | 3,360 ms/op  | 33,707 ms/op  |

_Note: those results are for the v2026.1.0 version_

The algorithmic complexity of the `split` method is O(n² / k) for a single chunk and O(n * k) for fragmented chunks.