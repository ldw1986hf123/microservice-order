package com.ldw.microservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class TestLimiter {

    public static void main(String[] args) {
        // 每秒最多 10 个任务，线程池 5 个线程，失败重试 2 次，重试间隔 500ms
        RateLimiterExecutor limiter = new RateLimiterExecutor(10, 5, 2, 500);

        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            int index = i;
            tasks.add(() -> simulateRequest(index));
        }

        limiter.submitBatch(
                tasks,
                10, // 每批执行 10 个任务
                result -> System.out.println("✅ 成功：" + result),
                error -> System.err.println("❌ 失败：" + error.getMessage())
        );

        limiter.shutdown();
    }

    private static String simulateRequest(int i) throws Exception {
        if (i % 13 == 0) throw new RuntimeException("接口异常: " + i);
        Thread.sleep(200);
        return "请求 " + i + " 成功 at " + System.currentTimeMillis();
    }
}
