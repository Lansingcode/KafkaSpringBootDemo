package org.bigdata.kafka.springbootdemo.controller;

import org.bigdata.kafka.springbootdemo.service.KafkaStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatsController {

    @Autowired
    private KafkaStatsService kafkaStatsService;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return kafkaStatsService.getKafkaStats();
    }
}
