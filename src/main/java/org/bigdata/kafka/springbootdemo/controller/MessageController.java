package org.bigdata.kafka.springbootdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.bigdata.kafka.springbootdemo.service.MessageStore;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MessageController {
    @Autowired
    private MessageStore messageStore;

    @GetMapping("/messages")
    public Map<String, Object> getMessages(@RequestParam(value = "limit", defaultValue = "20") int limit) {
        Map<String, Object> result = new HashMap<>();
        result.put("messages", messageStore.getMessages(limit));
        result.put("total", messageStore.getTotalCount());
        result.put("limit", limit);
        return result;
    }

    @GetMapping("/messages/all")
    public Map<String, Object> getAllMessages() {
        Map<String, Object> result = new HashMap<>();
        result.put("messages", messageStore.getAllMessages());
        result.put("total", messageStore.getTotalCount());
        return result;
    }

    @GetMapping("/messages/clear")
    public Map<String, Object> clearMessages() {
        messageStore.clearMessages();
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Messages cleared");
        result.put("total", 0);
        return result;
    }
}
