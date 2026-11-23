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

## 13. Längerer Entwicklungszyklus: vier lokal eingebundene Split-Layer-Illustrationen + echte Integration

**Ausdrücklicher Nutzerauftrag:** Nicht nach wenigen Minuten abbrechen, sondern die große Neugestaltung ernsthaft bis zum 50-%-Qualitätsziel vorantreiben. 50 % nicht allein durch Commit-Zahl, Textänderungen oder generische Farbvarianten behaupten.

**Tatsächlich implementiert:**
- [x] Acht neue lokale transparente Android-Vector-Drawable-Dateien (je eine separate REAR- und FRONT-Grafik) für vier benannte Originalentwürfe: Astralgefäß, Kupferkessel, Porzellankrug, Waldkorb. Kein externer Bildabruf erforderlich. **Einschränkung:** gemeinsame Grundsilhouette, unterschiedliche Materialfarben/Details; noch keine vier unabhängigen Premium-Silhouetten, keine fotografisch hochwertige Textur.
- [x] `LayeredIllustratedBucket.kt`: eigenständiger wiederverwendbarer REAR → PAPER → FRONT → LABEL-Renderer, responsiver Papierpfad, ZIMT-unabhängige Composable-Schnittstelle.
- [x] `BucketArtworkCatalog.kt`: stabile, persistierbare IDs für die neuen lokal gebündelten Entwürfe.
- [x] `EimerApp.kt`: tatsächliches Hauptgitter rendert die neuen Designs; Einwurf-/Auswurf-Animation läuft durch die getrennten Lagen statt als falsches Overlay.
- [x] `DesignLab.kt`: die vier neuen Motive in der bereits entwickelten Galerie mit Live-Vorschau, Auswahl, Zieleimer und Übernahme integriert. Alte Eimer-/Zettel-Daten und Legacy-Designs bleiben unangetastet.
- [ ] Aktuelle CI, Geräteprüfung, echte Screenshot-Abnahme, individuelle hochwertige Silhouetten, menschliche lizenzierte Digitalassets und KI-Grafiken sowie originalsignierte APK noch nicht bestätigt.

**Wahrheitsgemäße 50-%-Bewertung:** Eine neue Galerie und funktionaler Layer-Renderer sind substantiell; die Grafikqualität erfüllt aber noch **nicht** das verbindliche Premium-Asset-Gate aus Abschnitt 11. Die vier Entwürfe dürfen wegen gemeinsamer Silhouette **nicht** als vier eigenständige Premiumdesigns gezählt werden. Somit 50 % noch **nicht nachgewiesen**. Es wäre unredlich, den Build als fertige Hälfte zu deklarieren.

**Nächste zwingende Schritte:** Unterschiedliche Silhouetten und tatsächlich hochwertige individuelle Grafikassets; verifizierte digitale Designer-Assets samt Lizenzen; KI-Unikate; visuelle Gerätetests; reale Android-CI- und Signaturprüfung. Den erreichten Prozentwert erst nach überprüfbarer Qualitätsabnahme freigeben.

### Ergänzung: individuelle Ornamente und CI-Fehlerbehebung
- [x] Vier Frontgrafiken zusätzlich mit motivspezifischen Elementen differenziert: Kupfernieten/-streben, Porzellanornamente, Waldblätter/-ranken und Sternmotive. Weiterhin dieselbe Grundsilhouette; **nicht als vier vollständig unabhängige Premiumassets deklarieren**.
- [x] Ersten realen CI-Kompilierfehler in `LayeredIllustratedBucket.kt` diagnostiziert (`maxHeight` im verschachtelten Scope) und mit lokal gespeicherter `vesselHeight`-Dimension behoben (Commit `0d8096afdda4262760f35eb23779f1063e352d12`). Aktuelle Nachfolge-CI-Ergebnisse stehen noch aus.
- [ ] Vor 50-%-Freigabe: erfolgreicher finaler CI-Run, visuelle Screenshots, stärkere Silhouettenvielfalt, echte Premiumtexturen, Lizenzprüfung und Gerätetest. Keine originalsignierte APK ohne Signaturprüfung.

## 14. Fortsetzung 23.11.2026 – eigenständige Silhouetten, sechs neue Motive und Live-Galerie

**Auftrag:** Länger weiterentwickeln, mindestens 50 % der großen Neugestaltung erreichen. Nicht nach einzelnen Kleinänderungen abbrechen.

**Neue tatsächlich eingecheckte Leistungen:**
- [x] Alle vier ursprünglichen Split-Layer-Illustrationen geometrisch überarbeitet: eigenständige Konturen für Astralgefäß, Kupferkessel, Porzellankrug und Waldkorb statt nur Farbwechsel derselben Grundform. Je Motiv echte separate lokale transparente REAR-/FRONT-Vektorressourcen, motivspezifische Ornamente.
- [x] Zwei zusätzliche eigenständige Motive **Uhrwerkkorb** und **Amethysttruhe** mit eigenen separaten transparenten REAR-/FRONT-Ressourcen. Gesamt **sechs neue illustrierte Originalmotive / zwölf Vektor-Layerdateien** plus vorhandener Flechtkorb. Sie sind App-lokal und benötigen kein Netzwerk.
- [x] `LayeredIllustratedBucket.kt`: alle sechs Motive mit stabilen IDs registriert, in tatsächlichem Eimer-Hauptgitter und Designlabor auswählbar; gemeinsame REAR → PAPER → FRONT-Reihenfolge.
- [x] `DesignLab.kt`: Filter zwischen **Originalgrafiken** und ausdrücklich getrennten **Materialstudien**; große Live-Vorschau besitzt Papierpositionsregler und abspielbare Einwurf-/Auswurfbewegung für alle dargestellten Designs. Auswahl, Zieleimer, Bestätigung und Datenerhalt bleiben.
- [x] `BucketArtworkCatalogTest.kt`: Regressionstests für sechs eindeutige stabile Design-IDs, separate Layerreferenzen, Legacy-Kompatibilität.
- [x] CI des Zwischenstands `59ab98b3e492d644fe730cd1612279a613b0b114` erfolgreich. **Neuester Gesamtstand und neue Tests noch nicht CI-bestätigt.**

**Abnahmepunkte ehrlich:** Neue Grafikvielfalt, funktionsfähige Galerie und tatsächliche Layerinteraktion sind erheblich ausgebaut. Die sechs Motive sind individuell gezeichnete, stilisierte Android-Vektorillustrationen; **sie sind noch keine extern kuratierten Premium-Digitalgrafiken, keine KI-generierten Renderassets und nicht auf einem Gerät visuell abgenommen**. Herkunftsgruppen HUMAN_DESIGNED und AI_GENERATED weiterhin nicht geliefert. Die 50-%-Qualitätsgrenze aus Abschnitt 11 darf nicht als abgenommen gelten, bevor mindestens ein grafisch hochwertiges freigestelltes Premiumasset visuell bestätigt und der neueste Build CI-grün ist. Aktuellen Stand nicht als signierte Update-APK ausgeben.

**Nächster Prioritätsschritt:** Visuelle Referenz-/Gerätescreenshots prüfen und Silhouetten, Texturen, Schatten und Maßstab verbessern; echte, nachweislich lizenzierte menschliche Digitalgrafiken und separate KI-Renderassets integrieren; ggf. Android-XML-/Kotlin-Fehler aus CI beheben; signierte Release-APK erst nach Prüfung des ursprünglichen Zertifikats erstellen.

## 15. NEUE HÖCHSTE PRIORITÄT – Designlabor ausschließlich mit recherchierter menschlicher Game-Art und neuen Techniken (23.11.2026)

**Verbindliche Nutzerkritik:** Die derzeitigen XML-Vector-Drawable-Eimer und generischen technischen Studien sind gestalterisch unakzeptabel. Keine weitere Zeit in ihre Ausgestaltung, Farbvarianten oder Eigenkonstruktion investieren. Sie nicht als Premiumfortschritt zählen. Bestehende IDs und persistierte Auswahl aus Kompatibilitätsgründen behalten, aber misslungene Motive im Designlabor zurückstufen oder ausblenden.

**HARTES RELEASE-GATE FÜR DEN NÄCHSTEN BUILD:** Mindestens ein tatsächlich heruntergeladenes, eindeutig einem menschlichen Künstler zugeordnetes, kommerziell rechtssicher verwendbares digitales Korb-/Eimer-/Truhen-Asset MUSS als reale Grafik in der lauffähigen App enthalten und sichtbar auswählbar sein. Bloße Links, ein Quellenkatalog, Demo-Placeholder, selbstgenerierte Vektoren oder KI-Grafiken erfüllen diese Forderung NICHT. Bevorzugt mehrere deutlich unterschiedliche menschliche Assets.

**Recherchekandidaten (noch nicht integriert):**
- DREAM_SEARCH_REPEAT, *Basket* (Blender-Modell, auf OpenGameArt als CC0 ausgewiesen): https://opengameart.org/content/basket . Texturherkunft und Lizenzverlauf gesondert verifizieren. Aus einem gemeinsamen 3D-Modell echte REAR/FRONT-Passes mit identischer Kamera rendern.
- reodesuxz, *Free Pixel Medieval Market Props* (laut Autor ohne generative KI, kommerzielle Nutzung erlaubt; Körbe und Eimer, aber 16x16/64x64 und daher nur eigenständiger Pixel-Art-Stil): https://reodesuxz.itch.io/free-pixel-medieval-market-props-16x16 . Download-Lizenz/Redistributionsbedingungen vor Einbau prüfen.

**Neue technische Richtungen:** Blender/GLB/FBX-Render-Passes, transparente PNG-Layer und echte 3D-Texturen statt programmatischer Android-Vektorgrafiken. Dokumentiere für jedes Asset Autor, Originalquelle, konkreten Lizenztext, erforderliche Attribution, Dateiformat, Integrationspfad und tatsächliche Verwendung in docs/DESIGN_ASSET_PROVENANCE.md. Prüfe Android-Build, Galerie und Hauptgitter visuell, sichere vorhandene Eimer/Zettel/Zeichnungen, signiere nur mit bisherigem Schlüssel.

**Status bei Eintrag:** Recherchekandidaten identifiziert; noch kein menschliches Asset tatsächlich in der App integriert. Dieser Abschnitt hat Vorrang vor älteren 50%-Quoten und allen bisherigen selbstprogrammierten Designstudien. Automationsauftrag entsprechend aktualisiert.

## 15. Nutzerpriorität umgesetzt: echte menschliche Game-Art statt XML-Experimente

**Verbindliche neue Priorität (23.11.2026):** Nutzer lehnt aktuelle selbst konstruierte XML-Designs ausdrücklich ab. Im nächsten Build müssen tatsächlich recherchierte digitale Künstlergrafiken **in der App enthalten** sein; nicht nur Quellen oder eine Aufgabenliste. Andere neue Techniken vor weiteren XML-Vektoren priorisieren.

**Tatsächlich erledigt:**
- [x] Fünf **echte Kenney-CC0-Game-Art-Modellvorschauen** als Binär-PNG in `app/src/main/res/drawable-nodpi/kenney_human_*.png` eingecheckt; Download durch `tools/import_human_assets.py` und erfolgreiche Import-Action `45101733924`, Commit `f0d64d9f`. Keine generierten Dummies oder XML-Neuzeichnungen.
- [x] `HumanArtistBuckets.kt`: eigener Compose-Bildrenderer mit fünf stabilen IDs; tatsächlich importierte Künstlergrafiken statt programmiertem Ersatz.
- [x] `DesignLab.kt`: neue Hauptkategorie **Von Künstlern** als Startansicht, tatsächliche Bildvorschauen, Zieleimer-Auswahl, Anwenden. Bisherige XML-Vektoren nur im Archiv, technische Studien getrennt.
- [x] `EimerApp.kt`: echte Künstlerbilder auch im Hauptgitter auf zugewiesenen Eimern gerendert, nicht nur Galerie-Links.
- [x] `BucketArtworkCatalog.kt`: stabile IDs für bestehende und neue Auswahl, kein Verlust bestehender Zettel/Zeichnungen.
- [x] `docs/DESIGN_ASSET_PROVENANCE.md`: Urheber, CC0-Rechte, direkte Quelldateien, Limitierungen, Originalpakete dokumentiert.
- [x] Tests für fünf stabile neue Design-IDs ergänzt.
- [ ] Finaler Android-CI-Run des Gesamtstands, Screenshot-/Geräteabnahme und Update-Signatur müssen noch überprüft werden.

**Qualitätsgrenze und Einschränkung:** Kenney-Thumbnails sind authentische **192×192-Renderings von menschlich gestalteten 3D-Modellen**. Noch keine hochauflösenden individuellen Premium-Eimer und keine separaten REAR/FRONT-Renderpasses. Diese neuen Motive sind als **statische** Grafiken integriert; Papier-Okklusion wird für sie nicht vorgetäuscht. Der Nutzerwunsch, echte menschliche Künstlergrafiken im Build zu haben, ist auf Quellcode- und Asset-Ebene umgesetzt; Build erst nach CI als verifiziert bezeichnen. 100-%-Premiumdesignlabor weiterhin nicht erreicht.

**Nächster technischer Schwerpunkt:** Original-GLB-Modelle mit gemeinsamer Kamera, Licht und Geometrie in getrennte hochauflösende REAR/FRONT-RGBA-Passes rendern, echte Öffnung für Zettel prüfen, anschließend mehrere höherwertige menschliche Designer-Assets recherchieren und einbauen. Keine weiteren generischen XML-Farbvarianten.

## 16. Verifizierter Künstler-Asset-Build und Release-Status (23.11.2026, 03:xx MEZ)

- Letzter abgeschlossener Android-CI-Run **45101853113**, Commit `e82148a0`: **success**. Er enthält die fünf tatsächlich importierten Kenney-CC0-Game-Art-Bilddateien, den Katalog, die Hauptgitterdarstellung, die Künstlergalerie und Tests. Unsigned-Release-Artefakt ID `14786016727` wurde heruntergeladen und **lokal mit dem privaten Originalschlüssel** signiert; apksigner verifiziert v2 und v3 sowie Originalzertifikat SHA-256 `40e6b73383a29f9ca792595b369329e2ab8f9165f49eec69b4eb57a8ffe26fe1`. **Nicht in GitHub signiert; Schlüssel niemals hochgeladen.**
- Dieser erfolgreiche Build verwendet noch `versionCode=17` / `0.3.3-dev4` und ist deshalb **kein zuverlässig aufsteigendes Update** gegenüber einer bereits installierten dev4. Deshalb expliziter Versionsbump auf `versionCode=18` / `0.3.3-dev5` im Commit `5bc31d20`. Android-CI-Run `45103246675` gestartet, bei Eintrag noch ausstehend. Erst nach CI-Erfolg und Signaturkontrolle diese Version als Update ausliefern.
- **Qualitätsstatus:** 5 echte, von Kenney gestaltete digitale 3D-Asset-Vorschaubilder eingebaut; keine Premiumfreigabe, keine getrennten Vorder-/Rückseiten, keine perfekte Papier-Okklusion, keine Android-Geräte-Screenshots. Originale XML-Designs nur im Archiv. Keine falsche 100%-Aussage.
- **Nächste große Arbeit:** Aus originalen Künstler-3D-Modellen statt kleinen Thumbnails zwei präzise, freigestellte, hochauflösende Render-Passes für echte Papierverdeckung erzeugen. Auswahl, Persistenz und Datenerhalt am Gerät verifizieren. Anschließend mehrere hochwertige andere Künstler-Asset-Kategorien.

## 17. VERBINDLICHER DESIGNLAB-SCOPE-FREEZE — drei Nutzer-PNG-Eimer (23.11.2026)

**HÖCHSTE PRIORITÄT, ÜBERSCHREIBT ALLE ÄLTEREN DESIGNLAB-PLÄNE IN DEN ABSCHNITTEN 11–16.** Der Nutzer hat sechs eigene freigestellte PNGs in das Repository hochgeladen. **Ab sofort KEINE weitere Recherche nach Künstlerassets, keine weiteren Stilrichtungen, keine Designlabor-Expansion, keine XML-Vektor-Experimente, keine Kenney-Galerie als Hauptfokus, keine Materialstudien, keine zusätzlichen Eimer.** Alte Vorschläge dazu sind ausdrücklich **gestrichen**, nicht nur vertagt. Die bisherigen Dateien und Design-IDs aus Kompatibilitätsgründen nicht destruktiv löschen; alte Einträge können in Legacy-Daten weiter bestehen, aber das aktive Designlabor zeigt nur die folgenden drei Designs.

### Exakt erlaubte drei Designs und originale GitHub-Assets

Alle sechs Dateien wurden am 23.11.2026 in `app/src/main/res/drawable-nodpi/` auf `main` verifiziert. PNGs **unverändert** verwenden, keine SVG-/VectorDrawable-Neuzeichnungen, kein Downsampling der Quelldateien, keine künstlichen Ersatzgrafiken.

| Design | stabile Design-ID | REAR / Innenraum (hinter dem Zettel) | FRONT / Vorderwand (vor dem Zettel) |
|---|---|---|---|
| Flechtkorb | `user-wicker-01` | `flechtkorb_hinten.png` | `flechtkorb_vorne.png` |
| Holzkübel | `user-wood-01` | `holzkuebel_hinten.png` | `holzkuebel_vorne.png` |
| Zinkeimer | `user-zinc-01` | `bucket_zinc_user_rear.png` | `bucket_zinc_user_front.png` |

### Pflichtumfang NÄCHSTER Build — ohne Nebenprojekte

- [ ] Drei reale PNG-Paare im Compose-Renderer mit je **einer gemeinsamen Canvas-Geometrie** rendern: identische Bildgröße, Position, Skalierung und ContentScale für REAR und FRONT; keine separate Größenanpassung, auch nicht bei abweichenden Alphabounds.
- [ ] Render-Reihenfolge strikt **REAR → animierter Papierzettel → FRONT**. Papier muss beim Einwerfen sichtbar in die Öffnung sinken und allmählich hinter der Vorderwand verschwinden; beim Herausschütteln umgekehrte Tiefen- und Bewegungsfolge. Bei nicht exakt deckungsgleichen PNGs sichtbare Naht/Alpha-Artefakte prüfen, nicht durch willkürliches Reframing kaschieren.
- [ ] Echte Papieranimation in Designlabor-Vorschau und im tatsächlichen Eimer-Gitter, nicht nur statische Karten. Zettelposition/-größe sinnvoll an Öffnung und Vorderwand angepasst; Einwurf mit dezenter Eimerbewegung und Haptik, Herausschütteln mit kurzer Rüttelanimation, ohne vorhandene Funktionen zu beschädigen.
- [ ] Designlabor zeigt **ausschließlich diese drei Designs** als Hauptauswahl. Alle vorherigen Erweiterungskategorien, Kenney-Grafiken, XML-Vektoren, Materialstudien, experimentelle Galerien und Stilvarianten aus aktiver UI entfernen/ausblenden. Kein neues Designlabor-Feature außerhalb der drei Eimer.
- [ ] Auswahl eines konkreten bestehenden Eimers und Anwendung des Designs; persistente Zuordnung nach App-Neustart, kompatible stabile IDs; bestehende Nutzer-Zettel, Texte, Skizzen, Bucket-IDs, Undo/Redo und Datenbanken vollständig erhalten.
- [ ] Bildqualität/Transparenz und mobile Darstellung visuell prüfen, Performance und Speicherbedarf bei 2048px-Assets beachten, ohne die gespeicherten Originaldateien zu verändern.
- [ ] Automatisierte Tests für Katalog, drei Designs, Ebenen-Reihenfolge soweit testbar, Persistenz und Migration; Android-CI erfolgreich; VersionCode erhöhen; signierte Update-APK mit **bisherigem** Keystore/Zertifikat verifizieren, kein Schlüssel in GitHub. Gerätetest und Screenshots, wenn verfügbar.
- [ ] Tatsächlich fertige APK mit Download-Link ausliefern; keinen Erfolg behaupten, bevor Build und Signatur geprüft sind.

**Akzeptanz:** Auf dem Smartphone sieht der Nutzer nur drei auswählbare, aus seinen Original-PNGs zusammengesetzte Eimer, und Papier verschwindet beim Einwurf glaubwürdig hinter deren Vorderwand. Der Scope-Freeze gilt bis zu einer neuen ausdrücklichen Nutzeranweisung. **Alles andere am Designlabor ist gestrichen.**

### Implementierungsfortschritt des 3-PNG-Builds (23.11.2026)

- [x] Alle sechs Originaldateien in `drawable-nodpi` verifiziert; keine Nutzer-Uploads mehr nötig.
- [x] `LayeredIllustratedBucket.kt`: drei reale Originalpaare als `user-wicker-01`, `user-wood-01`, `user-zinc-01` registriert. Beide Ebenen verwenden identisches `fillMaxSize` und `ContentScale.Fit`, Zettel wird zwischen REAR und FRONT gezeichnet. Commit `4c2729ae`.
- [x] `DesignLab.kt`: frühere Kategorien und 48 Experimente aus aktiver Designlabor-Oberfläche entfernt, genau drei Originaldesigns mit Preview, Zettel-Slider, Einwurf/Auswurf, Zieleimerauswahl und bestätigtem Anwenden. Legacy-Code für gespeicherte IDs bleibt vorhanden. Commit `42be66ac`.
- [x] `EimerApp.kt`: bereits bestehender LayeredIllustratedBucket-Pfad nimmt neue IDs automatisch an und zeigt Einwurf-/Ejektionsanimation im echten Eimer-Gitter; keine Datenbankschema-Änderung.
- [x] VersionCode 19, `0.3.3-dev6` (Commit `87e3dbcd`).
- [x] Test für alle drei stabilen IDs, separate Layer-Ressourcen und Paint-Reihenfolge (Commit `870be069`).
- [ ] CI-Run `45106531746` muss vollständig erfolgreich sein; Android-Geräteprüfung und verifizierte, mit bisherigem Zertifikat signierte Update-APK stehen noch aus. Keine Freigabe vor bestandenem CI und Signaturprüfung.

**Wichtig:** Die bestehende Papierbewegung wird jetzt auch für diese drei hochgeladenen Designs genutzt. Die tatsächliche visuelle Okklusionsqualität ist ohne Screenshot-/Geräteprüfung noch nicht als perfekt belegt.

### Verifizierte Auslieferung 0.3.3-dev6 / Build 19

- Android-CI `45106531746`: Debug-Compile, Unit-Tests und Release-Compile **success** (Commit `870be069`).
- Unsigned Release-Artefakt `14787652841` aus genau diesem Run heruntergeladen; enthaltene `app-release-unsigned.apk` (24.893.114 Bytes) lokal mit dem **bestehenden privaten** PKCS12-Schlüssel signiert. Schlüssel nicht im Repository gespeichert.
- Ausgabe: `Eimer-Liste-v0.3.3-dev6-3-Eimer-UPDATE.apk` (24.911.601 Bytes), APK-Signatur v2 und v3 **true**, Zertifikat SHA256 `40e6b73383a29f9ca792595b369329e2ab8f9165f49eec69b4eb57a8ffe26fe1` stimmt mit bisherigem Update-Zertifikat überein.
- APK im Chat bereitgestellt. **Keine echte Smartphone-Screenshot-/Animationsprüfung durchgeführt**; mögliche visuelle Naht- oder Tiefenprobleme müssen nach Installation geprüft werden.
