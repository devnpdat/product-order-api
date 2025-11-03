#!/bin/bash

# Pre-commit hook to prevent committing sensitive data
# This will check for AWS credentials and other secrets

echo "🔍 Checking for sensitive data..."

# Patterns to check
PATTERNS=(
    "aws.s3.access-key=AKIA[A-Z0-9]{16}"
    "aws.s3.secret-key=[A-Za-z0-9/+=]{40}"
    "password=.*[^=][A-Za-z0-9]"
    "AKIA[A-Z0-9]{16}"
    "secret.*=.*[A-Za-z0-9/+=]{20,}"
)

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Check staged files
FOUND_SECRET=0

for pattern in "${PATTERNS[@]}"; do
    if git diff --cached --diff-filter=ACM | grep -E "$pattern" > /dev/null; then
        echo -e "${RED}✗ ERROR: Potential secret detected!${NC}"
        echo -e "${YELLOW}Pattern: $pattern${NC}"
        echo ""
        echo "Found in:"
        git diff --cached --name-only | while read file; do
            if git diff --cached "$file" | grep -E "$pattern" > /dev/null; then
                echo "  - $file"
            fi
        done
        echo ""
        FOUND_SECRET=1
    fi
done

# Check specific files
SENSITIVE_FILES=(
    "src/main/resources/application-prod.properties"
    "src/main/resources/application-local.properties"
    ".env"
    "credentials"
)

for file in "${SENSITIVE_FILES[@]}"; do
    if git diff --cached --name-only | grep -q "^$file$"; then
        echo -e "${RED}✗ ERROR: Attempting to commit sensitive file: $file${NC}"
        FOUND_SECRET=1
    fi
done

if [ $FOUND_SECRET -eq 1 ]; then
    echo ""
    echo -e "${RED}❌ COMMIT BLOCKED!${NC}"
    echo ""
    echo "Sensitive data detected in your commit."
    echo ""
    echo "Please:"
    echo "1. Remove credentials from the files"
    echo "2. Use environment variables instead"
    echo "3. Add sensitive files to .gitignore"
    echo ""
    echo "To bypass this check (NOT RECOMMENDED):"
    echo "  git commit --no-verify"
    echo ""
    exit 1
fi

echo -e "${GREEN}✓ No sensitive data detected${NC}"
exit 0

