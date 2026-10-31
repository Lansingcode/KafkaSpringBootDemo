package org.bigdata.kafka.springbootdemo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class MyConsumer {
    @KafkaListener(topics = "test1",groupId = "testGroup")
    public void listenGroup(ConsumerRecord<String ,String> record, Acknowledgment ack){
        String value=record.value();
        System.out.println(value);
        System.out.println(record);
        ack.acknowledge();
    }
}

