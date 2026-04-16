#!/bin/bash
set -e

echo "==> 최신 코드 pull..."
git pull

echo "==> 컨테이너 재빌드 및 재시작..."
docker compose down
docker compose build --no-cache app
docker compose up -d

echo "==> 상태 확인..."
docker compose ps
echo "==> 완료!"
