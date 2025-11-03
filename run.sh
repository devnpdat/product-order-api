#!/bin/bash

echo "🔧 Fixing and Running Product Order API..."
echo ""

# Step 1: Kill any process on port 8080 and 8081
echo "1️⃣ Checking ports 8080 and 8081..."
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo "   ⚠️  Port 8080 is in use. Killing process..."
    lsof -ti:8080 | xargs kill -9
    sleep 1
fi

if lsof -Pi :8081 -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo "   ⚠️  Port 8081 is in use. Killing process..."
    lsof -ti:8081 | xargs kill -9
    sleep 1
fi
echo "   ✅ Ports cleared"

echo ""
echo "2️⃣ Starting Spring Boot Application..."
echo "   🌐 URL: http://localhost:8081/api/products"
echo "   🗄️  H2 Console: http://localhost:8081/h2-console"
echo "   ⚠️  Press Ctrl+C to stop"
echo ""

# Step 2: Run application
cd /Users/npdat132/Desktop/product-order-api
mvn spring-boot:run

