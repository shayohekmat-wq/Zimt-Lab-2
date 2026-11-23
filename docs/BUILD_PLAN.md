# VERBINDLICHER MASTERPLAN · DESIGNLABOR REDESIGN
**Aktualisiert: 23. November 2026 · ersetzt bei Widerspruch ältere Designlabor-Ziele in diesem Dokument.**

## Entscheidung des Nutzers: keine festen acht Stilrichtungen
Die ursprünglich festgelegten Familien Comic, 3D, Pixel Art, Aquarell, Low Poly, Knetoptik, Kupferstich und CharmingX4 sind **nur Inspirationsquellen, keine Abnahmebedingung**. Die bisherige Forderung „8 × 6“ ist aufgehoben. Keine künstliche Zahl oder Stilquote erzwingen. **Entscheidend ist eine vielfältige, kohärent kuratierte Auswahl wirklich neu entworfener, hochwertiger und technisch funktionierender Eimer.** Jeder Eimer benötigt eine erkennbare eigene Silhouette, Konstruktion, Materialidee und gestalterische Identität; Umfärbungen und marginale Variationen zählen nicht als neue Designs. Die früheren Ziele 48/24/12/84 sind Planungsgrößen, keine Liefergarantie oder Qualitätsmetrik.

## Verbindliche Klarstellung · Von Menschen DESIGNTE digitale Grafikassets (23.11.2026)
**Korrektur des Nutzers:** Mit „von Menschen gemacht“ sind **digitale Grafikdateien von menschlichen Künstlern/Designern** gemeint – professionell gestaltete transparente PNGs, SVGs, Sprite-Assets, 2D-Illustrationen, UI-Objektgrafiken, manuell modellierte und gerenderte 3D-Assets, Game-Art-Packs usw. **Nicht** speziell historische Korbillustrationen und **nicht** Fotografien menschlich gefertigter Körbe. Diese früheren Vorschläge waren eine Fehlinterpretation und werden nicht als bevorzugte Beschaffungsrichtung fortgeführt.

### Parallele Pflichtstränge
- **Referenzeimer:** ein außergewöhnlich hochwertiger, vollständig neu konstruierter und in der App funktionierender Eimer mit korrekter Öffnung, Vorder-/Rückebene, Papier-Okklusion und Animation.
- **Human-designed Asset-Sourcing:** in jedem Designzyklus passende, wirklich von Menschen entworfene **digitale Grafikassets** identifizieren, prüfen, mit anklickbarem Original-Link und Lizenz dokumentieren und, soweit sinnvoll, technisch integrieren. Keine KI-generierten oder KI-assistierten Bilder in dieser Rubrik. Eine „No AI“-Selbstauskunft ist ein Indiz, keine absolute Garantie.
- Auswahl nach gestalterischer Qualität und App-Tauglichkeit, nicht nach dem Wort „Korb“ im Titel. Auch Schachteln, Papierkörbe, Kisten, Töpfe, Gefäße oder eigens entworfene Behälter sind geeignet, sofern die Interaktion mit Zetteln funktioniert.

### Konkrete digitale Asset-Quellen (Recherchekandidaten, nicht als integriert behaupten)
- [Kenney – Game Assets All-in-1](https://kenney.itch.io/kenney-game-assets): menschlich gestaltete 2D-Sprites, PNG/SVG, 3D-Modelle; Anbieter kennzeichnet das Paket als ohne generative KI, Lizenz CC0. Geeignete **einzelne Behälter-/Objektassets erst im Pack auswählen und prüfen**. Kein blindes Übernehmen ganzer Pakete.
- [Kenney – offizielle Lizenzinformationen](https://kenney.nl/support): kommerzielle Nutzung der Assetseiten-Pakete unter CC0 bestätigt; Datei-Lizenz mitführen.
- [Gelidus Canimons – Vegetable Asset](https://gelidus-canimons.itch.io/vegetable-asset): laut Autor 100 % handgemalt, keine KI; PNGs plus Krita-/Photoshop-Dateien, enthält ein „vegetable basket“. Kostenpflichtiges Paket; Nutzung/Modifikation in kommerziellen Projekten laut Anbieter erlaubt, Rohdatei-Weiterverkauf verboten. Als mögliche illustrative Einzelgrafik prüfen, nicht als fertig integrierte Grafik.
- [itch.io – Game Assets, No AI](https://itch.io/game-assets/newest/tag-free-asset/tag-no-ai): Suchquelle für manuell gestaltete Sprites, PNGs, 3D- und UI-Assets. Jede konkrete Datei, Lizenz und Urheberschaft separat prüfen.

### Pflichtprüfung pro Grafik
`Künstler/Studio | Original-Dateilink | Nachweis Nicht-KI | Lizenz und kommerzielle Nutzung | Dateiformat und Auflösung | Transparenz | Bildqualität bei Handygröße | Öffnung/Layer/Maskierung | Attribution | Integrationsstatus`.
Kein bloßes Web-Suchbild und keine einzelne Foto-/Zeichnungsreferenz als bereits integriertes Design ausgeben. Menschlich gestaltete Grafiken und generierte Unikate bleiben strikt getrennte Galerien.

## Konkreter nächster Entwicklungszyklus · ausführbarer Arbeitsauftrag
**Priorität: Redesign implementieren, nicht nur dokumentieren.** Die vorhandene 0.3.3-dev4-Codebasis enthält noch prozedurale Entwürfe und keine freigegebene hochwertige Asset-Sammlung. Neue Versionsnummern sind keine Qualitätsbeweise. Diese Liste wird pro überprüftem Arbeitsergebnis aktualisiert.

### Arbeitspaket 1 – Renderer und Datenmodell (P0)
- [ ] `BucketArtworkSpec` oder äquivalente stabile Schnittstelle definieren: `designId`, Asset-Herkunft, Rückebene, Öffnungsmaske/Anker, Vorderwand, Etikettposition, Variantenmetadaten und Fallback.
- [ ] Im Bucket-Tile tatsächlich in dieser Reihenfolge rendern: Hintergrund/Rückwand → Zettel mit Clip-Maske → Vorderwand/Rand → Name/Interaktionszustand. Einwurf- und Auswurfanimation dürfen nicht durch eine unbedingte Vordergrund-Zeichenreihenfolge fehlerhaft werden.
- [ ] Ein bestehendes gespeichertes `lab-...`-Design muss bei fehlendem/neuem Asset sicher auf die alte Darstellung zurückfallen, ohne Daten oder Designzuordnung zu löschen.
- [ ] Mindestens automatisierte Tests für Geometrie/Anker, ID-Stabilität, Fallback und Layer-Logik; ein visueller Nachweis mit einem Zettel vor, während und nach dem Einwurf.

### Arbeitspaket 2 – ein echtes Referenzdesign (P0)
- [ ] Mindestens ein **vollständig neu entworfenes** Eimerobjekt als verwendbares, lokal gebündeltes Asset erzeugen (keine bloße Canvas-Umfärbung).
- [ ] Silhouette, konstruktive Plausibilität, Material, Licht, Etikett und Öffnung separat prüfen; Render-Assets und Metadaten in der App integrieren.
- [ ] Einwurf, Entnahme, leichte Eimerreaktion, schmale Displaybreite und große Schrift prüfen. Dokumentierte Designerbewertung mit konkreten Schwächen und Iteration.
- [ ] Erst nach diesem Gate weitere Eimer in bewusst unterschiedlichen Formen/Materialien entwickeln; keine starre Acht-Stile-Quote.

### Arbeitspaket 3 – nutzbares Designlabor (P1, nach erstem Referenzdesign)
- [ ] Galerie mit wenigen wirklich verfügbaren Designs statt 48 unfertigen Kacheln; experimentelle Motive getrennt und ausdrücklich als unfertig markiert.
- [ ] Auswahl → große Live-Vorschau mit echtem Eimernamen → eindeutiger Ziel-Eimer → Übernehmen/Abbrechen; aktuelles Design klar sichtbar, Rücksetzen möglich.
- [ ] Herkunftsgruppen „Von Menschen gestaltet“ und „Bildgeneriert“ erst mit **tatsächlich geprüften und integrierten** Einzelobjekten befüllen. Keine leeren Rubriken als angeblich fertige Features verkaufen.

### Arbeitspaket 4 – Vielfalt, Sicherheit, Freigabe (P1)
- [ ] Weitere Designs nur bei nachweisbar eigenständiger Form/Konstruktion; Herkunft und kommerzielle Nutzbarkeit pro Asset in `docs/ASSETS_AND_LICENSES.md` dokumentieren.
- [ ] APK-Größe, Speicherdruck, Framerate, TalkBack, reduzierte Animationen und ältere Android-Geräte berücksichtigen.
- [ ] Datenhaltbarkeit, unveränderte Paket-ID, aufsteigender Versionscode, ursprünglicher Signaturfingerprint und reale Update-Installation prüfen.

### Verbindliches Fortschrittsprotokoll
Jeder Entwicklungszyklus soll angeben: **Commit/Dateien**, **welches sichtbare und funktionierende Ergebnis hinzugekommen ist**, **welche Tests tatsächlich bestanden sind**, **welche Geräteprüfung noch fehlt**, **ob eine originalsignierte UPDATE-APK vorliegt** und **was das nächste größte Qualitätsrisiko ist**. Ein CI-Erfolg ohne signierte APK ist ausdrücklich als solcher zu kennzeichnen.

## P0 · Qualitätssprung statt kosmetischer Patches
1. **Design- und Asset-Pipeline neu aufbauen.** Bestehende primitive Compose-Canvas-Prototypen sind als Experimente gekennzeichnet und dürfen nicht als fertige Galerie erscheinen. Für die endgültigen Designs echte Originalillustrationen, transparente gerenderte Einzelobjekte, sorgfältig gestaltete Vektoren oder andere geeignete Techniken einsetzen; keine Textplatzhalter oder Contact-Sheet-Ausschnitte.
2. **Ein herausragendes Referenzobjekt tatsächlich integrieren.** Ein Eimer mit überzeugender Silhouette, eigenständigem Material, glaubwürdigen Details, Schatten und lesbarem Etikett; auf dem Zieltelefon prüfen. Erst danach weitere Varianten produzieren. Design-Review mit klaren Kriterien, kein automatisches „10/10“.
3. **Funktionierendes Layer-System:** rückwärtiger Eimerkörper / Öffnung / Papier / vordere Lippe und Vorderwand in korrekter Zeichenreihenfolge. Öffnungsmaske und geometrische Anker je Asset; Notiz beim Einwurf und beim Herausfallen korrekt verdecken, ohne Artefakte oder Z-Order-Fehler. Labels bleiben lesbar.
4. **Designlabor als sinnvolles Produkt gestalten:** visuell kuratierte Galerie, Vorschau mit realem Eimernamen, klare Auswahl und Rücknahme, sinnvolle Gruppierung/Filter nur bei echtem Mehrwert, verständliche Zustände für Entwürfe und verfügbare Motive, keine überladene Tab-Wüste. Animations- und Haptikfeedback sparsam, hochwertig und barrierearm.
5. **Reale Sammlungen getrennt ausweisen:** „Von Menschen gestaltet“ nur mit überprüfter menschlicher Urheberschaft, Originalquelle, Nutzungsrecht für App-Verteilung und Attribution; „Bildgeneriert“ nur mit tatsächlich generierten, integrierten Einzelassets. Nichts als verfügbar markieren, bevor das Objekt nutzbar und getestet ist.
6. **Performance und Sicherheit:** keine permanenten Render-Loops; Speicherbedarf und Skalierung auf älteren Android-Geräten prüfen, minSdk 26 beachten. IDs stabil halten; bestehende Zuordnungen nicht löschen. Datenbank/Notizen/Zeichnungen unverändert erhalten. Spätere Integration in ZIMT über austauschbare Asset-/Render-/UI-Module und dokumentierte Schnittstellen.

## Arbeitsreihenfolge und Abnahme
- **Meilenstein A – technische Rendering-Grundlage:** nachweisbar korrektes Papier-Okklusionssystem, wiederverwendbare Asset-Schnittstelle und mindestens ein echter visueller Test. Kein „fertig“, wenn nur ein Designplan geschrieben wurde.
- **Meilenstein B – erstes kuratiertes Referenzobjekt:** tatsächlich eingebunden, überzeugende Originalgrafik, funktioniert bei Einwurf/Entnahme, inklusive Name und Animation; auf kleinen und großen Displays begutachtet.
- **Meilenstein C – Designlabor-Redesign:** nutzbare Galerie und Vorschau mit konsistenter Interaktion; Originaldesign wiederherstellbar; Experimente separat.
- **Meilenstein D – diverse hochwertige Kollektion:** Stück für Stück eigenständige Formen und Materialien. Anzahl ergibt sich aus Qualitätsprüfung, nicht aus einer fixen Stil-Matrix. Menschliche und bildgenerierte Assets getrennt und korrekt dokumentiert.
- **Meilenstein E – Releasequalität:** echte Geräte- und Update-Regression, Haptik, Motion-Reduction, TalkBack, FPS, RAM, Bestandsdaten, Kompatibilität und ZIMT-Modulgrenzen.

## Release-Regeln
Qualität vor Geschwindigkeit, aber **Zwischen-Builds sind ausdrücklich erlaubt**, wenn der tatsächliche Fortschritt und die offenen Probleme transparent benannt werden. Keine kosmetischen Mini-Commits als Ersatz für die Redesign-Meilensteine. Jeder als UPDATE ausgelieferte Build benötigt erfolgreiche CI, unveränderte `applicationId`, aufsteigenden `versionCode`, Originalsignatur und Schutz vorhandener Daten. Die installierbare, verifizierte Update-APK direkt im Chat bereitstellen, sofern technisch möglich; fehlende Signierung/Dateizugänglichkeit ehrlich als Blocker melden. Ein erfolgreicher GitHub-Workflow ist allein kein APK-Nachweis.

## Definition von „fertig“
Ein Design ist erst fertig, wenn es als Einzelobjekt überzeugt, **nicht** nur eine andere Farbe oder Formlinie besitzt, tatsächlich im Android-Build enthalten ist, auf dem Handy lesbar ist und sämtliche Papier-/Eimer-Interaktionen zuverlässig funktionieren. Unfertige Entwürfe niemals als fertige Sammlung bezeichnen.

---
## Historische Buildplanung (ältere Einträge; bei Widerspruch gilt der Masterplan oben)

## 23. November – Designlabor 0.3.0
- Acht grafische Familien: Comic, 3D, Pixel Art, Aquarell, Low Poly, Knetoptik, Kupferstich, CharmingX4. Sechs katalogisierte Entwürfe je Stil (48 IDs).
- Direkt unter den Eimern als Footer des bestehenden scrollbaren Gitters; Auswahl eines Entwurfs und Ziel-Eimers.
- Designzuordnung in separaten lokalen SharedPreferences mit stabiler Bucket-ID, keine Änderung an Room-v1, Namen, Notizen oder Zeichnungen.
- Sämtliche Zeichenformen als eigene Compose-Canvas-Geometrie. Keine fremden Assets oder Lizenzrisiken; Provenienz: neu programmierte Formen und Materialeffekte im Repository.
- Qualität vor Freigabe: Android CI, Kataloganzahl 8×6, UI-Layout, Persistenz nach Neustart, TalkBack und originale Signatur prüfen.
- Noch offen: stilistische Tiefe der 48 Entwürfe, echte Geräteprüfung, Reduzierung möglicher GPU-Kosten und robustere UI-Auswahl für viele Eimer.

## 23. November – Rückweg Zeichenpapier → Textfeld (P0)
- Kein Kollaps an der oberen Papierkante. Beim Wechsel zur Texteingabe soll das Blatt auf ungefähr die **reale Höhe des unteren Textfelds** verkleinern und gleichzeitig **nach unten** an dessen Bildschirmposition wandern.
- Breite beibehalten, statt zu einer winzigen Fläche zu schrumpfen. Werkzeuge sanft ausblenden, anschließend Textfeld mit vorhandenem Text/Skizze wieder zeigen; Fokus erst nach Transition.
- Bildschirmhöhen, IME und reduzierte Animationen berücksichtigen. Kein Datenverlust, keine neue Datenbankversion. Regressionsprüfung auf realem Gerät.

## 23. November – Vollflächiges Zeichenpapier (P0)
- P0: Zeichenkoordinaten für Editor und gespeicherte Vorschau auf **gesamte PaperSheet-Fläche** normalisieren; keine Insets auf der Zeichen-Canvas. Zacken und Seiten über denselben tatsächlichen Papierpfad maskieren, sodass Striche bis an die sichtbare Kante reichen, aber nie auf den Holzgrund auslaufen.
- P0: PaperSheet clippt **alle Inhalte** mit dem vorhandenen tornPath, einschließlich Live-Strichen, Text und gespeicherter Ansicht. Keine destruktive Migration: bestehende normalisierte Skizzen weiterhin dekodieren.
- P1: Textmodus-Schalter direkt unter der Papierkante links, in Messing-Optik analog zum Stiftmodus. Zwei getrennte, durch Größe/Strich symbolisierte Werkzeug-Kreise statt Werkzeugnamen; TalkBack-Namen erhalten.
- P1: Regressionsprüfung bei schmalem Bildschirm, großen Pinseln, Rotation, TalkBack und gespeichertem Zettel. Release 0.2.4 mit steigendem versionCode, ursprünglichem Zertifikat; APK nur nach erfolgreicher CI und Signaturprüfung.

## 23. November · Zeichenpapier UX-Hotfix (priorisiert)
1. P0: Keine Dialog-Pop-Skalierung: Blatt von oben entfalten; Controls erst nach Papierbewegung. Systemweit reduzierte Animationen respektieren.
2. P0: Zeichenebene mit Sicherheitsabstand innerhalb des sichtbaren Papierbereichs; gespeicherte Ansicht mit identischen Koordinaten für Text und Skizze.
3. P1: Rückgängig/Wiederholen als TalkBack-beschriftete Icons, Textwechsel mit Textsymbol, Bestätigen als Senden-Symbol.
4. P1: Aufklapp-/Zuklapp-Zustände, Rotation, IME/Insets und sehr lange Texte auf echten Geräten prüfen; Pixelvergleich von Editier- und Leseansicht.
5. P1: Bestehende Room-Daten und updatefähige Signatur beibehalten; APK erst nach erfolgreichem CI, Fingerprint- und Versionsprüfung veröffentlichen.

**Provenienz:** Sämtliche UI-Elemente sind Compose-gezeichnet; Material Icons aus der AndroidX-Compose-Abhängigkeit. Keine fremden Bild- oder Audioassets.

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


---

# Motion-Update · 0.2.2 – Papier, Eimer & Haptik (22.11.2026)

**Auslöser:** Nutzerfeedback nach 0.2.1: Übergang Text↔Skizze soll wie dasselbe physische Blatt wirken; bisherige „Faltung“ skaliert den gesamten Zettel und sieht nicht wie Papierfaltung aus. Eimer fühlen sich beim Einwerfen und Schütteln zu statisch an.

## Recherche und Motion-Entscheidung
Jetpack Compose unterstützt schnelle transformierte Layer via `graphicsLayer`; mit `Canvas`/Pfaden lassen sich gefaltete Flächen, Falzkanten und Schattierung selbst zeichnen. Statt Bildschirmfotos und Fremd-Animationsassets verwendet 0.2.2 **eine eigene, prozedurale dreistufige 2,5D-Papierfaltung**. Das ist keine physikalisch korrekte Stoff-/Papier-Simulation, aber eine klar unterscheidbare Flächenfaltung mit sichtbaren Knicken, Tiefenlicht und anschließend greifbarem Paket.

## P0 – Interaktionsregeln
1. Textfeld → Skizzeneditor: Papier wächst aus seiner kleinen Ausgangsform, schwenkt leicht auf und legt sich ruhig hin. Beim Wechsel zurück läuft dieselbe Skalierung rückwärts, **erst danach** wird die Texteingabe fokussiert. Keine Zeichnung wird beim Übergang gelöscht.
2. Zettel falten: **genau drei Schritte** (rechte Hälfte umlegen, untere Hälfte nach oben klappen, Ecke einschlagen) als getrennte Projektion/Schattierung, nicht bloß Gesamtskalierung und Rotation.
3. Eimer anvisieren: eindeutiger goldener Fokuszustand, kurzer Haptikimpuls **nur beim Wechsel** des Ziels. Kein ständiges Vibrieren beim Drag.
4. Erfolgreicher Einwurf: erst Datenbank-Commit bestätigen, dann zartes Eimerwippen (kurzer Impuls mit Rückfederung), diskrete Systemhaptik. Ein fehlgeschlagener Write darf keine Erfolgsgeste zeigen.
5. Eimer halten & schütteln: genau der gehaltene Eimer ist deutlich markiert; er rüttelt leicht, bei Treffer wird eine Papier-„Auswurf“-Bewegung abgespielt und erst danach der Zufallszettel angezeigt. Sensor bleibt während eines echten Holds aktiv, kein Polling im Hintergrund.

## P1 – Fehlerfälle und Accessibility
- Bei ausgeschalteten Systemanimationen möglichst direkte Zustandswechsel; keine Zeitverzögerung, wenn Bewegungen deaktiviert sind.
- Verletze nicht den Raum des Drag-Ziels: Render-Animation darf die tatsächlichen Drop-Hitboxes nicht verschieben.
- Bei fehlendem Eimer, leerem Eimer, abgebrochenem Drag, DB-Fehler oder Prozess-Neustart gehen keine Entwürfe verloren.
- Haptik über Android-System-API, die Geräteeinstellungen respektiert; keine Vibrations-Berechtigung oder Dauer-Vibration.
- Zeichnungs-/Textmode-Kontrollen müssen beschriftet sein und mit TalkBack erreichbar bleiben.

## Release-Gates
Bestehende Paketkennung `de.zimt.eimerliste` und Room v1 unverändert; Release `0.2.2` / monoton steigender `versionCode`; grüner Debug- und Release-Build sowie Unit-Tests. Erst nach Prüfung der ursprünglichen Release-Signatur und der APK-Daten veröffentlichen. Echten UI-/Haptiktest am Nutzertelefon nicht behaupten.

## Forschung
- Android Developers: Compose graphicsLayer und Animations-Quick-Guide (Transformationen und Animationszustände).
- Android Developers: DrawScope / Canvas für polygonale Faltungen.
- Android Developers: HapticFeedbackType, u.a. Confirm, Reject, GestureThresholdActivate.


---

# Zyklus 22.11.2026 17:00 · 0.2.2 Qualitäts-Gate: Faltphasen & Updatekanal

## Ist-Zustand
- Letzter verifizierter GitHub-Stand vor diesem Zyklus: 0.2.2, versionCode 5; Debug-Paket besitzt jetzt `applicationIdSuffix = ".debug"` und kann unabhängig von der Original-App installiert werden.
- GitHub-CI prüft Debug, Unit-Tests und unsigned Release; ein grüner CI-Lauf allein beweist **keine** Signaturgleichheit mit 0.1.0.
- Private Original-Signierdateien liegen außerhalb des öffentlichen Repositories; keine Schlüssel oder Passwörter committen.

## P0 – Abnahme und implementierter Test
- Faltsequenz als drei monotone, auf 0..1 begrenzte Phasen über 301 Zeitpunkte prüfen, damit weder negative Faltwinkel noch rückwärts springende Teilfaltungen entstehen.
- Ausreißer vor 0 und nach 3 müssen exakt die Start- bzw. Endpose ergeben.
- Neue Unit-Tests in `FoldPoseTest.kt` sind ein Schutz gegen spätere Änderungen an der Papieranimation; keine Laufzeit- oder Datenbankänderung.

## P1 – Nächste nutzerwirksame Verbesserung
- Text und Skizze gleichzeitig in der Papier-Vorschau anzeigen; aktuelle exklusive `if/else if`-Darstellung priorisiert Text und blendet die Zeichnung aus, sobald Text vorhanden ist.
- Für kombinierte Notizen getrennte Ebenen und stabile Textpositionen einführen, ohne das Payload-Format oder Room v1 zu ändern. Test auf alle drei Inhaltstypen.

## Release- und Daten-Gates
- Debug darf parallel installierbar sein (`de.zimt.eimerliste.debug`), Release bleibt `de.zimt.eimerliste`.
- Nur ein signiertes APK mit Fingerprint des originalen 0.1.0-Zertifikats, höherem versionCode und geprüfter Paketkennung ist als Update freizugeben.
- Vorhandene Eimer/Zettel/Skizzen bleiben erhalten; Realgerät-Update-/Restore-Test ist offen. Kein neuer Versionssprung nur für zusätzliche Tests.


---

# Zyklus 22.11.2026 20:04 · Eimer-Erstellung bei Rotation stabil halten

## Befund
Im Dialog `CreateBucketDialog` wird der Eimername mit `rememberSaveable` gehalten, die Materialwahl aber nur mit `remember`. Dreht man das Handy während der Erstellung, bleibt der Name erhalten, die Materialwahl springt auf Zink zurück. Das ist ein stiller, frustrierender Verlust einer bewussten Nutzerauswahl.

## P0 · Risikoarmer Fix
- Materialwahl ebenfalls mit `rememberSaveable` speichern. `BucketMaterial` ist ein Enum; die bestehende UI und Materialliste bleiben unverändert.
- Keine Änderung an `applicationId`, Datenbank, gespeicherten Zetteln oder der charmingX4-Gestaltung.

## P1 · Abnahme
- Eimer erstellen, z. B. Messing auswählen, Namen eingeben, Gerät drehen: Name und Messing müssen erhalten bleiben.
- Dialog abbrechen und neu öffnen: Default Zink, kein ungewolltes Übernehmen einer alten Auswahl.
- CI: Debug-Compile, Unit-Tests und unsigned Release-Compile. Realgerät-Rotation bleibt bis zum Gerätetest offen.

## Release-Gate
Dies ist ein Quellcode-Fix ohne eigenen Versionssprung. Eine neue updatefähige APK erst nach grünem CI und Signatur-/Paket-/Versionsvergleich mit 0.1.0 freigeben; vorhandene 0.2.2 APK nicht als neuen Build ausgeben.


---

# Zyklus 22.11.2026 23:00 · 0.3 Robustheit: Schüttelsensor-Zeitstempel

## Befund
`ShakeDecision` nimmt monotone Zeitstempel an. Nach einem Zeitbasiswechsel bzw. rückwärts laufenden Sensorzeitstempel können alte Peaks mit neuen Peaks kombiniert werden und eine ungewollte Ziehung auslösen. Außerdem können ungültige Sensormessungen die Erkennung beeinflussen.

## P0 · Kleine Korrektur
- Rückwärts laufende Zeitstempel verwerfen und Peak-Sequenz zurücksetzen, ohne eine Ziehung auszulösen.
- Nicht-endliche Magnituden und Schwellwerte ignorieren.
- Unit-Tests für rückwärts laufende Zeit und NaN/Infinity; bestehende Hysterese und 1600-ms-Sperre beibehalten.

## Release-Gate
Paketkennung `de.zimt.eimerliste`, Room-Schema und bestehende Nutzerdaten unangetastet. Debug-Tests und Release-Build über GitHub Actions; Update-APK erst nach Signaturvergleich mit 0.1.0, höherem versionCode und sicherem Download. Kein neuer Signierschlüssel.
