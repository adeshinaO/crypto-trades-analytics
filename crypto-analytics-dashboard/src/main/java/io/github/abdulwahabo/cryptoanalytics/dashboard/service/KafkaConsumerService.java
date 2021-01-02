package io.github.abdulwahabo.cryptoanalytics.dashboard.service;

import io.github.abdulwahabo.cryptoanalytics.common.model.AggregateTradeData;
import java.util.List;

public interface KafkaConsumerService {
    List<AggregateTradeData> poll();
    void commitOffsets();
}
