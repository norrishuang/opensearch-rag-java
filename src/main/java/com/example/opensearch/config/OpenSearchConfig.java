package com.example.opensearch.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for OpenSearch connection and RAG parameters
 */
public class OpenSearchConfig {
    private final Properties properties;

    public OpenSearchConfig() throws IOException {
        this.properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new IOException("Unable to find application.properties");
            }
            properties.load(input);
        }
    }

    public OpenSearchConfig(Properties properties) {
        this.properties = properties;
    }

    public String getHost() {
        return properties.getProperty("opensearch.host", "localhost");
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("opensearch.port", "9200"));
    }

    public String getScheme() {
        return properties.getProperty("opensearch.scheme", "https");
    }

    public String getAwsRegion() {
        return properties.getProperty("aws.region", "us-east-1");
    }

    public String getAwsService() {
        return properties.getProperty("aws.service", "es");
    }

    public String getIndexName() {
        return properties.getProperty("opensearch.index.name", "opensearch_kl_index");
    }

    public String getSearchPipeline() {
        return properties.getProperty("opensearch.search.pipeline", 
            "my-conversation-search-pipeline-deepseek-zh");
    }

    public String getEmbeddingModelId() {
        return properties.getProperty("opensearch.embedding.model.id", "<embedding-model-id>");
    }

    public int getContextSize() {
        return Integer.parseInt(properties.getProperty("opensearch.rag.context.size", "5"));
    }

    public int getTimeout() {
        return Integer.parseInt(properties.getProperty("opensearch.rag.timeout", "15"));
    }

    public String getLlmModel() {
        return properties.getProperty("opensearch.rag.llm.model", "bedrock/claude");
    }

    public int getResultSize() {
        return Integer.parseInt(properties.getProperty("opensearch.rag.result.size", "2"));
    }

    public int getNeuralK() {
        return Integer.parseInt(properties.getProperty("opensearch.neural.k", "5"));
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}