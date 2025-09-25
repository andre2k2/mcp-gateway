package com.github.mcpgateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class ApiToolConfig {
    private String name;
    private String description;
    private String endpoint;
    private String method;
    private Integer timeout;
    private String template;
    @JsonProperty("query_params")
    private Map<String, String> queryParams;
    private Map<String, ParameterConfig> parameters;

    // Constructors
    public ApiToolConfig() {}

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public Map<String, ParameterConfig> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, ParameterConfig> parameters) {
        this.parameters = parameters;
    }
}
