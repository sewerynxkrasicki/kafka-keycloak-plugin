package skrasicki.kafka.keycloak.plugin;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class KafkaEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(KafkaEventListenerProviderFactory.class));
    private KafkaConfiguration kafkaConfiguration;

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        KafkaProducerFactory producerFactory = new KafkaProducerFactory();
        KafkaProducer<String, String> producer = producerFactory.createProducer(kafkaConfiguration.getProducerConfig());

        return new KafkaEventListenerProvider(producer, kafkaConfiguration);
    }

    @Override
    public void init(Config.Scope config) {
        LOGGER.info("Initializing Kafka plugin.");

        kafkaConfiguration = new KafkaConfigurationProvider().getKafkaConfiguration();

        LOGGER.info("Kafka Plugin configuration loaded successfully.");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {
        LOGGER.info("Kafka plugin shutting down");
    }

    @Override
    public String getId() {
        return "kafka-plugin-event-listener";
    }

}
