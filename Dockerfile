# Stage 1: Build
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

RUN apt-get update && apt-get install -y libatomic1 && rm -rf /var/lib/apt/lists/*

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

ENTRYPOINT ["/bin/bash", "-c", "envsubst < /etc/nginx/nginx.conf > /tmp/nginx.conf && nginx -c /tmp/nginx.conf -g 'daemon off;'"]
