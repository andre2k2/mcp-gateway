# API Gateway MCP Server

Este é um servidor genérico para o Model Context Protocol (MCP) que permite facilmente envolver APIs REST como ferramentas MCP que podem ser acessadas pelo Claude e outros clientes MCP.

## Características

* Configuração YAML fácil para múltiplos endpoints de API
* Suporte para requisições GET e POST
* Validação de parâmetros e valores padrão
* Autenticação via variáveis de ambiente
* Timeouts customizáveis para chamadas de API
* Integração com Spring Boot e Spring AI

## Uso

1. Configure os endpoints de API no arquivo `src/main/resources/api-config.yaml`
2. Configure qualquer token de API necessário como variáveis de ambiente
3. Execute o servidor:

```bash
mvn spring-boot:run
```

## Formato de Configuração

O arquivo de configuração usa formato YAML com a seguinte estrutura:

```yaml
# Informações do servidor
server:
  name: "API Gateway MCP"
  description: "Generic API gateway that wraps REST APIs as MCP tools"
  version: "1.0.0"

# Autenticação
auth:
  token_env_var: "API_GATEWAY_TOKEN"  # Variável de ambiente para o token da API

# Definições de ferramentas
tools:
  - name: "nome-da-ferramenta"
    description: "Descrição da ferramenta"
    endpoint: "https://api.exemplo.com/endpoint"
    method: "POST"  # ou "GET"
    timeout: 30  # em segundos
    template: |
      {
        "param1": "{{variavel1}}",
        "param2": {{variavel2}}
      }
    # Para requisições GET, use query_params
    query_params:
      param1: "{{variavel1}}"
      param2: "{{variavel2}}"
    parameters:
      variavel1:
        type: "string"
        description: "Descrição da variavel1"
        required: true
      variavel2:
        type: "number"
        description: "Descrição da variavel2"
        default: 10
```

## Configuração de Arquivos Personalizados

Por padrão, o servidor usa o arquivo `src/main/resources/api-config.yaml`. Você pode especificar um arquivo de configuração personalizado de várias formas:

### 1. Variável de Ambiente (Recomendado)
```bash
export MCP_CONFIG_FILE="/caminho/para/seu/arquivo.yaml"
java -jar mcpgateway-0.0.1-SNAPSHOT.jar --mcp
```

### 2. Propriedade do Spring Boot
```bash
java -jar mcpgateway-0.0.1-SNAPSHOT.jar --mcp.config.file=/caminho/para/seu/arquivo.yaml
```

### 3. Usando o Script run.sh
```bash
MCP_CONFIG_FILE="/caminho/para/seu/arquivo.yaml" ./run.sh
```

### 4. Modificação do application.yml
Edite o arquivo `src/main/resources/application.yml`:
```yaml
mcp:
  config:
    file: /caminho/para/seu/arquivo.yaml
```

### Tipos de Caminho Suportados

- **Caminho absoluto**: `/caminho/completo/arquivo.yaml`
- **Caminho relativo**: `meu-arquivo.yaml` (busca primeiro no classpath, depois no filesystem)
- **URL**: `file://caminho/arquivo.yaml`

### Exemplos Práticos

```bash
# Usando arquivo personalizado
cp custom-config.yaml /home/usuario/minha-config.yaml
MCP_CONFIG_FILE="/home/usuario/minha-config.yaml" java -jar target/mcpgateway-0.0.1-SNAPSHOT.jar --mcp

# Usando arquivo relativo
cp custom-config.yaml minha-config.yaml
MCP_CONFIG_FILE="minha-config.yaml" java -jar target/mcpgateway-0.0.1-SNAPSHOT.jar --mcp

# Modo REST API com arquivo personalizado
MCP_CONFIG_FILE="/caminho/para/config.yaml" java -jar target/mcpgateway-0.0.1-SNAPSHOT.jar
```

## Integração com Claude Desktop

Para usar com Claude Desktop, adicione o seguinte ao seu `claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "api-gateway": {
      "command": "java",
      "args": ["-jar", "/caminho/para/mcpgateway-0.0.1-SNAPSHOT.jar", "--mcp"],
      "env": {
        "API_GATEWAY_TOKEN": "seu-token-da-api"
      }
    }
  }
}
```

**Localização do arquivo de configuração:**
- **Windows**: `%APPDATA%\Claude\claude_desktop_config.json`
- **macOS**: `~/Library/Application Support/Claude/claude_desktop_config.json`
- **Linux**: `~/.config/claude/claude_desktop_config.json`

### Script de Configuração Automática

Use o script `run.sh` para facilitar a configuração:

```bash
./run.sh
```

O script oferece opções para:
1. Executar como servidor REST API
2. Executar como servidor MCP
3. Mostrar configuração para Claude Desktop
4. Testar endpoints REST

## Exemplos

O arquivo `src/main/resources/api-config.yaml` contém exemplos de configurações de API, incluindo:

- **weather-lookup**: Busca informações meteorológicas usando OpenWeatherMap API
- **github-user-info**: Obtém informações de usuário do GitHub
- **jsonplaceholder-post**: Cria um novo post usando JSONPlaceholder API
- **httpbin-get**: Testa requisições GET usando httpbin.org

## Variáveis de Ambiente

* Configure o token de autenticação principal usando a variável de ambiente especificada no campo `auth.token_env_var`.
* Você também pode referenciar outras variáveis de ambiente em seus templates usando a sintaxe `{{env:NOME_DA_VARIAVEL}}`.

## Endpoints REST

O servidor também expõe endpoints REST para gerenciar as ferramentas:

- `GET /api/server/info` - Informações do servidor
- `GET /api/tools` - Lista todas as ferramentas disponíveis
- `GET /api/tools/{toolName}` - Informações de uma ferramenta específica
- `POST /api/tools/{toolName}/execute` - Executa uma ferramenta
- `GET /api/health` - Status de saúde do servidor

## Tecnologias Utilizadas

- Spring Boot 3.3.5
- Spring AI 1.0.0-M1
- Spring WebFlux
- SnakeYAML
- Java 21

## Desenvolvimento

Para executar em modo de desenvolvimento:

```bash
mvn spring-boot:run
```

O servidor estará disponível em `http://localhost:8080`.
