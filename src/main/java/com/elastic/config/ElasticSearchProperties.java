package com.elastic.config;

import lombok.Setter;
import org.apache.http.HttpHost;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Setter
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticSearchProperties {
    private String host;
    private int port;

    public HttpHost httpHost() {
        return new HttpHost(host, port, "http");
    }
}
