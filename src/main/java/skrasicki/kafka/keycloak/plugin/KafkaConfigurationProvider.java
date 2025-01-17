package skrasicki.kafka.keycloak.plugin;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.jboss.logging.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KafkaConfigurationProvider {

    private final Logger LOGGER = Logger.getLogger(KafkaConfigurationProvider.class);
    private final String PLUGIN_KAFKA_PRODUCER_CONFIG = "PLUGIN_KAFKA_PRODUCER_CONFIG";
    private final String PLUGIN_EVENT_TYPE_CONFIG = "PLUGIN_EVENT_TYPE_CONFIG";
    private final String PLUGIN_KAFKA_TOPIC = "PLUGIN_KAFKA_TOPIC";
    private final String PLUGIN_KAFKA_ADMIN_TOPIC = "PLUGIN_KAFKA_ADMIN_TOPIC";
    private final String PLUGIN_KAFKA_ADMIN_EVENTS_ENABLED = "PLUGIN_KAFKA_ADMIN_EVENTS_ENABLED";
    private final String PLUGIN_KAFKA_EVENTS_ENABLED = "PLUGIN_KAFKA_EVENTS_ENABLED";

    public KafkaConfiguration getKafkaConfiguration() {
        String topic = getEnvVariableOrDefault(PLUGIN_KAFKA_TOPIC, "kafka-plugin-topic");
        String adminTopic = getEnvVariableOrDefault(PLUGIN_KAFKA_ADMIN_TOPIC, "kafka-plugin-admin-topic");
        boolean eventsEnabled = getEnvBooleanOrDefault(PLUGIN_KAFKA_EVENTS_ENABLED, true);
        boolean adminEventsEnabled = getEnvBooleanOrDefault(PLUGIN_KAFKA_ADMIN_EVENTS_ENABLED, false);
        Map<String, Object> producerConfig = buildProducerConfig();

        if (!producerConfig.containsKey("bootstrap.servers")) {
            throw new RuntimeException("Bootstrap servers are not configured.");
        }

        return new KafkaConfiguration(producerConfig, getKafkaPluginEventConfig(), topic, adminTopic, adminEventsEnabled, eventsEnabled);
    }

    private Map<String, Object> buildProducerConfig() {
        Map<String, Object> producerConfig = new HashMap<>();
        Map<String, Object> producerConfigConstants = getProducerConfigConstants();

        Map<String, String> rawEnv = filterEnvVariablesByPrefix(PLUGIN_KAFKA_PRODUCER_CONFIG);

        for (Map.Entry<String, String> entry : rawEnv.entrySet()) {
            String kafkaConfigKey = transformEnvironmentKey(entry.getKey(), PLUGIN_KAFKA_PRODUCER_CONFIG);
            if (producerConfigConstants.containsKey(kafkaConfigKey)) {
                producerConfig.put(kafkaConfigKey, entry.getValue());
            } else {
                LOGGER.warn("No producer config found for key: " + kafkaConfigKey);
            }
        }

        return producerConfig;
    }

    private List<String> getKafkaPluginEventConfig() {
        Map<String, String> envVariables = filterEnvVariablesByPrefix(PLUGIN_EVENT_TYPE_CONFIG);

        return envVariables.values().stream()
                .flatMap(value -> Stream.of(value.split(",")))
                .collect(Collectors.toList());
    }

    private String getEnvVariableOrDefault(String key, String defaultValue) {
        return System.getenv().getOrDefault(key, defaultValue);
    }

    private boolean getEnvBooleanOrDefault(String key, boolean defaultValue) {
        return Boolean.parseBoolean(System.getenv().getOrDefault(key, Boolean.toString(defaultValue)));
    }

    private Map<String, Object> getProducerConfigConstants() {
        Map<String, Object> constants = new HashMap<>();
        for (Field field : ProducerConfig.class.getFields()) {
            if (field.getName().contains("_CONFIG")) {
                try {
                    constants.put((String) field.get(null), null);
                } catch (IllegalAccessException e) {
                    LOGGER.error("Failed to read ProducerConfig properties: " + e.getMessage());
                }
            }
        }
        return constants;
    }

    private Map<String, String> filterEnvVariablesByPrefix(String prefix) {
        Map<String, String> envVars = System.getenv();
        Map<String, String> filteredVars = new HashMap<>();

        for (Map.Entry<String, String> entry : envVars.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                filteredVars.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredVars;
    }

    private String transformEnvironmentKey(String key, String prefix) {
        return key.substring(prefix.length() + 1).toLowerCase().replace("_", ".");
    }
}
