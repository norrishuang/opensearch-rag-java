package com.example.opensearch.client;

import com.example.opensearch.config.OpenSearchConfig;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Wrapper class for OpenSearch client initialization and management
 */
public class OpenSearchClientWrapper implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(OpenSearchClientWrapper.class);
    
    private final OpenSearchConfig config;
    private final RestClient restClient;
    private final OpenSearchClient client;

    public OpenSearchClientWrapper(OpenSearchConfig config) {
        this.config = config;
        this.restClient = createRestClient();
        this.client = createOpenSearchClient();
    }

    public OpenSearchClientWrapper(OpenSearchConfig config, String username, String password) {
        this.config = config;
        this.restClient = createRestClientWithAuth(username, password);
        this.client = createOpenSearchClient();
    }

    /**
     * Create a REST client without authentication
     */
    private RestClient createRestClient() {
        logger.info("Creating OpenSearch REST client for {}:{}", 
            config.getHost(), config.getPort());
        
        return RestClient.builder(
            new HttpHost(config.getHost(), config.getPort(), config.getScheme())
        ).build();
    }

    /**
     * Create a REST client with basic authentication
     */
    private RestClient createRestClientWithAuth(String username, String password) {
        logger.info("Creating OpenSearch REST client with authentication for {}:{}", 
            config.getHost(), config.getPort());
        
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(username, password)
        );

        return RestClient.builder(
            new HttpHost(config.getHost(), config.getPort(), config.getScheme())
        ).setHttpClientConfigCallback(httpClientBuilder -> 
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
        ).build();
    }

    /**
     * Create OpenSearch Java client using the REST client
     */
    private OpenSearchClient createOpenSearchClient() {
        RestClientTransport transport = new RestClientTransport(
            restClient,
            new JacksonJsonpMapper()
        );
        return new OpenSearchClient(transport);
    }

    /**
     * Get the OpenSearch client instance
     */
    public OpenSearchClient getClient() {
        return client;
    }

    /**
     * Get the REST client instance for low-level operations
     */
    public RestClient getRestClient() {
        return restClient;
    }

    /**
     * Get the configuration
     */
    public OpenSearchConfig getConfig() {
        return config;
    }

    /**
     * Close the client connections
     */
    @Override
    public void close() throws IOException {
        if (restClient != null) {
            logger.info("Closing OpenSearch REST client");
            restClient.close();
        }
    }
}