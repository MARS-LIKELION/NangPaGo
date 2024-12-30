FROM node:lts-alpine as builder

WORKDIR /app

# 먼저 패키지 파일만 복사
COPY package*.json ./

# 의존성 설치
RUN npm install

# 그 다음 나머지 파일들 복사
COPY . .

# 빌드 실행
RUN npm run build

# 실행 스테이지
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE ${VITE_FRONT_SERVER_PORT}

CMD ["nginx", "-g", "daemon off;"]
