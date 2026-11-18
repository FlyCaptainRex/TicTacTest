# Release- und Versionierungskonzept (TicTacTest)

## 1. Zielsetzung
Dieses Konzept definiert den Release-, Versionierungs- und Umgebungsprozess für das Projekt **TicTacTest** gemäss den Vorgaben von **Modul 324 (DevOps)**. Es stellt sicher, dass:
1. **Anwendungs-Binaries (ausführbare JARs)** nachvollziehbar, reproduzierbar und automatisiert als GitHub Releases bereitgestellt werden.
2. **Entwicklungsumgebungen (DevContainer)** standardisiert gebaut, getaggt und auf der GitHub Container Registry (GHCR) veröffentlicht werden.
3. **Branch Protection & PR-Gates** verhindern, dass ungetesteter oder fehlerhafter Code in den `main`-Branch gelangt.
4. **Image-Tags** im gesamten Repository konsistent und fehlerfrei aufeinander abgestimmt sind.

---

## 2. Versionsschema (Semantic Versioning)
Wir setzen strikt auf **Semantic Versioning 2.0.0 (SemVer)** mit dem Git-Tag-Präfix `vMAJOR.MINOR.PATCH` (z.B. `v1.0.0`):

* **MAJOR (`vX.0.0`):** Inkompatible Änderungen an der Schnittstelle oder der Systembasis (z.B. Java-Upgrade, grundlegende Restrukturierung der Spiellogik).
* **MINOR (`v1.X.0`):** Neue abwärtskompatible Funktionen (z.B. neuer Minimax-KI-Spieler `PerfectPlayer` oder erweiterte DevContainer-Werkzeuge).
* **PATCH (`v1.0.X`):** Rückwärtskompatible Fehlerbehebungen, Performance-Optimierungen oder Dokumentationsanpassungen.

---

## 3. Zwei parallele Release-Artefakte
Beim Release-Prozess entstehen automatisch zwei komplementäre Artefakte:

| Artefakt | Zielort | Zweck |
| :--- | :--- | :--- |
| **Executable Binary JAR** (`tictactoe.jar`) | **GitHub Releases** | Ausführbare Anwendung für Benutzer und Tester (`java -jar tictactoe.jar`). |
| **DevContainer Docker Image** | **GitHub Container Registry (GHCR)** | Standardisierte, reproduzierbare Entwicklungsumgebung für Entwickler und CI-Runner. |

---

## 4. Harmonisierung der Image-Tags
Ein zentraler Kritikpunkt unsauberer Umsetzungen sind abweichende oder fehlerhafte Image-Referenzen. Im TicTacTest-Projekt gilt eine durchgehende, einheitliche Konvention:

```
+------------------------------------+-----------------------------------------------------+
| Datei                              | Konfigurierte Image-Referenz                        |
+------------------------------------+-----------------------------------------------------+
| .devcontainer/devcontainer.json    | ghcr.io/flycaptainrex/tictactest-devcontainer:stable |
| .github/workflows/ci.yml           | ghcr.io/flycaptainrex/tictactest-devcontainer:stable |
| .github/workflows/release.yml      | ghcr.io/flycaptainrex/tictactest-devcontainer:stable |
+------------------------------------+-----------------------------------------------------+
```

In `.github/workflows/devcontainer.yml` wird sichergestellt, dass bei jedem Build auf GHCR sowohl der Alias `stable` als auch `latest`, der Git-Tag (`v1.0.0`) und der Commit-SHA (`sha-<commit>`) gepusht werden. Dadurch ist sichergestellt, dass alle Workflows und die lokale Entwicklungsumgebung stets dieselbe stabile Umgebung nutzen.

---

## 5. Der automatisierte Release-Prozess

```
Entwickler (main) ──> git tag v1.0.x ──> git push origin v1.0.x
                                │
        ┌───────────────────────┴───────────────────────┐
        ▼                                               ▼
.github/workflows/release.yml           .github/workflows/devcontainer.yml
        │                                               │
  1. Baut tictactoe.jar im Container             1. Validiert SemVer-Tag
  2. Erstellt GitHub Release                     2. Baut Docker-Image
  3. Hängt Binary JAR als Asset an               3. Pusht auf ghcr.io (:v1.0.x, :stable, :latest)
                                                 4. Erstellt Auto-PR für devcontainer.json
```

### Schritt 1: Entwicklung & Pull Request
* Entwicklung erfolgt ausschliesslich auf Feature-Branches.
* Jeder PR gegen `main` durchläuft die vollständige CI-Pipeline (`ci.yml`):
  * `./gradlew test` (JUnit 5, Pioneer, jqwik)
  * `./gradlew jacocoTestCoverageVerification` (90 % Coverage Gate)
  * `./gradlew pitest` (Mutation Testing)
  * Coverage PR-Gate (Vergleich mit `main`-Coverage)

### Schritt 2: Tagging durch Maintainer
Nach dem erfolgreichen Merge auf `main` erstellt ein Maintainer den offiziellen Release-Tag:
```bash
git tag v1.0.0
git push origin v1.0.0
```

### Schritt 3: Parallele GitHub Actions
1. **Binary Release (`release.yml`):**
   * Führt `./gradlew jar` innerhalb des DevContainers aus.
   * Das JAR enthält ein Manifest mit `Main-Class: ch.bbw.m450.tictactoe.TicTacToeMain`.
   * Ruft `gh release create` auf, generiert Release Notes aus den Commits (`--generate-notes`) und hängt `tictactoe.jar` als binäres Asset an.
2. **Container Release (`devcontainer.yml`):**
   * Prüft das SemVer-Format des Tags per Regex (`^v[0-9]+\.[0-9]+\.[0-9]+$`).
   * Baut das `.devcontainer/Dockerfile` und pusht die Tags nach GHCR.
   * Öffnet automatisch einen PR zur Aktualisierung der `devcontainer.json` auf das neu erstellte Image.

---

## 6. Branch Protection & Sicherheitsrichtlinien

Um die Stabilität von `main` dauerhaft zu garantieren, sind auf GitHub folgende **Branch Protection Rules** für `main` konfiguriert:

1. **Require a pull request before merging:**
   * Direkte Pushes auf `main` sind für alle Benutzer (auch Administratoren) blockiert.
   * Jede Änderung muss als Pull Request eingereicht werden.
2. **Require status checks to pass before merging:**
   * Der CI-Job `build` (aus `.github/workflows/ci.yml`) ist als zwingender Status-Check hinterlegt.
   * Ein Merge ist nur möglich, wenn:
     * Alle Unit-, Pioneer- und Property-Tests grün sind.
     * Das 90-%-JaCoCo-Coverage-Gate bestanden ist.
     * PIT Mutation Testing erfolgreich durchgelaufen ist.
3. **Require branches to be up to date before merging:**
   * Verhindert Merge-Konflikte und stellt sicher, dass gegen den aktuellsten Stand getestet wurde.
4. **Tag Protection:**
   * Schutzregel für Tags nach dem Muster `v*.*.*`: Nur Maintainer besitzen Schreibrechte zum Erstellen von Release-Tags.
5. **Least-Privilege Secret Management:**
   * Für den Upload auf die GitHub Container Registry wird ein PAT mit minimalen Rechten (`packages: write`, `packages: read`) als Repository-Secret `CR_PAT` gespeichert.
