package com.example.opensearch.service;

import com.example.opensearch.client.OpenSearchClientWrapper;
import com.example.opensearch.config.OpenSearchConfig;
import com.example.opensearch.model.GenerativeQAParameters;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for OpenSearch Conversational Search with RAG
 */
public class ConversationalSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ConversationalSearchService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final OpenSearchClientWrapper clientWrapper;
    private final OpenSearchConfig config;

    public ConversationalSearchService(OpenSearchClientWrapper clientWrapper) {
        this.clientWrapper = clientWrapper;
        this.config = clientWrapper.getConfig();
    }

    /**
     * Perform a conversational search with RAG using neural search
     * 
     * @param question The user's question
     * @return The search response as a JsonNode
     * @throws IOException If the search request fails
     */
    public JsonNode conversationalSearch(String question) throws IOException {
        return conversationalSearch(
            question,
            config.getIndexName(),
            config.getSearchPipeline(),
            config.getEmbeddingModelId(),
            config.getNeuralK(),
            config.getResultSize(),
            new String[]{"text"},
            config.getLlmModel(),
            config.getContextSize(),
            config.getTimeout()
        );
    }

    /**
     * Perform a conversational search with RAG using neural search with custom parameters
     * 
     * @param question The user's question
     * @param indexName The index to search
     * @param searchPipeline The search pipeline to use
     * @param embeddingModelId The embedding model ID
     * @param k The number of nearest neighbors for neural search
     * @param size The number of results to return
     * @param sourceFields The fields to include in the response
     * @param llmModel The LLM model to use
     * @param contextSize The context size for RAG
     * @param timeout The timeout in seconds
     * @return The search response as a JsonNode
     * @throws IOException If the search request fails
     */
    public JsonNode conversationalSearch(
            String question,
            String indexName,
            String searchPipeline,
            String embeddingModelId,
            int k,
            int size,
            String[] sourceFields,
            String llmModel,
            int contextSize,
            int timeout) throws IOException {
        
        logger.info("Performing conversational search on index: {}", indexName);
        logger.info("Question: {}", question);
        
        // Build the search request body
        Map<String, Object> searchRequest = buildSearchRequest(
            question, embeddingModelId, k, size, sourceFields,
            llmModel, contextSize, timeout
        );
        
        String requestBody = objectMapper.writeValueAsString(searchRequest);
        logger.debug("Search request body: {}", requestBody);
        
        // Execute the search request
        RestClient restClient = clientWrapper.getRestClient();
        Request request = new Request("GET", "/" + indexName + "/_search");
        
        // Add search pipeline parameter
        request.addParameter("search_pipeline", searchPipeline);
        
        // Set the request body
        request.setEntity(new NStringEntity(requestBody, ContentType.APPLICATION_JSON));
        
        // Execute the request
        Response response = restClient.performRequest(request);
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        
        logger.info("Search completed successfully");
        logger.debug("Response: {}", responseBody);
        
        // Parse and return the response
        return objectMapper.readTree(responseBody);
    }

    /**
     * Build the search request body
     */
    private Map<String, Object> buildSearchRequest(
            String question,
            String embeddingModelId,
            int k,
            int size,
            String[] sourceFields,
            String llmModel,
            int contextSize,
            int timeout) {
        
        Map<String, Object> request = new HashMap<>();
        
        // Build neural query
        Map<String, Object> neuralQuery = new HashMap<>();
        Map<String, Object> textEmbedding = new HashMap<>();
        textEmbedding.put("query_text", question);
        textEmbedding.put("model_id", embeddingModelId);
        textEmbedding.put("k", k);
        
        neuralQuery.put("text_embedding", textEmbedding);
        
        Map<String, Object> query = new HashMap<>();
        query.put("neural", neuralQuery);
        
        request.put("query", query);
        
        // Set result size
        request.put("size", size);
        
        // Set source fields
        request.put("_source", sourceFields);
        
        // Build ext section with generative QA parameters
        Map<String, Object> ext = new HashMap<>();
        Map<String, Object> generativeQAParams = new HashMap<>();
        generativeQAParams.put("llm_model", llmModel);
        generativeQAParams.put("llm_question", question);
        generativeQAParams.put("context_size", contextSize);
        generativeQAParams.put("timeout", timeout);
        
        ext.put("generative_qa_parameters", generativeQAParams);
        request.put("ext", ext);
        
        return request;
    }

    /**
     * Extract the generated answer from the search response
     * 
     * @param response The search response
     * @return The generated answer text, or null if not found
     */
    public String extractGeneratedAnswer(JsonNode response) {
        try {
            JsonNode ext = response.get("ext");
            if (ext != null) {
                JsonNode retrieval = ext.get("retrieval_augmented_generation");
                if (retrieval != null) {
                    JsonNode answer = retrieval.get("answer");
                    if (answer != null) {
                        return answer.asText();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting generated answer", e);
        }
        return null;
    }

    /**
     * Extract search hits from the response
     * 
     * @param response The search response
     * @return Array of search hits
     */
    public JsonNode extractHits(JsonNode response) {
        try {
            JsonNode hits = response.get("hits");
            if (hits != null) {
                return hits.get("hits");
            }
        } catch (Exception e) {
            logger.error("Error extracting hits", e);
        }
        return null;
    }

    /**
     * Print the search results in a formatted way
     * 
     * @param response The search response
     */
    public void printSearchResults(JsonNode response) {
        System.out.println("\n=== Conversational Search Results ===\n");
        
        // Print generated answer
        String generatedAnswer = extractGeneratedAnswer(response);
        if (generatedAnswer != null) {
            System.out.println("Generated Answer:");
            System.out.println(generatedAnswer);
            System.out.println();
        }
        
        // Print search hits
        JsonNode hits = extractHits(response);
        if (hits != null && hits.isArray()) {
            System.out.println("Retrieved Documents:");
            int index = 1;
            for (JsonNode hit : hits) {
                System.out.println("\nDocument " + index + ":");
                System.out.println("Score: " + hit.get("_score"));
                
                JsonNode source = hit.get("_source");
                if (source != null) {
                    System.out.println("Content: " + source);
                }
                index++;
            }
        }
        
        System.out.println("\n====================================\n");
    }
}