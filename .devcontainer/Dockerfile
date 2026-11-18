FROM eclipse-temurin:21-jdk-jammy as gradle-builder

ARG GRADLE_VERSION=8.11.0

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       curl \
       unzip \
       ca-certificates \
    && update-ca-certificates \
    && mkdir -p /tmp /opt \
    && curl -fsSL -o /tmp/gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
    && unzip -q /tmp/gradle.zip -d /opt \
    && rm /tmp/gradle.zip \
    && rm -rf /var/lib/apt/lists/*

FROM eclipse-temurin:25-jdk-jammy

COPY --from=gradle-builder /opt /opt

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       bash \
       git \
       curl \
       ca-certificates \
    && update-ca-certificates \
    && ln -s "/opt/gradle-8.11.0/bin/gradle" /usr/local/bin/gradle \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 1000 vscode \
    && useradd --uid 1000 --gid vscode --create-home vscode