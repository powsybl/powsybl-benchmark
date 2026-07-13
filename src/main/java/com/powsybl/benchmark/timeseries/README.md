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

| Operation | 1000         | 2000        | 4000         | 8000         |
|-----------|--------------|-------------|--------------|--------------|
| print     | 0,28 (ms/op) | 1,1 (ms/op) | 4.25 (ms/op) | 16.6 (ms/op) |

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

| Layout       | newChunkSize (k) | size = 1000   | size = 10 000  | size = 100 000  | complexity |
|--------------|------------------|---------------|----------------|-----------------|------------|
| SINGLE_CHUNK | 100              | 0,005 (ms/op) | 0,472  (ms/op) | 42,079  (ms/op) | O(n² / k)  |
| SINGLE_CHUNK | 1000             | 0.001 (ms/op) | 0,051  (ms/op) | 4,198  (ms/op)  | O(n² / k)  |
| FRAGMENTED   | 100              | 0,086 (ms/op) | 0,861  (ms/op) | 10,834  (ms/op) | O(n * k)   |
| FRAGMENTED   | 1000             | 0,384 (ms/op) | 3,861  (ms/op) | 38,530  (ms/op) | O(n * k)   |

_Note: those results are for the v2026.0.0 version_