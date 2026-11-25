# ZIMT LAB 2 · Eimer-Liste

Ein eigenständiges Android-Lab für ZIMT: Gemeinsam Wünsche, Ideen und Erlebnisse auf Papier schreiben, viermal falten und in liebevoll gestaltete Eimer werfen.

**Stand: Build 0.2.0 kompiliert und privat mit dem ursprünglichen Updatezertifikat signiert.** Sketch-only, Text+Skizze, Zufallszettel via gedrückt halten + schütteln sowie barrierearme Antipp-Alternative sind im aktuellen Quellcode implementiert. Ein Realgeräte-Test aller Gesten steht aus.

## Leitidee
- Eimer sind eigene Sammlungen: benennbar, unterschiedlich gestaltet, für mehrere Personen.
- Das Eingabefeld **ist der Papierzettel**. Beim Absenden faltet sich genau dieser Zettel viermal, fliegt in den ausgewählten Eimer, und ein neuer erscheint.
- Öffnen des Eimers zeigt die gesammelten Zettel. Zettel können wieder aufgeklappt werden.
- Gute Bedienbarkeit, Datenschutz, Offline-Fähigkeit, flüssige Animationen und **verlustfreie Updates** sind Produktanforderungen, keine nachträglichen Extras.

## Start
Öffne das Projekt mit Android Studio (AGP-/SDK-Versionen siehe Builddateien). Zum Builden werden Android SDK und Gradle benötigt. Der Quellcode liegt unter `app/`.

Wichtig: Für den privaten Update-Kanal nur APKs mit dem seit 0.1.0 unveränderten Signaturzertifikat nutzen. Debug-APKs aus unterschiedlichen Rechnern/CI-Läufen sind nicht garantiert gegeneinander updatefähig. Die erste dauerhaft genutzte Installation benötigt einen **gleichbleibenden Release-Signaturschlüssel**. Mehr: [Update- und Datensicherheit](docs/RELEASE_AND_DATA.md).

Für Release-Verantwortliche ist `python3 tools/eimer_signer.py status` der gemeinsame Einstieg auf dem geschützten Arbeitsplatz. Der Befehl prüft den eingebundenen Originalschlüssel. [Einrichtung für Codex und Team-Signierläufe](docs/RELEASE_AND_DATA.md#privater-signierzugang-für-laufende-updates)

## Projektführung
- [Optimierter Build-Plan](docs/BUILD_PLAN.md)
- [Technische Architektur](docs/ARCHITECTURE.md)
- [Designsystem](docs/DESIGN_SYSTEM.md)
- [Update-, Backup- und Signaturstrategie](docs/RELEASE_AND_DATA.md)
- [Grafik-Provenienz und Lizenzhygiene](docs/ASSETS_AND_LICENSES.md)

Die Erprobung erfolgt **außerhalb der bestehenden ZIMT-App**. Ihre Daten werden nicht voraussetzungslos mit einer anderen App-ID zusammengeführt: die spätere Integration erhält einen expliziten, getesteten Import-/Übergabepfad.

**Projektregel:** Keine Datenbank-Resets bei Updates, keine Zugangsdaten im Repository, keine als fertig bezeichnete Funktion ohne Test.
