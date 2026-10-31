package org.bigdata.kafka.springbootdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg")
public class MyKafkaController {
    private final static String TOPIC_NAME = "test1";
    private final static String DEFAULT_MESSAGE = "this is a message!";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping("/send")
    public String sendMessage(@RequestParam(value = "message", required = false) String message) {
        String messageContent = message != null && !message.trim().isEmpty() ? message : DEFAULT_MESSAGE;
        kafkaTemplate.send(TOPIC_NAME, 0, "key", messageContent);
        return "send success!";
    }
}
