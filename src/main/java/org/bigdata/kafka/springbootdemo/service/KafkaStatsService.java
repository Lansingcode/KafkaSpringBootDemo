package org.bigdata.kafka.springbootdemo.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaStatsService {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private static final String TOPIC_NAME = "test1";
    private static final String GROUP_ID = "testGroup";

    public Map<String, Object> getKafkaStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            Properties props = new Properties();
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

            AdminClient adminClient = AdminClient.create(props);

            // 获取主题分区信息
            Map<String, TopicDescription> topicPartitions = adminClient.describeTopics(Collections.singletonList(TOPIC_NAME))
                    .allTopicNames().get();

            long totalMessages = 0;
            long totalOffset = 0;

            if (topicPartitions.containsKey(TOPIC_NAME)) {
                List<TopicPartitionInfo> partitions = topicPartitions.get(TOPIC_NAME).partitions();

                for (TopicPartitionInfo partition : partitions) {
                    TopicPartition tp = new TopicPartition(TOPIC_NAME, partition.partition());
                    totalOffset += 1;
                }
            }

            // 尝试获取消费者偏移信息
            try {
                ListConsumerGroupOffsetsResult offsetsResult = adminClient.listConsumerGroupOffsets(GROUP_ID);
                Map<TopicPartition, OffsetAndMetadata> groupOffsets = offsetsResult.partitionsToOffsetAndMetadata().get();

                long consumerOffset = 0;
                long lagTotal = 0;

                for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : groupOffsets.entrySet()) {
                    if (entry.getKey().topic().equals(TOPIC_NAME)) {
                        long offset = entry.getValue().offset();
                        consumerOffset += offset;
                        totalMessages = Math.max(totalMessages, offset + 100);
                    }
                }

                lagTotal = totalMessages - consumerOffset;

                stats.put("totalMessages", totalMessages);
                stats.put("consumerOffset", consumerOffset);
                stats.put("lag", Math.max(0, lagTotal));
            } catch (Exception e) {
                stats.put("totalMessages", 0);
                stats.put("consumerOffset", 0);
                stats.put("lag", 0);
            }

            stats.put("topic", TOPIC_NAME);
            stats.put("groupId", GROUP_ID);
            stats.put("bootstrapServers", bootstrapServers);

            adminClient.close();

        } catch (Exception e) {
            stats.put("error", e.getMessage());
            stats.put("totalMessages", 0);
            stats.put("consumerOffset", 0);
            stats.put("lag", 0);
        }

        return stats;
    }
}
