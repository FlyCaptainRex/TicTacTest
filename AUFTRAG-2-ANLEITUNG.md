# Auftrag 2: AWS EC2 Deployment

## 🎯 Ziel
Mache TicTacTest auf einem **AWS EC2 Server** öffentlich zugänglich (überall im Internet spielbar).

**Architektur:**
```
Internet (Browser überall) 
    ↓ HTTPS:443
┌───────────────────┐
│   Nginx:80/443    │
│ (Reverse Proxy)   │
└────────┬──────────┘
         ↓
┌────────────────────────────┐
│   EC2 Instance (Ubuntu)    │
│                            │
│ ┌──────────────────────┐   │
│ │ ttyd Port 8080       │   │
│ │ Java TicTacTest      │   │
│ └──────────────────────┘   │
│                            │
│ ┌──────────────────────┐   │
│ │ Frontend Port 8000   │   │
│ │ HTML/CSS/JS          │   │
│ └──────────────────────┘   │
└────────────────────────────┘
```

---

## 📋 Phase 1: EC2 Instanz starten

### Schritt 1.1: AWS Management Console öffnen

1. Gehe zu: https://console.aws.amazon.com/
2. Melde dich an mit deinem AWS Account
3. Region oben rechts: **Frankfurt (eu-central-1)** wählen

### Schritt 1.2: EC2 Service finden

1. Klicke auf "Services" (oben links)
2. Tippe "EC2" ein
3. Klicke auf "EC2"

### Schritt 1.3: Instanz starten

1. Klicke auf **"Launch Instance"** (orange Button)

**Schritt 1.3.1: Name + OS wählen**
```
Name: "tictactest-web-console"

AMI auswählen:
→ Suche nach "Ubuntu"
→ Wähle: "Ubuntu Server 22.04 LTS"
→ Klicke darauf
```

**Schritt 1.3.2: Instanztyp wählen**
```
Instance Type: t3.micro (Free Tier!)
Klicke "Next"
```

**Schritt 1.3.3: Netzwerk + Storage**
```
Default Settings (ist OK)
Klicke "Next"
```

**Schritt 1.3.4: Storage**
```
Size: 20 GB (Standard)
Klicke "Next"
```

**Schritt 1.3.5: Sicherheitsgruppe**
```
Sicherheitsgruppe Name: "tictactest-web-console"

Inbound-Regeln hinzufügen:
1. SSH
   - Type: SSH (22)
   - Protocol: TCP
   - Port: 22
   - Source: Deine IP (findest du bei "My IP" Dropdown)
   
2. HTTP
   - Type: HTTP (80)
   - Protocol: TCP
   - Port: 80
   - Source: 0.0.0.0/0 (Öffentlich!)
   
3. HTTPS
   - Type: HTTPS (443)
   - Protocol: TCP
   - Port: 443
   - Source: 0.0.0.0/0 (Öffentlich!)

4. Custom TCP (Optional - nur zum Debuggen)
   - Type: Custom TCP
   - Port Range: 8080
   - Source: 0.0.0.0/0

5. Custom TCP (Optional - nur zum Debuggen)
   - Type: Custom TCP
   - Port Range: 8000
   - Source: 0.0.0.0/0
```

**Schritt 1.3.6: SSH Key erstellen**

```
Key pair (login):
→ Klicke "Create new key pair"
→ Name: "tictactest-key"
→ Format: RSA (Standard)
→ Klicke "Create key pair"
→ `.pem` Datei wird heruntergeladen
   (SPEICHERN SIE DIESE! Sie können nicht neu heruntergeladen werden!)
```

**Speichern Sie die `tictactest-key.pem` Datei sicher!**
```
Empfohlener Speicherort:
C:\Users\<YourUsername>\.aws\tictactest-key.pem

WICHTIG: Teilen Sie diese Datei NIEMALS!
```

**Schritt 1.3.7: Fertig stellen**

1. Klicke "Launch Instance"
2. Warte 1-2 Minuten bis Instanz "running" ist
3. Notiere die **Public IP Address** (z.B. 1.2.3.4)

---

## 🔧 Phase 2: Mit SSH verbinden

### Schritt 2.1: SSH auf Windows (PowerShell)

```powershell
# Key-Datei mit korrekten Permissions:
icacls "C:\Users\<YourUsername>\.aws\tictactest-key.pem" /inheritance:r /grant:r "$env:USERNAME`:(F)"

# Mit SSH verbinden:
ssh -i "C:\Users\<YourUsername>\.aws\tictactest-key.pem" ubuntu@<EC2-PUBLIC-IP>

# Beispiel:
ssh -i "C:\Users\Nico\.aws\tictactest-key.pem" ubuntu@1.2.3.4
```

**Erste Verbindung:**
- SSH fragt: "Are you sure you want to continue connecting (yes/no)?"
- Antworte: `yes`

### Schritt 2.2: SSH auf Mac/Linux

```bash
# Key-Datei mit korrekten Permissions:
chmod 600 ~/.ssh/tictactest-key.pem

# Mit SSH verbinden:
ssh -i ~/.ssh/tictactest-key.pem ubuntu@<EC2-PUBLIC-IP>
```

**Du solltest sehen:**
```
ubuntu@ip-10-0-0-1:~$
```

---

## 📦 Phase 3: Server-Setup

### Schritt 3.1: Updates installieren

```bash
# Im SSH Terminal:
sudo apt-get update
sudo apt-get upgrade -y
```

### Schritt 3.2: Java installieren

```bash
sudo apt-get install -y openjdk-21-jdk
java -version
# Sollte zeigen: openjdk version "21..."
```

### Schritt 3.3: ttyd installieren

```bash
# Option A: Aus Ubuntu Repository (einfach)
sudo apt-get install -y ttyd

# Option B: Oder neueste Version kompilieren (falls nicht in Repo)
# (Meist nicht nötig, Repo-Version reicht)

# Test:
ttyd --version
# Sollte zeigen: ttyd x.x.x
```

### Schritt 3.4: Nginx installieren

```bash
sudo apt-get install -y nginx
sudo systemctl start nginx
sudo systemctl enable nginx

# Test (sollte Nginx Welcome-Page zeigen):
curl http://localhost
```

### Schritt 3.5: Python installieren (für Frontend-Server)

```bash
sudo apt-get install -y python3 python3-dev

python3 --version
# Sollte zeigen: Python 3.10...
```

---

## 🎮 Phase 4: TicTacTest auf Server bringen

### Schritt 4.1: Verzeichnis erstellen

```bash
# Im SSH Terminal:
mkdir -p /opt/tictactest
mkdir -p /opt/tictactest/frontend
mkdir -p /var/www/tictactest

cd /opt/tictactest
```

### Schritt 4.2: JAR auf Server laden

**Option A: Manuell hochladen**

```powershell
# Auf deinem Windows Computer (PowerShell):
$keyPath = "C:\Users\<YourUsername>\.aws\tictactest-key.pem"
$jarPath = "C:\4Lehrjahr\M450\TicTacTest\450-tictactest-mvk\build\libs\tictactoe.jar"
$serverIP = "<EC2-PUBLIC-IP>"

scp -i $keyPath $jarPath ubuntu@${serverIP}:/opt/tictactest/
```

**Option B: Oder direkt von GitHub (einfacher)**

```bash
# Im SSH Terminal auf Server:
cd /opt/tictactest
wget https://github.com/FlyCaptainRex/TicTacTest/releases/download/v0.0.1/tictactoe.jar
# Falls keine Release: JAR lokal bauen und hochladen
```

### Schritt 4.3: Frontend auf Server laden

```powershell
# Auf deinem Windows Computer (PowerShell):
$keyPath = "C:\Users\<YourUsername>\.aws\tictactest-key.pem"
$frontendPath = "C:\4Lehrjahr\M450\TicTacTest\450-tictactest-mvk\frontend"
$serverIP = "<EC2-PUBLIC-IP>"

scp -i $keyPath -r $frontendPath ubuntu@${serverIP}:/var/www/tictactest/
```

---

## 🔧 Phase 5: Systemd Services erstellen

### Schritt 5.1: TicTacTest (ttyd) Service

```bash
# Im SSH Terminal:
sudo nano /etc/systemd/system/tictactest.service
```

Folgendes einfügen:
```ini
[Unit]
Description=TicTacTest Web Console (ttyd)
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/opt/tictactest
ExecStart=/usr/bin/ttyd -p 8080 java -jar /opt/tictactest/tictactoe.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Speichern: `Ctrl+X` → `Y` → `Enter`

Aktivieren:
```bash
sudo systemctl daemon-reload
sudo systemctl enable tictactest
sudo systemctl start tictactest
sudo systemctl status tictactest
# Sollte "active (running)" zeigen
```

### Schritt 5.2: Frontend Service

```bash
sudo nano /etc/systemd/system/tictactest-frontend.service
```

Folgendes einfügen:
```ini
[Unit]
Description=TicTacTest Frontend Server
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/var/www/tictactest/frontend
ExecStart=/usr/bin/python3 -m http.server 8000
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Speichern: `Ctrl+X` → `Y` → `Enter`

Aktivieren:
```bash
sudo systemctl daemon-reload
sudo systemctl enable tictactest-frontend
sudo systemctl start tictactest-frontend
sudo systemctl status tictactest-frontend
# Sollte "active (running)" zeigen
```

Beide Services testen:
```bash
curl http://localhost:8080  # Sollte HTML von ttyd zeigen
curl http://localhost:8000  # Sollte HTML von Frontend zeigen
```

---

## 🌐 Phase 6: Nginx Reverse Proxy konfigurieren

### Schritt 6.1: Nginx Config erstellen

```bash
sudo nano /etc/nginx/sites-available/tictactest
```

Folgendes einfügen (mit deiner IP/Domain ersetzen):
```nginx
upstream tictactest_backend {
    server localhost:8080;
}

upstream tictactest_frontend {
    server localhost:8000;
}

server {
    listen 80;
    listen [::]:80;
    
    server_name <EC2-PUBLIC-IP>;  # oder deine Domain wenn vorhanden
    
    # Frontend
    location / {
        proxy_pass http://tictactest_frontend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
    
    # Terminal (ttyd)
    location /terminal {
        proxy_pass http://tictactest_backend/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        
        # WebSocket Support für ttyd
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    
    # API Endpoint für Health Check
    location /api/health {
        access_log off;
        return 200 "OK";
        add_header Content-Type text/plain;
    }
}
```

Speichern: `Ctrl+X` → `Y` → `Enter`

### Schritt 6.2: Nginx aktivieren

```bash
# Symlink erstellen
sudo ln -s /etc/nginx/sites-available/tictactest /etc/nginx/sites-enabled/

# Config testen
sudo nginx -t
# Sollte zeigen: "test is successful"

# Nginx neu starten
sudo systemctl restart nginx
sudo systemctl status nginx
# Sollte "active (running)" zeigen
```

---

## 🔐 Phase 7: Optional - HTTPS mit Let's Encrypt

### Schritt 7.1: Certbot installieren

```bash
sudo apt-get install -y certbot python3-certbot-nginx
```

### Schritt 7.2: SSL Zertifikat erstellen

```bash
# Wenn du eine Domain hast:
sudo certbot --nginx -d yourdomain.com

# Folge den Schritten (Email, Terms, etc.)
```

**Alternativ:** Wenn keine Domain vorhanden → HTTP reicht für lokales Testen

---

## 🧪 Phase 8: Testen!

### Schritt 8.1: Local testen (SSH-Session)

```bash
# Im SSH Terminal:

# Frontend:
curl http://localhost/
# Sollte HTML zeigen

# Terminal:
curl http://localhost/terminal
# Sollte HTML zeigen

# Health:
curl http://localhost/api/health
# Sollte "OK" zeigen
```

### Schritt 8.2: Von deinem Computer aus testen

**Im Browser:**
```
http://<EC2-PUBLIC-IP>
```

Du solltest sehen:
- Grünes Terminal Interface
- TicTacTest Spielfeld
- Version v0.0.1

Spielen! 🎮

### Schritt 8.3: Von anderer Maschine testen

Teile die URL mit jemandem:
```
http://<EC2-PUBLIC-IP>
```

Das Spiel sollte funktionieren!

---

## 📊 Monitoring

### Services anschauen

```bash
# Alle aktiven Services:
sudo systemctl status tictactest
sudo systemctl status tictactest-frontend
sudo systemctl status nginx

# Logs anschauen:
sudo journalctl -u tictactest -f          # Letzte Logs, live
sudo journalctl -u tictactest-frontend -f
sudo tail -f /var/log/nginx/error.log
```

### Ports überprüfen

```bash
# Welche Ports sind offen:
sudo netstat -tlnp | grep LISTEN

# Sollte zeigen:
# 8080 (ttyd)
# 8000 (frontend)
# 80 (nginx)
# 443 (nginx + SSL falls konfiguriert)
```

---

## 🆘 Troubleshooting

### Problem: "Connection refused"
```bash
# Services sind möglicherweise nicht gestartet:
sudo systemctl restart tictactest
sudo systemctl restart tictactest-frontend
sudo systemctl restart nginx
```

### Problem: "Port already in use"
```bash
# Welcher Prozess nutzt den Port:
sudo lsof -i :8080
sudo lsof -i :8000
sudo lsof -i :80

# Service beenden:
sudo systemctl stop tictactest
sudo systemctl stop tictactest-frontend
```

### Problem: Nginx zeigt Error Page
```bash
# Config prüfen:
sudo nginx -t

# Logs anschauen:
sudo tail -100 /var/log/nginx/error.log
```

### Problem: SSH Verbindung funktioniert nicht
```powershell
# Windows: Key Permissions prüfen
# Öffne PowerShell als Admin:
icacls "C:\Users\<YourUsername>\.aws\tictactest-key.pem" /inheritance:r /grant:r "$env:USERNAME`:(F)"
```

---

## ✅ Checkliste Auftrag 2

- [ ] AWS Account erstellt
- [ ] EC2 Instanz gestartet (t3.micro, Ubuntu 22.04)
- [ ] Security Group konfiguriert (SSH, HTTP, HTTPS)
- [ ] SSH Key heruntergeladen und gespeichert
- [ ] Mit SSH verbunden (`ssh -i key.pem ubuntu@IP`)
- [ ] Java installiert (`java -version`)
- [ ] ttyd installiert (`ttyd --version`)
- [ ] Nginx installiert
- [ ] JAR auf Server hochgeladen
- [ ] Frontend auf Server hochgeladen
- [ ] Systemd Services erstellt (tictactest.service)
- [ ] Systemd Services erstellt (tictactest-frontend.service)
- [ ] Nginx konfiguriert
- [ ] Lokaler Test erfolgreich (curl)
- [ ] Browser Test erfolgreich (http://<IP>)
- [ ] Von anderer Maschine spielbar
- [ ] Optional: SSL mit Let's Encrypt

---

## 🎓 Lernziele (Auftrag 2)

Nach diesem Schritt kannst du:
- ✅ AWS EC2 Instanzen starten und konfigurieren
- ✅ Linux Server-Administration (SSH, systemctl, nginx)
- ✅ Systemd Services erstellen und verwalten
- ✅ Reverse Proxy mit Nginx konfigurieren
- ✅ WebSocket über Nginx tunneln (für ttyd)
- ✅ Sicherheit mit Security Groups

---

## 🚀 Nächster Schritt: Auftrag 3

Nach Auftrag 2 erfolgreich:
- Automation: GitHub Actions Workflow für Auto-Deploy
- Trigger: `git tag v0.0.2` → automatischer Deploy zu AWS
- CD Pipeline: Release → JAR → SSH → Systemd Restart

Siehe: **AUFTRAG-3-ANLEITUNG.md** (kommt)

---

## 💡 Pro-Tipps

**SSH-Shortcut erstellen:**

```powershell
# PowerShell Profil öffnen:
code $PROFILE

# Folgendes hinzufügen:
function connect-tictactest {
    $key = "C:\Users\<YourUsername>\.aws\tictactest-key.pem"
    $ip = "<EC2-PUBLIC-IP>"
    ssh -i $key ubuntu@$ip
}

# Speichern, dann in neuem Terminal:
connect-tictactest
```

**EC2 IP merken:**

Wenn EC2 Instanz gestoppt/gestartet wird, ändert sich die IP. Besser:
1. Elastische IP beantragen (AWS → Elastic IPs)
2. Instanz mit Elastic IP verbinden
3. IP bleibt immer gleich

---

## 📞 Fragen?

Siehe Logs: `sudo journalctl -u tictactest -f`
Oder: https://github.com/FlyCaptainRex/TicTacTest/issues

**Viel Erfolg mit Auftrag 2! 🚀**
