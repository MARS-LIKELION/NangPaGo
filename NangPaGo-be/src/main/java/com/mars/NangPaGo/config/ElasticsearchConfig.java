package com.mars.NangPaGo.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import java.net.URL;
import java.net.MalformedURLException;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {
    @Value("${ELASTICSEARCH_HOST}")
    private String elasticsearchHost;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        try {
            URL url = new URL(elasticsearchHost);
            String host = url.getHost();
            int port = url.getPort();

            RestClient restClient = RestClient.builder(
                new HttpHost(host, port, url.getProtocol())
            ).build();

            RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

            return new ElasticsearchClient(transport);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid Elasticsearch Host URL: " + elasticsearchHost, e);
        }
    }
}
