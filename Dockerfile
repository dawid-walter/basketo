# Stage 1: Build
FROM gradle:8.11-jdk21-alpine AS builder

# Set Gradle user home to a known location for caching
ENV GRADLE_USER_HOME=/cache/gradle

WORKDIR /app

# Copy only dependency-related files first (for better caching)
COPY build.gradle settings.gradle ./
COPY gradle gradle

# Download dependencies in a separate layer (will be cached)
# Using --refresh-dependencies to ensure we get all deps
RUN --mount=type=cache,target=/cache/gradle \
    gradle dependencies --no-daemon --refresh-dependencies || true

# Now copy source code (changes here won't invalidate dependency cache)
COPY src src

# Build the application (reuse cached dependencies)
RUN --mount=type=cache,target=/cache/gradle \
    gradle bootJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
