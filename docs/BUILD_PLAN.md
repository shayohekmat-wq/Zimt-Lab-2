# Eimer-Liste · verbindlicher aktiver Buildplan
**Stand: 23.11.2026 · Gültig für Zimt-Lab-2 · Priorität: echtes Designlabor-Redesign**

Dieser Plan ist die **einzige aktive Aufgabenpriorisierung**. Frühere Entwürfe, Versionsideen und bereits erledigte Hotfix-Pläne stehen in [BUILD_PLAN_HISTORY.md](BUILD_PLAN_HISTORY.md), nicht hier. Herkunftsregeln: [DESIGN_ASSET_PROVENANCE.md](DESIGN_ASSET_PROVENANCE.md). Datensicherheit: [RELEASE_AND_DATA.md](RELEASE_AND_DATA.md).

## Build-Auslöser und Benachrichtigung (Nutzervorgabe)
- **„weiter machen“** (auch „weitermachen“) in diesem Projektchat bedeutet: **sofort den nächsten tatsächlichen Entwicklungs-/Buildzyklus starten**, nicht bloß den Plan erläutern. GitHub-Codeänderung, Tests, Build und überprüfbare Artefakte soweit technisch möglich durchführen. Wenn ein Schritt blockiert ist, Blocker und tatsächlichen Stand nennen; keinen Build-Erfolg behaupten.
- Die bestehende zeitgesteuerte Entwicklungsautomatisierung läuft zusätzlich unabhängig von Chat-Nachrichten. Eine Chat-Nachricht löst nicht technisch automatisch einen GitHub-Actions-Workflow aus; der Assistent muss den Build bei Eingang aktiv anstoßen.
- **Nur bei vollständig fertiggestelltem und qualitätsgeprüftem neuen Designlabor** die besondere Abschlussbenachrichtigung mit **✨** verwenden. Für Zwischenversionen, Commits, CI-Erfolge und Einzelverbesserungen ist das Symbol verboten. Nenne Version, konkrete sichtbare Verbesserungen, Teststatus und APK-Link nur wenn wirklich verfügbar. Keine Erfolgsmeldung bei reiner Planung, grünem CI ohne Qualitätsprüfung oder fehlender signierter Update-APK.

## 1. Unverhandelbare Produktentscheidung
Wir wollen **viele außergewöhnlich schöne, wirklich unterschiedliche und voll funktionierende Eimer**. Keine festen acht Stilrichtungen, keine 8×6-Pflicht und keine künstliche Zielzahl. Umfärbungen derselben Grundform sind keine neuen Designs.

**Drei ausdrücklich erwünschte und gleichberechtigte Entstehungswege:**
1. **Viele von Menschen gestaltete digitale Grafikassets** (PNG, SVG, Sprites, handmodellierte 3D-Objekte/Renderings, professionelles Game-Art-Design). Fortlaufend konkrete, lizenzierbare Einzeldateien mit Urheber und Original-Link recherchieren.
2. **KI-generierte Einzelgrafiken**, mit echter Qualität, eindeutiger Herkunftsangabe und technisch nutzbaren Assets.
3. **Eigene, neu konstruierte Originaldesigns**, insbesondere ein hervorragend ausgearbeiteter Referenzeimer.

**Wichtig:** Viele menschlich gestaltete Optionen sind Pflicht als Angebot, **nicht** als exklusive Beschaffungsquelle. Fotos handgefertigter Körbe und historische Korbillustrationen sind nicht mit professionell designten digitalen Grafikassets gleichzusetzen. Provenienz ehrlich ausweisen; unsichere Urheberschaft als unbestätigt markieren.

## 2. Audit des bestehenden Codes (nach Quellprüfung)
| Technischer Befund | Konsequenz |
| --- | --- |
| `DesignLab.kt`: 48 `LabDesign`-IDs aus 8 festen Stilgruppen, überwiegend Compose-Canvas-Formen | Als **Legacy-Experimente** behalten, nicht als fertige Premiumkollektion zählen; neuen dynamischen Katalog ergänzen |
| `BucketVisualProfile.hasSeparateFrontLayer` standardmäßig `false` | Echte Vorder-/Rückebenen fehlen im bisherigen Profil; korrekte Papier-Okklusion zuerst entwickeln |
| `BucketDesignPreferences.set` akzeptiert nur `findLabDesign(designId)` | Resolver und Speicherung für neue Asset-IDs erweitern, alte Zuordnungen unverändert erhalten |
| Bisherige App-Darstellung zeichnet auswerfenden Zettel über dem kompletten Eimer | Gemeinsames Layer-/Maskenmodell für Einwurf und Entnahme erforderlich |
| Keine nachgewiesene Sammlung fertig integrierter Premium-Bildassets | Keine Platzhalter als lieferbare Designs verkaufen |
| Originalsignierung ist von der normalen CI zu unterscheiden | Kein unsigned/debug Build als updatefähige APK bezeichnen |

## 3. Umsetzung: vertikale, prüfbare Entwicklungsschritte

### P0-A · Rendering-Grundlage
- [ ] Datengetriebenen `BucketArtworkSpec`-Katalog einführen: unveränderliche ID, Herkunft, Grafikressourcen, Layer, Öffnungsgeometrie, Papierpfad, Namensschildanker, Lizenzstatus, Fallback.
- [ ] Reihenfolge verbindlich: **Rückwand → Innenraum/Papier mit passender Maske → Vorderwand/Lippe → Etikett/Interaktionszustand**. Bei Einwurf und Auswurf kein unplausibles Durchscheinen oder Überlagern.
- [ ] Bestehende `lab-...`-IDs weiterhin auflösen; alte Eimer, Notizen und Zeichnungen unverändert. Neue IDs nicht an alte acht Stilindizes koppeln.
- [ ] Automatisierte Tests für Katalog, stabile IDs, Fallback, Anker/Masken und State-Übergänge; Screenshot-/Visuelltest in drei Papierpositionen.
**Gate A:** Ein einfacher Testeimer demonstriert nachweisbar korrekte Okklusion bei Einwurf **und** Auswurf. Kein Gate-Erfolg allein durch neue Datenklassen.

### P0-B · Premium-Referenzeimer + parallele Grafikrecherche
- [ ] Einen **komplett neu gestalteten, herausragenden Referenzeimer** als echtes lokal eingebundenes Asset herstellen: individuelle Silhouette, glaubwürdiges Material, hochwertige Textur/Licht, transparente Kanten, sichtbare Öffnung, lesbares Namensschild.
- [ ] Die Vorder-/Rückebenen und Animationen **in der laufenden App** demonstrieren. Qualitätsreview bei echter Kachelgröße und großer Vorschau; konkrete Schwächen iterativ beheben.
- [ ] **Parallel in jedem Designzyklus mehrere konkrete menschlich gestaltete digitale Asset-Kandidaten** finden: Künstler, Originaldatei, Lizenz, Dateiformat, Integrationsaufwand. Nicht nur allgemeine Marktplätze verlinken.
- [ ] KI-generierte und selbst konstruierte Optionen ebenfalls entwickeln; keine Quelle ausschließen.
**Gate B:** Ein echtes Referenzdesign funktioniert visuell und technisch; dazu existiert eine nachvollziehbare Shortlist konkreter menschlicher Digitalgrafiken. Keine Behauptung „10/10“ ohne Bewertung.

### P1-C · Neues Designlabor
- [ ] Kuratierte, visuell überzeugende Galerie mit **tatsächlich verfügbaren** Einzelobjekten; nicht fertige Legacy-Canvas-Prototypen in separater, klar beschrifteter Experiment-Rubrik.
- [ ] Große Live-Vorschau mit tatsächlichem Eimernamen, eindeutigem Ziel-Eimer, sichtbar ausgewähltem Design, Übernehmen/Abbrechen und rückgängig machbarer Auswahl.
- [ ] Herkunft transparent darstellen: „Von Menschen gestaltet“, „Bildgeneriert“, „Eigene Designs“. Keine leeren Kategorien als fertige Features verkaufen.
- [ ] Bei vielen Designs leichtes Stöbern/Filtern und ressourcenschonendes Laden statt überladener Listen.
**Gate C:** Nutzer kann Design wählen, ansehen, übernehmen, nach Neustart wiederfinden und zurücksetzen; Zettel und Zeichnungen bleiben erhalten.

### P1-D · Ausbau zur vielfältigen Kollektion
- [ ] Neue Designs unterscheiden sich deutlich in **Silhouette, Konstruktion, Material und visueller Sprache**. Nicht bloß Farbwechsel oder eine neue Textur.
- [ ] Für jede konkrete menschliche Datei kommerzielle App-Nutzung, Bearbeitung, Weiterverteilung, Attribution und Nicht-KI-Herkunft nachprüfen. Bei Unsicherheit nicht als bestätigt ausgeben.
- [ ] KI-Assets einzeln generieren, freistellen, Ebenen aufbereiten und tatsächlich bündeln; keine Contact-Sheets oder Texte als Grafik ausgeben.
- [ ] Gemeinsame Layer-Schnittstelle auch für ungewöhnliche Behälterformen; notfalls ein geeignetes Design verwerfen, statt die Funktion zu beschädigen.
**Gate D:** Jedes freigegebene Design besteht dieselben visuellen, funktionalen und rechtlichen Prüfungen. **Keine starre Stückzahl**, aber fortlaufend viele menschliche Optionen.

### P0-Release · Datensicherheit, Geräte, ZIMT-Integration
- [ ] `applicationId = de.zimt.eimerliste` für Updates unverändert; `versionCode` höher; originales Signaturzertifikat verifiziert.
- [ ] Update einer bestehenden Installation **mit Eimern, Notizen, Zeichnungen und Designauswahl** ohne Verlust prüfen; kein destruktives Room-Migrationsfallback.
- [ ] Debug-/unsigned APK ausdrücklich nicht als reguläres Update anbieten. Ohne nachgewiesenen Original-Key **Signierungsblocker melden**.
- [ ] Tests auf Nothing Phone 3a und einem älteren Android-Gerät (z. B. Moto G6) planen und tatsächliche Ergebnisse separat ausweisen. 48-dp-Touchflächen, TalkBack, 200%-Schrift, Motion-Reduction, Haptik-Fallback, Speicherverbrauch, FPS.
- [ ] Designkatalog/Renderer als ZIMT-übernehmbares Modul mit klaren Schnittstellen bauen; Datenmigration zwischen App-Paketkennungen gesondert testen.
**Release-Gate:** Erfolgreicher Build **plus** verifizierte APK-Signatur, Paket-/Versionskontrolle und Daten-Update-Test. Gerätetest nur als bestanden kennzeichnen, wenn wirklich durchgeführt.

## 4. Prioritäts- und Qualitätsregeln
- **Keine kosmetischen Mini-Commits als Ersatz für A–D.** Kleine Fehlerbehebungen nur, wenn sie einen Meilenstein blockieren oder Daten gefährden.
- **Qualität vor Geschwindigkeit; Zwischen-Builds sind erlaubt**, aber mit präzisem Entwicklungsstatus und ohne erfundene APK-Verfügbarkeit.
- **Nutzerfeedback überstimmt alte Pläne.** Widersprüchliche ältere Aussagen in das Archiv verschieben; keine mehrfachen Masterpläne.
- **Nicht fertig, solange nur dokumentiert.** Fortschritt wird an integrierten Assets, korrekter Papierinteraktion, sichtbarer Galerie und verifizierten Tests gemessen.
- **Signierschlüssel und Passwörter niemals committen oder im Chat veröffentlichen.**

## 5. Verbindlicher Bericht je Entwicklungszyklus
`Commit/Dateien | sichtbare funktionierende Änderung | tatsächliche Tests | offener Gerätetest | mehrere konkrete menschliche Grafikassets + Urheber/Lizenz/Original-Link | KI-/Eigendesign-Fortschritt | originalsignierte UPDATE-APK verfügbar? | nächster P0-Blocker`

**Nächster Auftrag:** Das verbindliche große Build-Paket aus Abschnitt 6 implementieren und verifizieren. Keine neue Versionsnummer nur für Planänderungen.

## 6. Aktualisierung 23.11.2026 – verbindlicher nächster großer Designlabor-Build

**Nutzerfeedback:** Die bisherigen kleinen Material-, Kontur-, Button- und Prototypänderungen erzeugten keine wirklich neue Designlabor-Erfahrung. Ab jetzt nur substanzielle, zusammenhängende, sichtbar nutzbare Entwicklungsmeilensteine als Produktfortschritt melden. Kleine interne Commits bleiben zulässig, zählen aber nicht als Meilenstein.

### Tatsächlicher Quellstand (Commit e9edeeab8d888be1d42f7218d8a3a2b641c7190c)
- [x] `BucketArtworkCatalog.kt`: ID-, Herkunfts-, Öffnungs- und Papierpfad-Grundmodell, Legacy- und Studien-IDs.
- [x] `LayeredBucketPrototype.kt`: technischer REAR→PAPER→FRONT→LABEL-Test mit drei Materialstudien, **kein Premiumdesign**.
- [x] `WickerReferenceBucket.kt`: erster eigenständig konstruierter geflochtener Picknickkorb mit Henkel, Innenraum, Front und Papierpfad.
- [x] `DesignLab.kt` / `EimerApp.kt`: neue Picknickkorb-Vorschau mit Papierregler, Ziel-Eimer-Auswahl, Übernahme und Rendering im Hauptgitter im Quellcode verdrahtet.
- [ ] Noch **nicht nachgewiesen**: erfolgreicher aktueller CI-Build, Geräteprüfung, echter Einwurf/Auswurf im Hauptgitter, Premium-Grafikqualität, menschliche Lizenzassets, KI-Einzelassets, vollwertige neue Galerie. Quellcode allein ist keine Qualitätsabnahme.

### Nächstes Build-Paket – alle Punkte als zusammenhängendes Produktziel
1. **Echtes Premium-Asset:** Mindestens ein visuell herausragender freigestellter Eimer mit individuellen lokal eingebundenen Rück-/Vordergrafiken, glaubwürdigem Material, Schatten, Öffnungsmaske und Namensschild. Den bisherigen Vektor-Picknickkorb kritisch prüfen, nicht als fertiges Premiumasset ausgeben.
2. **Reale Papieranimation:** Einwurf und Auswurf im tatsächlichen Eimer-Tile durch denselben Layer-Renderer, mit echter Vorderlippen-Okklusion, Falten/Knüllen, passender Eimerbewegung und Haptik. Drei Papierpositionen visuell prüfen; Slider allein reicht nicht.
3. **Neue kuratierte Galerie:** Große echte Asset-Vorschauen, Herkunftsgruppen, Ziel-Eimer, Übernehmen/Abbrechen, Rücksetzen, Persistenz. Legacy-Experimente klar getrennt; keine Textplatzhalter als neue Designs.
4. **Parallel menschliche digitale Assets:** Pro Entwicklungszyklus mehrere **konkrete Einzelgrafiken** von menschlichen Designern mit Originaldatei-URL, Urheber, Lizenz, kommerzieller Nutzbarkeit, Dateiformat und Layer-Aufwand recherchieren und dokumentieren. Keine Rohfotos oder historische Illustrationen als Ersatz. KI-generierte und eigene Assets bleiben gleichberechtigt erwünscht.
5. **Qualitäts-Gate:** Erfolgreiche CI und Tests, echte Screenshot-/Visuellprüfung in Galerie und Hauptgitter, Papierpfad, Auswahl/Persistenz, Android-Kompatibilität, Datenbestandsschutz. Für eine Update-APK Originalsignatur, unveränderte Paketkennung und steigenden `versionCode` prüfen; APK als Datei liefern, wenn technisch möglich. Nicht getestete Eigenschaften ausdrücklich offenlassen.

### Verbindliche Regeln für jeden weiteren Zyklus
- **Keine Mini-Updates als großer Fortschritt.** Der Nutzer muss unmittelbar einen deutlich neuen und tatsächlich nutzbaren Designlabor-Zustand sehen können.
- **Keine Schein-Galerien:** Nur nachweislich vorhandene, korrekt integrierte, nutzbare Assets als fertig anbieten.
- **Vor jeder Rückfrage vorhandene Zugänge prüfen:** Dateien, frühere Uploads, Schlüssel, Passwörter, GitHub und Artefakte selbst suchen; keine unnötige erneute Upload-Anforderung. Geheime Daten niemals committen oder offenlegen.
- **Keine unverdiente Abschlussmeldung:** ✨ ausschließlich bei vollständig fertiggestelltem, qualitätsgeprüftem neuen Designlabor.
- **ZIMT-Integration sicherstellen:** Renderer und Designkatalog modular und aus der Lab-App übernehmbar halten.
- **Versionen nicht durch Planänderungen erhöhen.** Build und APK erst als erfolgreich bezeichnen, wenn die relevanten Prüfungen tatsächlich vorliegen.

## 7. Dauerhafte Semantik von „Weiter machen“ – Entwicklungszyklus UND Planpflege

**Verbindliche Nutzeranweisung:** Jedes „Weiter machen“ bedeutet ab sofort zwei untrennbare Arbeitsaufträge:
1. **Tatsächlich entwickeln:** Den nächsten substanziellen Schritt des aktiven Designlabor-Ziels im Repository umsetzen, testen und soweit möglich bauen; keine reine Planantwort oder kosmetische Ersatzleistung.
2. **Build-Plan synchronisieren:** In demselben Zyklus diese Datei `docs/BUILD_PLAN.md` aktualisieren: nachweislich erledigte Aufgaben abhaken, tatsächlichen CI-/Teststatus eintragen, offene Blocker benennen und den nächsten großen, zusammenhängenden Schritt präzisieren.

**Zielschutz:** Der Plan darf bei jedem Zyklus konkreter werden, aber niemals durch neue Nebenideen, Versionsnummern, Mikrotasks oder alte Prototypen vom Hauptziel abweichen: eine hochwertige, vielfältige, kuratierte und vollständig nutzbare Eimer-Designgalerie mit echten Assets, korrektem Papier-Layering, lizenzierter menschlicher Digitalgrafik, KI- und Eigendesigns, Datenerhalt und späterer ZIMT-Integration. Änderungen der Priorität müssen an diesem Ziel gemessen werden; widersprüchliche alte Anweisungen werden archiviert, nicht als paralleler Masterplan fortgeführt.

**Wahrheitsregel:** Häkchen nur bei implementiertem und nachweislich geprüftem Umfang; Quellcode-Commit, CI-Erfolg, Gerätetest, visuelle Qualität und originalsignierte APK getrennt ausweisen. Keine bloßen Plan-Commits als Entwicklungsfortschritt zählen. Die Planaktualisierung ist Bestandteil jedes echten Builds, nicht sein Ersatz.

## 8. Entwicklungszyklus 23.11.2026 – echte Papierinteraktion im Hauptgitter

**Implementiert (Commit de4cb6dd42a423bbfdf5fef50599969e74b3c831):** Der eigenständige Flechtkorb nutzt bei tatsächlichem Zettel-Einwurf den `impactToken`, um die Papierposition innerhalb seiner REAR/PAPER/FRONT-Darstellung von oberhalb der Öffnung ins Innere zu animieren. Beim tatsächlichen Auswurf wird dieselbe interne Papierposition rückwärts über `ejectFlight` animiert. Der vorher zusätzlich über den Korb gelegte Auswurf-Zettel wird für dieses Design unterdrückt, um Doppelbilder und falsche Überdeckung zu vermeiden. Bestehende Legacy-Eimer bleiben unverändert. Das ist eine **funktionale Integration im Hauptgitter**, keine Premium-Asset-Freigabe.

**Noch offen / Qualitätsgrenzen:** Animation ist im Quellcode verdrahtet, aber Screenshot-/Geräteabnahme und CI dieses Commits müssen separat bestätigt werden. Es fehlt weiterhin die hochwertige freigestellte Premium-Grafik, eine breite kuratierte Galerie und die geprüften menschlichen/AI-Einzelassets. Keine Behauptung, das neue Designlabor sei schon fertig. Der derzeitige Vektorkorb ist ein Funktionsreferenzobjekt.

**Nächster großer Build-Schritt:** Echte hochwertige, getrennte transparente Vorder-/Rückgrafiken statt Canvas-Demo in denselben funktionalen Renderer integrieren; mehrere unverwechselbare Designs in einer echten Galerie bereitstellen; parallel konkret lizenzierte menschliche digitale Asset-Dateien recherchieren. Danach drei Papierpositionen und Einwurf/Auswurf auf Gerät prüfen, Datenmigration und signiertes Update verifizieren. Planpflege immer gemeinsam mit tatsächlicher Entwicklung.

## 9. Release-Abschluss 23.11.2026 – 0.3.3-dev4 (versionCode 17)

- [x] CI-Lauf [45097506539](https://github.com/shayohekmat-wq/Zimt-Lab-2/actions/runs/45097506539) für Commit `0768954f24b3786790f4e86a73703297e08072c8`: **success**; Debug-/Unit-Test- und Release-Kompilierung erfolgreich.
- [x] Originales UNSIGNED-release-Artefakt `14784046604` privat heruntergeladen und mit ursprünglichem lokalem PKCS12-Schlüssel signiert. Schlüssel und Passwort nicht im Repository/CI hinterlegt.
- [x] `apksigner verify --verbose --print-certs`: **Verifies**, v2/v3 erfolgreich; Zertifikats-SHA256 `40e6b73383a29f9ca792595b369329e2ab8f9165f49eec69b4eb57a8ffe26fe1`.
- [x] Android-Manifest geprüft: Paket `de.zimt.eimerliste`, Version `0.3.3-dev4`, versionCode `17` (vorher `16`).
- [x] Signierte Datei privat erstellt: `Eimer-Liste-Designlabor-2026-11-23-0200-UPDATE.apk`, SHA256 `c2a38ea0d4a3c7fdb20918b2943b559303f6989357928077c9fc486f6809c838`.
- [ ] **Nicht durchgeführt:** echte Installation über Bestands-App auf Nothing Phone 3a/Moto G6, Room-Datenmigration auf Gerät, Screenshot-/visuelle Premium-Abnahme. Daher Entwicklungs-Update, **kein Designlabor-Finalrelease**.

**Nächster Entwicklungszyklus:** Hochwertige lokale Einzelassets mit echten transparenten Front-/Back-Layern statt Canvas-Demos, Galerie mit mehreren tatsächlich auswählbaren Designs, konkrete lizenzgeprüfte digitale Grafiken menschlicher Urheber. Vor weiteren Änderungen zuerst aktuelle Release-Artefakte prüfen; Plan mit jedem Zyklus synchronisieren.

## 9. Entwicklungszyklus 23.11.2026 – Skizzen-Enter + Designlabor (Ergänzung, kein Zielwechsel)

**Nutzerkorrektur:** In der aufgeklappten Mal-/Skizzenansicht soll die Enter-/Falt-Schaltfläche den Zettel **wirklich falten**, nicht bloß zur Texteingabe zurückkehren. Der Text auf dem großen Zeichenblatt soll etwas tiefer stehen. Diese Änderungen sind Ergänzungen und dürfen das Designlabor-Hauptziel nicht ersetzen. In der normalen mehrzeiligen Texteingabe bleibt Enter ein Zeilenumbruch.

**Implementierter Quellstand:**
- [x] `PaperSketchEditor.kt`: separater `onFold`-Callback für die untere Falt-Schaltfläche, bestehender Textwechsel-Button bleibt unabhängig; Text auf dem ausgeklappten Papier von 21 auf 32 dp oberen Abstand versetzt (Commit `56f3f9816ace0aa325c7c613f8546a11ce1367e8`).
- [x] `EimerApp.kt`: Skizze wird vor dem Falten gesichert; Falten nur bei gültigem Zettel und vorhandenem Eimer, dann startet der vorhandene echte Falt-/Einwurfablauf (Commit `410d4a837e33ce4f1d5bdc3ca7c0d8b9234232ef`).
- [x] **Parallel Designlabor:** Die echte Flechtkorb-Vorschau erhält eine abspielbare bidirektionale Einwurf-/Auswurfdemonstration zusätzlich zum manuellen Regler (Commit `1c49e3a0be2e8e4b43df93becc74bdd42eed8cd9`).
- [ ] CI-Ergebnis dieses Standes, reale Tastatur-/TalkBack-/Geräteprüfung, visuelle Abnahme und originalsignierte UPDATE-APK separat nachweisen.

**Nächstes großes Designlabor-Ziel unverändert:** Professionelle lokal eingebundene, transparente Back-/Front-Grafikassets, kuratierte Vielfalt inklusive konkret lizenzierter menschlicher Digitalgrafiken, eigene und KI-Designs, durchgängige Papierinteraktion in echten Tiles, geprüfte Persistenz und originalsigniertes Update. Kleine UX-Korrekturen dürfen den Hauptentwicklungszyklus nicht verdrängen.

## 10. Entwicklungszyklus 23.11.2026 – Papieröffnung absichern, CI der vorigen Änderungen geprüft

**Bestätigt:** GitHub Actions für Commit `9674ef0acd63c418e3196e673439f38c7b23822d` (Skizzen-Faltschaltfläche, abgesenkter Text und Designlabor-Papiervorschau) erfolgreich abgeschlossen. Das ist eine **CI-Bestätigung**, keine Geräte-/Grafik-/Signaturabnahme.

**Neue tatsächliche Codeänderung:**
- [x] `BucketArtworkCatalog.kt` (Commit `9b910d723052a23825e0b9b51f21427b5d1d1472`): Ein Papierpfad darf die Eimeröffnung nicht nur vertikal berühren, sondern muss auch **horizontal durch die sichtbare Öffnung** laufen. Damit werden spätere Back-/Front-Premiumassets mit fehlerhaftem Einwurf durch eine massive Seitenwand bereits im Katalog zurückgewiesen.
- [x] `BucketArtworkCatalogTest.kt` (Commit `f112d92bb03584718943a335a87a33a3b2eaef9b`): Regressionstests für ungültigen seitlichen Einwurf und gültigen mittigen Einwurf ergänzt.
- [ ] CI dieser beiden neuen Commits noch prüfen; Tests nicht vor CI als bestanden ausgeben.

**Zielkontrolle:** Diese technische Schutzmaßnahme dient dem zentralen Designlabor-Ziel, ist aber **kein neuer Premiumeimer** und kein Ersatz für hochwertige Grafikassets. Nächster substantieller Meilenstein bleibt eine kuratierte Galerie mit echten transparenten Vorder-/Rückgrafiken und vielfältigen, rechtlich geprüften menschlich designten Digitalgrafiken sowie KI-/Originaldesigns. Vor Freigabe visuelle Abnahme, Datenbestandsschutz und Originalsignatur nachweisen.

## 11. Nächster Build: große Neugestaltung – Mindestziel 50 % (Nutzeranweisung)

**Verbindliche Priorität:** Im **nächsten tatsächlichen Build** mindestens **50 % der geplanten großen Designlabor-Neugestaltung** als sichtbare, nutzbare und überprüfte Produktleistung liefern, ausdrücklich gerne deutlich mehr. Das ist ein **Ziel**, keine Behauptung des bisherigen Fortschritts. Kleine technische Änderungen, bloße Commits, vorhandene Legacy-Designs, kosmetische Varianten und unfertige Canvas-Studien dürfen nicht als erreichte 50 % gezählt werden. Keine neue Version nur zur Darstellung eines Prozentwerts.

### Transparente, gewichtete Abnahme (100 Punkte)
- **30 Punkte – hochwertige neue Eimerassets:** deutlich unterschiedliche, individuell gestaltete Silhouetten und Materialien; echte freigestellte Vorder-/Rückgrafiken und Öffnung; überzeugend bei Handy-Kachelgröße und großer Vorschau. Keine Farbduplikate. Punkte nur für tatsächlich integrierte, sichtbare Assets.
- **25 Punkte – neues Designlabor als Galerie:** große visuelle Vorschauen, klare Herkunftskennzeichnung, einfache Navigation/Filter, ausgewählter Zieleimer, Übernehmen/Abbrechen/Rücksetzen, korrekte Darstellung im Hauptgitter. Keine Scheinrubriken.
- **20 Punkte – vollständige Papierinteraktion:** Falt-/Knüll-, Einwurf- und Auswurfanimation durch reale Öffnung, korrekte Verdeckung und Eimerbewegung, Haptik-Fallback. In echten Tiles, nicht nur per Testslider.
- **15 Punkte – echte Designvielfalt und Herkunft:** konkret nachgewiesene, lizenzierte menschliche digitale Assets sowie eigenständige KI- und Originaldesigns, mit korrekt dokumentierter Provenienz und nutzbaren Einzelgrafiken. Nicht erfüllte Herkunftsgruppen ehrlich offenlassen.
- **10 Punkte – Robustheit und Integration:** Datenpersistenz, Legacy-Fallback, Tests/CI, Android-Kompatibilität, modular übernehmbare ZIMT-Komponenten; signiertes Update erst nach Originalzertifikatsprüfung als fertig ausgeben.

**50-%-Gate:** Mindestens 50 tatsächlich belegte Punkte **und** zwingend ein sichtbar neues, hochwertiges Designasset, eine funktionierende Galerie-Auswahl mit Übernahme und echte Papierinteraktion. Kein Punktesammeln durch isolierte Tests oder kosmetische Funktionen. **Stretch:** so viele weitere Kategorien und eigenständige Premiumeimer wie qualitätsgesichert möglich. Kein Freigabeversprechen ohne bestandene Prüfungen.

### Nächste unmittelbare Entwicklungsarbeiten
1. Vorhandenen Katalog/Renderer und erfolgreichen CI-Stand nutzen, keine alten Funktionen erneut erfinden.
2. Hochwertige lokale freigestellte Eimergrafiken mit separater Rückwand/Vorderwand/Öffnungsmaske erstellen und integrieren; echte Unterschiede statt Varianten derselben Grundform.
3. Designlabor von einem einzelnen Referenzkorb mit Textabschnitten zu einer kuratierten, bildstarken Galerie umbauen. Menschliche lizenzierte Digitalgrafiken konkret mit Künstler/Original-Link/Lizenz prüfen; KI- und Originaldesigns gleichberechtigt weiterentwickeln.
4. Echte Einwurf-/Auswurf-/Faltanimation, Auswahl, Persistenz, Haptik und ZIMT-Modulgrenzen gemeinsam prüfen.
5. Im **gleichen Buildzyklus** diesen Masterplan mit belegtem Erfüllungsgrad je Kategorie, Commits, Tests, CI, offenen Blockern und dem nächsten großen Schritt aktualisieren. Wenn 50 % nicht belegbar sind, ehrlichen Iststand melden und **keine** künstliche Erfolgsfreigabe.

## 12. Umsetzung des 50-%-Auftrags – tatsächlicher Zwischenstand

**Nutzerauftrag:** Im nächsten Build mindestens 50 % der großen Neugestaltung erreichen. Der Qualitätsmaßstab aus Abschnitt 11 bleibt verbindlich; Prozentwerte sind kein Marketing.

**Tatsächliche Implementierung, Commit `58ced7c7a6ec2310f78a9919cb98687f14e63e40`:** Eine direkt bedienbare, zweispaltige Designauswahl wurde in das Designlabor integriert: vier real gerenderte Motive (ein selbst konstruierter Flechtkorb und drei ausdrücklich als technische Studien gekennzeichnete Materialvarianten), große Live-Vorschau, sichtbare Auswahl, Zieleimer-Auswahl, Übernehmen mit Bestätigungsdialog, Abbrechen, vorhandene persistente Auswahlfunktion, Datenerhalt-Hinweis. Legacy-Experimente bleiben separat. **Diese vier Motive sind keine vier Premiumgrafiken und werden nicht als solche gezählt.**

**CI:** Für den vorherigen Stand `4432508f0e2929d06c07367a6f85dab9e763e550` erfolgreich; CI für die neue Galerie zunächst ausstehend. Kein Geräte- oder Screenshottest nachgewiesen.

**50-%-Gate noch NICHT erfüllt:** Insbesondere die zwingenden hochwertigen freigestellten Premiumassets und belegte lizenzierte menschliche Digitalgrafiken fehlen. Die neue Galerie ist funktional, aber die Qualität der Bildinhalte reicht noch nicht zur visuellen Freigabe. Den 50-%-Meilenstein nicht als abgeschlossen melden, solange die Bedingungen aus Abschnitt 11 nicht erfüllt sind.

**Unmittelbare Fortsetzung (Priorität vor weiteren Kleinänderungen):** Mehrere tatsächlich unterschiedliche, hochwertig ausgearbeitete freigestellte Back-/Front-Grafikassets in App integrieren, korrekte Öffnung/Okklusion in Galerie und Hauptgitter verbinden; geprüfte menschliche digitale Grafikassets und eigenständige KI-/Originalmotive ergänzen; visuelle Abnahme und Signaturprüfung durchführen. Erst dann Punkte belegen und ggf. 50 % oder mehr freigeben.
