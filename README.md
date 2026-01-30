# OpenSearch Conversational Search with RAG (Java Implementation)

A Java implementation of OpenSearch Conversational Search using Retrieval-Augmented Generation (RAG) with neural search capabilities.

## Overview

This project provides a Java client library and example application for performing conversational searches on OpenSearch using RAG. It implements the same functionality as the Python example provided in the OpenSearch documentation.

### Key Features

- **Neural Search**: Uses embedding models to perform semantic search
- **RAG Integration**: Retrieves relevant context and generates answers using LLMs (e.g., AWS Bedrock Claude)
- **Search Pipelines**: Supports custom search pipelines for advanced query processing
- **Configurable**: Easily customize search parameters through configuration files
- **AWS Integration**: Compatible with AWS OpenSearch Service and Bedrock

## Project Structure

```
opensearch-rag-java/
├── pom.xml                                          # Maven project configuration
├── README.md                                        # This file
├── src/
│   └── main/
│       ├── java/com/example/opensearch/
│       │   ├── ConversationalSearchExample.java    # Main example application
│       │   ├── client/
│       │   │   └── OpenSearchClientWrapper.java    # OpenSearch client wrapper
│       │   ├── config/
│       │   │   └── OpenSearchConfig.java           # Configuration loader
│       │   ├── model/
│       │   │   └── GenerativeQAParameters.java     # RAG parameter model
│       │   └── service/
│       │       └── ConversationalSearchService.java # Main search service
│       └── resources/
│           └── application.properties               # Configuration file
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Access to an OpenSearch cluster with:
  - Neural search plugin enabled
  - RAG (Retrieval-Augmented Generation) capabilities configured
  - A configured search pipeline
  - An embedding model deployed

## Configuration

Edit `src/main/resources/application.properties` to configure your OpenSearch connection:

```properties
# OpenSearch Configuration
opensearch.host=your-opensearch-host
opensearch.port=9200
opensearch.scheme=https

# AWS Configuration (if using AWS OpenSearch Service)
aws.region=us-east-1
aws.service=es

# Index and Search Pipeline Configuration
opensearch.index.name=opensearch_kl_index
opensearch.search.pipeline=my-conversation-search-pipeline-deepseek-zh

# Embedding Model Configuration
opensearch.embedding.model.id=your-embedding-model-id

# RAG Configuration
opensearch.rag.context.size=5
opensearch.rag.timeout=15
opensearch.rag.llm.model=bedrock/claude
opensearch.rag.result.size=2

# Neural Search Configuration
opensearch.neural.k=5
```

### Configuration Parameters

| Parameter | Description | Default |
|-----------|-------------|---------|
| `opensearch.host` | OpenSearch host address | localhost |
| `opensearch.port` | OpenSearch port | 9200 |
| `opensearch.scheme` | Connection scheme (http/https) | https |
| `opensearch.index.name` | Index to search | opensearch_kl_index |
| `opensearch.search.pipeline` | Search pipeline name | my-conversation-search-pipeline-deepseek-zh |
| `opensearch.embedding.model.id` | Embedding model ID | \<embedding-model-id\> |
| `opensearch.rag.context.size` | Number of documents for context | 5 |
| `opensearch.rag.timeout` | RAG timeout in seconds | 15 |
| `opensearch.rag.llm.model` | LLM model identifier | bedrock/claude |
| `opensearch.rag.result.size` | Number of results to return | 2 |
| `opensearch.neural.k` | Number of nearest neighbors | 5 |

## Building the Project

Build the project using Maven:

```bash
cd /home/ec2-user/workspace/opensearch-rag-java
mvn clean package
```

This will create an executable JAR file in the `target/` directory.

## Running the Application

### Using Maven

```bash
mvn exec:java -Dexec.mainClass="com.example.opensearch.ConversationalSearchExample"
```

### Using the JAR

```bash
java -jar target/opensearch-rag-java-1.0-SNAPSHOT.jar
```

### With Custom Question

```bash
java -jar target/opensearch-rag-java-1.0-SNAPSHOT.jar "Your question here"
```

## Usage Examples

### Basic Usage

```java
// Load configuration
OpenSearchConfig config = new OpenSearchConfig();

// Create OpenSearch client (with authentication if needed)
try (OpenSearchClientWrapper clientWrapper = new OpenSearchClientWrapper(config)) {
    
    // Create conversational search service
    ConversationalSearchService searchService = new ConversationalSearchService(clientWrapper);
    
    // Perform search with default configuration
    String question = "OpenSearch Serverless 是什么？";
    JsonNode response = searchService.conversationalSearch(question);
    
    // Print results
    searchService.printSearchResults(response);
    
    // Extract generated answer
    String answer = searchService.extractGeneratedAnswer(response);
    System.out.println("Answer: " + answer);
}
```

### Advanced Usage with Custom Parameters

```java
JsonNode response = searchService.conversationalSearch(
    "What are the key features?",           // question
    "my_index",                              // index name
    "my_pipeline",                           // search pipeline
    "my-embedding-model-id",                 // embedding model ID
    5,                                       // k (nearest neighbors)
    3,                                       // size (number of results)
    new String[]{"text", "title"},          // source fields
    "bedrock/claude",                        // LLM model
    5,                                       // context size
    20                                       // timeout (seconds)
);
```

### With Authentication

```java
OpenSearchConfig config = new OpenSearchConfig();
try (OpenSearchClientWrapper clientWrapper = 
        new OpenSearchClientWrapper(config, "username", "password")) {
    // Use the client...
}
```

## API Reference

### ConversationalSearchService

Main service class for performing conversational searches.

#### Methods

- `conversationalSearch(String question)`: Perform search with default configuration
- `conversationalSearch(String question, String indexName, ...)`: Perform search with custom parameters
- `extractGeneratedAnswer(JsonNode response)`: Extract the generated answer from response
- `extractHits(JsonNode response)`: Extract search hits from response
- `printSearchResults(JsonNode response)`: Print formatted search results

### OpenSearchClientWrapper

Wrapper for OpenSearch client management.

#### Constructors

- `OpenSearchClientWrapper(OpenSearchConfig config)`: Create client without authentication
- `OpenSearchClientWrapper(OpenSearchConfig config, String username, String password)`: Create client with authentication

## Request Format

The Java implementation generates the following request format (equivalent to the Python example):

```json
{
  "query": {
    "neural": {
      "text_embedding": {
        "query_text": "Your question here",
        "model_id": "<embedding-model-id>",
        "k": 5
      }
    }
  },
  "size": 2,
  "_source": ["text"],
  "ext": {
    "generative_qa_parameters": {
      "llm_model": "bedrock/claude",
      "llm_question": "Your question here",
      "context_size": 5,
      "timeout": 15
    }
  }
}
```

## Response Format

The response includes:

1. **Generated Answer**: AI-generated answer based on retrieved context
2. **Search Hits**: Relevant documents from the search
3. **Scores**: Relevance scores for each document

## Troubleshooting

### Connection Issues

- Verify OpenSearch host and port are correct
- Check if HTTPS is required and set `opensearch.scheme=https`
- Ensure network connectivity to OpenSearch cluster

### Authentication Errors

- Provide credentials when creating the client wrapper
- For AWS OpenSearch, ensure proper IAM roles/credentials

### Search Pipeline Errors

- Verify the search pipeline exists and is properly configured
- Check that the embedding model is deployed
- Ensure RAG capabilities are enabled in your OpenSearch cluster

### Model Not Found

- Verify the embedding model ID is correct
- Ensure the model is deployed in your OpenSearch cluster
- Check LLM model configuration (e.g., Bedrock access)

## Dependencies

Main dependencies used in this project:

- OpenSearch Java Client 2.11.1
- AWS SDK 2.21.0
- Jackson 2.15.3 (JSON processing)
- SLF4J 2.0.9 (Logging)
- Apache HTTP Client 4.5.14

## Contributing

Feel free to submit issues and enhancement requests!

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## References

- [OpenSearch Documentation](https://opensearch.org/docs/latest/)
- [OpenSearch Neural Search](https://opensearch.org/docs/latest/search-plugins/neural-search/)
- [AWS Bedrock](https://aws.amazon.com/bedrock/)
- [OpenSearch Java Client](https://github.com/opensearch-project/opensearch-java)

## Author

Generated as an example implementation of OpenSearch Conversational Search with RAG.