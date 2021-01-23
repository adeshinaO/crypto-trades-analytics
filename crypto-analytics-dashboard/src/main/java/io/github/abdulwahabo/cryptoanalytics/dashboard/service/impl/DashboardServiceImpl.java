package io.github.abdulwahabo.cryptoanalytics.dashboard.service.impl;

import io.github.abdulwahabo.cryptoanalytics.common.model.AggregateTradeData;
import io.github.abdulwahabo.cryptoanalytics.dashboard.model.DashboardDataset;
import io.github.abdulwahabo.cryptoanalytics.dashboard.service.DashboardService;
import io.github.abdulwahabo.cryptoanalytics.dashboard.service.KafkaConsumerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private KafkaConsumerService kafkaConsumerService;

    // Crude datastore.
    private Map<String, DashboardDataset.Data> buyDataCache = new HashMap<>(60);
    private Map<String, DashboardDataset.Data> sellDataCache = new HashMap<>(60);

    @Autowired
    public DashboardServiceImpl(KafkaConsumerService kafkaConsumerService) {
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @Override
    public Optional<DashboardDataset> buildDataset() {
        List<AggregateTradeData> tradeDataList = kafkaConsumerService.poll();

        if (tradeDataList.isEmpty()) {
            if (!buyDataCache.isEmpty() && !sellDataCache.isEmpty()) {
                return Optional.of(returnCachedDataset());
            } else {
                return Optional.empty();
            }
        } else {
            tradeDataList.forEach(tradeData -> {
                DashboardDataset.Data buy = new DashboardDataset.Data();
                String timeBuy = tradeData.getTime();
                buy.setTime(timeBuy);
                buy.setValue(tradeData.getAggregateBuys());
                buyDataCache.put(timeBuy, buy);

                DashboardDataset.Data sell = new DashboardDataset.Data();
                String timeSell = tradeData.getTime();
                sell.setTime(timeSell);
                sell.setValue(tradeData.getAggregateSales());
                sellDataCache.put(timeSell, sell);
            });
            kafkaConsumerService.commitOffsets();
            return Optional.of(returnCachedDataset());
        }
    }

    private DashboardDataset returnCachedDataset() {
        DashboardDataset dataset = new DashboardDataset();
        dataset.setBuy(new ArrayList<>(buyDataCache.values()));
        dataset.setSell(new ArrayList<>(sellDataCache.values()));
        return dataset;
    }
}
