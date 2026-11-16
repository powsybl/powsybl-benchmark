| Layout       | Compressed | newChunkSize (k) | size = 1000  | size = 10000 | size = 100000 |
|--------------|------------|------------------|--------------|--------------|---------------|
| SINGLE_CHUNK | false      | 100              | 1.000 ms/op  | 2.000 ms/op  | 3.000 ms/op   |
| SINGLE_CHUNK | false      | 1000             | 4.000 ms/op  | 5.000 ms/op  | 6.000 ms/op   |
| SINGLE_CHUNK | true       | 100              | 7.000 ms/op  | 8.000 ms/op  | 9.000 ms/op   |
| SINGLE_CHUNK | true       | 1000             | 10.000 ms/op | 11.000 ms/op | 12.000 ms/op  |
| FRAGMENTED   | false      | 100              | 13.000 ms/op | 14.000 ms/op | 15.000 ms/op  |
| FRAGMENTED   | false      | 1000             | 16.000 ms/op | 17.000 ms/op | 18.000 ms/op  |
| FRAGMENTED   | true       | 100              | 19.000 ms/op | 20.000 ms/op | 21.000 ms/op  |
| FRAGMENTED   | true       | 1000             | 22.000 ms/op | 23.000 ms/op | 24.000 ms/op  |
