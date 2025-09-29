package com.github.mcpgateway.mcp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mcpgateway.model.ApiToolConfig;
import com.github.mcpgateway.model.ServerConfig;
import com.github.mcpgateway.service.ApiWrapperMcpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
public class McpServer {
    
    @Autowired
    private ApiWrapperMcpService apiWrapperMcpService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Scanner scanner = new Scanner(System.in);
    
    public void start() {
        try {
            // Initialize MCP handshake
            initializeMcpConnection();
            
            // Main message loop
            while (true) {
                String line = scanner.nextLine();
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    JsonNode message = objectMapper.readTree(line);
                    handleMessage(message);
                } catch (Exception e) {
                    sendError("Invalid JSON: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            sendError("MCP Server error: " + e.getMessage());
        }
    }
    
    private void initializeMcpConnection() {
        // Send initialize response
        Map<String, Object> response = new HashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("id", 1);
        
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", "2024-11-05");
        result.put("capabilities", createCapabilities());
        result.put("serverInfo", createServerInfo());
        
        response.put("result", result);
        
        sendMessage(response);
    }
    
    private Map<String, Object> createCapabilities() {
        Map<String, Object> capabilities = new HashMap<>();
        
        Map<String, Object> tools = new HashMap<>();
        tools.put("listChanged", true);
        capabilities.put("tools", tools);
        
        return capabilities;
    }
    
    private Map<String, Object> createServerInfo() {
        ServerConfig config = apiWrapperMcpService.getServerConfig();
        Map<String, Object> serverInfo = new HashMap<>();
        
        if (config != null && config.getServer() != null) {
            serverInfo.put("name", config.getServer().getName());
            serverInfo.put("version", config.getServer().getVersion());
        } else {
            serverInfo.put("name", "API Gateway MCP");
            serverInfo.put("version", "1.0.0");
        }
        
        return serverInfo;
    }
    
    private void handleMessage(JsonNode message) {
        String method = message.get("method").asText();
        
        switch (method) {
            case "tools/list":
                handleToolsList(message);
                break;
            case "tools/call":
                handleToolsCall(message);
                break;
            case "ping":
                handlePing(message);
                break;
            default:
                sendError("Unknown method: " + method);
        }
    }
    
    private void handleToolsList(JsonNode message) {
        ServerConfig config = apiWrapperMcpService.getServerConfig();
        List<Map<String, Object>> tools = new ArrayList<>();
        
        if (config != null && config.getTools() != null) {
            for (ApiToolConfig toolConfig : config.getTools()) {
                Map<String, Object> tool = new HashMap<>();
                tool.put("name", toolConfig.getName());
                tool.put("description", toolConfig.getDescription());
                tool.put("inputSchema", createInputSchema(toolConfig));
                tools.add(tool);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("id", message.get("id"));
        
        Map<String, Object> result = new HashMap<>();
        result.put("tools", tools);
        response.put("result", result);
        
        sendMessage(response);
    }
    
    private void handleToolsCall(JsonNode message) {
        JsonNode params = message.get("params");
        String toolName = params.get("name").asText();
        JsonNode arguments = params.get("arguments");
        
        ServerConfig config = apiWrapperMcpService.getServerConfig();
        ApiToolConfig toolConfig = null;
        
        if (config != null && config.getTools() != null) {
            toolConfig = config.getTools().stream()
                    .filter(tool -> tool.getName().equals(toolName))
                    .findFirst()
                    .orElse(null);
        }
        
        if (toolConfig == null) {
            sendError("Tool not found: " + toolName);
            return;
        }
        
        // Convert arguments to Map
        Map<String, Object> input = new HashMap<>();
        if (arguments != null) {
            arguments.fields().forEachRemaining(entry -> {
                input.put(entry.getKey(), entry.getValue());
            });
        }
        
        // Execute the tool
        Map<String, Object> result = apiWrapperMcpService.executeApiCall(toolConfig, input);
        
        Map<String, Object> response = new HashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("id", message.get("id"));
        response.put("result", result);
        
        sendMessage(response);
    }
    
    private void handlePing(JsonNode message) {
        Map<String, Object> response = new HashMap<>();
        response.put("jsonrpc", "2.0");
        response.put("id", message.get("id"));
        response.put("result", new HashMap<>());
        
        sendMessage(response);
    }
    
    private Map<String, Object> createInputSchema(ApiToolConfig toolConfig) {
        Map<String, Object> schema = new HashMap<>();
        schema.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();
        
        if (toolConfig.getParameters() != null) {
            for (Map.Entry<String, com.github.mcpgateway.model.ParameterConfig> entry : toolConfig.getParameters().entrySet()) {
                String paramName = entry.getKey();
                com.github.mcpgateway.model.ParameterConfig paramConfig = entry.getValue();
                
                Map<String, Object> paramSchema = new HashMap<>();
                paramSchema.put("type", paramConfig.getType());
                paramSchema.put("description", paramConfig.getDescription());
                
                if (paramConfig.getDefaultValue() != null) {
                    paramSchema.put("default", paramConfig.getDefaultValue());
                }
                
                properties.put(paramName, paramSchema);
                
                if (paramConfig.isRequired()) {
                    required.add(paramName);
                }
            }
        }
        
        schema.put("properties", properties);
        schema.put("required", required);
        
        return schema;
    }
    
    private void sendMessage(Map<String, Object> message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            System.out.println(json);
            System.out.flush();
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    
    private void sendError(String error) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("jsonrpc", "2.0");
        errorResponse.put("error", Map.of("message", error));
        
        sendMessage(errorResponse);
    }
}
