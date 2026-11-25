#!/bin/bash
#
# Quick-Start für Auftrag 1: Web-Konsole lokal testen
# 
# Nutzung:
#   chmod +x start-local.sh
#   ./start-local.sh
#

set -e

PROJECT_DIR="/workspaces/TicTacTest/450-tictactest-mvk"
FRONTEND_DIR="$PROJECT_DIR/frontend"
JAR_PATH="$PROJECT_DIR/build/libs/tictactoe.jar"

echo "🚀 Starte TicTacTest Web-Konsole (Auftrag 1)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Schritt 1: JAR bauen falls nicht vorhanden
if [ ! -f "$JAR_PATH" ]; then
    echo "📦 Baue JAR (tictactoe.jar)..."
    cd "$PROJECT_DIR"
    ./gradlew jar -q
    echo "✅ JAR gebaut: $JAR_PATH"
    echo ""
fi

# Schritt 2: ttyd installieren falls nicht vorhanden
if ! command -v ttyd &> /dev/null; then
    echo "📥 Installiere ttyd..."
    sudo apt-get update -qq
    sudo apt-get install -y ttyd > /dev/null 2>&1
    echo "✅ ttyd installiert"
    echo ""
fi

# Schritt 3: Terminal-Server im Hintergrund starten
echo "🎮 Starte Terminal-Server auf Port 8080..."
ttyd -p 8080 java -jar "$JAR_PATH" &
TTY_PID=$!
echo "✅ Terminal-Server PID: $TTY_PID"
sleep 2
echo ""

# Schritt 4: Frontend-Server starten
echo "🌐 Starte Frontend-Server auf Port 8000..."
cd "$FRONTEND_DIR"
python3 -m http.server 8000 &
FRONTEND_PID=$!
echo "✅ Frontend-Server PID: $FRONTEND_PID"
sleep 1
echo ""

# Schritt 5: Browser-Info
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Alles läuft!"
echo ""
echo "📍 Öffne im Browser: http://localhost:8000"
echo ""
echo "Terminal-Server: http://localhost:8080"
echo "Frontend-Server: http://localhost:8000"
echo ""
echo "🎮 Spielfeld:"
echo "  1 | 2 | 3"
echo "  ---------"
echo "  4 | 5 | 6"
echo "  ---------"
echo "  7 | 8 | 9"
echo ""
echo "Zum Beenden: Drücke Ctrl+C (beendet beide Server)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Cleanup bei Ctrl+C
trap cleanup EXIT INT TERM
cleanup() {
    echo ""
    echo "🛑 Fahre Server herunter..."
    kill $TTY_PID 2>/dev/null || true
    kill $FRONTEND_PID 2>/dev/null || true
    wait $TTY_PID 2>/dev/null || true
    wait $FRONTEND_PID 2>/dev/null || true
    echo "✅ Server beendet"
}

# Warte auf Ctrl+C
wait
