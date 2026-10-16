| Layout       | newChunkSize (k) | size = 1000  | size = 10000 | size = 100000 |
|--------------|------------------|--------------|--------------|---------------|
| SINGLE_CHUNK | 100              | 1.000 ms/op  | 2.000 ms/op  | 3.000 ms/op   |
| SINGLE_CHUNK | 1000             | 4.000 ms/op  | 5.000 ms/op  | 6.000 ms/op   |
| FRAGMENTED   | 100              | 7.000 ms/op  | 8.000 ms/op  | 9.000 ms/op   |
| FRAGMENTED   | 1000             | 10.000 ms/op | 11.000 ms/op | 12.000 ms/op  |
