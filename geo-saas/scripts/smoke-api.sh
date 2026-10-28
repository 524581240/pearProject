#!/usr/bin/env bash
set -euo pipefail
BASE=${1:-http://localhost:8080/api}

echo "== login demo =="
TOKEN=$(curl -s -X POST "$BASE/auth/login" -H 'Content-Type: application/json' \
  -d '{"username":"demo","password":"demo123"}' | python3 -c 'import sys,json; print(json.load(sys.stdin)["data"]["token"])')
AUTH="Authorization: Bearer $TOKEN"

echo "== create brand =="
BRAND=$(curl -s -X POST "$BASE/brands" -H "$AUTH" -H 'Content-Type: application/json' \
  -d '{"name":"青禾咖啡","aliases":"青禾,QingHe","domain":"qinghe.example.com","industry":"餐饮"}')
BRAND_ID=$(echo "$BRAND" | python3 -c 'import sys,json; print(json.load(sys.stdin)["data"]["id"])')
echo "brandId=$BRAND_ID"

echo "== create prompt =="
PROMPT=$(curl -s -X POST "$BASE/prompts" -H "$AUTH" -H 'Content-Type: application/json' \
  -d "{\"brandId\":$BRAND_ID,\"content\":\"上海哪家精品咖啡更适合办公续命？\",\"groupName\":\"推荐\",\"tags\":\"推荐\"}")
PROMPT_ID=$(echo "$PROMPT" | python3 -c 'import sys,json; print(json.load(sys.stdin)["data"]["id"])')
echo "promptId=$PROMPT_ID"

echo "== verify multi engines =="
BATCH=$(curl -s -X POST "$BASE/verify" -H "$AUTH" -H 'Content-Type: application/json' \
  -d "{\"brandId\":$BRAND_ID,\"promptIds\":\"$PROMPT_ID\",\"engineCodes\":\"chatgpt,doubao,deepseek\",\"repeatCount\":1}")
echo "$BATCH" | python3 -m json.tool | head -40

echo "== create order + mock pay =="
ORDER=$(curl -s -X POST "$BASE/orders" -H "$AUTH" -H 'Content-Type: application/json' \
  -d '{"planCode":"pro","billingCycle":"MONTHLY"}')
ORDER_ID=$(echo "$ORDER" | python3 -c 'import sys,json; print(json.load(sys.stdin)["data"]["id"])')
curl -s -X POST "$BASE/orders/$ORDER_ID/pay" -H "$AUTH" -H 'Content-Type: application/json' \
  -d '{"channel":"MOCK"}' | python3 -m json.tool

echo "== subscription =="
curl -s "$BASE/subscriptions/current" -H "$AUTH" | python3 -m json.tool | head -50

echo "SMOKE OK"
