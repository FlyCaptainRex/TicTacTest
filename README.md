# TicTacTest

> **BBW Module 450 (Applikationen testen) & Modul 324 (DevOps)**  
> Klasse 5IA23c · Berufsbildungsschule Winterthur

TicTacTest ist eine vollständige TicTacToe-Spielanwendung in Java 25 mit verschiedenen Spielerstrategien, einer umfassenden Testsuite (JUnit 5, AssertJ, JUnit Pioneer, jqwik), statischer und mutationsbasierter Testabdeckung (JaCoCo 90 % Gate, PIT) und einer reproduzierbaren CI/CD-Pipeline inklusive DevContainer- und Binary-JAR-Releases.

---

## 🎮 Enthaltene Spielerstrategien
* **`HumanPlayer`:** Interaktive Tastatureingabe über `System.in`.
* **`RandomPlayer`:** Wählt zufällige freie Felder.
* **`GreedyPlayer`:** Setzt sofortige Gewinnzüge um, falls vorhanden.
* **`PerfectPlayer`:** Perfekt spielende KI basierend auf dem **Minimax-Algorithmus** (kann mathematisch bewiesen niemals verlieren).

---

## 📚 Konzepte & Dokumentation
* **[Testkonzept](Testkonzept.md):** Testobjekt, ECP/BVA-Testentwurf, Testpyramide, Frameworks (JUnit 5, Pioneer, jqwik, PIT, JaCoCo) und Test-Gates.
* **[Release- & Versionierungskonzept](Releasekonzept.md):** SemVer 2.0.0, Binary-JAR-Release via GitHub Releases, DevContainer-Release via GHCR, Branch Protection & Image-Tag-Harmonisierung.
* **[Devcontainer-Versionierung](Devcontainer-Versionierung.md):** Ergänzende Details zum Build- und Freigabeprozess des DevContainers.

---

## 🛠️ Entwicklung & Ausführung

### 1. Ausführen des Spiels
```bash
# Über Gradle
./gradlew run --console=plain

# Als ausführbare Binärdatei (JAR)
./gradlew jar
java -jar build/libs/tictactoe.jar
```

### 2. Tests & Qualitätssicherung
```bash
# Alle Tests ausführen (JUnit 5, Pioneer, jqwik)
./gradlew test

# Testabdeckungsbericht generieren & 90-%-Gate prüfen
./gradlew jacocoTestReport jacocoTestCoverageVerification

# PIT Mutation Testing ausführen
./gradlew pitest
```

### 3. Aktuelle Qualitätsmetriken
* **Instruction Coverage:** **97.8 %** (Gate: $\ge 90\,\%$)
* **Branch Coverage:** **100.0 %** (Gate: $\ge 90\,\%$)
* **PIT Mutation Score:** **86.0 %** (73 von 85 Mutanten getötet)

---

## 🚀 CI/CD Pipelines (GitHub Actions)

* **`ci.yml` (Continuous Integration):**
  * Führt alle Tests, JaCoCo-Coverage-Verification und PIT Mutation Testing im DevContainer aus.
  * Aktualisiert bei Pushes auf `main` die historische Coverage-Zeitreihe auf `gh-pages`.
  * Prüft bei Pull Requests via **PR-Gate**, ob die Coverage im Vergleich zu `main` nicht sinkt.
* **`devcontainer.yml` (DevContainer Continuous Delivery):**
  * Baut bei SemVer-Tags (`v*.*.*`) das Container-Image und pusht es auf GHCR (`ghcr.io/flycaptainrex/tictactest-devcontainer:stable` und `:vX.Y.Z`).
  * Erstellt einen automatischen Pull Request zur Aktualisierung von `devcontainer.json`.
* **`release.yml` (Binary Application Release):**
  * Erstellt bei SemVer-Tags ein offizielles GitHub Release mit generierten Release Notes.
  * Baut und hängt die ausführbare Binärdatei `tictactoe.jar` als Download-Asset an.

