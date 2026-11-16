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

The benchmark isolates the factors that actually drive the cost:

- `size` (n): the number of points in the series.
- `newChunkSize` (k): the target size of each output chunk.
- `compressed`: whether data chunks are compressed (`CompressedDoubleDataChunk`) or uncompressed (`UncompressedDoubleDataChunk`).

**Layout**: the input chunk topology.
- `SINGLE_CHUNK`: one large chunk (uncompressed or compressed)
- `FRAGMENTED`: many single chunks (uncompressed or compressed)

| Layout       | Compressed | newChunkSize (k) | size = 1000 | size = 10000 | size = 100000 |
|--------------|------------|------------------|-------------|--------------|---------------|
| SINGLE_CHUNK | false      | 100              | 0,004 ms/op | 0,329 ms/op  | 33,004 ms/op  |
| SINGLE_CHUNK | false      | 1000             | 0,000 ms/op | 0,036 ms/op  | 3,468 ms/op   |
| FRAGMENTED   | false      | 100              | 0,043 ms/op | 0,440 ms/op  | 4,646 ms/op   |
| FRAGMENTED   | false      | 1000             | 0,292 ms/op | 3,055 ms/op  | 31,031 ms/op  |
| SINGLE_CHUNK | true       | 100              | 0,001 ms/op | 0,006 ms/op  | 0,048 ms/op   |
| SINGLE_CHUNK | true       | 1000             | 0,000 ms/op | 0,001 ms/op  | 0,006 ms/op   |
| FRAGMENTED   | true       | 100              | 0,075 ms/op | 0,756 ms/op  | 8,375 ms/op   |
| FRAGMENTED   | true       | 1000             | 0,477 ms/op | 4,758 ms/op  | 47,592 ms/op  |

_Note: those results are for the v2026.2.0 version_

The algorithmic complexity of the `split` method is O(n² / k) for a single chunk and O(n * k) for fragmented chunks.
