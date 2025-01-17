package skrasicki.kafka.keycloak.plugin;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

public class KafkaEventListenerProvider implements EventListenerProvider {

    private final Logger LOGGER = Logger.getLogger(String.valueOf(KafkaEventListenerProvider.class));
    private final KafkaProducer<String, String> producer;
    private final KafkaConfiguration kafkaConfiguration;

    public KafkaEventListenerProvider(KafkaProducer<String, String> producer, KafkaConfiguration kafkaConfiguration) {
        this.producer = producer;
        this.kafkaConfiguration = kafkaConfiguration;
    }

    @Override
    public void onEvent(Event event) {
        if (shouldHandleEvent(event) && kafkaConfiguration.isEventsEnabled()) {
            handleEvent(
                    EventSerializer.serializeEvent(event),
                    kafkaConfiguration.getKafkaTopic(),
                    String.format("Event %s sent to Kafka successfully.", event.getId())
            );
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        if (kafkaConfiguration.isAdminEventsEnabled()) {
            handleEvent(
                    EventSerializer.serializeAdminEvent(event),
                    kafkaConfiguration.getKafkaAdminTopic(),
                    String.format("Admin event %s sent to Kafka successfully.", event.getId())
            );
        }
    }

    @Override
    public void close() { }

    private void handleEvent(String serializedEvent, String topic, String successLogMessage) {
        try {
            LOGGER.info("Sending event to Kafka: " + serializedEvent);
            producer.send(new ProducerRecord<>(topic, serializedEvent));
            LOGGER.info(successLogMessage);
        } catch (Exception e) {
            LOGGER.error(String.format("Failed to send event to topic %s", topic), e);
        }

    }

    private boolean shouldHandleEvent(Event event) {
        return kafkaConfiguration.getEventTypes().contains(event.getType().name());
    }
}
