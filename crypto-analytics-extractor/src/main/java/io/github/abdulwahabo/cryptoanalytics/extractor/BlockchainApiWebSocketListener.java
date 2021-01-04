package io.github.abdulwahabo.cryptoanalytics.extractor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.abdulwahabo.cryptoanalytics.common.model.TradeEvent;

import java.net.http.WebSocket;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockchainApiWebSocketListener implements WebSocket.Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockchainApiWebSocketListener.class);
    private final CountDownLatch latch;
    private final TradeEventKafkaProducer kafkaProducer;
    private final ObjectMapper mapper = new ObjectMapper();
    private StringBuilder builder = new StringBuilder();
    private CompletableFuture<?> completable = new CompletableFuture<>();

    public BlockchainApiWebSocketListener(CountDownLatch latch, TradeEventKafkaProducer kafkaProducer) {
        this.latch = latch;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {

        builder.append(data);
        webSocket.request(1);
        if (last) {
            try {
                String messageJson = builder.toString();
                TradeEvent tradeEvent = mapper.readValue(messageJson, TradeEvent.class);
                ZonedDateTime zdt = ZonedDateTime.parse(tradeEvent.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME);

                // Remove seconds and nanoseconds.
                LocalDateTime localDateTime =
                        LocalDateTime.of(zdt.getYear(), zdt.getMonth(), zdt.getDayOfMonth(), zdt.getHour(), zdt.getMinute());
                String timeString = localDateTime.toString();

                if (tradeEvent.getEvent().equals("updated")) {
                    kafkaProducer.send(timeString, tradeEvent);
                } else {
                    LOGGER.info("New non-update event: " + messageJson);
                }

                builder = new StringBuilder();
                completable.complete(null);
                CompletionStage<?> completionStage = completable;
                completable = new CompletableFuture<>();
                return completionStage;
            } catch (JsonProcessingException e) {
                LOGGER.error("JSON deserialization failed for: " + data, e);
            }
        }
        return completable;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        LOGGER.info("Opened WebSocket connection to Blockchain API");
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        kafkaProducer.close();
        latch.countDown();
        LOGGER.info("Closing WebSocket Connection | Reason: " + reason + " | Status code: " + statusCode);
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        kafkaProducer.close();
        latch.countDown();
        LOGGER.error("Error in WebSocket connection", error);
    }
}
