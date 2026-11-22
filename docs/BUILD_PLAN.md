# Eimer-Liste · optimierter Build-Plan

**Stand:** 22. November 2026 · **Projekt:** Zimt-Lab-2 · **Zielplattform:** Android · **später:** ZIMT-Modul.

## Produktversprechen
Eine emotionale, spielerische Bucket-Liste für zwei oder mehr Menschen. Keine Tabellenästhetik; trotzdem jederzeit klar, schnell, zugänglich und zuverlässig. Das Papier ist ein echtes wiederverwendetes UI-Objekt, nicht ein austauschbares Eingabeformular mit Dekoration.

## Kritischer Nutzungspfad
1. Eine Person legt einen Eimer an, wählt Material und Namen.
2. Sie wählt einen Eimer, tippt auf den Papierfetzen am unteren Bildschirmrand und schreibt eine Idee.
3. Enter oder der Falten-Button startet **drei Faltungen**. Der gefaltete Zettel bleibt lokal als Entwurf erhalten und kann per Drag-and-drop in **jeden sichtbaren Eimer** geworfen werden. Erst beim erfolgreichen Ablegen in einem Eimer wird der Zettel dauerhaft gespeichert; bei einem Schreibfehler bleibt er erneut verwendbar.
4. Wird das Ziel verfehlt, springt der Zettel zurück in die Ablage; er kann erneut gezogen oder per Tippen auf einen Eimer zugeordnet werden. Nach erfolgreichem Einwurf erscheint ein neuer Papierfetzen. Ein optionales taktiles und akustisches Feedback bestätigt den Vorgang.
5. Ein Tipp auf den Eimer öffnet seine Zettelsammlung; ein Tipp auf einen Zettel entfaltet ihn zum Lesen.
6. Bei gemeinsamer Nutzung erscheinen neue Zettel nach Synchronisierung auf allen berechtigten Geräten; offline erstellte Notizen werden nicht verworfen.

## Abnahmestufen (keine künstliche Gleichsetzung von Prototyp und fertiger Kollaboration)

### 0.1 · Lokales Kern-Erlebnis (zuerst)
- [ ] Native Android-App mit stabiler Paketkennung, Versionsnummer und klarer Navigation
- [ ] Einmaliges Onboarding ohne Registrierung; Eimer neu erstellen, umbenennen und Material wählen
- [x] Papier-Eingabefeld bleibt beim Einblenden der Tastatur oberhalb der IME sichtbar (edge-to-edge Insets), Enter/Falten, leeren Eingaben vorbeugen
- [x] Dreistufige Faltsequenz und manuelles Ziehen auf beliebigen sichtbaren Eimer, mit Rückkehr bei Fehlwurf; Speicherung erst bei erfolgreicher Ablage
- [ ] Eimer öffnen, Inhalte ansehen, Zettel lesen/entfalten
- [ ] Dauerhafte lokale Datenhaltung, IDs und Datenmodell für spätere Zeichnungen
- [ ] Leere Zustände, Fehler- und Ladezustände, TalkBack und große Schrift
- [ ] UI-/Datenbank-/Prozess-Neustart-Tests; keine destructive Migration
- [ ] Reduzierte Animationen bei Systemeinstellung „Animationen entfernen“
- [ ] Stabile **Release-Signierung und signierter Update-Test**, bevor Nutzer produktive Daten anlegen

### 0.2 · Gemeinsam in Echtzeit (Voraussetzung für das zugesagte gemeinsame Produkt)
- [ ] Authentifizierung mit sicherer Wiederherstellung; keine dauerhafte Bindung nur an anonymes Konto
- [ ] Einladungslinks/-codes sicher serverseitig einlösen; private Eimer nur für Mitglieder
- [ ] Echtzeit-Ereignisse für Eimer und Zettel, gleichzeitige Bearbeitung
- [ ] Offline-Outbox, Wiederholversuche, Duplikatschutz, Konflikt- und Löschregeln
- [ ] Mitgliedschaft entziehen, offene Einladungen widerrufen, sensible Daten nicht in Links
- [ ] Tests auf zwei Geräten, instabilem WLAN/LTE, doppeltem Absenden und konkurrierendem Löschen
- [ ] Synchronisierungsstatus diskret sichtbar; bei Fehlern **nie** stillschweigend Einträge verlieren

### 0.3 · Material- und Erlebnisveredelung
- [ ] Varianten aus Metall, Geflecht, Papier, Holz, Emaille, Vintage und Messing
- [ ] Licht, Materialtexturen, Schatten und subtile Mikrointeraktionen
- [ ] Effiziente 60-fps-Animationen ohne unnötige Recompositionen oder permanent aktive GPU-Effekte
- [ ] Optionale, dezente Papier-/Metallgeräusche und Haptik, abschaltbar
- [ ] Tablet, kleine Displays, dynamische Schrift, Hoch-/Querformat
- [ ] Regressionstest: Einträge vor/ nach Update identisch

### 0.4 · Erweiterungen (bewusst **nicht** im ersten Build)
- [ ] Pinsel neben dem Papier; Zettel öffnet sich zur Zeichenfläche
- [ ] Text, Skizze oder beides, Vektorstrokes mit stabiler Payload-Version
- [ ] Eimer gedrückt halten + Gerät schütteln → einen zufälligen Zettel herausfallen lassen
- [ ] Schüttelgeste mit Debounce, Schwellwert, Barrierefreiheits-Alternative und Schutz vor versehentlicher Auslösung
- [ ] Erledigte Wünsche als Erinnerungen archivieren, ohne den Ursprung zu löschen

### 1.0 · In ZIMT überführen
- [ ] Feature-Modul mit klaren Schnittstellen zu Navigation, Identität, Design und Synchronisierung
- [ ] Versioniertes Export-/Importformat, Mapping der IDs, sichere Dublettenerkennung
- [ ] Migrationsassistent mit Vorschau und Backup; erst nach Validierung alte Instanz ablösen
- [ ] Ende-zu-Ende-Test ZIMT-Integration auf echten Bestandsdaten

## Qualitäts-Gates
**Daten:** Ein gefalteter Zettel bleibt als wiederherstellbarer Entwurf bestehen; ein erfolgreicher Einwurf wird erst nach erfolgreichem Write quittiert. Kein `fallbackToDestructiveMigration`. Schema-Migrationen aus jeder veröffentlichten Version prüfen. Export/Restore vor der ersten produktiven Nutzung. Bei Konflikt nicht unsichtbar überschreiben.

**Erlebnis:** 48-dp-Touchflächen; kontrastreiche Beschriftung, glaubwürdige Tiefe, keine unlesbaren Mini-Zettel, kein aufdringliches Dauerfunkeln. Motion-Reduction und Low-End-Modus.

**Sicherheit:** Rollenbasierter Zugriff, Security Rules/RLS und Backend-Checks; jede Eimer-/Zettelabfrage autorisiert; keine frei lesbare öffentliche Datenbank. Kein Produktionszugang in Git.

**Update:** identische `applicationId` + gleichbleibender privater Signaturschlüssel + wachsender `versionCode` + rückwärtskompatible Migration. Ein CI-Debug-APK ist **kein** dauerhafter Update-Kanal.

## Bewusst offene Abhängigkeiten
Der produktive Mehrpersonenmodus braucht einen bereitgestellten Backend-Dienst und Authentifizierungs-Konfiguration; ohne diese darf die App nur als lokal verwendbarer Prototyp ausgewiesen werden. Release-Key und Backuport müssen sicher eingerichtet werden; beides wird nicht in diesem öffentlichen Repository abgelegt.


---

# Build 0.2 · Skizzen & Zufallszettel – Umsetzung

Stand: 22.11.2026, vor Veröffentlichung. Fortsetzung von 0.1.1. Nächster planmäßiger Zyklus um 08:00 (Europe/Berlin) fokussiert 0.3, **nur falls 0.2 tatsächlich erfolgreich signiert und geprüft wurde**.

## Produktprinzip
Ein einziger Papierzettel trägt Text, Skizze oder beides. Er kann jederzeit vor dem Einwurf bearbeitet werden; der Eimer ist eine physische, leicht bedienbare Entscheidungshilfe. Spaß ohne Datenverlust.

## Umfang & Abnahmekriterien
1. **Zeichenpapier**: Pinsel-Button im Zettel-Composer. Vergrößerter Papierfetzen mit Stift, drei Farben, zwei Strichbreiten, Undo/Redo; eine Zeichnung kann **ohne Text** gespeichert werden. Vorhandener Text bleibt erhalten.
2. **Vektorzeichnung**: Strokes als relative 0–1-Koordinaten mit versioniertem Format, damit Rotation, verschiedene Displaygrößen, Rendering und späterer Sync funktionieren. Begrenzte Punkte/Größe, defensiver Parser, alte Zettel bleiben lesbar.
3. **Dreifaches Falten**: Vor dem Einwurf bleiben Text und Zeichnung im Entwurf. Fehlwurf oder Datenbankfehler löscht keinen Inhalt. Der Papierfetzen darf erst nach erfolgreicher Datenbank-Transaktion verschwinden.
4. **Schüttel-Eimer**: Eimer mit Finger gedrückt halten + bewusst zweimal schütteln, dann zufälligen enthaltenen Zettel öffnen. Sensor nur während der Berührung aktiv; Schwelle, Sperrzeit und Trigger-Debounce; kein permanentes Sensor-Polling.
5. **Fallback / Inklusion**: Im geöffneten Eimer steht „Zufallszettel ziehen“, auch ohne Sensor oder für Menschen, die das Gerät nicht schütteln können. Leere Eimer führen zu einer freundlichen, klaren Meldung.
6. **Optische Qualität**: eigene, prozedural gerenderte Eimer mit lebendigeren Materialien, besseren Geflecht-/Metall-/Papierdetails und stabilen Etiketten; keine fremden Bilder ohne ausdrücklich dokumentierte kommerzielle Lizenz. Beim 0.2-Release genügt optische Verfeinerung ohne neue Assets.
7. **Update & Datensicherheit**: applicationId `de.zimt.eimerliste` und Room-Schema v1 bleiben gleich. Vorhandene `drawingPayload`/`drawingPayloadVersion`-Felder werden verwendet, **keine destruktiven Migrationen**. versionCode höher als 2. Gleiches privates Signaturzertifikat wie 0.1.0/0.1.1.
8. **Qualität**: Gradle debug + Release + Unit-Tests. Parser: gültig/ungültig, reine Zeichnung und Obergrenzen testen. Interaktionen auf echtem Androidgerät bis zur Freigabe **nicht als getestet deklarieren**.

## UX-Details
- Pinsel ist ein separater, klar beschrifteter Schalter, keine Geste versteckt hinter dem Textfeld.
- Der Zeichenbereich nimmt beim Arbeiten den Großteil des Displays ein; Abbrechen verändert keine zuvor gespeicherte Zeichnung.
- Undo/Redo wird nur für tatsächlich vorhandene Striche angeboten.
- Schütteln hat eine visuelle Aufforderung und dezentes Haptikfeedback; einfache Antipp-Alternative bleibt jederzeit verfügbar.
- Kleine Screens, IME-Insets, Statusleisten-Safe-Areas und TalkBack sind Teil des Review.

## Nicht im 0.2-Release
- Live-Synchronisierung anderer Nutzer (braucht einen abgesicherten Backend-Dienst), Bildimporte, unendliche Leinwand, fortgeschrittene Layer.
- Kein Versprechen automatischer Veröffentlichung, falls Release-Signaturschlüssel in der Laufumgebung fehlt.

## Nächster 0.3-Zyklus: Design/Erlebnis/Robustheit
Priorisierung nach 0.2-Build und Fehlerprotokoll: gefaltete Notizen im Eimer differenzieren, Zettel-Entfalten, sicherer Undo für Aktionen, Accessibility- und Material-Politur, optimierte Renderpfade, langfristiger Export/Import und echtes Wiederherstellungsszenario. Jede Änderung mit User-Story, Fehleranalyse, Test und Update-Prüfung.


---

# Zyklus 22.11.2026 08:00 · 0.3 Qualitätsplan

## P0 · Entwürfe bei Rotation und Unterbrechung bewahren
User-Story: Beim Drehen des Handys oder App-Wechsel bleiben Text, Skizze und Undo-Verlauf erhalten.
Umsetzung: Vorhandene saveable-state-Pfade auditieren; Entwürfe vor destruktiver Navigation schützen; keine Room-Schema-Änderung.
Abnahme: Text-only, Sketch-only und kombinierte Notizen überstehen Rotation und Hintergrundwechsel. Abbrechen verändert gespeicherte Notizen nicht.

## P1 · Fehlwurf verständlich und barrierefrei behandeln
User-Story: Nach einem verfehlten Eimer kann derselbe Zettel erneut geworfen werden.
Umsetzung: Zugängliche Statusmeldung, Fokus- und Entwurfserhalt bei Fehler; Inhalt erst nach erfolgreichem Datenbank-Write leeren.
Abnahme: Fehlwurf und simulierter Write-Fehler erhalten Zeichnung/Text; Erfolg legt genau einen Zettel ab.

## P2 · charmingX4, ohne Material- oder Performance-Regressionsrisiko
User-Story: Eimer wirken handgemacht und bleiben bei großer Schrift gut bedienbar.
Umsetzung: Nur bestehende prozedurale Grafiken; 48-dp-Touchflächen, Kontrast und Label bei 200 % Schrift prüfen; reduzierte Animation respektieren.
Abnahme: Kein Text-Clipping, kein dauerhafter Render-Loop im Ruhezustand, keine unlizenzierte Grafik.

## Release-Gate
Aktueller Quellstand: 0.2.0, versionCode 3. Keine erneute Implementierung von Skizzen/Schütteln. Vor neuer Versionsnummer: Debug-Build, Unit-Tests, Release-Build, Signatur-Fingerprint gegen 0.1.0 und verlustfreier Update-Test. Ohne Nachweis keine Update-APK. Paketkennung de.zimt.eimerliste und Room-Schema v1 unverändert. Realgerätetest ausdrücklich offen.


---

# Zyklus 22.11.2026 11:04 · 0.3.1: verlässliche Rückmeldung beim Fehlwurf

## Befund und Priorisierung
Der Quellcode von `EimerApp.kt` erhält bei einem Drag-and-drop-Fehlwurf den Entwurf (`pendingPaper` und `drawing`) und setzt den Versatz zurück. Anders als bei einem Datenbankfehler wird jedoch keine verständliche Rückmeldung angezeigt. Dies ist besonders für TalkBack und bei kleinen Displays unklar. Die Skizzen-/Schüttelfunktionen aus 0.2 werden **nicht** neu implementiert.

### P0 – Kleiner risikoarmer Fix (vor Release)
- Beim Loslassen außerhalb eines Eimers eine kurze, lokalisierbare Snackbar: „Keinen Eimer getroffen. Dein Zettel bleibt erhalten – ziehe ihn erneut oder tippe auf einen Eimer.“
- Nur bei echtem Fehlwurf melden, nicht bei abgebrochener Drag-Geste oder erfolgreichem Treffer. Keine blockierende Meldung und kein Reset des Drafts.
- Regression: Text-only, Sketch-only, kombinierte Zettel, mehrfacher Fehlwurf, Treffer per Tap und Drag, DB-Schreibfehler.

### P1 – Barrierefreiheit
- Bucket-Tile als benannte, bedienbare Semantik-Aktion (Eimername + Zettelanzahl) statt nur unsichtbarer Pointer-Geste. TalkBack: per Tap öffnen bzw. gefalteten Zettel einwerfen; Schütteln bleibt optional.
- Touch-Flächen mindestens 48 dp, 200%-Schrift und reduzierten Bewegungsmodus auf Gerät prüfen.

### P2 – Robustheit und visuelle Treue
- Wiederherstellung von Zeichnungs-Editor-Zustand bei Prozessneustart prüfen; keine UI-Optik ändern, bevor die Tests stehen.
- Bestehende prozedurale charmingX4-Materialien beibehalten; keine Drittgrafiken ohne dokumentierte kommerzielle Provenienz.

## Gates / Status
Quellstand vor diesem Plan: 0.2.0, versionCode 3, `de.zimt.eimerliste`, Room-Schema v1. Kein neuer Release ohne tatsächlich erfolgreiche Debug- und Unit-Tests, Release-Build, Zertifikats-Fingerprint-Prüfung gegen 0.1.0 und verlustfreien Update-Test. GitHub-Workflow wird auf `main` bei Push ausgelöst; sein Erfolg ist separat nachzuweisen. Kein Realgerätetest behauptet.


---

# Zyklus 22.11.2026 14:01 · 0.3.2: Eimer mit TalkBack bedienbar machen

## Verifizierter Ausgangspunkt
Main HEAD vor dem Zyklus: 86c58bb (Fehlwurf-Meldung); App weiterhin 0.2.0 / versionCode 3. Die BucketTile-Oberfläche reagiert über detectTapGestures, besitzt aber keine semantische Aktivierungsaktion. Dadurch kann ein Screenreader den Eimer nicht zuverlässig per Doppeltipp öffnen bzw. einen gefalteten Zettel einwerfen. Das ist ein eigenständiger Accessibility-Fehler; Skizzen und Schütteln werden nicht neu gebaut.

## P0 – Kleiner, risikoarmer Code-Fix
- BucketTile erhält einen eindeutigen semantischen Namen aus Eimername und Zettelanzahl sowie Role.Button.
- Semantics.onClick ruft exakt denselben onOpen-Pfad auf wie ein Pointer-Tap. Mit gefaltetem Papier ist die Aktion „Zettel einwerfen“, sonst „Eimer öffnen“.
- Kein zusätzliches clickable über pointerInput legen (würde Gesten/Long-press beeinflussen). Pointer-Schüttelgeste bleibt erhalten; Zufallszettel-Button bleibt im Eimer erreichbar.

## P1 – Prüfungen
- Kotlin/Compose Debug-Compile und Unit-Tests; Release-Compile; CI-Jobs und Logs auswerten.
- TalkBack auf echtem Android: Fokus, Name, Anzahl, Doppeltipp, gefalteten Zettel einwerfen, Eimer halten + schütteln, Zufallszettel per Button.
- 200%-Schrift, 48-dp-Aktivierungsfläche, IME, Rotation, Fehlwurf und Entwurfserhalt regressionsprüfen.

## P2 – Nachfolgend, nicht Teil dieses Fixes
- Skizzen-Editor-Zwischenstand über Prozessneustart erhalten; vollständiger verlustfreier Update-/Restore-Test.
- CharmingX4-Prozeduralgrafiken unverändert, keine neuen Assets und keine Änderung an Room-Schema oder applicationId.

## Release-Gate
Kein Versionssprung und keine neue Update-APK allein wegen eines Code-Commits. Release erst nach nachgewiesenem CI-Erfolg, Vergleich des Signaturzertifikats mit 0.1.0 und Test auf erhaltene Bestandsdaten. Debug- und unsigned-APK nicht als Update verteilen.
