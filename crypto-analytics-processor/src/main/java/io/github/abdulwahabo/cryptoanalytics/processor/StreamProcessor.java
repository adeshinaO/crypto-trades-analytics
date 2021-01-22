package io.github.abdulwahabo.cryptoanalytics.processor;

import io.github.abdulwahabo.cryptoanalytics.common.CommonPropertiesProvider;
import io.github.abdulwahabo.cryptoanalytics.common.exception.PropertiesFileException;
import io.github.abdulwahabo.cryptoanalytics.common.model.AggregateTradeData;
import io.github.abdulwahabo.cryptoanalytics.common.model.TradeEvent;
import io.github.abdulwahabo.cryptoanalytics.common.serdes.AggregateTradeDataSerde;
import io.github.abdulwahabo.cryptoanalytics.common.serdes.TradeEventSerde;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamProcessor.class);
    private static final Serde<TradeEvent> TRADE_DATA_SERDE = Serdes.serdeFrom(new TradeEventSerde(), new TradeEventSerde());
    private static final Serde<AggregateTradeData> TRADE_AGGREGATE_SERDE = Serdes.serdeFrom(new AggregateTradeDataSerde(), new AggregateTradeDataSerde());
    private static final CommonPropertiesProvider properties = new CommonPropertiesProvider();

    public static void main( String[] args ) throws PropertiesFileException {
        StreamsBuilder builder = new StreamsBuilder();
        Consumed<String, TradeEvent> consumedWith = Consumed.with(Serdes.String(), TRADE_DATA_SERDE);
        properties.loadProperties();
        KStream<String, TradeEvent> source = builder.stream(properties.inputTopic(), consumedWith);
        KGroupedStream<String, TradeEvent> groupedStream = source.groupByKey();

        KTable<String, AggregateTradeData> aggregateData = groupedStream.aggregate(AggregateTradeData::new, AGGREGATOR, Materialized.with(Serdes.String(), TRADE_AGGREGATE_SERDE));
        aggregateData.toStream().to(properties.outputTopic(), Produced.with(Serdes.String(), TRADE_AGGREGATE_SERDE));
        Topology topology = builder.build();

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, properties.kafkaProcessorId());
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, properties.kafkaServers());

        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.setUncaughtExceptionHandler((thread, throwable) -> LOGGER.error(throwable.getMessage()));
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static final Aggregator<String, TradeEvent, AggregateTradeData> AGGREGATOR = ((key, tradeEvent, aggregate) -> {
        if (aggregate.getTime() == null) {
            aggregate.setTime(key);
        }
        if (tradeEvent.getSide().equals("buy")) {
            double bought = aggregate.getAggregateBuys();
            aggregate.setAggregateBuys(bought + Double.parseDouble(tradeEvent.getQty()));
        } else if (tradeEvent.getSide().equals("sell")) {
            double sold = aggregate.getAggregateBuys();
            aggregate.setAggregateBuys(sold + Double.parseDouble(tradeEvent.getQty()));
        }
        return aggregate;
    });
}
