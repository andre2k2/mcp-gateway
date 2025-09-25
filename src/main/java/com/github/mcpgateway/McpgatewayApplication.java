package com.github.mcpgateway;

import com.github.mcpgateway.mcp.McpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpgatewayApplication {

    @Autowired
    private McpServer mcpServer;

    public static void main(String[] args) {
        // Check if running in MCP mode
        if (args.length > 0 && "--mcp".equals(args[0])) {
            System.setProperty("spring.main.web-application-type", "none");
        }
        
        SpringApplication.run(McpgatewayApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            // Check if running in MCP mode
            if (args.length > 0 && "--mcp".equals(args[0])) {
                System.err.println("Starting MCP Server...");
                mcpServer.start();
            } else {
                System.err.println("Starting REST API Server...");
                System.err.println("Available endpoints:");
                System.err.println("  GET  /api/server/info");
                System.err.println("  GET  /api/tools");
                System.err.println("  GET  /api/tools/{toolName}");
                System.err.println("  POST /api/tools/{toolName}/execute");
                System.err.println("  GET  /api/health");
                System.err.println("");
                System.err.println("To run as MCP server, use: java -jar mcpgateway-0.0.1-SNAPSHOT.jar --mcp");
            }
        };
    }
}