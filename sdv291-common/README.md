# A library "sdv291-common" will make your Java application better!

Author: [Dmitry Salanzhyi](https://sdv291.com)

## Process

```
Logger logger = Logger.getLogger("root");

ProcessConfig config = ProcessConfig.newBuilder()
  .setOverloadThreshold(2)
  .setMaxWorkerCount(3)
  .setMaxPackSize(10)
  .build();
Process<Integer> smartProcess = Processes.newProcess(
  config,
  (items) -> logger.info(Thread.currentThread().getName() + " - " + items.toString())
);
for (int i = 10; i < 100; i++) {
  smartProcess.offer(i);
}

Thread.sleep(3000);

>> output result
[pool-1-thread-1] INFO: [10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
[pool-1-thread-1] INFO: [20, 21, 22, 23, 24, 25, 26, 27, 28, 29]
[pool-1-thread-2] INFO: [30, 31, 32, 33, 34, 35, 36, 37, 38, 39]
[pool-1-thread-1] INFO: [40, 41, 42, 43, 44, 45, 46, 47, 48, 49]
[pool-1-thread-3] INFO: [60, 61, 62, 63, 64, 65, 66, 67, 68, 69]
[pool-1-thread-2] INFO: [50, 51, 52, 53, 54, 55, 56, 57, 58, 59]
[pool-1-thread-3] INFO: [80, 81, 82, 83, 84, 85, 86, 87, 88, 89]
[pool-1-thread-1] INFO: [70, 71, 72, 73, 74, 75, 76, 77, 78, 79]
[pool-1-thread-2] INFO: [90, 91, 92, 93, 94, 95, 96, 97, 98, 99]
```

As you can see, this process automatically scales up to 3 worker processes when overload occurs.

* `OverloadThreshold` to know when a process is overloaded and it's time to scale
* `MaxWorkerCount` to set the limit for processing this process
* `MaxPackSize` for batch processing

## PoolProvider

```
PoolProvider<SimpleDateFormat> provider = new PoolProvider<>(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
try (Provider<SimpleDateFormat> providerEntry = provider.get()) {
  SimpleDateFormat sdf = providerEntry.getEntry();
  System.out.println(sdf.format(new Timestamp(System.currentTimeMillis())));
}
```

As you know, `SimpleDateFormat` is **not thread-safe** and has a huge structure.\
Therefore, to ease the work with the JVM RAM, your app should pool the created instances.\
This class allows you to do this safely, simply and clearly.\
The same case can be applied to `HttpClient` to reuse connections in your app.

## CommonException

In my opinion, all exceptions should be at runtime. It helps to make your code simpler/cleaner.

```
throw CommonException.build("Something went wrong");
```

## FunnelExecutor

Do you need to limit concurrent interaction, then this is what are you needed.

```
FunnelExecutor funnelHttp = new FunnelExecutor(2);

ExecutorService executorService = Executors.newFixedThreadPool(10);
for (int i = 0; i < 10; i++) {
  executorService.submit(() -> HttpResponse response = funnelHttp.execute(() -> return new HttpResonse() // make http call));
}
```

This is concurrent thread safe code for limit HTTP calls to 2 at time.

## FunnelTime

Do you need to limit count of calls in a period of time, then this is what are you needed.

```
FunnelTime funnelTime = new FunnelTime(10_000, 60);
for (int i = 0; i < 20_000; i++) {
  funnelTime.execute(Object::new);
}
```

This is concurrent thread safe code for limit count of calls to 10k in 1min.

## FunnelTraffic

Do you need to limit traffic in a period of time, then this is what are you needed.

```
FunnelTraffic funnelTraffic = new FunnelTraffic(10 * 1024 * 1024, 60);
for (int i = 0; i < 20; i++) {
  funnelTraffic.execute(Object::new, 1 * 1024 * 1024);
}
```

This is concurrent thread safe code for limit traffic to 10MB in 1min.

## Measure

Do you need to measure interaction time, then this is what are you needed.

```
Measure measure = new Measure("test");
for (int i = 0; i < 3; i++) {
  measure.addInteractionTime(TimeUnit.SECONDS.toMillis(1));
}
measure.getHeaders(); // Name      Total time(ms)      Average time(ms)    Exec count
measure.getMeasure(); // test      3000                1000                3
```
