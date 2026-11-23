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
