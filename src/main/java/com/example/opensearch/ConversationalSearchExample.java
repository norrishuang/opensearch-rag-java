package com.example.opensearch;

import com.example.opensearch.client.OpenSearchClientWrapper;
import com.example.opensearch.config.OpenSearchConfig;
import com.example.opensearch.service.ConversationalSearchService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Example application demonstrating OpenSearch Conversational Search with RAG
 */
public class ConversationalSearchExample {
    private static final Logger logger = LoggerFactory.getLogger(ConversationalSearchExample.class);

    public static void main(String[] args) {
        logger.info("Starting OpenSearch Conversational Search Example");

        try {
            // Load configuration
            OpenSearchConfig config = new OpenSearchConfig();
            
            // Create OpenSearch client
            // If you need authentication, use: new OpenSearchClientWrapper(config, "username", "password")
            try (OpenSearchClientWrapper clientWrapper = new OpenSearchClientWrapper(config)) {
                
                // Create conversational search service
                ConversationalSearchService searchService = new ConversationalSearchService(clientWrapper);
                
                // Example question (in Chinese, matching the example in the task)
                String question = "OpenSearch Serverless 是什么，和OpenSearch集群模式有什么区别，使用 OpenSearch Serverless，还需要管理服务器资源么？";
                
                // You can also pass your own question via command line arguments
                if (args.length > 0) {
                    question = args[0];
                    logger.info("Using question from command line: {}", question);
                } else {
                    logger.info("Using default question: {}", question);
                }
                
                // Perform the conversational search
                System.out.println("\nExecuting conversational search...");
                System.out.println("Question: " + question);
                
                JsonNode response = searchService.conversationalSearch(question);
                
                // Print the results
                searchService.printSearchResults(response);
                
                // Optionally, you can also access specific parts of the response
                String generatedAnswer = searchService.extractGeneratedAnswer(response);
                if (generatedAnswer != null) {
                    logger.info("Successfully retrieved generated answer");
                } else {
                    logger.warn("No generated answer found in response");
                }
                
                // Example with custom parameters
                demonstrateCustomParametersSearch(searchService);
                
            } catch (IOException e) {
                logger.error("Error during search operation", e);
                System.err.println("Error performing search: " + e.getMessage());
                System.exit(1);
            }
            
        } catch (IOException e) {
            logger.error("Error loading configuration", e);
            System.err.println("Error loading configuration: " + e.getMessage());
            System.exit(1);
        }
        
        logger.info("Example completed successfully");
    }

    /**
     * Demonstrate conversational search with custom parameters
     */
    private static void demonstrateCustomParametersSearch(ConversationalSearchService searchService) {
        System.out.println("\n\n=== Example with Custom Parameters ===\n");
        
        try {
            String customQuestion = "What are the key features of OpenSearch?";
            
            JsonNode response = searchService.conversationalSearch(
                customQuestion,
                "opensearch_kl_index",                              // index name
                "my-conversation-search-pipeline-deepseek-zh",      // search pipeline
                "<embedding-model-id>",                             // embedding model ID
                5,                                                   // k (nearest neighbors)
                3,                                                   // size (number of results)
                new String[]{"text", "title"},                      // source fields
                "bedrock/claude",                                   // LLM model
                5,                                                   // context size
                20                                                   // timeout (seconds)
            );
            
            searchService.printSearchResults(response);
            
        } catch (IOException e) {
            logger.error("Error in custom parameters example", e);
            System.err.println("Error in custom example: " + e.getMessage());
        }
    }
}