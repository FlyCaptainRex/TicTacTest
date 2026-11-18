# Testkonzept: TicTacTest (M450 & M324)

## 1. Zielsetzung und Scope
Das Projekt **TicTacTest** umfasst eine TicTacToe-Spielelogik in Java 25 mit verschiedenen Spieler-Implementierungen (Mensch, Zufallsspieler, gierige KI und fehlerfreie Minimax-KI).
Ziel dieses Testkonzepts ist es:
* Eine lückenlose Verifikation der Spiellogik (Zustandsübergänge, Gewinnprüfung, Unentschieden) zu gewährleisten.
* Die verschiedenen Spieler-Strategien systematisch auf Regelkonformität und Zielerreichung zu prüfen.
* Ein automatisiertes Qualitäts-Gate (90 % Coverage, Mutation Testing) in der CI/CD-Pipeline durchzusetzen.

---

## 2. Testobjekt und Systemarchitektur

### 2.1 Komponenten
* **`TicTacToe`:** Zustandsmaschine des Spiels. Verwaltet das Spielfeld (`Stone[9]`), den aktuellen Zug, Zuggesteuerung, Siegprüfung (`isGameOver`, `getWinner`) und Spielabbruch.
* **`TicTacToePlayer` (Interface):** Schnittstelle für Spielstrategien (`play(Stone[] board)`).
* **Spieler-Implementierungen:**
  * `HumanPlayer`: Liest Spielzüge über Standard-Input (`System.in`).
  * `RandomPlayer`: Wählt zufällig ein freies Spielfeld.
  * `GreedyPlayer`: Versucht sofortige Gewinnzüge zu machen; andernfalls Zufall.
  * `PerfectPlayer`: Implementiert den Minimax-Algorithmus; kann mathematisch bewiesen niemals verlieren (spielt immer optimal).

### 2.2 System-Invarianten & Spielfeld
* Das Spielfeld besteht aus 9 Feldern (Indizes `0` bis `8`).
* Ein Zug ist nur auf einem noch leeren Feld (`Stone.NONE`) zulässig.
* Es existieren genau 8 Gewinnreihen:
  * 3 Zeilen: `[0,1,2]`, `[3,4,5]`, `[6,7,8]`
  * 3 Spalten: `[0,3,6]`, `[1,4,7]`, `[2,5,8]`
  * 2 Diagonalen: `[0,4,8]`, `[2,4,6]`
* Sobald ein Spieler 3 Steine in einer Gewinnreihe hat, ist das Spiel beendet (`isGameOver() == true`).
* Sind alle 9 Felder belegt ohne 3er-Reihe, endet das Spiel unentschieden (`isGameOver() == true`, `getWinner() == Stone.NONE`).

---

## 3. Testentwurfsmethoden

### 3.1 Äquivalenzklassenbildung (ECP)
Für Eingaben bei Spielzügen und Methodenaufrufen:
* **Gültige Züge:** Indizes `[0..8]`, Feldwert ist aktuell `Stone.NONE`.
* **Ungültige Züge (Feld besetzt):** Indizes `[0..8]`, Feldwert ist `Stone.RED` oder `Stone.BLUE`.
* **Ungültige Züge (Out-of-Bounds):** Indizes `< 0` oder `> 8`.

### 3.2 Grenzwertanalyse (BVA)
* Unterste Feldgrenze: Index `0` (Ecke oben links).
* Oberste Feldgrenze: Index `8` (Ecke unten rechts).
* Spielrundenanzahl:
  * Minimal mögliche Spielzüge bis Sieg: 5 Züge (z.B. X gewinnt in Zug 5).
  * Maximal mögliche Züge: 9 Züge (Unentschieden oder Sieg im letzten Zug).

### 3.3 Entscheidungstabellen
Für die Gewinnermittlung (`isGameOver` / `getWinner`) werden alle 8 Linien systematisch per Testfällen abgedeckt:
* Horizontale Siege (Zeile 1, Zeile 2, Zeile 3)
* Vertikale Siege (Spalte 1, Spalte 2, Spalte 3)
* Diagonale Siege (Hauptdiagonale `\`, Nebendiagonale `/`)
* Nicht-Sieg-Zustände (unvollständige Reihen, wechselnde Steine `X-O-X`)

---

## 4. Teststufen und Testpyramide

```
               /\
              /  \      System- & Integrationstests
             /    \     (Vollständige Spielabläufe, I/O mit Pioneer)
            /------\
           /        \   Komponententests & Property-Tests
          /          \  (KI-Strategien, Minimax-Garantie, jqwik Invarianten)
         /------------\
        /              \ Unit-Tests (JUnit 5, AssertJ)
       /________________\ (TicTacToe Kernlogik, 8 Gewinnreihen, ECP/BVA)
```

1. **Unit-Tests (Basis):**
   * Isolierte Prüfung von `TicTacToe`-Methoden (`isGameOver`, `checkWin`, `play`).
   * Parametrisierte Tests (`@ParameterizedTest`, `@CsvSource`) für alle 8 Gewinnlinien und ungültige Eingaben.
2. **Komponenten- & Strategie-Tests:**
   * Verifikation von `PerfectPlayer`: Darf gegen keinen Spieler verlieren. Bei Spiel gegen sich selbst zwingend Unentschieden.
   * `jqwik` Property-Based Testing: Generiert hunderte zufällige Spielfeldzustände und verifiziert Invarianten (kein Spieler darf auf belegte Felder setzen).
3. **Integrationstests (I/O & Ablauf):**
   * Prüfung von `HumanPlayer` mittels **JUnit Pioneer** (`@StdIo`), um Tastatureingaben deterministisch zu simulieren und Exceptions bei Falscheingaben zu validieren.

---

## 5. Eingesetzte Testwerkzeuge & Frameworks

| Technologie | Version | Einsatzzweck im Projekt |
| :--- | :--- | :--- |
| **JUnit Jupiter (5)** | `5.11.4` | Test-Runner, Lebenszyklus (`@BeforeEach`), Parametrisierung (`@ParameterizedTest`). |
| **AssertJ** | `3.26.3` | Fluente, ausdrucksstarke Assertions (`assertThat(winner).isEqualTo(Stone.RED)`). |
| **JUnit Pioneer** | `2.3.0` | Simulation von Konsoleneingaben (`@StdIo("4\n")`) für `HumanPlayer`. |
| **jqwik** | `1.9.2` | Property-Based Testing zur automatischen Erkennung von unerwarteten Randfällen. |
| **JaCoCo** | `0.8.12` | Code-Coverage-Messung mit striktem Gate in Gradle. |
| **PIT (Pitest)** | `1.17.4` | Mutation Testing: Prüft die Qualität und Effektivität der Test-Assertions. |

---

## 6. Qualitätskriterien und Test-Gates

### 6.1 JaCoCo Coverage Gate (90 %)
In `build.gradle` ist `jacocoTestCoverageVerification` direkt an den Task `check` gebunden:
* **Branch-Abdeckung:** $\ge 90\,\%$ (Aktuell: **100.0 %**)
* **Instruction-Abdeckung:** $\ge 90\,\%$ (Aktuell: **97.8 %**)
* Verhindert Builds und Commits, wenn neue Logik ohne ausreichende Tests implementiert wird.

### 6.2 Mutation Testing (PIT)
* Verhindert "Scheintests" (hohe Coverage ohne echte Assertions).
* PIT injiziert Mutanten (Bedingungsumkehrung, Rückgabewertänderung, Rechenoperatoren).
* Zielvorgabe: **Mutation Test Strength $\ge 80\,\%$** (Aktuell: **86 % Mutation Score** mit 73 getöteten Mutanten).

### 6.3 CI/CD & Time-Series PR-Gate
* **Time-Series Coverage:** Jeder Push auf `main` aktualisiert `coverage-history.csv` auf dem Branch `gh-pages` und visualisiert den Trend via HTML/Chart.
* **PR-Gate:** Bei jedem Pull Request vergleicht die GitHub Action die Branch-Coverage mit dem `main`-Stand. Fällt die Coverage, schlägt der CI-Check fehl und blockiert den Merge.
