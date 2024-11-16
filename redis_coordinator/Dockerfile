FROM openjdk:21-jdk-slim

WORKDIR /app

COPY ./build/libs/redis_coordinator-1.0-SNAPSHOT.jar /app/redis_coordinator.jar

CMD ["java", "-jar", "redis_coordinator.jar"]
