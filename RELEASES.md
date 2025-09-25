# Releases

## v1.0.0 - Initial Release

**Download:** [mcpgateway-1.0.0.jar](https://github.com/andre2k2/mcp-gateway/releases/download/v1.0.0/mcpgateway-1.0.0.jar)

### Features
- ✅ Spring Boot application with REST API and MCP server modes
- ✅ YAML configuration with parameter substitution
- ✅ Multiple configuration file support via environment variables
- ✅ Examples for weather, GitHub, and JSONPlaceholder APIs
- ✅ Easy deployment script (`run.sh`)
- ✅ Support for GET and POST requests
- ✅ Parameter validation and default values
- ✅ Authentication via environment variables
- ✅ Custom timeouts for API calls

### Quick Start

1. **Download the JAR:**
   ```bash
   wget https://github.com/andre2k2/mcp-gateway/releases/download/v1.0.0/mcpgateway-1.0.0.jar
   ```

2. **Run as REST API server:**
   ```bash
   java -jar mcpgateway-1.0.0.jar
   ```

3. **Run as MCP server:**
   ```bash
   java -jar mcpgateway-1.0.0.jar --mcp
   ```

4. **Use custom configuration:**
   ```bash
   MCP_CONFIG_FILE=my-config.yaml java -jar mcpgateway-1.0.0.jar
   ```

### Claude Desktop Integration

Add to your `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "api-gateway": {
      "command": "java",
      "args": ["-jar", "/path/to/mcpgateway-1.0.0.jar", "--mcp"],
      "env": {
        "MCP_CONFIG_FILE": "my-config.yaml"
      }
    }
  }
}
```

### Requirements
- Java 21 or higher
- Maven (for building from source)

### Documentation
- [README.md](README.md) - Complete documentation
- [API Configuration Examples](src/main/resources/api-config.yaml)
- [Custom Configuration Example](custom-config.yaml)
