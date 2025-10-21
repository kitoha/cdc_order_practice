#!/bin/bash

# CDC Order Demo - Setup Script
echo "========================================="
echo "🚀 Starting CDC Order Demo"
echo "========================================="

# Step 1: Docker Compose 시작
echo ""
echo "📦 Step 1: Starting Docker containers..."
docker-compose up -d

echo "Waiting for services to be healthy..."
sleep 30

# Step 2: MySQL 상태 확인
echo ""
echo "🔍 Step 2: Checking MySQL binlog settings..."
docker exec mysql-cdc mysql -uroot -proot -e "SHOW VARIABLES LIKE 'log_bin';"
docker exec mysql-cdc mysql -uroot -proot -e "SHOW VARIABLES LIKE 'binlog_format';"
docker exec mysql-cdc mysql -uroot -proot -e "SHOW VARIABLES LIKE 'binlog_row_image';"

# Step 3: Kafka Connect 상태 확인
echo ""
echo "🔍 Step 3: Checking Kafka Connect status..."
for i in {1..30}; do
    if curl -s http://localhost:8083/ > /dev/null; then
        echo "✅ Kafka Connect is ready!"
        break
    fi
    echo "Waiting for Kafka Connect... ($i/30)"
    sleep 2
done

# Step 4: Debezium Connector 등록
echo ""
echo "📡 Step 4: Registering Debezium MySQL Connector..."
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" \
  http://localhost:8083/connectors/ \
  -d @debezium-config/mysql-order-connector.json

# Step 5: Connector 상태 확인
echo ""
echo ""
echo "🔍 Step 5: Checking connector status..."
sleep 5
curl -s http://localhost:8083/connectors/mysql-order-connector/status | jq .

# Step 6: Kafka 토픽 확인
echo ""
echo ""
echo "📋 Step 6: Listing Kafka topics..."
docker exec kafka-cdc kafka-topics --bootstrap-server localhost:9092 --list

echo ""
echo "========================================="
echo "✅ Setup Complete!"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Start Order Service: cd order-service && ./gradlew bootRun"
echo "2. Start Notification Service: cd notification-service && ./gradlew bootRun"
echo "3. Test the API: curl -X POST http://localhost:8081/api/orders ..."
echo ""
echo "Useful commands:"
echo "- View Kafka UI: http://localhost:8080"
echo "- View connector status: curl http://localhost:8083/connectors/mysql-order-connector/status"
echo "- View MySQL logs: docker logs mysql-cdc -f"
echo "- View Debezium logs: docker logs debezium-connect -f"
echo ""
