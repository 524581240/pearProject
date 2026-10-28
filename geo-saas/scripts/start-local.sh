#!/usr/bin/env bash
# Start GEO SaaS locally from ANY directory.
# Usage: bash /path/to/geo-saas/scripts/start-local.sh
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo "ROOT=$ROOT"

need_cmd() {
  command -v "$1" >/dev/null 2>&1 || { echo "缺少命令: $1"; exit 1; }
}
need_cmd mvn
need_cmd npm
need_cmd curl

port_busy() {
  local port="$1"
  if command -v fuser >/dev/null 2>&1; then
    fuser "${port}/tcp" >/dev/null 2>&1
  else
    curl -s -o /dev/null "http://127.0.0.1:${port}/" && return 0 || return 1
  fi
}

start_backend() {
  if curl -s -o /dev/null -w "%{http_code}" "http://127.0.0.1:8080/api/plans" | grep -q "200"; then
    echo "[backend] 已在 8080 运行，跳过"
    return
  fi
  if port_busy 8080; then
    echo "[backend] 8080 被占用但 API 不可用。请先执行: fuser -k 8080/tcp"
    exit 1
  fi
  echo "[backend] 启动中..."
  (cd "$ROOT/backend" && nohup mvn -q spring-boot:run >"$ROOT/scripts/.backend.log" 2>&1 &)
  for i in $(seq 1 60); do
    if curl -s -o /dev/null -w "%{http_code}" "http://127.0.0.1:8080/api/plans" | grep -q "200"; then
      echo "[backend] OK http://localhost:8080"
      return
    fi
    sleep 1
  done
  echo "[backend] 启动超时，看日志: $ROOT/scripts/.backend.log"
  exit 1
}

start_frontend() {
  local name="$1" dir="$2" port="$3"
  if curl -s -o /dev/null -w "%{http_code}" "http://127.0.0.1:${port}/" | grep -q "200"; then
    echo "[$name] 已在 ${port} 运行，跳过"
    return
  fi
  if [[ ! -d "$ROOT/$dir/node_modules" ]]; then
    echo "[$name] npm install..."
    (cd "$ROOT/$dir" && npm install)
  fi
  echo "[$name] 启动中..."
  (cd "$ROOT/$dir" && nohup npm run dev -- --host 0.0.0.0 --port "$port" >"$ROOT/scripts/.${name}.log" 2>&1 &)
  for i in $(seq 1 30); do
    if curl -s -o /dev/null -w "%{http_code}" "http://127.0.0.1:${port}/" | grep -q "200"; then
      echo "[$name] OK http://localhost:${port}"
      return
    fi
    sleep 1
  done
  echo "[$name] 启动超时，看日志: $ROOT/scripts/.${name}.log"
  exit 1
}

start_backend
start_frontend "web-client" "web-client" 5173
start_frontend "web-admin" "web-admin" 5174

echo
echo "全部就绪："
echo "  客户端   http://localhost:5173  (demo / demo123)"
echo "  管理后台 http://localhost:5174  (admin / admin123)"
echo "  API      http://localhost:8080"
echo
echo "日志目录: $ROOT/scripts/.*.log"
echo "停止: fuser -k 8080/tcp 5173/tcp 5174/tcp"
