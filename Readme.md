# Kafka Keycloak Plugin

This project is an extension for [Keycloak](https://www.keycloak.org/) that facilitates sending Keycloak events to [Apache Kafka](https://kafka.apache.org/) brokers. It works as an Event Listener plugin, meaning it integrates with Keycloak's event system (both user events and administrative events).

## Requirements

- Java 17
- A compatible version of Keycloak (tested with 26.0.8)
- A running Apache Kafka broker or cluster to which Keycloak will send messages

## Features

- **Event capturing** (e.g., user registration, login, logout) and sending them to the configured Kafka topic.
- **Administrative event support** (if admin events are enabled in the configuration).
- **Customization** via environment variables (e.g., topic names, enabling/disabling events, setting producer configuration parameters).
- **Serialized event transmission** in JSON format using built-in serializers.

## Configuration

In order to configure the plugin, set the required environment variables for the system running Keycloak. Below are the most important ones:

- **PLUGIN_KAFKA_TOPIC**  
  The default topic name where user events will be sent (default value: `kafka-plugin-topic`).

- **PLUGIN_KAFKA_ADMIN_TOPIC**  
  The topic for administrative events (default value: `kafka-plugin-admin-topic`).

- **PLUGIN_KAFKA_EVENTS_ENABLED**  
  A boolean flag enabling or disabling the transmission of user events (default value: `true`).

- **PLUGIN_KAFKA_ADMIN_EVENTS_ENABLED**  
  A boolean flag enabling or disabling the transmission of administrative events (default value: `false`).

- **PLUGIN_EVENT_TYPE_CONFIG**  
  A variable storing a list of event types (e.g., `LOGIN, LOGOUT, REGISTER`) that should be sent to Kafka.

- **PLUGIN_KAFKA_PRODUCER_CONFIG_BOOTSTRAP_SERVERS**  
  The address of Kafka brokers.

For other Kafka producer settings (e.g., timeout, retry), you need to use an environment variable prefixed with `PLUGIN_KAFKA_PRODUCER_CONFIG_`, followed by the specific Kafka configuration parameter (e.g., `PLUGIN_KAFKA_PRODUCER_CONFIG_ACKS` or `PLUGIN_KAFKA_PRODUCER_CONFIG_RETRIES`).

## Installation and Startup

1. **Build the package**
2. **Copy the artifact** to the `providers/` directory in Keycloak so it can load the plugin on startup.
3. **Start Keycloak** with the necessary environment variables set.
4. **Setup event listener** in keycloak administrative panel.

## Running with Docker

For quick testing or local deployment, you can use the provided `docker-compose.yml` file, which defines services for Zookeeper, Kafka, Kafka UI, and Keycloak with a basic plugin configuration:
- Ensure that the `docker-compose.yml` file contains the correct environment variables (e.g., `PLUGIN_KAFKA_PRODUCER_CONFIG_BOOTSTRAP_SERVERS`).
- Place your plugin JAR file in the appropriate volume or directory within the Keycloak container (either in the `volumes` section or by creating your own Keycloak Docker image).
- Run the following command in the directory containing the `docker-compose.yml` file:
```shell script
docker-compose up -d
```
- After successful startup, the following services should be accessible:
    - Keycloak (e.g., at port `8383`)
    - Kafka UI (e.g., at port `8080`)
    - Kafka brokers and Zookeeper as part of Docker's internal network

## Usage

Once Keycloak is running with the plugin and the required environment variables, all user events (based on the types specified in `PLUGIN_EVENT_TYPE_CONFIG`) will be sent to the specified Kafka topic. Similarly, administrative events will be sent if they are enabled via `PLUGIN_KAFKA_ADMIN_EVENTS_ENABLED`.
