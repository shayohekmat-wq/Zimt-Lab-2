# Eimer-Liste · optimierter Build-Plan

**Stand:** 22. November 2026 · **Projekt:** Zimt-Lab-2 · **Zielplattform:** Android · **später:** ZIMT-Modul.

## Produktversprechen
Eine emotionale, spielerische Bucket-Liste für zwei oder mehr Menschen. Keine Tabellenästhetik; trotzdem jederzeit klar, schnell, zugänglich und zuverlässig. Das Papier ist ein echtes wiederverwendetes UI-Objekt, nicht ein austauschbares Eingabeformular mit Dekoration.

## Kritischer Nutzungspfad
1. Eine Person legt einen Eimer an, wählt Material und Namen.
2. Sie wählt einen Eimer, tippt auf den Papierfetzen am unteren Bildschirmrand und schreibt eine Idee.
3. Enter oder der Senden-Button bestätigt. Die Papiernotiz faltet sich **viermal**, wird in den Eimer geworfen und ist **vor Beginn der Animation bereits dauerhaft gespeichert**.
4. Der nächste leere Papierfetzen erscheint. Ein optionales taktiles und akustisches Feedback bestätigt den Vorgang.
5. Ein Tipp auf den Eimer öffnet seine Zettelsammlung; ein Tipp auf einen Zettel entfaltet ihn zum Lesen.
6. Bei gemeinsamer Nutzung erscheinen neue Zettel nach Synchronisierung auf allen berechtigten Geräten; offline erstellte Notizen werden nicht verworfen.

## Abnahmestufen (keine künstliche Gleichsetzung von Prototyp und fertiger Kollaboration)

### 0.1 · Lokales Kern-Erlebnis (zuerst)
- [ ] Native Android-App mit stabiler Paketkennung, Versionsnummer und klarer Navigation
- [ ] Einmaliges Onboarding ohne Registrierung; Eimer neu erstellen, umbenennen und Material wählen
- [ ] Papier-Eingabefeld mit Bildschirmtastatur, Enter/Senden, leeren Eingaben vorbeugen
- [ ] Echte vierstufige Falt- und Wurfsequenz; zuerst Daten speichern, dann Animation
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
**Daten:** Keine Animation vor erfolgreichem Write. Kein `fallbackToDestructiveMigration`. Schema-Migrationen aus jeder veröffentlichten Version prüfen. Export/Restore vor der ersten produktiven Nutzung. Bei Konflikt nicht unsichtbar überschreiben.

**Erlebnis:** 48-dp-Touchflächen; kontrastreiche Beschriftung, glaubwürdige Tiefe, keine unlesbaren Mini-Zettel, kein aufdringliches Dauerfunkeln. Motion-Reduction und Low-End-Modus.

**Sicherheit:** Rollenbasierter Zugriff, Security Rules/RLS und Backend-Checks; jede Eimer-/Zettelabfrage autorisiert; keine frei lesbare öffentliche Datenbank. Kein Produktionszugang in Git.

**Update:** identische `applicationId` + gleichbleibender privater Signaturschlüssel + wachsender `versionCode` + rückwärtskompatible Migration. Ein CI-Debug-APK ist **kein** dauerhafter Update-Kanal.

## Bewusst offene Abhängigkeiten
Der produktive Mehrpersonenmodus braucht einen bereitgestellten Backend-Dienst und Authentifizierungs-Konfiguration; ohne diese darf die App nur als lokal verwendbarer Prototyp ausgewiesen werden. Release-Key und Backuport müssen sicher eingerichtet werden; beides wird nicht in diesem öffentlichen Repository abgelegt.
