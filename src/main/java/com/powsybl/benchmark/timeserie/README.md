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

| Operation            | 1000 | 2000 | 4000 | 8000 |
|----------------------|------|------|------|------|
| print - time (ms/op) | 0,28 | 1,1  | 4.25 | 16.6 |

_Note: those results are for the v2026.0.0 version_

### Split benchmark
This benchmark measures `TimeSeries.split(...)`, which slices a stored double time series
into smaller chunks. The `size` parameter is the number of points in the series.

| Size (points)        | 100000    | 200000   | 400000    |
|----------------------|-----------|----------|-----------|
| split - time (ms/op) | 3412,02   | 13318,47 | 60140,93  |

_Note: those results are for the v2026.0.0 version_