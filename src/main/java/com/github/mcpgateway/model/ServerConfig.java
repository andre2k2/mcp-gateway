package com.github.mcpgateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class ServerConfig {
    private ServerInfo server;
    private AuthConfig auth;
    private List<ApiToolConfig> tools;

    // Constructors
    public ServerConfig() {}

    // Getters and Setters
    public ServerInfo getServer() {
        return server;
    }

    public void setServer(ServerInfo server) {
        this.server = server;
    }

    public AuthConfig getAuth() {
        return auth;
    }

    public void setAuth(AuthConfig auth) {
        this.auth = auth;
    }

    public List<ApiToolConfig> getTools() {
        return tools;
    }

    public void setTools(List<ApiToolConfig> tools) {
        this.tools = tools;
    }

    public static class ServerInfo {
        private String name;
        private String description;
        private String version;

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

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    public static class AuthConfig {
        @JsonProperty("token_env_var")
        private String tokenEnvVar;

        // Getters and Setters
        public String getTokenEnvVar() {
            return tokenEnvVar;
        }

        public void setTokenEnvVar(String tokenEnvVar) {
            this.tokenEnvVar = tokenEnvVar;
        }
    }
}
