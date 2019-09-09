## wy.ja.lock.demo
```
AccountDemoSynchronized
1.Runnable + synchronized + stream + Future -> 偶尔NG
  Runnable + Future @ stream，并发不靠谱
  Runnable改Callable，不使用stream使用for，结果正确
2.java.lang.OutOfMemoryError: unable to create new native thread
  newFixedThreadPool(N) N超限
3.Callable + synchronized + for + Future -> OK
  测试耗时
  17.24ms 14.9ms 11.99ms 11.99ms 12.2ms
5.Future.get()改为countDownLatch的countDown()和await()
  测试耗时
  11.3ms 11.37ms 9.46ms 12.22ms 11.24ms

AccountDemoLock
1.tryLock no if -> NG
2.unlock at catch -> NG
3.newCachedThreadPool - java.lang.OutOfMemoryError: unable to create new native thread
4.lock & unlock -> OK
  测试耗时
  17.92ms 13.16ms 10.59ms 14.33ms 14.14ms
  18.42ms 12.98ms 13.3ms 11.48ms 17.08ms
  15.52ms 9.37ms 13.4ms 16.82ms 14.6ms

AccountDemoCas
1.AtomicInteger compareAndSet
  测试耗时
  16.96ms 21.34ms 11.96ms 15.74ms 9.74ms
  12.81ms 12.55ms 11.54ms 11.54ms 10.98ms

AccountDemoFaa
1.AtomicInteger getAndAdd -> OK
  测试耗时
  12.59ms 16.07ms 10.96ms 14.13ms 11.47ms
  11.61ms 13.32ms 11.1ms 11.42ms 11.17ms
```

## 耗时
```
1000次试验均值
AccountDemoSynchronized
  6.271ms 7.559ms 4.675ms
AccountDemoLock
  7.004ms 5.541ms 5.624ms
AccountDemoCas
  7.104ms 5.211ms 5.772ms
AccountDemoFaa
  7.641ms 4.924ms 4.727ms

10次试验均值
AccountDemoSynchronized
  36.6ms 38.7ms 28.1ms
AccountDemoLock
  48.4ms 35.8ms 40.2ms
AccountDemoCas
  35.0ms 36.9ms 32.4ms
AccountDemoFaa
  26.2ms 36.4ms 31.3ms

JIT吗？执行越多速度越快？
关闭JIT -Xint / -Djava.compiler=NONE
AccountDemoSynchronized 100次关闭前后
  16.66ms 13.77ms 11.26ms
  93.14ms 102.12ms 81.13ms
AccountDemoCas 100次关闭前后
  12.74ms 10.5ms 12.42ms
  82.48ms 74.7ms 77.09ms
```

## FAA by CAS
```
AtomicInteger
  int getAndAdd(int delta)
    return unsafe.getAndAddInt(this, valueOffset, delta)
Unsafe
  int getAndAddInt(Object var1, long var2, int var4)
    int var5;
    do {
      var5 = this.getIntVolatile(var1, var2);
    } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
    return var5;
```


