package org.bigdata.kafka.springbootdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class KafkaSpringBootDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaSpringBootDemoApplication.class, args);
    }

}
