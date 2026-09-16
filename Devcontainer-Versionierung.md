# Devcontainer-Versionierung und Continuous Deployment

## Ziel

Der Devcontainer wird als Docker-Image in der GitHub Container Registry (GHCR)
veröffentlicht. Lokale Entwicklung und CI verwenden nur veröffentlichte Images.
Nicht freigegebene Builds werden weder automatisch gepusht noch verwendet.

## Versionskonzept

Es wird Semantic Versioning verwendet:

- `vMAJOR.MINOR.PATCH`, zum Beispiel `v1.0.0`
- `MAJOR`: inkompatible Änderung an Umgebung oder Werkzeugen
- `MINOR`: rückwärtskompatible Funktion oder zusätzliches Werkzeug
- `PATCH`: Fehlerbehebung oder Aktualisierung ohne Änderung der Schnittstelle

Ein Image wird nur veröffentlicht, wenn ein geschützter Git-Tag im Format
`vX.Y.Z` auf `main` gepusht wird. Der Tag ist unveränderlich und verweist auf
genau eine freigegebene Image-Version.

## Freigabeprozess

1. Änderungen am Dockerfile oder an der Devcontainer-Konfiguration werden über
   einen Pull Request geprüft.
2. Nach erfolgreicher CI-Prüfung wird der Pull Request nach `main` gemergt.
3. Ein Maintainer erstellt und pusht einen geschützten Tag, zum Beispiel
   `v1.0.0`.
4. Die GitHub-Action baut das Image und pusht es mit den Tags `v1.0.0` und
   `stable` nach `ghcr.io/flycaptainrex/tictactest-devcontainer`.
5. Erst nach einem erfolgreichen Push wird automatisch ein Pull Request
   erstellt, der `devcontainer.json` auf die konkrete Version aktualisiert.
6. Nach Prüfung und Merge dieses PRs verwendet die lokale Umgebung die neue
   freigegebene Version.

In den Repository-Einstellungen müssen `main` und die Tags `v*` geschützt
werden. Für Tags muss der Push auf Maintainer oder einen Release-Bot begrenzt
sein. Dadurch können Feature-Branches keine offiziell verwendeten Images
veröffentlichen.

## Verwendung

Die lokale Devcontainer-Konfiguration verwendet den Alias `stable`. Dieser
Alias wird ausschließlich beim erfolgreichen Release aktualisiert. Nach dem
Merge des automatisch erstellten PRs kann der Container in VS Code neu gebaut
oder aktualisiert werden.

Die CI verwendet ebenfalls `:stable` und erhält dadurch automatisch die letzte
freigegebene Umgebung. Der Workflow startet nicht auf normalen Branch-Pushes
und kann daher kein unveröffentlichtes Image verwenden.

Der unveränderliche Versions-Tag bleibt für reproduzierbare Builds verfügbar:

```text
ghcr.io/flycaptainrex/tictactest-devcontainer:v1.0.0
```

## Ersteinrichtung

Der erste Release-Tag kann lokal oder über die GitHub-Oberfläche erstellt
werden:

```powershell
git tag v1.0.0
git push origin v1.0.0
```

Danach wartet man auf den automatisch erstellten PR und merged ihn nach der
Prüfung. Die Action benötigt `packages: write`, `contents: write` und
`pull-requests: write`; diese Berechtigungen sind im Workflow festgelegt.