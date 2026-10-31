package org.bigdata.kafka.springbootdemo.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageStore {
    private final static int MAX_MESSAGES = 100;
    private final LinkedList<Map<String, Object>> messages = new LinkedList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public void addMessage(String content) {
        Map<String, Object> message = new HashMap<>();
        message.put("id", UUID.randomUUID().toString());
        message.put("content", content);
        message.put("timestamp", LocalDateTime.now().format(formatter));
        message.put("time", System.currentTimeMillis());

        messages.addFirst(message);

        if (messages.size() > MAX_MESSAGES) {
            messages.removeLast();
        }
    }

    public List<Map<String, Object>> getMessages(int limit) {
        return messages.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllMessages() {
        return new ArrayList<>(messages);
    }

    public int getTotalCount() {
        return messages.size();
    }

    public void clearMessages() {
        messages.clear();
    }
}
