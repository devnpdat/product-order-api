#!/bin/bash

echo "🔧 Fixing IntelliJ IDEA Maven Dependencies Issue..."
echo "=================================================="
echo ""

# Navigate to project directory
cd "$(dirname "$0")"

echo "📍 Current directory: $(pwd)"
echo ""

# Step 1: Clean Maven
echo "🧹 Step 1: Cleaning Maven project..."
mvn clean
echo ""

# Step 2: Force update dependencies
echo "📦 Step 2: Force updating all dependencies..."
mvn dependency:purge-local-repository -DreResolve=true
echo ""

# Step 3: Download dependencies
echo "⬇️  Step 3: Downloading all dependencies..."
mvn dependency:resolve dependency:resolve-plugins
echo ""

# Step 4: Compile
echo "🔨 Step 4: Compiling project..."
mvn compile
echo ""

# Step 5: List dependencies
echo "📋 Step 5: Listing dependencies..."
mvn dependency:tree | head -30
echo ""

echo "✅ Maven dependencies fixed!"
echo ""
echo "📌 Next steps in IntelliJ IDEA:"
echo "   1. Open Maven tool window (View → Tool Windows → Maven)"
echo "   2. Click 🔄 'Reload All Maven Projects'"
echo "   3. File → Invalidate Caches → Invalidate and Restart"
echo "   4. Wait for indexing to complete"
echo "   5. Build → Rebuild Project"
echo ""
echo "✨ Done! Check FIX_DEPENDENCIES.md for detailed instructions."

