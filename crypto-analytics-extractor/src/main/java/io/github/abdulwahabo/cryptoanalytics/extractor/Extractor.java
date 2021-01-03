package io.github.abdulwahabo.cryptoanalytics.extractor;

import io.github.abdulwahabo.cryptoanalytics.common.CommonPropertiesHelper;
import io.github.abdulwahabo.cryptoanalytics.common.exception.PropertiesFileException;
import io.github.abdulwahabo.cryptoanalytics.common.model.TradeEvent;
import io.github.abdulwahabo.cryptoanalytics.common.serdes.TradeEventSerde;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Extractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Extractor.class);

    public static void main(String[] args) {
        try {
            start().await();
        } catch (PropertiesFileException | InterruptedException e) {
            LOGGER.error("Extractor has failed.", e);
        }
    }

    private static CountDownLatch start() throws PropertiesFileException {
        CountDownLatch latch = new CountDownLatch(1);
        BlockchainApiWebSocketListener listener = new BlockchainApiWebSocketListener(latch, kafkaProducer());
        HttpClient client = HttpClient.newHttpClient();
        WebSocket webSocket = client.newWebSocketBuilder()
                                    .header("Origin", "https://exchange.blockchain.com")
                                    .connectTimeout(Duration.ofMillis(10000))
                                    .buildAsync(URI.create("wss://ws.prod.blockchain.info/mercury-gateway/v1/ws"), listener)
                                    .join();

        webSocket.sendText(subscribeMsg(), true);
        return latch;
    }

    private static TradeEventKafkaProducer kafkaProducer() throws PropertiesFileException {
        CommonPropertiesHelper propertiesHelper = new CommonPropertiesHelper();
        propertiesHelper.loadProperties();
        Map<String, Object> kafkaConfig = new HashMap<>();
        kafkaConfig.put(ProducerConfig.ACKS_CONFIG, propertiesHelper.kafkaAcksConfig());
        kafkaConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, propertiesHelper.kafkaServers());
        kafkaConfig.put(ProducerConfig.CLIENT_ID_CONFIG, propertiesHelper.kafkaExtractorId());
        KafkaProducer<String, TradeEvent> kafkaProducerService = new KafkaProducer<>(kafkaConfig, new StringSerializer(), new TradeEventSerde());
        TradeEventKafkaProducer kafkaProducer = new TradeEventKafkaProducer(kafkaProducerService, propertiesHelper.inputTopic());
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaProducer::close));
        return kafkaProducer;
    }

    private static String subscribeMsg() {
        return "{\n"
                + "  \"action\": \"subscribe\",\n"
                + "  \"channel\": \"trades\",\n"
                + "  \"symbol\": \"ETH-USDT\"\n"
                + "}";
    }
}
