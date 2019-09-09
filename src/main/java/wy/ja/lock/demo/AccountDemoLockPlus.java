package wy.ja.lock.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.var;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class AccountDemoLockPlus {
    public static void main(String[] args) throws Exception {
        var mss = new ArrayList<Long>();
        for (int ii = 0; ii < 10; ii++)
            mss.add(test(10000));
        Double average = mss.stream().collect(Collectors.averagingLong(Long::longValue));
        System.out.println("average: " + average + "ms"); // 约 64.84ms 68.19ms 67.52ms 67.8ms 63.62ms
    }

    public static long test(int times) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(4);
        long start = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(times);
        Lock lock = new ReentrantLock();
        Account account = new Account("xx", 0, lock, countDownLatch);
        for (int index = 0; index < times; index++)
            es.submit(() -> account.add(1));
        countDownLatch.await();
        long ms = System.currentTimeMillis() - start;
        System.out.println("balance: " + account.getBalance());
        es.shutdown();
        return ms;
    }

    @AllArgsConstructor
    @Data
    static class Account {
        private String name;
        private int balance;
        private Lock lock;
        private CountDownLatch countDownLatch;

        public void add(int amount) {
            try (MyLock myLock = new MyLock(lock)) {
                lock.lock();
                int newVal = this.getBalance() + 1;
                this.setBalance(newVal);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
            // lock.unlock();
                countDownLatch.countDown();
            }
        }
    }

    @AllArgsConstructor
    static class MyLock implements Closeable {
        private Lock lock;

        @Override
        public void close() throws IOException {
            this.lock.unlock();
        }
    }
}
