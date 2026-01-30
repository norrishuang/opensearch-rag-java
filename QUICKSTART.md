# Quick Start Guide

Get started with OpenSearch Conversational Search with RAG in 5 minutes.

## Step 1: Configure Your Connection

Edit `src/main/resources/application.properties`:

```properties
# Update these values with your OpenSearch cluster details
opensearch.host=your-opensearch-host.com
opensearch.port=9200
opensearch.scheme=https

# Update with your embedding model ID
opensearch.embedding.model.id=your-embedding-model-id

# Update with your index name and search pipeline
opensearch.index.name=your_index_name
opensearch.search.pipeline=your-search-pipeline-name
```

## Step 2: Build the Project

```bash
cd /home/ec2-user/workspace/opensearch-rag-java
mvn clean package
```

## Step 3: Run the Example

```bash
java -jar target/opensearch-rag-java-1.0-SNAPSHOT.jar
```

Or with Maven:

```bash
mvn exec:java -Dexec.mainClass="com.example.opensearch.ConversationalSearchExample"
```

## Step 4: Ask Your Own Question

```bash
java -jar target/opensearch-rag-java-1.0-SNAPSHOT.jar "What is OpenSearch?"
```

## Sample Output

```
=== Conversational Search Results ===

Generated Answer:
OpenSearch is an open-source search and analytics suite...

Retrieved Documents:

Document 1:
Score: 0.92
Content: {"text":"OpenSearch is a community-driven..."}

Document 2:
Score: 0.85
Content: {"text":"Key features include..."}

====================================
```

## Using in Your Code

```java
import com.example.opensearch.client.OpenSearchClientWrapper;
import com.example.opensearch.config.OpenSearchConfig;
import com.example.opensearch.service.ConversationalSearchService;
import com.fasterxml.jackson.databind.JsonNode;

// Initialize
OpenSearchConfig config = new OpenSearchConfig();
try (OpenSearchClientWrapper client = new OpenSearchClientWrapper(config)) {
    ConversationalSearchService service = new ConversationalSearchService(client);
    
    // Search
    JsonNode response = service.conversationalSearch("Your question here");
    
    // Get answer
    String answer = service.extractGeneratedAnswer(response);
    System.out.println(answer);
}
```

## Authentication

If your cluster requires authentication:

```java
OpenSearchConfig config = new OpenSearchConfig();
try (OpenSearchClientWrapper client = 
        new OpenSearchClientWrapper(config, "username", "password")) {
    // Your code here
}
```

## Troubleshooting

**Cannot connect to OpenSearch:**
- Check your host and port in `application.properties`
- Verify your network can reach the OpenSearch cluster
- Ensure you're using the correct scheme (http/https)

**Model not found:**
- Verify your embedding model ID is correct
- Ensure the model is deployed in your cluster

**Authentication failed:**
- Provide credentials when creating the client wrapper
- Check your username and password are correct

## Next Steps

- Read the full [README.md](README.md) for detailed documentation
- Customize the configuration for your use case
- Integrate into your existing application

## Need Help?

- Check the [OpenSearch Documentation](https://opensearch.org/docs/latest/)
- Review the example code in `ConversationalSearchExample.java`
- Examine the service implementation in `ConversationalSearchService.java`