package com.github.mcpgateway.controller;

import com.github.mcpgateway.model.ApiToolConfig;
import com.github.mcpgateway.model.ServerConfig;
import com.github.mcpgateway.service.ApiWrapperMcpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiWrapperController {

    @Autowired
    private ApiWrapperMcpService apiWrapperMcpService;

    @GetMapping("/server/info")
    public ResponseEntity<Map<String, Object>> getServerInfo() {
        ServerConfig config = apiWrapperMcpService.getServerConfig();
        Map<String, Object> info = new HashMap<>();
        
        if (config != null && config.getServer() != null) {
            info.put("name", config.getServer().getName());
            info.put("description", config.getServer().getDescription());
            info.put("version", config.getServer().getVersion());
        } else {
            info.put("name", "API Gateway MCP");
            info.put("description", "Generic API gateway that wraps REST APIs as MCP tools");
            info.put("version", "1.0.0");
        }
        
        return ResponseEntity.ok(info);
    }

    @GetMapping("/tools")
    public ResponseEntity<List<ApiToolConfig>> getTools() {
        ServerConfig config = apiWrapperMcpService.getServerConfig();
        if (config != null) {
            return ResponseEntity.ok(config.getTools());
        }
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/tools/{toolName}")
    public ResponseEntity<ApiToolConfig> getTool(@PathVariable String toolName) {
        ServerConfig config = apiWrapperMcpService.getServerConfig();
        if (config != null && config.getTools() != null) {
            return config.getTools().stream()
                    .filter(tool -> tool.getName().equals(toolName))
                    .findFirst()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/tools/{toolName}/execute")
    public ResponseEntity<Map<String, Object>> executeTool(
            @PathVariable String toolName,
            @RequestBody Map<String, Object> parameters) {
        
        ServerConfig config = apiWrapperMcpService.getServerConfig();
        if (config != null && config.getTools() != null) {
            ApiToolConfig toolConfig = config.getTools().stream()
                    .filter(tool -> tool.getName().equals(toolName))
                    .findFirst()
                    .orElse(null);
            
            if (toolConfig != null) {
                // Execute the tool using the service
                Map<String, Object> result = apiWrapperMcpService.executeApiCall(toolConfig, parameters);
                return ResponseEntity.ok(result);
            }
        }
        
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("success", false);
        errorResult.put("error", "Tool not found: " + toolName);
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "API Gateway MCP");
        return ResponseEntity.ok(health);
    }
}
