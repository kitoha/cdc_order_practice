#!/bin/bash

# CDC Order Demo - Test Script
echo "========================================="
echo "🧪 Testing CDC Order Demo"
echo "========================================="

BASE_URL="http://localhost:8080/api/orders"

# Test 1: 주문 생성
echo ""
echo "Test 1: Creating a new order..."
CREATE_RESPONSE=$(curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "홍길동",
    "totalAmount": 50000,
    "notes": "배송 전 연락 주세요"
  }')

echo "$CREATE_RESPONSE" | jq .

ORDER_ID=$(echo "$CREATE_RESPONSE" | jq -r '.data.id')
ORDER_NUMBER=$(echo "$CREATE_RESPONSE" | jq -r '.data.orderNumber')

echo "✅ Order created: ID=$ORDER_ID, Number=$ORDER_NUMBER"
echo ""
echo "👀 Check notification-service logs to see the CDC event!"
sleep 3

# Test 2: 주문 조회
echo ""
echo "Test 2: Retrieving order details..."
curl -s $BASE_URL/$ORDER_ID | jq .
sleep 2

# Test 3: 주문 승인 (PENDING -> APPROVED)
echo ""
echo "Test 3: Approving the order..."
curl -s -X PATCH $BASE_URL/$ORDER_ID/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "APPROVED"
  }' | jq .

echo ""
echo "👀 Check notification-service logs for status change event!"
sleep 3

# Test 4: 배송 시작 (APPROVED -> SHIPPED)
echo ""
echo "Test 4: Shipping the order..."
curl -s -X PATCH $BASE_URL/$ORDER_ID/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "SHIPPED"
  }' | jq .

echo ""
echo "👀 Check notification-service logs for shipping event!"
sleep 3

# Test 5: 배송 완료 (SHIPPED -> DELIVERED)
echo ""
echo "Test 5: Delivering the order..."
curl -s -X PATCH $BASE_URL/$ORDER_ID/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "DELIVERED"
  }' | jq .

echo ""
echo "👀 Check notification-service logs for delivery event!"
sleep 3

# Test 6: 다른 주문 생성
echo ""
echo "Test 6: Creating another order..."
curl -s -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "김철수",
    "totalAmount": 75000,
    "notes": "선물 포장 부탁드립니다"
  }' | jq .

echo ""
sleep 2

# Test 7: 모든 주문 조회
echo ""
echo "Test 7: Retrieving all orders..."
curl -s $BASE_URL | jq .

# Test 8: 특정 상태의 주문 조회
echo ""
echo "Test 8: Retrieving DELIVERED orders..."
curl -s "$BASE_URL?status=DELIVERED" | jq .

echo ""
echo "========================================="
echo "✅ All tests completed!"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Check Kafka UI: http://localhost:8080"
echo "2. View messages in topic: dbserver1.order_db.orders"
echo "3. Check notification-service logs for processed events"
echo ""
