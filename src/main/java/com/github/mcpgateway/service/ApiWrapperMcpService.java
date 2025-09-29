package com.github.mcpgateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mcpgateway.model.ApiToolConfig;
import com.github.mcpgateway.model.ServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiWrapperMcpService {

    @Value("${mcp.config.file:api-config.yaml}")
    private String configFilePath;

    private ServerConfig serverConfig;
    private WebClient webClient;

    @PostConstruct
    public void initialize() {
        loadConfiguration();
        initializeWebClient();
    }

    private void loadConfiguration() {
        try {
            // Use Jackson ObjectMapper for better YAML support
            ObjectMapper mapper = new ObjectMapper(new com.fasterxml.jackson.dataformat.yaml.YAMLFactory());
            Resource resource = getConfigResource();
            InputStream inputStream = resource.getInputStream();
            serverConfig = mapper.readValue(inputStream, ServerConfig.class);
            
            // Only show configuration log if not running in MCP mode
            if (!isMcpMode()) {
                System.err.println("✅ Configuration loaded from: " + resource.getDescription());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from: " + configFilePath, e);
        }
    }
    
    private boolean isMcpMode() {
        // Check if running in MCP mode by looking for the system property
        return "none".equals(System.getProperty("spring.main.web-application-type"));
    }

    private Resource getConfigResource() {
        // Check if it's an absolute path or relative path
        if (configFilePath.startsWith("/") || configFilePath.contains(":")) {
            // Absolute path or URL
            return new FileSystemResource(configFilePath);
        } else {
            // Relative path - try classpath first, then filesystem
            try {
                ClassPathResource classPathResource = new ClassPathResource(configFilePath);
                if (classPathResource.exists()) {
                    return classPathResource;
                }
            } catch (Exception e) {
                // Fall through to filesystem
            }
            
            // Try as filesystem resource
            return new FileSystemResource(configFilePath);
        }
    }

    private void initializeWebClient() {
        webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    public Map<String, Object> executeApiCall(ApiToolConfig toolConfig, Map<String, Object> input) {
        try {
            String url = toolConfig.getEndpoint();
            String method = toolConfig.getMethod() != null ? toolConfig.getMethod().toUpperCase() : "GET";

            Object response;
            
            if ("GET".equals(method)) {
                // For GET requests, add query parameters
                if (toolConfig.getQueryParams() != null) {
                    StringBuilder urlBuilder = new StringBuilder(url);
                    urlBuilder.append("?");
                    boolean first = true;
                    for (Map.Entry<String, String> param : toolConfig.getQueryParams().entrySet()) {
                        String paramName = param.getKey();
                        String paramTemplate = param.getValue();
                        String paramValue = substituteTemplate(paramTemplate, input);
                        if (!first) {
                            urlBuilder.append("&");
                        }
                        urlBuilder.append(paramName).append("=").append(paramValue);
                        first = false;
                    }
                    url = urlBuilder.toString();
                }
                response = webClient.get().uri(url)
                        .retrieve()
                        .bodyToMono(Object.class)
                        .block();
            } else {
                // For POST/PUT/PATCH requests, use template
                String body = toolConfig.getTemplate();
                if (body != null) {
                    String requestBody = substituteTemplate(body, input);
                    response = webClient.method(org.springframework.http.HttpMethod.valueOf(method))
                            .uri(url)
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(Object.class)
                            .block();
                } else {
                    response = webClient.method(org.springframework.http.HttpMethod.valueOf(method))
                            .uri(url)
                            .retrieve()
                            .bodyToMono(Object.class)
                            .block();
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            return result;

        } catch (WebClientResponseException e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "API call failed: " + e.getMessage());
            errorResult.put("status", e.getStatusCode().value());
            return errorResult;
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "Unexpected error: " + e.getMessage());
            return errorResult;
        }
    }

    private String substituteTemplate(String template, Map<String, Object> input) {
        String result = template;
        
        // Substitute input parameters
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, String.valueOf(entry.getValue()));
        }
        
        // Substitute environment variables
        while (result.contains("{{env:")) {
            int start = result.indexOf("{{env:");
            int end = result.indexOf("}}", start);
            if (start != -1 && end != -1) {
                String envVar = result.substring(start + 6, end);
                String envValue = System.getenv(envVar);
                if (envValue != null) {
                    result = result.substring(0, start) + envValue + result.substring(end + 2);
                } else {
                    result = result.substring(0, start) + "" + result.substring(end + 2);
                }
            } else {
                break;
            }
        }
        
        return result;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }
}
