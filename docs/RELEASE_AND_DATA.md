# Datenerhalt, Updates, Backups, Signierung

**Nicht verhandelbar:** Wer in Build 0.1 Eimer anlegt, muss sie in späteren Versionen wiederfinden. Eine neue APK allein garantiert das **nicht**.

## Privater Fundort des Originalschlüssels

Der dokumentierte Fundort ist die **private ChatGPT-Dateibibliothek des Projektinhabers**. Suche dort nach dem exakten Dateinamen `Eimer-Liste-Update-Schluessel.p12`. Die zugehörige Passwortdatei lag beim Signieren von Build 38 in derselben privaten Bibliothek. Einen stabilen Datei-Link oder eine Datei-ID nennt der damalige Bericht nicht. [Signiernachweis zu Build 38](https://github.com/shayohekmat-wq/Zimt-Lab-2/blob/5a92737946d451c895cd91b3ad42ba68a8520f3f/docs/BUILD_PLAN.md#L455)

Ein autorisierter Lauf mit Bibliothekszugriff kann beide Dateien im privaten Signierkontext verwenden. In Codex kann die Auswahl eingebundener Bibliotheksdateien unter `/workspace/library-files` pro Lauf variieren. Prüfe den Zertifikatsfingerabdruck der signierten APK gegen die Release-Checkliste und vergleiche die APK mit dem gebauten Commit. Übergib nachfolgenden Läufen den Artefaktverweis und das Prüfergebnis. Für CI sind die vier vorhandenen Secret-Namen unten dokumentiert. Der Entwicklungsbranch baut das unsignierte Release für die private Signierung.

## Identität
- `applicationId` bleibt über alle eigenständigen Lab-Builds identisch.
- `versionCode` steigt monoton; auch Rückrollversuche werden explizit behandelt.
- Alle für echte Nutzung vorgesehenen APKs werden mit **demselben privaten Release-Key** signiert.
- Debug-Key aus Android Studio oder kurzlebigen CI-Agenten ist nicht als Produktions-/Update-Key geeignet.
- Keystore niemals in GitHub-Commits, Issues, Releases oder unverschlüsselten Artefakten ablegen. Recovery-Kopie außerhalb Git.

## Migration
- Room/SQLite-Schema explizit versionieren.
- Bei jeder Änderung: Migration schreiben, von allen bereits veröffentlichten Schemaversionen testen.
- Kein destruktives Fallback und keine App-ID-Wechsel.
- Vor größeren Updates: exportierbares, versioniertes Backupformat (JSON plus Assets in ZIP), validierbarer Restore. Backups nicht nur als lose App-Cachedaten.
- Android Auto Backup ist zusätzlich nützlich, aber kein Ersatz für einen prüfbaren Export.
- Deinstallation kann lokale Daten löschen; Fernsync hilft nur für bereits hochgeladene Daten.

## Gemeinsam & offline
- Lokal gespeicherte Notizen überleben Prozessabbruch, Flugmodus und erneutes Starten.
- Ein Sync-Fehler darf weder die lokale Kopie löschen noch einen Erfolg vortäuschen.
- Für neue Geräte ist wiederherstellbare Identität / Anmeldung erforderlich.
- Nutzer muss Mitglied eines Eimers sein; private Inhalte niemals über erratbare ID abrufen.

## Erstes echtes Installationspaket
Ein produktiv verwendbarer APK-Download wird erst als 'updatefähig' bezeichnet, wenn:
1. Der Release-Key gesichert und in GitHub Actions **als Secrets**, nicht im Quelltext, hinterlegt ist.
2. CI eine signierte APK erzeugt und ihre Signatur verifiziert.
3. Ein Geräte-Test Altversion → neuere Version mit existierenden Eimern/Zetteln **ohne Datenverlust** bestanden wurde.
4. Ein Export/Import-Wiederherstellungstest bestanden wurde.

Solange diese Punkte fehlen, liefern CI-Builds lediglich **temporäre Test-APKs** und sollten nicht mit wichtigen Eimerlisten befüllt werden.

## ZIMT-Übernahme
Eine Integration in die bestehende ZIMT-Paketkennung erfordert ein einmaliges, getestetes Export-/Importverfahren oder serverseitige Kontomigration. Android führt lokale Daten verschiedener App-IDs nicht automatisch zusammen.

## 0.1 Release-Pipeline (erstes produktives APK)
- CI baut eine **unsignierte Release-APK** und testet zusätzlich den Debug-Build.
- Die App wird **außerhalb** der öffentlichen CI mit einem dauerhaften privaten Keystore signiert.
- Der Keystore muss sicher beim Besitzer verbleiben. Jeder spätere Installer muss mit genau diesem Schlüssel signiert werden, sonst verweigert Android das Update.
- Der signierte Installer `Eimer-Liste-v0.1.0.apk` ist der Beginn der Update-Kette; Debug-APKs dürfen **nicht** über diesen Installer installiert werden.
- Installation der nächsten Version: gleiche `applicationId` und derselbe Keystore, höherer `versionCode`.
- CI stellt bewusst **keinen** Signaturschlüssel bereit und veröffentlicht kein signiertes APK; dies verhindert Schlüsselabfluss über öffentliche Artefakte.


## Automatische signierte Drei-Stunden-Builds (ab 0.2)

Die CI erzeugt auf jedem Commit eine unsignierte Release-APK und die Debug-Tests. Wenn (und **nur wenn**) folgende vier verschlüsselten GitHub-Actions-Repository-Secrets einmalig vom Repository-Eigentümer hinterlegt werden, signiert CI zusätzlich eine herunterladbare Update-APK:

1. `EIMER_RELEASE_KEYSTORE_B64`: Base64-Kodierung der bestehenden `Eimer-Liste-Update-Schluessel.p12` **ohne Zeilenumbrüche**.
2. `EIMER_RELEASE_STORE_PASSWORD`: Passwort dieser bestehenden .p12.
3. `EIMER_RELEASE_KEY_ALIAS`: Alias des enthaltenen PrivateKeyEntry.
4. `EIMER_RELEASE_KEY_PASSWORD`: Schlüsselpasswort (kann gleich dem Store-Passwort sein).

**Keines dieser Geheimnisse in Chatnachrichten, Commits, Issues, Actions-Logs oder öffentlichen Dateien posten.** Sie gehören ausschließlich in GitHub → Repository → Settings → Secrets and variables → Actions → New repository secret. API-Zugriff auf diese Secret-Einstellung ist in diesem Projektanschluss momentan nicht verfügbar; eine Einrichtung durch den Eigentümer ist nötig.

GitHub Actions verifiziert das resultierende APK anhand des festen Zertifikats-Fingerprints `40e6b73383a29f9ca792595b369329e2ab8f9165f49eec69b4eb57a8ffe26fe1` und der APK-v2/v3-Signaturen. Bei falschem Schlüssel schlägt der Build fehl, **keine** scheinbar updatefähige APK wird veröffentlicht.

Der aktuelle Codex-Entwicklungsauftrag prüft die Build-Ergebnisse stündlich. Sie liefert eine neue APK nur dann als direkt aktualisierbare Datei aus, wenn Build, Signaturprüfung und verfügbarer Download nachweisbar sind. Ohne Secrets kann sie weiterhin dokumentierte Build-Pläne, Quellcode und unsignierte Testpakete erzeugen, aber nicht zuverlässig einen updatefähigen Release über verschiedene Ausführungsumgebungen hinweg signieren.
