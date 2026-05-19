## IIDM Network impl benchmark
### Test case: core #1413
https://github.com/powsybl/powsybl-core/issues/1413

### Benchmark configuration
All the benchmark results presented here were obtained on the same hardware and software configuration:

| Component      | Specification               |
|----------------|-----------------------------|
| Hardware model | Dell Inc. Precision 5520    |
| Processor      | Intel(R) Core(TM) i7-6820HQ |
| RAM            | 32 Go                       |
| OS             | Ubuntu 22.04.5 LTS          |


```java
// NetworkImpl
public <C extends Connectable> Stream<C> getConnectableStream(Class<C> clazz) {
    // getAll() + filter(clazz::isInstance) scan all objects in the network
    return index.getAll().stream().filter(clazz::isInstance).map(clazz::cast);
}
public <C extends Connectable> Stream<C> getConnectableStream2(Class<C> clazz) {
    // streamAssignable(clazz) scan first the classes (map keys)
    return index.streamAssignable(clazz);
}
```

```java
// NetworkIndex
// introduce streamAssignable(clazz): scans first the classes (map keys) then filter
<T extends Identifiable> Stream<T> streamAssignable(Class<T> clazz) {
    return objectsByClass.entrySet().stream()
            .filter(e -> clazz.isAssignableFrom(e.getKey()))
            .flatMap(e -> e.getValue().stream())
            .map(clazz::cast);
}

// getAll: scan all objects in the network
Collection<Identifiable<?>> getAll() {
    return objectsById.values();
}
// getAll(clazz): scan all objects (concrete class) that match exactly the clazz, no subtype or interface lookup
<T extends Identifiable> Set<T> getAll(Class<T> clazz) {
    Set<Identifiable<?>> all = objectsByClass.get(clazz);
    if (all == null) {
        return Collections.emptySet();
    }
    return (Set<T>) all;
}
```

### Results
``` shell
java -jar target/benchmark.jar NetworkConnectableBenchmark
```
``` yaml
Benchmark                                                            Mode  Cnt  Score   Error  Units
NetworkConnectableBenchmark.getConnectableCallIndexStreamAssignable  avgt   10  1,133 ± 0,138  us/op
NetworkConnectableBenchmark.getConnectableCallIndexGetAll            avgt   10  5,802 ± 0,314  us/op
```
---
``` shell
java -jar target/benchmark.jar NetworkConnectableBenchmark -prof gc
```
``` yaml
Benchmark                                                                               Mode  Cnt    Score    Error   Units
NetworkConnectableBenchmark.getConnectableCallIndexStreamAssignable                     avgt   10    1,126 ±  0,090   us/op
NetworkConnectableBenchmark.getConnectableCallIndexStreamAssignable:gc.alloc.rate       avgt   10  455,113 ± 35,535  MB/sec
NetworkConnectableBenchmark.getConnectableCallIndexStreamAssignable:gc.alloc.rate.norm  avgt   10  536,001 ±  0,001    B/op
NetworkConnectableBenchmark.getConnectableCallIndexStreamAssignable:gc.count            avgt   10  153,000           counts
NetworkConnectableBenchmark.getConnectableCallIndexStreamAssignable:gc.time             avgt   10  251,000               ms
NetworkConnectableBenchmark.getConnectableCallIndexGetAll                               avgt   10    6,419 ±  0,537   us/op
NetworkConnectableBenchmark.getConnectableCallIndexGetAll:gc.alloc.rate                 avgt   10   47,621 ±  2,670  MB/sec
NetworkConnectableBenchmark.getConnectableCallIndexGetAll:gc.alloc.rate.norm            avgt   10  320,004 ± 12,749    B/op
NetworkConnectableBenchmark.getConnectableCallIndexGetAll:gc.count                      avgt   10   16,000           counts
NetworkConnectableBenchmark.getConnectableCallIndexGetAll:gc.time                       avgt   10   32,000               ms
```

### Notes
- getConnectableCallIndexStreamAssignable : 1,133 us/op
- getConnectableCallIndexGetAll : 5,802 us/op
> `streamAssignable` is about 5 faster than `getAll + filter`, but it allocates more memory per call,
- streamAssignable: 536 B/op
- getAll + filter: 320 B/op