package com.ldw.microservice.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.ldw.microservice.service.TjzbRptSumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TjzbRptSumServiceImpl implements TjzbRptSumService {
    String host = "http://localhost:8001/";


    private String getDateFromRemote() {
        String result = "";
        String path = "getUserById";
        log.info("开始请求外部接口: {}", host + path);
        try {
            result = HttpRequest.post(host + path)
                    .timeout(10000)   // 可选：10 秒超时
                    .execute()
                    .body();
            log.info("请求成功，返回 resul {}", JSONUtil.toJsonStr(result));
        } catch (Exception e) {
            log.info("请求失败, result {}", result, e);
        }
        return result;
    }


    private List<String> getSumData() {
        List<String> dataDatilList = new ArrayList<>();

        // 2. 定义线程池（核心线程5，最大10，队列200）
        ExecutorService executor = new ThreadPoolExecutor(
                5,
                10,
                60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        List<Future<String>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < 50; i++) {
                Future<String> future = executor.submit(() -> {
                    // 调远程接口，返回 JSON
                    return getDateFromRemote();
                });
                futures.add(future);
            }
            // 4. 等待任务完成并解析结果
            for (Future<String> future : futures) {
                try {
                    String json = future.get(); // 阻塞等待返回
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt(); // 恢复中断状态
                }
            }
        } finally {
            // 5. 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        return dataDatilList;
    }


    /**
     * 只会调用一次，把 办案汇总数据拉去到数据库
     */
    @Override
    public void initZBSumData(int totalYear) {

        List<DateTime> monthList = getAllMonth(totalYear);
        for (DateTime month : monthList) {
            int fromYear = month.year();
            int fromMonth = month.month() + 1;
            Future<?> deptFuture = ThreadUtil.execAsync(() -> {
                List<String> reportDataList = getSumData();
                saveDeptData(reportDataList);
            });
        }

    }

    private List<DateTime> getAllMonth(int totalYear) {
        // 当前日期
        DateTime end = DateUtil.date();

        // 开始日期：pastYears年前的1月1日
        DateTime start = DateUtil.beginOfYear(DateUtil.offset(end, DateField.YEAR, -totalYear));
        // 按月生成日期范围
        DateRange range = DateUtil.range(start, end, DateField.MONTH);
        // 收集到 List
        List<DateTime> monthList = new ArrayList<>();
        range.forEach(monthList::add);

        // 打印每月第
        monthList.forEach(date -> System.out.println(DateUtil.format(date, "yyyy-MM")));
        return monthList;
    }

    private void saveDeptData(List<String> reportDataList) {
//        saveBatch(tjzbRptSumEntityList);
        log.info("reportDataList {}", JSONUtil.toJsonStr(reportDataList));
    }

}
