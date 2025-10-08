package com.ldw.microservice.util;

import com.google.common.util.concurrent.RateLimiter;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * 批量限流执行器
 * 支持：批量任务限速、重试、异步回调
 */
public class RateLimiterExecutor {

    private final RateLimiter rateLimiter;
    private final ExecutorService executorService;
    private final int maxRetries;
    private final long retryDelayMillis;

    /**
     * @param permitsPerSecond 每秒允许执行的任务数
     * @param threadPoolSize   执行线程数
     * @param maxRetries       失败重试次数
     * @param retryDelayMillis 重试间隔时间（毫秒）
     */
    public RateLimiterExecutor(double permitsPerSecond, int threadPoolSize, int maxRetries, long retryDelayMillis) {
        this.rateLimiter = RateLimiter.create(permitsPerSecond);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    /**
     * 批量提交任务
     *
     * @param tasks    任务列表
     * @param batchSize 每批执行任务数
     * @param onSuccess 成功回调
     * @param onError   失败回调
     */
    public <T> void submitBatch(List<Callable<T>> tasks, int batchSize,
                                Consumer<T> onSuccess, Consumer<Exception> onError) {

        int total = tasks.size();
        int start = 0;

        while (start < total) {
            int end = Math.min(start + batchSize, total);
            List<Callable<T>> subList = tasks.subList(start, end);

            for (Callable<T> task : subList) {
                rateLimiter.acquire(); // 限流控制
                executorService.submit(() -> executeWithRetry(task, onSuccess, onError));
            }

            start += batchSize;

            // 批次之间留一定间隔，避免瞬间突发
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private <T> void executeWithRetry(Callable<T> task, Consumer<T> onSuccess, Consumer<Exception> onError) {
        int attempt = 0;
        while (attempt <= maxRetries) {
            try {
                T result = task.call();
                if (onSuccess != null) onSuccess.accept(result);
                return;
            } catch (Exception e) {
                attempt++;
                if (attempt > maxRetries) {
                    if (onError != null) onError.accept(e);
                    return;
                }
                try {
                    Thread.sleep(retryDelayMillis);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
