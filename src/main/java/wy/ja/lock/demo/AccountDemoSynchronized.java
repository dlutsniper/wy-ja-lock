package wy.ja.lock.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.var;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AccountDemoSynchronized {
    public static void main(String[] args) throws Exception {
        var mss = new ArrayList<Long>();
        for (int ii = 0; ii < 1000; ii++)
            mss.add(test(10000));
        System.out.println();
        Double average = mss.stream().collect(Collectors.averagingLong(Long::longValue));
        System.out.println("average: " + average + "ms");
    }

    public static long test(int times) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(4);
        CountDownLatch countDownLatch = new CountDownLatch(times);
        long start = System.currentTimeMillis();
        Account account = new Account("xx", 0, countDownLatch);
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
        private CountDownLatch countDownLatch;

        public void add(int amount) {
            synchronized (this) {
                int newVal = this.getBalance() + 1;
                this.setBalance(newVal);
                countDownLatch.countDown();
            }
        }
    }
}
