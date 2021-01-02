package io.github.abdulwahabo.cryptoanalytics.dashboard.service.impl;

import io.github.abdulwahabo.cryptoanalytics.common.model.AggregateTradeData;
import io.github.abdulwahabo.cryptoanalytics.dashboard.model.DashboardDataset;
import io.github.abdulwahabo.cryptoanalytics.dashboard.service.DashboardService;
import io.github.abdulwahabo.cryptoanalytics.dashboard.service.KafkaConsumerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private KafkaConsumerService kafkaConsumerService;

    @Autowired
    public DashboardServiceImpl(KafkaConsumerService kafkaConsumerService) {
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @Override
    public Optional<DashboardDataset> buildDataset() {
        List<AggregateTradeData> tradeDataList = kafkaConsumerService.poll();
        if (tradeDataList.isEmpty()) {
            return Optional.empty();
        }
        List<DashboardDataset.Data> sales = new ArrayList<>();
        List<DashboardDataset.Data> buys = new ArrayList<>();

        tradeDataList.forEach(tradeData -> {
            DashboardDataset.Data buy = new DashboardDataset.Data();
            buy.setTime(tradeData.getTime());
            buy.setValue(tradeData.getAggregateBuys());
            buys.add(buy);

            DashboardDataset.Data sell = new DashboardDataset.Data();
            sell.setTime(tradeData.getTime());
            sell.setValue(tradeData.getAggregateSales());
            sales.add(sell);
        });

        DashboardDataset dataset = new DashboardDataset();
        dataset.setBuy(buys);
        dataset.setSell(sales);

        kafkaConsumerService.commitOffsets();
        return Optional.of(dataset);
    }
}
