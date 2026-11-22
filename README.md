# ZIMT LAB 2 · Eimer-Liste

Ein eigenständiges Android-Lab für ZIMT: Gemeinsam Wünsche, Ideen und Erlebnisse auf Papier schreiben, viermal falten und in liebevoll gestaltete Eimer werfen.

**Status: Entwicklungsstart · noch kein freigegebener APK-Build.**

## Leitidee
- Eimer sind eigene Sammlungen: benennbar, unterschiedlich gestaltet, für mehrere Personen.
- Das Eingabefeld **ist der Papierzettel**. Beim Absenden faltet sich genau dieser Zettel viermal, fliegt in den ausgewählten Eimer, und ein neuer erscheint.
- Öffnen des Eimers zeigt die gesammelten Zettel. Zettel können wieder aufgeklappt werden.
- Gute Bedienbarkeit, Datenschutz, Offline-Fähigkeit, flüssige Animationen und **verlustfreie Updates** sind Produktanforderungen, keine nachträglichen Extras.

## Start
Öffne das Projekt mit Android Studio (AGP-/SDK-Versionen siehe Builddateien). Zum Builden werden Android SDK und Gradle benötigt. Der Quellcode liegt unter `app/`.

Wichtig: Debug-APKs aus unterschiedlichen Rechnern/CI-Läufen sind nicht garantiert gegeneinander updatefähig. Die erste dauerhaft genutzte Installation benötigt einen **gleichbleibenden Release-Signaturschlüssel**. Mehr: [Update- und Datensicherheit](docs/RELEASE_AND_DATA.md).

## Projektführung
- [Optimierter Build-Plan](docs/BUILD_PLAN.md)
- [Technische Architektur](docs/ARCHITECTURE.md)
- [Designsystem](docs/DESIGN_SYSTEM.md)
- [Update-, Backup- und Signaturstrategie](docs/RELEASE_AND_DATA.md)

Die Erprobung erfolgt **außerhalb der bestehenden ZIMT-App**. Ihre Daten werden nicht voraussetzungslos mit einer anderen App-ID zusammengeführt: die spätere Integration erhält einen expliziten, getesteten Import-/Übergabepfad.

**Projektregel:** Keine Datenbank-Resets bei Updates, keine Zugangsdaten im Repository, keine als fertig bezeichnete Funktion ohne Test.
