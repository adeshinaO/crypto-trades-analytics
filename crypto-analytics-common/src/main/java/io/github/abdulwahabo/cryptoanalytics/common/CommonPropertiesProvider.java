package io.github.abdulwahabo.cryptoanalytics.common;

import io.github.abdulwahabo.cryptoanalytics.common.exception.PropertiesFileException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// TODO: JavaDoc should warn that loadProperties() is to be called first.
public class CommonPropertiesProvider {

    private final Properties properties = new Properties();

    public String kafkaServers() {
        return properties.getProperty("kafka.servers");
    }

    public String kafkaProcessorId() {
        return properties.getProperty("kafka.processor.id");
    }

    public String kafkaExtractorId() {
        return properties.getProperty("kafka.extractor.id");
    }

    public String inputTopic() {
        return properties.getProperty("kafka.topic.input");
    }

    public String outputTopic() {
       return properties.getProperty("kafka.topic.output");
    }

    public String kafkaAcksConfig() {
       return properties.getProperty("kafka.acks");
    }

    public void loadProperties() throws PropertiesFileException {
        try {
            InputStream fileStream = getClass().getClassLoader().getResourceAsStream("common.properties");
            properties.load(fileStream);
        } catch (IOException e) {
            throw new PropertiesFileException("Failed to load properties from file", e);
        }
    }
}
