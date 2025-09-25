#!/bin/bash

# Script para configurar e executar o MCP Gateway

echo "🚀 MCP Gateway Configuration Script"
echo "=================================="

# Verificar se o JAR existe
JAR_FILE="target/mcpgateway-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ JAR file not found. Building project..."
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "❌ Build failed!"
        exit 1
    fi
fi

echo "✅ JAR file found: $JAR_FILE"

# Verificar arquivo de configuração
CONFIG_FILE="${MCP_CONFIG_FILE:-api-config.yaml}"
if [ ! -f "$CONFIG_FILE" ] && [ ! -f "src/main/resources/$CONFIG_FILE" ]; then
    echo "⚠️  Config file not found: $CONFIG_FILE"
    echo "   Using default configuration from classpath"
else
    echo "✅ Config file found: $CONFIG_FILE"
fi

# Mostrar opções
echo ""
echo "Escolha o modo de execução:"
echo "1) Servidor REST API (padrão)"
echo "2) Servidor MCP (para Claude Desktop)"
echo "3) Mostrar configuração para Claude Desktop"
echo "4) Testar endpoints REST"
echo ""
read -p "Digite sua escolha (1-4): " choice

case $choice in
    1)
        echo "🌐 Iniciando servidor REST API..."
        echo "   Config file: $CONFIG_FILE"
        MCP_CONFIG_FILE="$CONFIG_FILE" java -jar "$JAR_FILE"
        ;;
    2)
        echo "🤖 Iniciando servidor MCP..."
        echo "   Config file: $CONFIG_FILE"
        MCP_CONFIG_FILE="$CONFIG_FILE" java -jar "$JAR_FILE" --mcp
        ;;
    3)
        echo "📋 Configuração para Claude Desktop:"
        echo ""
        echo "Adicione o seguinte ao seu arquivo claude_desktop_config.json:"
        echo ""
        echo "{"
        echo "  \"mcpServers\": {"
        echo "    \"api-gateway\": {"
        echo "      \"command\": \"java\","
        echo "      \"args\": [\"-jar\", \"$(pwd)/$JAR_FILE\", \"--mcp\"],"
        echo "      \"env\": {"
        echo "        \"API_GATEWAY_TOKEN\": \"seu-token-opcional\""
        echo "      }"
        echo "    }"
        echo "  }"
        echo "}"
        echo ""
        echo "Localização do arquivo de configuração:"
        echo "- Windows: %APPDATA%\\Claude\\claude_desktop_config.json"
        echo "- macOS: ~/Library/Application Support/Claude/claude_desktop_config.json"
        echo "- Linux: ~/.config/claude/claude_desktop_config.json"
        ;;
    4)
        echo "🧪 Testando endpoints REST..."
        echo ""
        echo "Iniciando servidor em background..."
        echo "   Config file: $CONFIG_FILE"
        MCP_CONFIG_FILE="$CONFIG_FILE" java -jar "$JAR_FILE" &
        SERVER_PID=$!
        
        # Aguardar servidor iniciar
        sleep 5
        
        echo "Testando endpoints:"
        echo ""
        
        echo "1. Informações do servidor:"
        curl -s http://localhost:8080/api/server/info | jq . 2>/dev/null || curl -s http://localhost:8080/api/server/info
        echo ""
        
        echo "2. Lista de ferramentas:"
        curl -s http://localhost:8080/api/tools | jq . 2>/dev/null || curl -s http://localhost:8080/api/tools
        echo ""
        
        echo "3. Status de saúde:"
        curl -s http://localhost:8080/api/health | jq . 2>/dev/null || curl -s http://localhost:8080/api/health
        echo ""
        
        echo "4. Testando ferramenta github-user-info:"
        curl -s -X POST http://localhost:8080/api/tools/github-user-info/execute \
             -H "Content-Type: application/json" \
             -d '{"username": "octocat"}' | jq . 2>/dev/null || curl -s -X POST http://localhost:8080/api/tools/github-user-info/execute \
             -H "Content-Type: application/json" \
             -d '{"username": "octocat"}'
        echo ""
        
        # Parar servidor
        kill $SERVER_PID
        echo "✅ Testes concluídos!"
        ;;
    *)
        echo "❌ Opção inválida!"
        exit 1
        ;;
esac
