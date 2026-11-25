# Datenerhalt, Updates, Backups, Signierung

**Nicht verhandelbar:** Wer in Build 0.1 Eimer anlegt, muss sie in späteren Versionen wiederfinden. Eine neue APK allein garantiert das **nicht**.

## Privater Signierzugang für laufende Updates

Der bisher belegte Signierweg verwendet die beiden Originaldateien aus der [privaten Dateibibliothek](#privater-fundort-des-originalschlüssels). Ein dauerhaft erreichbarer Signierrechner ist bislang unbestätigt. Sobald ein solcher Zugang eingerichtet ist, prüft der autorisierte Lauf dort die Registrierung mit:

```sh
python3 tools/eimer_signer.py status
```

Der Befehl prüft den fest hinterlegten SHA-256-Fingerabdruck des Zertifikats und fordert mit dem zugehörigen privaten Schlüssel eine Signatur an. Ein erfolgreicher Aufruf genügt, um mit dem Signieren fortzufahren. Für das Werkzeug werden Python ab 3.11 und JDK-`keytool` benötigt. Zum Signieren müssen außerdem `aapt` und `apksigner` aus den Android SDK Build Tools im `PATH` stehen.

**Ziel einer dauerhaften Registrierung.** Das Standardverzeichnis ist `~/.local/share/zimt/eimerliste` (bei gesetztem `XDG_DATA_HOME` entsprechend unter diesem Datenverzeichnis). Ein bereits vorhandener privater, dauerhaft gespeicherter Bestand kann dem Signierprozess über den absoluten Verzeichnispfad `EIMER_SIGNING_DIR` bereitgestellt werden. Die Ablage enthält `Eimer-Liste-Update-Schluessel.p12`, `store.password` und `key.password`. Verzeichnis nur für den Signierbenutzer zugänglich (0700), Dateien 0600 oder nur lesbar 0400. Die Verbindung zum privaten Bestand muss bei einem Rechnerwechsel einmal für den neuen Signierkontext eingerichtet werden. Die Variable enthält ausschließlich einen Pfad.

**Einmalige Registrierung.** Auf einem zugriffsbeschränkten Signierrechner mit verschlüsseltem persistentem Speicher die Originaldatei und die private Passwortdatei aus der Bibliothek einbinden. Das Werkzeug erwartet in der Passwortdatei das Passwort als einzelne Zeile. Wenn die Bibliotheksdatei Begleittext enthält, den Wert im privaten Kontext in einer geschützten Eingabedatei bereitstellen. Nur vertrauenswürdigen, geprüften Code im Signierkontext ausführen. Auf flüchtigen Build-Runnern und in geteilten Arbeitsbereichen keine Signierablage anlegen.

```sh
python3 tools/eimer_signer.py enroll /privater/import/Eimer-Liste-Update-Schluessel.p12 /privater/import/store.password
```

Bei getrenntem Schlüsselpasswort zusätzlich `--key-password-file /privater/import/key.password` angeben. Das Werkzeug erkennt den Alias über das Originalzertifikat, prüft den privaten Zugriff und registriert die Identität erst nach erfolgreicher Prüfung. Wiederholung prüft die schon registrierte Identität. Der stündliche Entwicklungsauftrag benötigt ausschließlich den Verweis auf diesen Zugang.

**Kandidaten signieren.** Zuerst den erfolgreichen CI-Lauf, den Quell-Commit und die Prüfsumme der entpackten APK abgleichen. Die höchste bereits ausgegebene Versionsnummer aus der Release-Historie ermitteln. Im folgenden Beispiel bezeichnen die Variablen öffentliche Build-Metadaten.

```sh
python3 tools/eimer_signer.py sign app-release-unsigned.apk Eimer-Liste-UPDATE.apk \
  --sha256 "$CI_APK_SHA256" --previous-version-code "$HOECHSTER_AUSGEGEBENER_VERSIONSCODE"
```

Das Werkzeug gibt die APK erst nach Prüfung von Paketkennung, Versionsfolge, APK-Signaturen v2 und v3 sowie Originalzertifikat aus. Den ausgegebenen SHA-256 der signierten APK zusammen mit dem ursprünglichen Build-Commit und CI-Lauf dokumentieren. Geräte- und Datenprüfung gemäß [Release-Checkliste](RELEASE_CHECKLIST.md) durchführen.

## Privater Fundort des Originalschlüssels

Der dokumentierte Fundort ist die **private ChatGPT-Dateibibliothek des Projektinhabers**. Der Projektinhaber hat dort die Originaldateien `Eimer-Liste-Update-Schluessel.p12` und `Eimer-Liste-Update-Passwort.txt` erneut gefunden. Einen stabilen Datei-Link oder eine Datei-ID nennt der damalige Bericht nicht. [Signiernachweis zu Build 38](https://github.com/shayohekmat-wq/Zimt-Lab-2/blob/5a92737946d451c895cd91b3ad42ba68a8520f3f/docs/BUILD_PLAN.md#L455)

Ein autorisierter Lauf mit Bibliothekszugriff kann beide Dateien im privaten Signierkontext verwenden. Prüfe unter `/workspace/library-files` beide exakten Dateinamen auf Vorhandensein und Lesbarkeit. Ein leeres Verzeichnis bedeutet, dass diesem Lauf dort keine Bibliotheksdateien bereitgestellt wurden. Prüfe den Zertifikatsfingerabdruck der signierten APK gegen die Release-Checkliste und vergleiche die APK mit dem gebauten Commit. Übergib nachfolgenden Läufen den Artefaktverweis und das Prüfergebnis. Der Entwicklungsbranch baut das unsignierte Release für die private Signierung.

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
1. Der Originalschlüssel privat gesichert ist und der autorisierte Signierprozess seinen Zugriff geprüft hat.
2. Die APK aus dem geprüften CI-Build privat signiert wurde und die Signaturprüfung bestanden hat.
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

## Bestehender GitHub-Build

Der Entwicklungsbranch erzeugt den unsignierten Kandidaten als Actions-Artefakt. Der Entwicklungsstand des Android-Workflows bindet die bestehenden CI-Signierdaten ausschließlich im Signierschritt eines separaten Runners ein. Dieser ist beim manuellen Start auf main vorgesehen, sobald der Workflow dort übernommen ist. Er prüft den APK-Hash desselben Build-Laufs und verlangt zum Signieren die höchste bereits verteilte Versionsnummer. Der Runner führt weder Gradle noch Code aus dem Checkout aus. Die oben beschriebene Registrierung verwendet ausschließlich den privaten Signierrechner. Der Originalschlüssel bleibt aus dem Build- und PR-Kontext heraus. Eine übersprungene CI-Signierung gibt keine Auskunft über den privaten Bestand.
