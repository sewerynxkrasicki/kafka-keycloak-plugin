package skrasicki.kafka.keycloak.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.events.Event;
import org.keycloak.events.admin.AdminEvent;

public class EventSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serializeEvent(Event event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Event", e);
        }
    }

    public static String serializeAdminEvent(AdminEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing AdminEvent", e);
        }
    }
}
