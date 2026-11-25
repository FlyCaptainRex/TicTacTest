FROM eclipse-temurin:25-jdk-jammy

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       bash \
       git \
       ttyd \
       python3-dev \
       wget \
       curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 1000 vscode \
    && useradd --uid 1000 --gid vscode --create-home vscode