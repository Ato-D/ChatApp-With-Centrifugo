package com.example.Centrifugo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("centrifugo")
public class CentrifugoConfiguration {

    private String url;
    private String secret;
    private String method;
    private String apiKey;
    private String channel1;
    private String channel2;
    private String channel3;
    private String activitySummary;
    private String websocket;
}
