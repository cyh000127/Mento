FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

RUN apk add --no-cache tzdata curl && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone

COPY build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="\
-XX:+UseContainerSupport \
-XX:InitialRAMPercentage=60.0 \
-XX:MaxRAMPercentage=70.0 \
-XX:+UseG1GC \
-XX:MaxGCPauseMillis=100 \
-XX:+UseStringDeduplication \
-Djdk.virtualThreadScheduler.parallelism=2 \
-Djdk.virtualThreadScheduler.maxPoolSize=128 \
-XX:MetaspaceSize=128M \
-XX:MaxMetaspaceSize=256M \
-Xlog:gc*:file=/app/logs/gc.log:time,level,tags:filecount=3,filesize=5M \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=/app/logs/heapdump.hprof \
-XX:+ExitOnOutOfMemoryError \
-Djava.security.egd=file:/dev/./urandom"

RUN mkdir -p /app/logs

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]