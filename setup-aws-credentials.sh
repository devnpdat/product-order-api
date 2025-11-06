#!/bin/bash

# Setup AWS Credentials Safely
# This script helps you configure AWS S3 credentials without exposing them in Git

echo "🔐 AWS S3 Credentials Setup"
echo "================================"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if running in git repository
if [ -d .git ]; then
    echo -e "${GREEN}✓${NC} Git repository detected"
else
    echo -e "${RED}✗${NC} Not in a git repository"
    exit 1
fi

# Check .gitignore
if grep -q "application-prod.properties" .gitignore; then
    echo -e "${GREEN}✓${NC} .gitignore configured correctly"
else
    echo -e "${YELLOW}⚠${NC}  Adding application-prod.properties to .gitignore"
    echo "application-prod.properties" >> .gitignore
fi

echo ""
echo "Choose setup method:"
echo "1. Environment Variables (Recommended for Development)"
echo "2. application-prod.properties (Local file, not committed)"
echo "3. AWS CLI Default Credentials (~/.aws/credentials)"
echo ""
read -p "Enter choice [1-3]: " choice

case $choice in
    1)
        echo ""
        echo "Setting up Environment Variables..."
        echo ""
        read -p "AWS Access Key: " access_key
        read -sp "AWS Secret Key: " secret_key
        echo ""
        read -p "S3 Bucket Name: " bucket_name
        read -p "AWS Region [us-east-1]: " region
        region=${region:-us-east-1}

        # Detect shell
        if [ -n "$ZSH_VERSION" ]; then
            SHELL_RC="$HOME/.zshrc"
        else
            SHELL_RC="$HOME/.bashrc"
        fi

        echo ""
        echo "Adding to $SHELL_RC..."

        # Backup
        cp "$SHELL_RC" "$SHELL_RC.backup.$(date +%Y%m%d%H%M%S)"

        # Add to shell config
        cat >> "$SHELL_RC" << EOF

# AWS S3 Credentials for Product Order API
export AWS_S3_ACCESS_KEY="$access_key"
export AWS_S3_SECRET_KEY="$secret_key"
export AWS_S3_BUCKET_NAME="$bucket_name"
export AWS_REGION="$region"
EOF

        echo -e "${GREEN}✓${NC} Environment variables added to $SHELL_RC"
        echo ""
        echo "Run the following command to load the variables:"
        echo -e "${YELLOW}source $SHELL_RC${NC}"
        echo ""
        echo "Or restart your terminal"
        ;;

    2)
        echo ""
        echo "Creating application-prod.properties..."

        read -p "AWS Access Key: " access_key
        read -sp "AWS Secret Key: " secret_key
        echo ""
        read -p "S3 Bucket Name: " bucket_name
        read -p "AWS Region [us-east-1]: " region
        region=${region:-us-east-1}

        cat > src/main/resources/application-prod.properties << EOF
# Production Configuration
# DO NOT COMMIT THIS FILE!

# AWS S3 Configuration
aws.s3.enabled=true
aws.s3.access-key=$access_key
aws.s3.secret-key=$secret_key
aws.s3.bucket-name=$bucket_name
aws.s3.region=$region

# File Upload
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
EOF

        echo -e "${GREEN}✓${NC} Created application-prod.properties"
        echo ""
        echo "To use this configuration, run:"
        echo -e "${YELLOW}mvn spring-boot:run -Dspring-boot.run.profiles=prod${NC}"
        echo "or"
        echo -e "${YELLOW}java -jar target/app.jar --spring.profiles.active=prod${NC}"
        ;;

    3)
        echo ""
        echo "Configuring AWS CLI credentials..."
        echo ""

        if command -v aws &> /dev/null; then
            echo "AWS CLI found. Running 'aws configure'..."
            aws configure
            echo ""
            echo -e "${GREEN}✓${NC} AWS CLI configured"
            echo ""
            echo "Credentials saved to ~/.aws/credentials"
            echo "The application will automatically use these credentials"
        else
            echo -e "${RED}✗${NC} AWS CLI not found"
            echo ""
            echo "Install AWS CLI first:"
            echo "  macOS: brew install awscli"
            echo "  Linux: pip install awscli"
            exit 1
        fi
        ;;

    *)
        echo "Invalid choice"
        exit 1
        ;;
esac

echo ""
echo "================================"
echo -e "${GREEN}✓ Setup Complete!${NC}"
echo ""
echo "Next steps:"
echo "1. Test the connection:"
echo "   mvn spring-boot:run"
echo "   curl http://localhost:8086/api/upload/status"
echo ""
echo "2. Upload a test image:"
echo "   Open test-upload.html in your browser"
echo ""
echo "⚠️  IMPORTANT:"
echo "   - NEVER commit credentials to Git"
echo "   - Keep your AWS keys secure"
echo "   - Rotate keys regularly"
echo ""

