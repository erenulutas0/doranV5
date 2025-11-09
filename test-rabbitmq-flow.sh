#!/bin/bash
# RabbitMQ Flow Test Script
# Bu script bir sipariş oluşturup RabbitMQ üzerinden mesaj akışını test eder

echo "=== RabbitMQ Flow Test ==="
echo ""

# 1. Mevcut User'ları Listele
echo "1. Mevcut User'lar:"
USERS=$(curl -s http://localhost:8080/api/users)
echo "$USERS" | jq -r '.[0] | "   User ID: \(.id), Email: \(.email)"' 2>/dev/null || echo "   User listesi alınamadı"
USER_ID=$(echo "$USERS" | jq -r '.[0].id' 2>/dev/null)
echo ""

# 2. Mevcut Product'ları Listele
echo "2. Mevcut Product'lar:"
PRODUCTS=$(curl -s http://localhost:8080/api/products)
echo "$PRODUCTS" | jq -r '.[0] | "   Product ID: \(.id), Name: \(.name)"' 2>/dev/null || echo "   Product listesi alınamadı"
PRODUCT_ID=$(echo "$PRODUCTS" | jq -r '.[0].id' 2>/dev/null)
echo ""

# 3. Order Oluştur
echo "3. Order Oluşturuluyor..."
ORDER_JSON=$(cat <<EOF
{
  "userId": "$USER_ID",
  "shippingAddress": "Test Address, Test Street 123",
  "city": "Istanbul",
  "zipCode": "34000",
  "phoneNumber": "5551234567",
  "orderItems": [
    {
      "productId": "$PRODUCT_ID",
      "quantity": 2
    }
  ]
}
EOF
)

ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d "$ORDER_JSON")

ORDER_ID=$(echo "$ORDER_RESPONSE" | jq -r '.id' 2>/dev/null)

if [ "$ORDER_ID" != "null" ] && [ -n "$ORDER_ID" ]; then
  echo "   ✓ Order oluşturuldu: $ORDER_ID"
  echo "$ORDER_RESPONSE" | jq -r '"   Status: \(.status), Total: \(.totalAmount)"' 2>/dev/null
else
  echo "   ✗ Order oluşturulamadı"
  echo "$ORDER_RESPONSE"
  exit 1
fi
echo ""

# 4. RabbitMQ Queue Kontrolü
echo "4. RabbitMQ Queue Kontrolü (2 saniye bekleniyor...)"
sleep 2

QUEUES=$(curl -s -u guest:guest http://localhost:15672/api/queues)
ORDER_CREATED_MSG=$(echo "$QUEUES" | jq -r '.[] | select(.name=="order.created") | .messages' 2>/dev/null)
ORDER_STATUS_MSG=$(echo "$QUEUES" | jq -r '.[] | select(.name=="order.status.changed") | .messages' 2>/dev/null)

echo "   order.created queue mesaj sayısı: $ORDER_CREATED_MSG"
echo "   order.status.changed queue mesaj sayısı: $ORDER_STATUS_MSG"
echo ""

# 5. Notification Kontrolü
echo "5. Notification Kontrolü (2 saniye bekleniyor...)"
sleep 2

NOTIFICATIONS=$(curl -s http://localhost:8080/api/notifications)
NOTIF_COUNT=$(echo "$NOTIFICATIONS" | jq 'length' 2>/dev/null)

echo "   Toplam bildirim sayısı: $NOTIF_COUNT"
echo ""

echo "=== Test Tamamlandı ==="
echo ""
echo "Order ID: $ORDER_ID"
echo "RabbitMQ Management: http://localhost:15672"
echo ""

