package com.sydh.common.utils;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Threads {
    private static final Logger aK = LoggerFactory.getLogger(Threads.class);


    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException interruptedException) {
            return;
        }
    }


    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        if (pool != null && !pool.isShutdown()) {

            pool.shutdown();

            try {
                if (!pool.awaitTermination(120L, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                    if (!pool.awaitTermination(120L, TimeUnit.SECONDS)) {
                        aK.info("Pool did not terminate");
                    }
                }

            } catch (InterruptedException interruptedException) {

                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }


    public static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future) {

            try {

                Future future = (Future) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException cancellationException) {

                t = cancellationException;
            } catch (ExecutionException executionException) {

                t = executionException.getCause();
            } catch (InterruptedException interruptedException) {

                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            aK.error(t.getMessage(), t);
        }
    }
}
