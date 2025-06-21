package com.ldw.microservice.controller;

import com.google.common.base.Stopwatch;
import com.ldw.common.vo.Result;
import com.ldw.microservice.feign.OrderFeign;
import com.ldw.microservice.feign.UserServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class UCController {
    @Autowired
    UserServiceFeign userServiceFeign;

    @Autowired
    OrderFeign orderFeign;

    @GetMapping("/getUserById")
    public Result getUserById(Long deptId) {
        String username = userServiceFeign.getUserById();
        return Result.success(username);
    }


    @GetMapping("/getOrder")
    public Result getOrder(Long deptId) {
        String username = orderFeign.getOrder();
        return Result.success(username);
    }

    @GetMapping("/getOrderRank")
    public Result getOrderRank(Long deptId) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        forkJoinPool.execute(() -> {
            userServiceFeign.getUserById();
            try {
                Thread.sleep(5000l);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        stopwatch.elapsed(TimeUnit.SECONDS);
        log.info("statsCoRanking_end {}", stopwatch);
        return Result.success("");
    }


 /*   @GetMapping("/getOrder")
    public Result getOrderRank(Long deptId) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("statsCoRanking_start customBeginDate{}", beginDateStr);
        Date nowTime = new Date();
        ForkJoinPool forkJoinPool = ForkJoinUtils.randomGetPool();
        forkJoinPool.execute(() -> {
            try {
                Date beginDateTime = DateUtils.getLastHourStart(new Date());
                Date lastItemDate = coTradeRankingService.findLastDate();
                log.info("lastItemDate = {}", DateUtils.format(lastItemDate));
                if (lastItemDate != null) {
                    if (lastItemDate.after(nowTime)) {
                        log.info("statsCoRanking has been done, lastItemDate.getTime() >= nowTime.getTime()");
                        return;
                    }
                }
                //Delete abnormal data (delete data on the day the program is running) 删除异常数据(删除程序运行当天的数据)
                coTradeRankingService.deleteAllData(beginDateTime);
                if (StringUtils.isNotBlank(beginDateStr)) {
                    Date beginDate = DateUtils.parseDate(beginDateStr);
                    beginDateTime = Objects.isNull(beginDate)? nowTime : DateUtils.getBeforeDayByParam(beginDate, 0);
                }
                //由于合约每5分钟统计交易数据，统计59分前交易落库时更新时间超过0分0秒，所以结束时间加59秒查询
                beginDateTime=DateUtils.addMinute(beginDateTime, 1);
                Date lastHourEnd = DateUtils.addSecond(DateUtils.getHourBeginTime(nowTime),59);
                log.info("statsCoRanking start getCoData, beginDateTime:{}, lastHourEnd:{}", beginDateTime, lastHourEnd);
                coTradeRankingService.getCoDataNew(beginDateTime,lastHourEnd);
                log.info("statsCoRanking success");
            } catch (Exception e) {
                log.error("statsCoRanking error ", e);
            }
        });
        stopwatch.elapsed(TimeUnit.SECONDS);
        log.info("statsCoRanking_end {}", stopwatch);
    }*/

}
