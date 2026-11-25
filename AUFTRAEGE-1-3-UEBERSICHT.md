# 🎮 TicTacTest: Web-Konsole Aufträge 1-3

Vollständige Anleitung für ein 3-wöchiges Projekt: Von der lokalen Web-Konsole zum Cloud-Deployment mit automatisierter CI/CD.

## 📅 Projekt-Übersicht

| Auftrag | Titel | Zeitaufwand | Fokus |
|---------|-------|-------------|-------|
| **1** | 🌐 Web-Konsole (Lokal) | ~3-4 Stunden | ttyd + HTML-Frontend |
| **2** | 🚀 AWS EC2 Deployment | ~4-5 Stunden | Systemd Services + Nginx |
| **3** | 🔄 CI/CD Automation | ~3-4 Stunden | GitHub Actions + Auto-Deploy |

---

## ✅ Auftrag 1: Web-Konsole (Lokal)

**Status:** 🟢 Implementiert

### Was du lernst:
- Wie man CLI-Apps in Web-Terminal bringt
- ttyd (Terminal über HTTP)
- Dynamische URL-Erkennung
- ANSI-Farben im Web-Terminal

### Dateistruktur:
```
frontend/
├── index.html          # Web-Interface
├── style.css           # Grün-auf-Schwarz Design
├── config.js           # Dynamische URL
└── version.txt         # Version v0.0.1

tictactoe.jar          # Spiel-Binary
start-local.sh         # Linux Start-Skript
start-local.ps1        # PowerShell Start-Skript
AUFTRAG-1-ANLEITUNG.md # Detaillierte Schritte
```

### Quick-Start:

**Öffne DevContainer in VS Code:**
```bash
F1 → "Dev Containers: Reopen in Container"
```

**Im DevContainer Terminal:**
```bash
cd /workspaces/TicTacTest/450-tictactest-mvk
chmod +x start-local.sh
./start-local.sh
```

**Im Browser:**
```
http://localhost:8000
```

### Checkliste:
- [ ] DevContainer öffnen
- [ ] `./start-local.sh` ausführen
- [ ] Browser: http://localhost:8000
- [ ] Spiel spielen & gewinnen
- [ ] Version oben rechts sichtbar

---

## 🚀 Auftrag 2: AWS EC2 Deployment

**Status:** 🟡 Vorbereitet (Schritt-für-Schritt unten)

### Was du lernst:
- AWS EC2 Instanzen starten
- Linux Server-Setup
- Systemd Services
- Nginx Reverse-Proxy
- Security Groups

### Architektur:
```
┌─────────────────────────┐
│   Browser (überall)     │
└────────────┬────────────┘
             │ HTTPS:443
       ┌─────▼──────────┐
       │ Nginx:80/443   │
       │ (Reverse Proxy)│
       └─────┬──────────┘
             │
       ┌─────▼──────────────────┐
       │ EC2 Instance (Ubuntu)  │
       │                        │
       │ ┌──────────────────┐   │
       │ │ ttyd (Port 8080) │   │
       │ │ Java Spiel       │   │
       │ └──────────────────┘   │
       │                        │
       │ ┌──────────────────┐   │
       │ │ Frontend (8000)  │   │
       │ │ HTML/CSS/JS      │   │
       │ └──────────────────┘   │
       └────────────────────────┘
```

### Schritt-für-Schritt (kommt später als detaillierte Anleitung):

1. **AWS Account erstellen**
   - https://aws.amazon.com
   - Region: `eu-central-1` (Frankfurt, nähester EU-Datacenter)

2. **EC2 Instanz starten**
   ```
   - AMI: Ubuntu 22.04 LTS
   - Typ: t3.micro (kostenlos mit Free Tier)
   - Storage: 20GB (gp3)
   - Security Group:
     * SSH (22) - von deiner IP
     * HTTP (80) - öffentlich
     * HTTPS (443) - öffentlich
     * Port 8080 - öffentlich (ttyd)
     * Port 8000 - öffentlich (Frontend)
   ```

3. **Mit SSH verbinden**
   ```bash
   ssh -i key.pem ubuntu@<EC2-IP>
   ```

4. **Java + ttyd installieren**
   ```bash
   sudo apt-get update
   sudo apt-get install -y openjdk-21-jdk ttyd nginx
   ```

5. **JAR auf Server übertragen**
   ```bash
   scp -i key.pem tictactoe.jar ubuntu@<EC2-IP>:/tmp/
   ```

6. **Frontend auf Server übertragen**
   ```bash
   scp -i key.pem -r frontend ubuntu@<EC2-IP>:/var/www/
   ```

7. **Systemd Services erstellen**
   ```bash
   # /etc/systemd/system/tictactoe.service
   # /etc/systemd/system/frontend.service
   ```

8. **Nginx konfigurieren**
   ```
   - Reverse Proxy für Port 8080
   - Statische Dateien für /var/www/frontend
   - SSL/TLS (Let's Encrypt)
   ```

9. **Testen**
   ```
   https://<DOMAIN>.com
   ```

---

## 🔄 Auftrag 3: CI/CD Automation

**Status:** 🟡 Vorbereitet (Schritt-für-Schritt unten)

### Was du lernst:
- GitHub Actions
- Automated Deployments
- Release-Management
- Infrastructure-as-Code (optional: Terraform)

### Workflow:
```
1. Developer pushed Code auf "main" Branch
   ↓
2. GitHub Actions startet:
   - Build JAR
   - Run Tests
   - Create Release
   ↓
3. Release verfügbar: v0.0.2 (z.B.)
   ↓
4. GitHub Actions deployed zu AWS:
   - Download JAR
   - SSH zu EC2
   - Systemd Service neu starten
   ↓
5. Users sehen neue Version im Browser
```

### GitHub Actions Workflow:
```yaml
# .github/workflows/deploy-aws.yml
name: Deploy to AWS

on:
  push:
    tags:
      - 'v*'  # z.B. v0.0.2

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v5
      - name: Build JAR
        run: ./gradlew jar
      - name: Create Release
        run: gh release create ${{ github.ref }}
      - name: Deploy to EC2
        env:
          AWS_KEY: ${{ secrets.AWS_PRIVATE_KEY }}
          EC2_HOST: ${{ secrets.EC2_HOST }}
        run: |
          # SSH zu EC2
          # Download JAR
          # Restart Service
          # Health Check
```

### Checkliste Auftrag 3:
- [ ] GitHub Actions Workflow erstellen
- [ ] AWS Credentials in GitHub Secrets speichern
- [ ] Release-Tag pushen (z.B. `git tag v0.0.2`)
- [ ] Automatischer Deploy triggern
- [ ] Deployment erfolgreich verifiziert

---

## 📂 Komplette Verzeichnis-Struktur

```
450-tictactest-mvk/
├── .devcontainer/
│   ├── Dockerfile          ← ttyd + build tools
│   └── devcontainer.json
│
├── .github/workflows/
│   ├── ci.yml              ← Bestehender Test-Workflow
│   ├── devcontainer.yml    ← Bestehender Build-Workflow
│   └── deploy-aws.yml      ← Neu für Auftrag 3
│
├── frontend/               ← NEU für Auftrag 1
│   ├── index.html
│   ├── style.css
│   ├── config.js
│   └── version.txt
│
├── src/main/java/...
├── build/libs/
│   └── tictactoe.jar       ← JAR für Auftrag 1+2
│
├── gradlew
├── build.gradle
│
├── AUFTRAG-1-ANLEITUNG.md  ← NEU
├── AUFTRAG-2-ANLEITUNG.md  ← Kommt
├── AUFTRAG-3-ANLEITUNG.md  ← Kommt
│
├── start-local.sh          ← NEU (Linux)
├── start-local.ps1         ← NEU (PowerShell)
└── README.md               ← Diese Datei
```

---

## 🔧 Lokales Setup für ALLE Aufträge

### Anforderungen:
- **Windows 11+** mit WSL2 oder **macOS/Linux**
- **VS Code** mit Dev Containers Extension
- **Git**
- (Alles andere lädt sich der DevContainer selbst)

### Einmalige Installation:
```bash
# Klone Repository
git clone https://github.com/FlyCaptainRex/TicTacTest.git
cd TicTacTest/450-tictactest-mvk

# Öffne in VS Code
code .

# F1 → "Dev Containers: Reopen in Container"
# (VS Code lädt automatisch den DevContainer)
```

### DevContainer lädt automatisch:
- ✅ Java 25 JDK
- ✅ Gradle mit Wrapper
- ✅ ttyd (für Auftrag 1)
- ✅ Python3 (für Frontend-Server)
- ✅ Git

---

## 🧪 Testen

### Auftrag 1 lokal testen:
```bash
cd /workspaces/TicTacTest/450-tictactest-mvk
chmod +x start-local.sh
./start-local.sh
# Browser: http://localhost:8000
```

### Unit-Tests:
```bash
./gradlew test
```

### JAR bauen:
```bash
./gradlew jar
# Output: build/libs/tictactoe.jar
```

### Spiel direkt ausführen:
```bash
java -jar build/libs/tictactoe.jar
```

---

## 🔐 Sicherheit

### Auftrag 1-2: Nur JAR, keine Shell
```bash
# ✅ SICHER:
ttyd -p 8080 java -jar tictactoe.jar

# ❌ UNSICHER:
ttyd -p 8080 /bin/bash
# (Jeder bekommt Shell-Zugriff!)
```

### Auftrag 2: EC2 Security Groups
```
- SSH (22): Nur von deiner IP
- HTTP (80): Öffentlich
- HTTPS (443): Öffentlich
- Port 8080: Hinter Nginx (nicht direkt öffentlich)
```

### Auftrag 3: GitHub Secrets
```
- AWS_PRIVATE_KEY: Geheim halten!
- EC2_HOST: IP oder Hostname
- Credentials: Minimal permissions (Principle of Least Privilege)
```

---

## 📊 Lessons Learned

### Docker Build Saga:
- ❌ Gradle-Download in Docker ist unreliabel (exit code 4)
- ✅ Gradle Wrapper ist sauberer (im Repo enthalten)
- ✅ Java 21-jammy ist stabiler als Java 25

### Web-Terminal Tools:
- ❌ GoTTY: Linux-only, nicht auf Windows
- ✅ ttyd: Cross-platform, ANSI-Farben, bidirektionales I/O
- ⚠️ Custom WebSocket-Backend: Zu viel Aufwand

### Frontend-Dynamik:
- ✅ Hardcodierte URLs sind schlecht (Dev vs. Prod)
- ✅ `config.js` erkennt automatisch Umgebung
- ✅ HTML-iframe + Python HTTP-Server = minimal Setup

---

## 📞 FAQ

**F: Warum devcontainer.json und nicht lokal Java installieren?**
- A: Konsistenz! Entwicklungs-Container hat exakt die gleiche Umgebung wie CI/CD und Production.

**F: Warum ttyd und nicht Web Socket Server selbst schreiben?**
- A: ttyd ist getestet, stabil, produktiv-ready. Eigene Implementierung = unnötige Komplexität.

**F: Warum Nginx für Auftrag 2?**
- A: Professional Best Practice. Reverse-Proxy trennt Frontend/Backend sauber und erlaubt SSL-Terminierung.

**F: Warum GitHub Actions für Auftrag 3?**
- A: Automatisierung spart Zeit. Einmal konfigurieren = jeder Release deployt automatisch.

---

## 🚀 Nächste Schritte

1. **Auftrag 1 starten:**
   ```bash
   cd /workspaces/TicTacTest/450-tictactest-mvk
   chmod +x start-local.sh
   ./start-local.sh
   ```

2. **Auftrag 2 vorbereiten:**
   - AWS Account erstellen
   - EC2 Instanz starten (Free Tier)
   - Domäne registrieren (optional)

3. **Auftrag 3 vorbereiten:**
   - GitHub Actions Workflow-Datei erstellen
   - AWS Credentials als GitHub Secrets speichern

---

## 📚 Zusätzliche Ressourcen

- [ttyd GitHub](https://github.com/tsl0922/ttyd)
- [AWS EC2 Dokumentation](https://docs.aws.amazon.com/ec2/)
- [GitHub Actions Dokumentation](https://docs.github.com/actions)
- [Nginx Reverse Proxy](https://nginx.org/en/docs/)
- [Systemd Services](https://systemd.io/)
- [Let's Encrypt SSL](https://letsencrypt.org/)

---

**Viel Spaß mit den Aufträgen! 🎮✨**

Erstellt für M450-Projekt | FlyCaptainRex | 2024
