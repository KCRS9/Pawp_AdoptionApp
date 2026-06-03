# Stage 1: Build
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY . .

RUN chmod +x Frontend/gradlew && \
    cd Frontend && \
    ./gradlew wasmJsBrowserDistribution

# Stage 2: Serve
FROM nginx:latest

COPY --from=builder /app/Frontend/composeApp/build/dist/wasmJs/productionExecutable /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf

RUN apt-get update && apt-get install -y gettext-base && rm -rf /var/lib/apt/lists/*

EXPOSE 80

CMD ["sh", "-c", "envsubst '$$PORT' < /etc/nginx/nginx.conf > /etc/nginx/nginx.conf.temp && mv /etc/nginx/nginx.conf.temp /etc/nginx/nginx.conf && nginx -g 'daemon off;'"]
