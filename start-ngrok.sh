#!/bin/bash

# Script para iniciar Docker Compose con ngrok

echo "🚀 Iniciando servicios con ngrok..."
docker-compose up -d

echo "⏳ Esperando 10 segundos para que ngrok se conecte..."
sleep 10

echo ""
echo "📡 URLs públicas de ngrok:"
echo "============================================"
echo ""
echo "🌐 FRONTEND:"
docker logs licoreria-ngrok-frontend 2>/dev/null | grep "started tunnel" | tail -1 | grep -oP 'url=\K[^ ]*'
echo ""
echo "⚙️  BACKEND:"
docker logs licoreria-ngrok-backend 2>/dev/null | grep "started tunnel" | tail -1 | grep -oP 'url=\K[^ ]*'
echo ""
echo "============================================"
echo "📊 Dashboards:"
echo "  Frontend: http://localhost:4040"
echo "  Backend:  http://localhost:4041"
echo ""
echo "Para ver logs en vivo:"
echo "  docker logs licoreria-ngrok-frontend -f"
echo "  docker logs licoreria-ngrok-backend -f"


