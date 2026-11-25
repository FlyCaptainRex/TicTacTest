/**
 * Dynamische Konfiguration für Frontend
 * Erkennt automatisch die Backend-URL basierend auf Umgebung
 */

(function() {
    // Backend-URL basierend auf Umgebung erkennen
    function getBackendUrl() {
        // Lokale Entwicklung (localhost:8000)
        if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
            return 'http://localhost:8080';
        }
        
        // Production (AWS EC2 oder Cloud)
        // Backend läuft auf Port 8080 auf demselben Host
        const protocol = window.location.protocol;
        const hostname = window.location.hostname;
        return `${protocol}//${hostname}:8080`;
    }

    const backendUrl = getBackendUrl();
    
    // Anzeige in Footer
    document.addEventListener('DOMContentLoaded', function() {
        document.getElementById('backend-url').textContent = backendUrl;
        
        // Terminal-URL für ttyd
        const terminalUrl = `${backendUrl}/`;
        document.getElementById('terminal-iframe').src = terminalUrl;
        
        // Health-Check
        checkBackendHealth(backendUrl);
        
        // Version laden
        loadVersion();
        
        // Auto-reload bei Disconnect
        setupAutoReload();
    });

    /**
     * Prüft ob Backend erreichbar ist
     */
    function checkBackendHealth(url) {
        const statusEl = document.getElementById('status');
        
        fetch(`${url}/api/health`, {
            method: 'GET',
            mode: 'cors',
            cache: 'no-store'
        })
        .then(response => {
            if (response.ok) {
                statusEl.style.color = '#00ff00';
                statusEl.title = 'Backend verbunden';
            } else {
                throw new Error('Health check failed');
            }
        })
        .catch(error => {
            statusEl.style.color = '#ff0000';
            statusEl.title = 'Backend nicht erreichbar: ' + error.message;
            console.warn('⚠️ Backend Health Check fehlgeschlagen:', error);
        });
    }

    /**
     * Lädt Versions-Information
     */
    function loadVersion() {
        fetch('version.txt')
            .then(response => response.text())
            .then(version => {
                document.getElementById('version').textContent = `Version: ${version.trim()}`;
            })
            .catch(error => {
                console.warn('⚠️ Version nicht gefunden:', error);
                document.getElementById('version').textContent = 'Version: unbekannt';
            });
    }

    /**
     * Auto-Reload wenn Terminal-Verbindung unterbrochen
     */
    function setupAutoReload() {
        window.reloadGame = function() {
            const iframe = document.getElementById('terminal-iframe');
            const backendUrl = getBackendUrl();
            iframe.src = '';
            setTimeout(() => {
                iframe.src = `${backendUrl}/`;
            }, 500);
            
            const infoEl = document.getElementById('info-text');
            infoEl.textContent = '✅ Spiel neu geladen';
            setTimeout(() => {
                infoEl.textContent = '';
            }, 3000);
        };
        
        window.clearTerminal = function() {
            // Kann von ttyd aus nicht kontrolliert werden
            // Aber wir können einen UI-Hinweis geben
            const infoEl = document.getElementById('info-text');
            infoEl.textContent = '💡 Tipp: Geben Sie "clear" in der Konsole ein';
            setTimeout(() => {
                infoEl.textContent = '';
            }, 5000);
        };
    }

    // Globale Konfiguration exportieren
    window.TTY_CONFIG = {
        backendUrl: backendUrl,
        checkHealth: checkBackendHealth
    };
})();
