package skrasicki.kafka.keycloak.plugin;

import java.util.List;
import java.util.Map;

public class KafkaConfiguration {
    private final Map<String, Object> producerConfig;
    private final List<String> eventTypes;
    private final String kafkaTopic;
    private final String kafkaAdminTopic;
    private boolean adminEventsEnabled = false;
    private boolean eventsEnabled = true;

    public KafkaConfiguration(Map<String, Object> producerConfig, List<String> eventConfig, String kafkaTopic, String kafkaAdminTopic, boolean adminEventsEnabled, boolean eventsEnabled) {
        this.producerConfig = producerConfig;
        this.eventTypes = eventConfig;
        this.kafkaTopic = kafkaTopic;
        this.kafkaAdminTopic = kafkaAdminTopic;
        this.adminEventsEnabled = adminEventsEnabled;
        this.eventsEnabled = eventsEnabled;
    }

    public Map<String, Object> getProducerConfig() {
        return producerConfig;
    }

    public List<String> getEventTypes() {
        return eventTypes;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public String getKafkaAdminTopic() {
        return kafkaAdminTopic;
    }

    public boolean isAdminEventsEnabled() {
        return adminEventsEnabled;
    }

    public boolean isEventsEnabled() {
        return eventsEnabled;
    }
}
