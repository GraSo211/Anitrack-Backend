# STAGE 1: Build app
FROM eclipse-temurin:21-jdk AS builder

# set working directory
WORKDIR /app



# copy application files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY src src
# build application with maven:
RUN ./mvnw clean package -DskipTests

# STAGE 2: Run app
FROM eclipse-temurin:21-jre

# set working directory
WORKDIR /app

# copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# expose port
EXPOSE 8080

# run the application
ENTRYPOINT ["java", "-jar", "app.jar"]