package com.ldw.microservice.controller;

import com.ldw.microservice.service.TjzbRptSumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/tjzb/rptsum")
@Tag(name = "指标汇总接口", description = "指标汇总接口")
public class TjzbRptSumController {


    @Autowired
    public TjzbRptSumService tjzbRptSumService;



    @Operation(summary = "指标数据 初始化数据 ", description = "根据传入参数,拉取所有部门的数据, ")
    @GetMapping("/initSave")
    public void initSave(@RequestParam @Parameter(description = "需要生成的年的数量，例如 5 表示生成最近 5 年的数据") int totalYear) {
        tjzbRptSumService.initZBSumData(totalYear);
    }

}
