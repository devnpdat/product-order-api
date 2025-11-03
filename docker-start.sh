#!/bin/bash

echo "🐳 DOCKER COMPOSE QUICK START"
echo "=============================="
echo ""

cd /Users/npdat132/Desktop/product-order-api

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running!"
    echo "   Please start Docker Desktop first."
    exit 1
fi

echo "✅ Docker is running"
echo ""

# Start infrastructure only (MySQL, Redis, Elasticsearch)
echo "Starting infrastructure services..."
echo "This will take 1-2 minutes..."
echo ""

docker compose up -d mysql redis elasticsearch kibana

echo ""
echo "⏳ Waiting for services to be healthy..."
echo ""

# Wait for MySQL
echo -n "MySQL: "
until docker compose exec mysql mysqladmin ping -h localhost --silent 2>/dev/null; do
    echo -n "."
    sleep 2
done
echo " ✅ Ready"

# Wait for Redis
echo -n "Redis: "
until docker compose exec redis redis-cli -a redispassword ping 2>/dev/null | grep -q PONG; do
    echo -n "."
    sleep 2
done
echo " ✅ Ready"

# Wait for Elasticsearch
echo -n "Elasticsearch: "
until curl -s http://localhost:9200/_cluster/health 2>/dev/null | grep -q '"status":"green"\|"status":"yellow"'; do
    echo -n "."
    sleep 5
done
echo " ✅ Ready"

echo ""
echo "✅ All infrastructure services are ready!"
echo ""
echo "📊 Service URLs:"
echo "   MySQL:         localhost:3306 (user: orderuser, pass: orderpassword)"
echo "   Redis:         localhost:6379 (pass: redispassword)"
echo "   Elasticsearch: http://localhost:9200"
echo "   Kibana:        http://localhost:5601"
echo ""
echo "🚀 Now you can:"
echo "   1. Run app from IntelliJ with profile 'prod'"
echo "   2. Or run: ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod"
echo "   3. Or build and run app container: docker compose up -d app"
echo ""

