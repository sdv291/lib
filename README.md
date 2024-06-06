A common library that will make your Java application better

Author: Dmitry Salanzhyi

Simple example:

`

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
    
    Thread.sleep(5000);

    >> output
    INFO: pool-1-thread-1 - [10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
    INFO: pool-1-thread-1 - [20, 21, 22, 23, 24, 25, 26, 27, 28, 29]
    INFO: pool-1-thread-2 - [30, 31, 32, 33, 34, 35, 36, 37, 38, 39]
    INFO: pool-1-thread-1 - [40, 41, 42, 43, 44, 45, 46, 47, 48, 49]
    INFO: pool-1-thread-3 - [60, 61, 62, 63, 64, 65, 66, 67, 68, 69]
    INFO: pool-1-thread-2 - [50, 51, 52, 53, 54, 55, 56, 57, 58, 59]
    INFO: pool-1-thread-3 - [80, 81, 82, 83, 84, 85, 86, 87, 88, 89]
    INFO: pool-1-thread-1 - [70, 71, 72, 73, 74, 75, 76, 77, 78, 79]
    INFO: pool-1-thread-2 - [90, 91, 92, 93, 94, 95, 96, 97, 98, 99]
`
