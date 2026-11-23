| Layout       | Compressed | newChunkSize (k) | size = 1000          | size = 10000        | size = 100000       |
|--------------|------------|------------------|----------------------|---------------------|---------------------|
| SINGLE_CHUNK | false      | 100              | 1.000 ms/op (+100%)  | 2.000 ms/op (-13%)  | 3.000 ms/op (0%)    |
| SINGLE_CHUNK | false      | 1000             | 4.000 ms/op (-20%)   | 5.000 ms/op (-17%)  | 6.000 ms/op (-14%)  |
| SINGLE_CHUNK | true       | 100              | 7.000 ms/op (0%)     | 8.000 ms/op (0%)    | 9.000 ms/op (0%)    |
| SINGLE_CHUNK | true       | 1000             | 10.000 ms/op (0%)    | 11.000 ms/op (0%)   | 12.000 ms/op (0%)   |
| FRAGMENTED   | false      | 100              | 13.000 ms/op (+550%) | 14.000 ms/op (+75%) | 15.000 ms/op (+67%) |
| FRAGMENTED   | false      | 1000             | 16.000 ms/op (+60%)  | 17.000 ms/op (+13%) | 18.000 ms/op (+80%) |
| FRAGMENTED   | true       | 100              | 19.000 ms/op (0%)    | 20.000 ms/op (0%)   | 21.000 ms/op (0%)   |
| FRAGMENTED   | true       | 1000             | 22.000 ms/op (0%)    | 23.000 ms/op (0%)   | 24.000 ms/op (+20%) |
