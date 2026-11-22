# Datenerhalt, Updates, Backups, Signierung

**Nicht verhandelbar:** Wer in Build 0.1 Eimer anlegt, muss sie in späteren Versionen wiederfinden. Eine neue APK allein garantiert das **nicht**.

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
