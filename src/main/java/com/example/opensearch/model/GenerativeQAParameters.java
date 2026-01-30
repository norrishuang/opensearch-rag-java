package com.example.opensearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Parameters for Generative QA with RAG
 */
public class GenerativeQAParameters {
    
    @JsonProperty("llm_model")
    private String llmModel;
    
    @JsonProperty("llm_question")
    private String llmQuestion;
    
    @JsonProperty("context_size")
    private int contextSize;
    
    @JsonProperty("timeout")
    private int timeout;

    public GenerativeQAParameters() {
    }

    public GenerativeQAParameters(String llmModel, String llmQuestion, int contextSize, int timeout) {
        this.llmModel = llmModel;
        this.llmQuestion = llmQuestion;
        this.contextSize = contextSize;
        this.timeout = timeout;
    }

    public String getLlmModel() {
        return llmModel;
    }

    public void setLlmModel(String llmModel) {
        this.llmModel = llmModel;
    }

    public String getLlmQuestion() {
        return llmQuestion;
    }

    public void setLlmQuestion(String llmQuestion) {
        this.llmQuestion = llmQuestion;
    }

    public int getContextSize() {
        return contextSize;
    }

    public void setContextSize(int contextSize) {
        this.contextSize = contextSize;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "GenerativeQAParameters{" +
                "llmModel='" + llmModel + '\'' +
                ", llmQuestion='" + llmQuestion + '\'' +
                ", contextSize=" + contextSize +
                ", timeout=" + timeout +
                '}';
    }
}