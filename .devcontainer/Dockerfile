FROM eclipse-temurin:25-jdk-alpine

ARG GRADLE_VERSION=9.7.0

RUN apk add --no-cache bash git nodejs npm unzip wget \
    && wget -q "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -O /tmp/gradle.zip \
    && unzip -q /tmp/gradle.zip -d /opt \
    && ln -s "/opt/gradle-${GRADLE_VERSION}/bin/gradle" /usr/local/bin/gradle \
    && rm /tmp/gradle.zip \
    && addgroup -g 1000 vscode \
    && adduser -D -u 1000 -G vscode vscode

USER vscode