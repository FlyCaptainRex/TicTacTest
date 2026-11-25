# Auftrag 1: Web-Konsole für TicTacTest

## 🎯 Ziel
Machen Sie das TicTacTest-Spiel über einen Browser spielbar, ohne dass Sie es lokal installieren müssen.

---

## 📋 Schritt-für-Schritt Anleitung

### **Phase 1: Vorbereitung (Lokal auf Windows)**

#### **Schritt 1.1: DevContainer öffnen**
```bash
# In VS Code:
# 1. Drücke F1
# 2. Tippe: "Dev Containers: Reopen in Container"
# 3. Warte, bis der Container gestartet ist
```

Warum? Der DevContainer hat bereits Java und alle Tools installiert. Es ist sauberer als alles lokal zu installieren.

#### **Schritt 1.2: JAR bauen (optional - macht das System automatisch)**
```bash
# Im DevContainer Terminal (nicht PowerShell!):
cd /workspaces/TicTacTest/450-tictactest-mvk
./gradlew jar
```

JAR sollte hier sein: `build/libs/tictactoe.jar`

#### **Schritt 1.3: ttyd im DevContainer installieren**
```bash
# Im DevContainer Terminal:
sudo apt-get update
sudo apt-get install -y ttyd
```

**Warum ttyd?**
- Cross-Platform (Linux, macOS, Windows mit WSL)
- Bidirektionales I/O (Eingabe + Ausgabe)
- ANSI-Farben werden korrekt dargestellt
- Leichtgewichtig
- Perfekt für Java-Console-Apps

---

### **Phase 2: Lokales Testen (Auftrag 1 Anforderung)**

#### **Schritt 2.1: Terminal-Server starten**
```bash
# Im DevContainer Terminal:
cd /workspaces/TicTacTest/450-tictactest-mvk
ttyd -p 8080 java -jar build/libs/tictactoe.jar
```

**Was passiert:**
- ttyd startet auf Port 8080
- Öffnet einen Web-Terminal-Server
- Führt das Java-Spiel darin aus

#### **Schritt 2.2: Frontend-Server starten (neues Terminal)**
```bash
# Öffne ein ZWEITES Terminal im DevContainer:
cd /workspaces/TicTacTest/450-tictactest-mvk/frontend
python3 -m http.server 8000
```

**Output sollte sein:**
```
Serving HTTP on 0.0.0.0 port 8000 (http://0.0.0.0:8000/) ...
```

#### **Schritt 2.3: Browser öffnen**
```
http://localhost:8000
```

**Du solltest sehen:**
- Grüner Terminal mit schwarzem Hintergrund (Retro-Look)
- TicTacTest Spielfeld als ASCII-Art
- "Version: v0.0.1" oben rechts

#### **Schritt 2.4: Spielen!**
```
 1 | 2 | 3
-----------
 4 | 5 | 6
-----------
 7 | 8 | 9
```

Gib eine Zahl von 1-9 ein und drücke Enter!

---

## 🔧 Troubleshooting (Lokal)

### Problem: "Port 8080 bereits in Benutzung"
```bash
# Andere Ports verwenden:
ttyd -p 9090 java -jar build/libs/tictactoe.jar
# Dann im Browser: http://localhost:8000 aufrufen und config.js anpassen
```

### Problem: "ttyd: command not found"
```bash
# Vergewissere dich, dass du im DevContainer bist!
# Nicht auf Windows PowerShell!
```

### Problem: JAR nicht vorhanden
```bash
# JAR neu bauen:
./gradlew clean jar
# Output: build/libs/tictactoe.jar
```

---

## 📦 Dateistruktur (Auftrag 1)

```
frontend/
├── index.html          # HTML-Seite mit iframe für ttyd
├── style.css           # Grüner Terminal-Look
├── config.js           # Dynamische Backend-URL
└── version.txt         # Version v0.0.1

tictactoe.jar          # Kompiliertes Spiel
```

---

## ✅ Checkliste für Auftrag 1

- [ ] DevContainer öffnen
- [ ] JAR mit `./gradlew jar` bauen
- [ ] ttyd mit `apt-get install ttyd` installieren
- [ ] Terminal mit `ttyd -p 8080 java -jar build/libs/tictactoe.jar` starten
- [ ] Frontend mit `python3 -m http.server 8000` starten
- [ ] Browser: `http://localhost:8000` öffnen
- [ ] Mindestens 1 Spiel spielen und gewinnen/verlieren
- [ ] Versionsinfo oben rechts sichtbar
- [ ] Green-on-Black Terminal sichtbar

---

## 🎯 Lernziele (Auftrag 1)

Du hast gelernt:
1. ✅ Wie man ein CLI-Spiel in einen Web-Terminal bringt
2. ✅ Wie ttyd funktioniert (bidirektionales I/O über HTTP)
3. ✅ Wie man ein einfaches HTML-Frontend mit iframe erstellt
4. ✅ Dynamische URL-Erkennung für verschiedene Umgebungen
5. ✅ ANSI-Escape-Codes bleiben in Web-Terminals erhalten

---

## 🚀 Nächste Schritte: Auftrag 2 (AWS EC2)

Nach Auftrag 1 ist klar, dass:
- Das Spiel funktioniert im Web
- ttyd ist das richtige Tool
- Frontend ist modular und einfach

**Auftrag 2** werden wir:
1. AWS EC2 Instanz starten
2. Dieselbe Setup reproduzieren (aber auf Linux Server)
3. Systemd Services für Auto-Start erstellen
4. Nginx als Reverse-Proxy konfigurieren
5. Von überall erreichbar machen

---

## 💡 Pro-Tipps

**Mehrere Instanzen parallel testen:**
```bash
# Terminal 1: Spiel 1
ttyd -p 8080 java -jar build/libs/tictactoe.jar

# Terminal 2: Spiel 2 (andere Instanz)
ttyd -p 8081 java -jar build/libs/tictactoe.jar

# Frontend 2 Browser-Tabs öffnen
# Tab 1: http://localhost:8000 (Port 8080)
# Tab 2: http://localhost:8000 (Port 8081)
# Diese spielen UNABHÄNGIG voneinander!
```

**SSH-Tunnel für Remote-Testing:**
```bash
# Falls Spiel auf Remote-Server läuft:
ssh -L 8080:localhost:8080 user@server.com
# Dann lokal: http://localhost:8080
```

---

## 🔒 Sicherheit (Warum nur JAR, nicht Shell?)

```bash
# ❌ NICHT SO:
ttyd -p 8080 /bin/bash
# Problem: Jeder hat Shell-Zugriff! Datenlecks, Malware, Ransomware

# ✅ SO:
ttyd -p 8080 java -jar tictactoe.jar
# Sicher: Nur Spiel läuft, keine Shell-Befehle möglich
```

---

## 📞 Fragen?

Falls etwas nicht funktioniert:
1. Checke die Checkliste oben
2. Schau ins Troubleshooting-Kapitel
3. Vergewissere dich, dass du im **DevContainer** bist, nicht auf Windows PowerShell
4. `java -version` sollte Java 25 zeigen
5. `ttyd --version` sollte Version anzeigen

**Viel Spaß mit Auftrag 1! 🎮**
