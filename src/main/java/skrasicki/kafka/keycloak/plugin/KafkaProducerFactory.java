package skrasicki.kafka.keycloak.plugin;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class KafkaProducerFactory {

    private static final Logger LOGGER = Logger.getLogger(KafkaProducerFactory.class);

    public KafkaProducer<String, String> createProducer(Map<String, Object> customConfig) {
        try {
            LOGGER.info("Creating Kafka producer with custom configuration.");
            return new KafkaProducer<>(buildProducerConfig(customConfig));
        } catch (Exception e) {
            LOGGER.error("Error while creating Kafka producer", e);
            throw new RuntimeException("Failed to create a Kafka producer", e);
        }
    }

    private Map<String, Object> buildProducerConfig(Map<String, Object> customConfig) {
        Map<String, Object> producerConfig = new HashMap<>(customConfig);

        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return producerConfig;
    }
}