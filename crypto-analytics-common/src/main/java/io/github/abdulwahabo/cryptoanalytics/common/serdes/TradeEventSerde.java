package io.github.abdulwahabo.cryptoanalytics.common.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.abdulwahabo.cryptoanalytics.common.model.TradeEvent;
import io.github.abdulwahabo.cryptoanalytics.common.exception.SerdeException;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

// TODO: Consider unit testing for this class.

public class TradeEventSerde implements Serializer<TradeEvent>, Deserializer<TradeEvent> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public TradeEvent deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return mapper.readValue(new String(data), TradeEvent.class);
        } catch (JsonProcessingException e) {
            throw new SerdeException("Failed to deserialize", e);
        }
    }

    @Override
    public byte[] serialize(String topic, TradeEvent data) {
        if (data == null) {
            return null;
        }
        try {
            return mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new SerdeException("Failed to serialize", e);
        }
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) { }

    @Override
    public void close() { }
}
