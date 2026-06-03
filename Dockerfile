# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

COPY . .

RUN chmod +x Frontend/gradlew && \
    cd Frontend && \
    ./gradlew wasmJsMainDistribution

# Stage 2: Serve
FROM nginx:alpine

COPY --from=builder /app/Frontend/composeApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
