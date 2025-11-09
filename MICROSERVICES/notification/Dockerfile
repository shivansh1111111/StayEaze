FROM eclipse-temurin:21-jre-alpine

# Create a non-root user and group
RUN addgroup -g 2000 appuser && adduser -u 2000 -G appuser -h /home/appuser -D appuser

# Set the working directory
WORKDIR /home/appuser

# Copy your jar into the image
COPY target/notification-0.0.1-SNAPSHOT.jar notification-0.0.1-SNAPSHOT.jar

# Adjust ownership and permissions
RUN chown appuser:appuser notification-0.0.1-SNAPSHOT.jar && chmod 700 notification-0.0.1-SNAPSHOT.jar

# Switch to the non-root user
USER appuser

# Start the application
ENTRYPOINT ["java", "-jar", "notification-0.0.1-SNAPSHOT.jar"]
