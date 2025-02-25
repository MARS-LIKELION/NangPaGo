package com.mars.app.config.elasticsearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchIndexConfig {

    @Value("${spring.elasticsearch.index-name}")
    private String indexName;

    public String getIndexName() {
        return indexName;
    }
}
