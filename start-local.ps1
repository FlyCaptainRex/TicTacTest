# Quick-Start für Auftrag 1: Web-Konsole lokal testen (Windows PowerShell)
#
# Im DevContainer Terminal (nicht PowerShell!) ausführen:
#   chmod +x start-local.ps1
#   pwsh ./start-local.ps1
#

Write-Host "🚀 Starte TicTacTest Web-Konsole (Auftrag 1)" -ForegroundColor Green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Green
Write-Host ""

$ProjectDir = "/workspaces/TicTacTest/450-tictactest-mvk"
$FrontendDir = "$ProjectDir/frontend"
$JarPath = "$ProjectDir/build/libs/tictactoe.jar"

# Schritt 1: JAR bauen falls nicht vorhanden
if (-not (Test-Path $JarPath)) {
    Write-Host "📦 Baue JAR (tictactoe.jar)..." -ForegroundColor Cyan
    Set-Location $ProjectDir
    & ./gradlew jar -q
    Write-Host "✅ JAR gebaut: $JarPath" -ForegroundColor Green
    Write-Host ""
}

# Schritt 2: ttyd installieren falls nicht vorhanden
$ttydCheck = & which ttyd 2>$null
if (-not $ttydCheck) {
    Write-Host "📥 Installiere ttyd..." -ForegroundColor Cyan
    & sudo apt-get update -qq 2>$null
    & sudo apt-get install -y ttyd >$null 2>&1
    Write-Host "✅ ttyd installiert" -ForegroundColor Green
    Write-Host ""
}

# Schritt 3: Terminal-Server im Hintergrund starten
Write-Host "🎮 Starte Terminal-Server auf Port 8080..." -ForegroundColor Cyan
& ttyd -p 8080 java -jar $JarPath &
$TtyPid = $?
Write-Host "✅ Terminal-Server gestartet" -ForegroundColor Green
Start-Sleep -Seconds 2
Write-Host ""

# Schritt 4: Frontend-Server starten
Write-Host "🌐 Starte Frontend-Server auf Port 8000..." -ForegroundColor Cyan
Set-Location $FrontendDir
& python3 -m http.server 8000 &
$FrontendPid = $?
Write-Host "✅ Frontend-Server gestartet" -ForegroundColor Green
Start-Sleep -Seconds 1
Write-Host ""

# Schritt 5: Browser-Info
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Green
Write-Host "✅ Alles läuft!" -ForegroundColor Green
Write-Host ""
Write-Host "📍 Öffne im Browser: http://localhost:8000" -ForegroundColor Yellow
Write-Host ""
Write-Host "Terminal-Server: http://localhost:8080" -ForegroundColor Cyan
Write-Host "Frontend-Server: http://localhost:8000" -ForegroundColor Cyan
Write-Host ""
Write-Host "🎮 Spielfeld:" -ForegroundColor Magenta
Write-Host "  1 | 2 | 3"
Write-Host "  ---------"
Write-Host "  4 | 5 | 6"
Write-Host "  ---------"
Write-Host "  7 | 8 | 9"
Write-Host ""
Write-Host "Zum Beenden: Drücke Ctrl+C" -ForegroundColor Yellow
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Green
Write-Host ""

# Warte auf Ctrl+C
Write-Host "⏳ Server läuft..." -ForegroundColor Cyan
while ($true) {
    Start-Sleep -Seconds 10
}
