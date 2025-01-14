#!/bin/bash

# Docker config 설정
mkdir -p ~/.docker
echo '{"credsStore":""}' > ~/.docker/config.json

# 환경변수로 Docker 호스트 지정
export DOCKER_HOST="unix:///var/run/docker.sock"

# 작업 디렉토리 변경
cd /var/jenkins_home/nangpago

# 환경변수 설정
APP_NAME=nangpago
LOG_FILE=./deploy.log

# 블루 그린 상태 확인
EXIST_BLUE=$(docker compose -p "${APP_NAME}-blue" -f docker-compose.blue.yml ps | grep -E "Up|running")

echo "배포 시작일자 : $(date '+%Y-%m-%d %H:%M:%S')" >> $LOG_FILE

if [ -z "$EXIST_BLUE" ]; then
  echo "blue 배포 시작 : $(date '+%Y-%m-%d %H:%M:%S')" >> $LOG_FILE

  # Blue 환경 배포
  docker compose -p ${APP_NAME}-blue -f docker-compose.blue.yml up -d --build

  sleep 30

  echo "green 중단 시작 : $(date '+%Y-%m-%d %H:%M:%S')" >> $LOG_FILE
  # Green 환경 중단
  docker compose -p ${APP_NAME}-green -f docker-compose.green.yml down

  docker image prune -af

  echo "green 중단 완료 : $(date '+%Y-%m-%d %H:%M:%S')" >> $LOG_FILE

else
  echo "green 배포 시작 : $(date '+%Y-%m-%d %H:%M:%S')" >> $LOG_FILE

  # Green 환경 배포
  docker compose -p ${APP_NAME}-green -f docker-compose.green.yml up -d --build

  sleep 30

  echo "blue 중단 시작 : $(date '+%Y-%m-%d %H:%M:%S')" >> $LOG_FILE
  # Blue 환경 중단
  docker compose -p ${APP_NAME}-blue -f docker-compose.blue.yml down

  docker image prune -af

  echo "blue 중단 완료 : $(date '+%Y-%m-%d %H:%M:%S')" >> $LOG_FILE

fi
  echo "배포 종료  : $(date '+%Y-%m-%d %H:%M:%S')" >> $LOG_FILE
  echo "===================== 배포 완료 =====================" >> $LOG_FILE
  echo >> $LOG_FILE
