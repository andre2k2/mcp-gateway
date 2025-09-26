# Guia de Configuração do MCP Server

Este guia ensina como configurar o MCP Gateway Server em diferentes plataformas e clientes MCP.

## Índice

- [Pré-requisitos](#pré-requisitos)
- [Instalação e Build](#instalação-e-build)
- [Configuração do Servidor](#configuração-do-servidor)
- [Configuração por Plataforma](#configuração-por-plataforma)
  - [Claude Desktop](#claude-desktop)
  - [Amazon Q](#amazon-q)
  - [Gemini CLI](#gemini-cli)
  - [Outros Clientes MCP](#outros-clientes-mcp)
- [Configuração de APIs](#configuração-de-apis)
- [Troubleshooting](#troubleshooting)

## Pré-requisitos

- **Java 21+** instalado
- **Maven 3.6+** (para build)
- **Git** (para clonar o repositório)
- **Token de API** (dependendo das APIs que você quer usar)

## Instalação e Build

### 1. Clone o repositório

```bash
git clone https://github.com/andre2k2/mcp-gateway.git
cd mcp-gateway
```

### 2. Build do projeto

```bash
mvn clean package
```

### 3. Verificar se o JAR foi criado

```bash
ls -la target/mcpgateway-*.jar
```

## Configuração do Servidor

### 1. Configurar variáveis de ambiente

```bash
# Token principal do gateway (opcional)
export API_GATEWAY_TOKEN="seu-token-principal"

# Tokens específicos das APIs (exemplo: OpenWeatherMap)
export OPENWEATHER_API_KEY="sua-chave-openweather"

# Arquivo de configuração personalizado (opcional)
export MCP_CONFIG_FILE="/caminho/para/sua-config.yaml"
```

### 2. Testar o servidor

```bash
# Modo REST API
java -jar target/mcpgateway-*.jar

# Modo MCP Server
java -jar target/mcpgateway-*.jar --mcp
```

## Configuração por Plataforma

### Claude Desktop

#### 1. Localizar o arquivo de configuração

**Windows:**

```text
%APPDATA%\Claude\claude_desktop_config.json
```

**macOS:**

```text
~/Library/Application Support/Claude/claude_desktop_config.json
```

**Linux:**

```text
~/.config/claude/claude_desktop_config.json
```

#### 2. Configurar o arquivo JSON

```json
{
  "mcpServers": {
    "api-gateway": {
      "command": "java",
      "args": [
        "-jar",
        "/caminho/completo/para/target/mcpgateway-0.0.1-SNAPSHOT.jar",
        "--mcp"
      ],
      "env": {
        "API_GATEWAY_TOKEN": "seu-token-principal",
        "OPENWEATHER_API_KEY": "sua-chave-openweather"
      }
    }
  }
}
```

#### 3. Exemplo completo para Windows

```json
{
  "mcpServers": {
    "api-gateway": {
      "command": "java",
      "args": [
        "-jar",
        "C:\\Users\\SeuUsuario\\mcp-gateway\\target\\mcpgateway-0.0.1-SNAPSHOT.jar",
        "--mcp"
      ],
      "env": {
        "OPENWEATHER_API_KEY": "sua-chave-aqui"
      }
    }
  }
}
```

### Amazon Q

#### 1. Configurar via AWS CLI

```bash
# Instalar AWS CLI se não tiver
pip install awscli

# Configurar credenciais
aws configure
```

#### 2. Criar arquivo de configuração para Amazon Q

```json
{
  "mcpServers": {
    "api-gateway": {
      "command": "java",
      "args": [
        "-jar",
        "/caminho/para/mcpgateway-0.0.1-SNAPSHOT.jar",
        "--mcp"
      ],
      "env": {
        "AWS_ACCESS_KEY_ID": "sua-access-key",
        "AWS_SECRET_ACCESS_KEY": "sua-secret-key",
        "AWS_DEFAULT_REGION": "us-east-1",
        "OPENWEATHER_API_KEY": "sua-chave-openweather"
      }
    }
  }
}
```

#### 3. Usar com Amazon Q Developer

1. Abra o Amazon Q Developer
2. Vá para Settings > MCP Servers
3. Adicione a configuração acima
4. Reinicie o Amazon Q Developer

### Gemini CLI

#### 1. Instalar Gemini CLI

```bash
# Via npm
npm install -g @google/generative-ai-cli

# Ou via pip
pip install google-generativeai
```

#### 2. Configurar arquivo de configuração

Crie o arquivo `~/.gemini/mcp_config.json`:

```json
{
  "mcpServers": {
    "api-gateway": {
      "command": "java",
      "args": [
        "-jar",
        "/caminho/para/mcpgateway-0.0.1-SNAPSHOT.jar",
        "--mcp"
      ],
      "env": {
        "GOOGLE_API_KEY": "sua-chave-google",
        "OPENWEATHER_API_KEY": "sua-chave-openweather"
      }
    }
  }
}
```

#### 3. Usar com Gemini CLI

```bash
# Executar com MCP
gemini --mcp-config ~/.gemini/mcp_config.json "Qual é o clima em São Paulo?"
```

### Outros Clientes MCP

#### Cursor IDE

1. Abra as configurações do Cursor
2. Procure por "MCP" ou "Model Context Protocol"
3. Adicione a configuração do servidor:

```json
{
  "mcpServers": {
    "api-gateway": {
      "command": "java",
      "args": ["-jar", "/caminho/para/mcpgateway-0.0.1-SNAPSHOT.jar", "--mcp"],
      "env": {
        "OPENWEATHER_API_KEY": "sua-chave"
      }
    }
  }
}
```

#### Continue.dev

1. Abra o Continue.dev
2. Vá para Settings > Models
3. Adicione a configuração MCP:

```json
{
  "mcpServers": {
    "api-gateway": {
      "command": "java",
      "args": ["-jar", "/caminho/para/mcpgateway-0.0.1-SNAPSHOT.jar", "--mcp"],
      "env": {
        "OPENWEATHER_API_KEY": "sua-chave"
      }
    }
  }
}
```

## Configuração de APIs

### 1. Arquivo de configuração padrão

O arquivo `src/main/resources/api-config.yaml` contém a configuração das APIs:

```yaml
# Server info
server:
  name: "API Gateway MCP"
  description: "Generic API gateway that wraps REST APIs as MCP tools"
  version: "1.0.0"

# Authentication
auth:
  token_env_var: "API_GATEWAY_TOKEN"

# Tool definitions
tools:
  - name: "weather-lookup"
    description: "Get current weather information for a city"
    endpoint: "https://api.openweathermap.org/data/2.5/weather"
    method: "GET"
    timeout: 30
    query_params:
      q: "{{city}}"
      appid: "{{env:OPENWEATHER_API_KEY}}"
      units: "metric"
    parameters:
      city:
        type: "string"
        description: "Name of the city to get weather for"
        required: true
```

### 2. Adicionar novas APIs

Para adicionar uma nova API, edite o arquivo `api-config.yaml`:

```yaml
tools:
  - name: "minha-api"
    description: "Descrição da minha API"
    endpoint: "https://api.exemplo.com/endpoint"
    method: "POST"
    timeout: 30
    template: |
      {
        "param1": "{{valor1}}",
        "param2": "{{valor2}}"
      }
    parameters:
      valor1:
        type: "string"
        description: "Descrição do parâmetro"
        required: true
      valor2:
        type: "number"
        description: "Outro parâmetro"
        required: false
        default: 10
```

### 3. Usar arquivo de configuração personalizado

```bash
# Criar arquivo personalizado
cp src/main/resources/api-config.yaml minha-config.yaml

# Editar o arquivo
nano minha-config.yaml

# Usar com o servidor
MCP_CONFIG_FILE="minha-config.yaml" java -jar target/mcpgateway-*.jar --mcp
```

## Troubleshooting

### Problemas Comuns

#### 1. Erro "Java not found"

```bash
# Verificar se Java está instalado
java -version

# Instalar Java 21+ se necessário
# Ubuntu/Debian:
sudo apt update
sudo apt install openjdk-21-jdk

# macOS:
brew install openjdk@21

# Windows: Baixar do site oficial da Oracle
```

#### 2. Erro "Port already in use"

```bash
# Verificar qual processo está usando a porta 8080
lsof -i :8080

# Matar o processo
kill -9 <PID>

# Ou usar porta diferente
java -jar target/mcpgateway-*.jar --server.port=8081
```

#### 3. Erro de permissão no arquivo de configuração

```bash
# Dar permissão de leitura
chmod 644 claude_desktop_config.json

# Verificar se o arquivo existe
ls -la ~/.config/claude/claude_desktop_config.json
```

#### 4. Erro de variáveis de ambiente

```bash
# Verificar se as variáveis estão definidas
echo $OPENWEATHER_API_KEY
echo $API_GATEWAY_TOKEN

# Definir variáveis temporariamente
export OPENWEATHER_API_KEY="sua-chave-aqui"
export API_GATEWAY_TOKEN="seu-token-aqui"
```

### Logs e Debug

#### 1. Habilitar logs detalhados

```bash
# Executar com logs DEBUG
java -jar target/mcpgateway-*.jar --mcp --logging.level.com.github.mcpgateway=DEBUG
```

#### 2. Verificar se o servidor está funcionando

```bash
# Testar endpoint de saúde
curl http://localhost:8080/api/health

# Listar ferramentas disponíveis
curl http://localhost:8080/api/tools
```

#### 3. Testar execução de ferramenta

```bash
# Testar weather-lookup
curl -X POST http://localhost:8080/api/tools/weather-lookup/execute \
  -H "Content-Type: application/json" \
  -d '{"city": "São Paulo"}'
```

### Verificação de Configuração

#### 1. Script de verificação

Crie um arquivo `check-config.sh`:

```bash
#!/bin/bash

echo "=== Verificação da Configuração MCP ==="

# Verificar Java
echo "Java version:"
java -version

# Verificar JAR
echo -e "\nJAR file:"
ls -la target/mcpgateway-*.jar

# Verificar variáveis de ambiente
echo -e "\nVariáveis de ambiente:"
echo "OPENWEATHER_API_KEY: ${OPENWEATHER_API_KEY:-'NÃO DEFINIDA'}"
echo "API_GATEWAY_TOKEN: ${API_GATEWAY_TOKEN:-'NÃO DEFINIDA'}"

# Testar servidor
echo -e "\nTestando servidor..."
timeout 10s java -jar target/mcpgateway-*.jar --mcp &
SERVER_PID=$!
sleep 5

if kill -0 $SERVER_PID 2>/dev/null; then
    echo "✅ Servidor iniciou com sucesso"
    kill $SERVER_PID
else
    echo "❌ Erro ao iniciar servidor"
fi

echo -e "\n=== Verificação concluída ==="
```

```bash
chmod +x check-config.sh
./check-config.sh
```

## Recursos Adicionais

- [Documentação oficial do MCP](https://modelcontextprotocol.io/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [OpenWeatherMap API](https://openweathermap.org/api)
- [GitHub do projeto](https://github.com/andre2k2/mcp-gateway)

## Suporte

Se encontrar problemas:

1. Verifique os logs do servidor
2. Confirme se todas as variáveis de ambiente estão definidas
3. Teste a conectividade com as APIs externas
4. Verifique se o arquivo de configuração está no formato correto

Para reportar bugs ou solicitar features, abra uma issue no [GitHub do projeto](https://github.com/andre2k2/mcp-gateway/issues).
