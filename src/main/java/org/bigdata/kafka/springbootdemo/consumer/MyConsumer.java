package org.bigdata.kafka.springbootdemo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.bigdata.kafka.springbootdemo.service.MessageStore;

@Component
public class MyConsumer {
    @Autowired
    private MessageStore messageStore;

    @KafkaListener(topics = "test1", groupId = "testGroup")
    public void listenGroup(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String value = record.value();
        System.out.println("Consumed message: " + value);

        // Store the message
        messageStore.addMessage(value);

        // Acknowledge the message
        ack.acknowledge();
    }
}

