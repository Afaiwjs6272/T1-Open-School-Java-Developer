package ru.ukhanov.t1.java.aspects.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("kafka-aspects")
@ConfigurationProperties(prefix = "aspects.kafka")
public class KafkaAspectsConfig {
    private String bootstrapServers;
    private String topic;
    private String groupId;
    private boolean enable = true;

    public String getBootstrapServers() { return bootstrapServers; }
    public void setBootstrapServers(String bootstrapServers) { this.bootstrapServers = bootstrapServers; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public boolean isEnable() { return enable; }
    public void setEnable(boolean enable) { this.enable = enable; }
}
