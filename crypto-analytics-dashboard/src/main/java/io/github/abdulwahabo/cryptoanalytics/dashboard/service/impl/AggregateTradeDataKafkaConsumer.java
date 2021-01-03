package io.github.abdulwahabo.cryptoanalytics.dashboard.service.impl;

import io.github.abdulwahabo.cryptoanalytics.common.model.AggregateTradeData;
import io.github.abdulwahabo.cryptoanalytics.dashboard.service.KafkaConsumerService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AggregateTradeDataKafkaConsumer implements KafkaConsumerService<AggregateTradeData> {

    private final KafkaConsumer<String, AggregateTradeData> consumer;

    @Value("${topic}")
    private String topic;

    @Autowired
    public AggregateTradeDataKafkaConsumer(KafkaConsumer<String, AggregateTradeData> consumer) {
        this.consumer = consumer;
    }

    @Override
    public List<AggregateTradeData> poll() {
        List<AggregateTradeData> result = new ArrayList<>();
        consumer.poll(Duration.ofMillis(150)).records(topic).forEach(record -> result.add(record.value()));
        return result;
    }

    @Override
    public void commitOffsets() {
        consumer.commitSync();
    }
}
