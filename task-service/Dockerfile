# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Stage 2: Runtime ----------
FROM quay.io/wildfly/wildfly:39.0.0.Final-jdk17

USER root

# Install the PostgreSQL JDBC driver as a WildFly module
RUN mkdir -p /opt/jboss/wildfly/modules/org/postgresql/main
COPY postgresql-42.7.4.jar /opt/jboss/wildfly/modules/org/postgresql/main/postgresql-42.7.4.jar

COPY module.xml /opt/jboss/wildfly/modules/org/postgresql/main/module.xml

# Run the CLI script at build time to configure the driver + datasource
COPY configure-datasource.cli /tmp/configure-datasource.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/configure-datasource.cli

RUN chown -R jboss:root /opt/jboss/wildfly/standalone \
    && chown -R jboss:root /opt/jboss/wildfly/modules/org/postgresql

USER jboss

# Copy the built WAR
COPY --from=builder /build/target/task-service.war \
     /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
