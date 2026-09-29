FROM eclipse-temurin:25-jdk

ARG GRADLE_VERSION=9.7.0

RUN apt-get update \
    && apt-get install -y --no-install-recommends bash git nodejs npm unzip wget \
    && wget -q "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -O /tmp/gradle.zip \
    && unzip -q /tmp/gradle.zip -d /opt \
    && ln -s "/opt/gradle-${GRADLE_VERSION}/bin/gradle" /usr/local/bin/gradle \
    && rm /tmp/gradle.zip \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 1000 vscode \
    && useradd --uid 1000 --gid vscode --create-home vscode

USER vscode