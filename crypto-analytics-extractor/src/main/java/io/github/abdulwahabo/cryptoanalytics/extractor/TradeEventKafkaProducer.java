package io.github.abdulwahabo.cryptoanalytics.extractor;

import io.github.abdulwahabo.cryptoanalytics.common.model.TradeEvent;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradeEventKafkaProducer implements KafkaProducerService<TradeEvent> {

    private Logger logger = LoggerFactory.getLogger(TradeEventKafkaProducer.class);
    private final KafkaProducer<String, TradeEvent> kafkaProducer;
    private final String topic;

    public TradeEventKafkaProducer(KafkaProducer<String, TradeEvent> kafkaProducer, String topic) {
        this.kafkaProducer = kafkaProducer;
        this.topic = topic;
    }

    @Override
    public void send(String key, TradeEvent data) {
        ProducerRecord<String, TradeEvent> record = new ProducerRecord<>(topic, key, data);
        kafkaProducer.send(record, ((metadata, exception) -> {
            if (exception != null) {
                logger.error("Failed to write record", exception);
            } else {
                logger.info("one record written to partition: " + metadata.partition() + " of topic: " + metadata.topic());
            }
        }));
    }

    @Override
    public void close() {
        kafkaProducer.close();
    }
}
