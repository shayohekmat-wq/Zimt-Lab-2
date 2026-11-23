# Designlabor – Herkunft, Rechte und Integrationsfähigkeit

**Aktueller verbindlicher Stand: 23.11.2026.** Diese Richtlinie ersetzt die frühere Einordnung von Fotografien handgefertigter Körbe als „Von Menschen gemacht“.

## Vielfalt ist Pflicht, Ausschließlichkeit nicht
Das Designlabor soll **viele** von Menschen gestaltete digitale Grafikoptionen enthalten, **zusätzlich** zu hochwertigen KI-generierten Designs und unseren selbst konstruierten Originalen. Alle drei Entstehungswege sind ausdrücklich erlaubt und erwünscht. Getrennte Kennzeichnung dient nur der ehrlichen Herkunftsinformation, **nicht** dem Ausschluss anderer Herstellungsverfahren.

## Kategorien – Herkunft transparent kennzeichnen

### Von Menschen designt (digitale Grafikassets)
Professionelle **digitale Grafikdateien**, von menschlichen Grafikdesignern, Illustratoren, Pixel-Art- oder 3D-Künstlern geschaffen: transparente PNGs, SVGs, Sprites, handmodellierte 3D-Objekte/Renderings, UI-Assets. **Keine KI-generierten oder KI-assistierten Grafiken in dieser Kategorie.** Die bloße Behauptung „No AI“ ist ein Prüfhinweis, kein Beweis. Bevorzugt Quelldateien/Entstehungsnachweise und direkte Urheberangaben.

### Bildgeneriert
Eigenständig generierte digitale Grafiken, als solche deklariert, tatsächlich als einzelne nutzbare Assets exportiert und in die App eingebunden. Kein Kontaktbogen, Textplatzhalter oder prozeduraler Dummy zählt als fertiges Bildasset.

### Eigenes App-Design
Manuell konstruierte Originalobjekte und selbst geschriebene Vektor-/Compose-Geometrie. Bei relevanter KI-Beteiligung transparent kennzeichnen. Das Referenzobjekt muss auch technisch als Eimer funktionieren.

### Recherche-/Archivmaterial (nicht Teil der Menschen-Grafikgalerie)
Fotos realer Körbe und historische Korbillustrationen sind **allenfalls Form- und Materialreferenzen**, nicht die vom Nutzer geforderte Galerie menschlich designter digitaler Grafikassets. Die zuvor recherchierten CC0-Korbfotografien bleiben ausschließlich historische Recherchehinweise, nicht Produktkandidaten für die Hauptkategorie.

## Kandidaten und Integrationsfreigabe
Pro konkreter **Datei** dokumentieren:

| Feld | Pflicht |
| --- | --- |
| Asset-ID, Künstler/Studio und Originaldatei | Ja; direkter prüfbarer Link, nicht nur Plattform-Suchseite |
| Herkunft/Nicht-KI-Nachweis | Ja; Quelle und Unsicherheit dokumentieren |
| Lizenz, kommerzielle App-Nutzung, Bearbeitung und Weiterverteilung | Ja; konkreter Lizenztext und Datum |
| Format, Auflösung, Alpha und Größe | Ja; Handy-Tauglichkeit nachweisen |
| Öffnungsmaske, hintere/vordere Ebene, Label-Anker | Ja; oder dokumentierter Bearbeitungsplan |
| Tatsächlicher Status | Gefunden → Rechte geprüft → technisch aufbereitet → integriert → auf Gerät geprüft → freigegeben |
| Attribution | Falls erforderlich, in App und Dokumentation |

**Nie „integriert“ behaupten, wenn lediglich ein Link oder ein Vorschaubild existiert.** Externe Assets nur nach Rechteprüfung und als lokal gebündelte, versionierte Dateien. Keine fremden Fonts ohne eigene Lizenzprüfung.

## Aktuelle Recherchequellen, keine verifizierten Einzelassets
- [Kenney Game Assets](https://kenney.itch.io/kenney-game-assets) – digital gestaltete Asset-Pakete; konkrete Behälterdateien und Paketlizenz einzeln prüfen.
- [Kenney Support/Lizenz](https://kenney.nl/support) – allgemeine Lizenzinformationen, keine Freigabe einer ungeprüften Einzeldatei.
- [Gelidus Canimons Vegetable Asset](https://gelidus-canimons.itch.io/vegetable-asset) – handgemaltes Grafikpaket laut Anbieter; konkrete Datei, Lizenz und Eimer-Funktion prüfen.
- [itch.io No-AI Game Assets](https://itch.io/game-assets/tag-no-ai) – Suchquelle, keine automatisch verifizierte Urheberschaft.

## Technisches Gate
Keine Grafik als auswählbares finales Eimerdesign anbieten, bevor der Zettel hinter der Vorderwand verschwinden kann, die Öffnung plausibel ist, der Name lesbar bleibt, das Objekt auf kleinen Android-Displays funktioniert und die Nutzungsrechte geklärt sind. Alte Design-IDs und Bestandsdaten bleiben erhalten.

## Tatsächlich integrierte digitale Künstlerassets — 23.11.2026

**Urheber/Studio:** Kenney (von Menschen gestaltete 3D-Game-Art-Modelle). **Originale Lizenz:** CC0 1.0, kommerziell verwendbar, Namensnennung nicht verpflichtend. Offizielle Quellen: https://kenney.nl/assets/food-kit , https://kenney.nl/support und https://kenney.nl/assets/furniture-kit , https://kenney.nl/assets/pirate-kit . **Redistribution/konkrete Dateien:** https://github.com/Hidencod/tge-assets (GitHub-Projekt mit Kenney-GLB-Modellen und aus diesen gerenderten 192x192-WebP-Thumbnails). Import-Quellcode: `tools/import_human_assets.py`; Originaldateien wurden automatisch unverändert in der Bildkomposition als PNG neu kodiert und nach `app/src/main/res/drawable-nodpi/` committed.

| Stable App ID | Originales Artist-Asset | Direkte Quelldatei | App-Datei |
| --- | --- | --- | --- |
| human-kenney-open-box | Kenney Furniture Kit, open cardboard box | https://github.com/Hidencod/tge-assets/blob/main/packs/furniture-kit/thumbs/cardboardboxopen.webp | kenney_human_open_box.png |
| human-kenney-barrel | Kenney Food Kit, barrel | https://github.com/Hidencod/tge-assets/blob/main/packs/food-kit/thumbs/barrel.webp | kenney_human_barrel.png |
| human-kenney-pot | Kenney Food Kit, pot | https://github.com/Hidencod/tge-assets/blob/main/packs/food-kit/thumbs/pot.webp | kenney_human_pot.png |
| human-kenney-chest | Kenney Pirate Kit, chest | https://github.com/Hidencod/tge-assets/blob/main/packs/pirate-kit/thumbs/chest.webp | kenney_human_pirate_chest.png |
| human-kenney-crate | Kenney Pirate Kit, crate | https://github.com/Hidencod/tge-assets/blob/main/packs/pirate-kit/thumbs/crate.webp | kenney_human_crate.png |

**Wichtige ehrliche Einschränkung:** Diese fünf Bilder sind **echte gerenderte 3D-Modellvorschauen von Kenney-Assets**, keine eigens für Eimer-Liste gestalteten hochauflösenden Premiumgrafiken. Sie sind lediglich 192×192 Pixel groß und **keine pixelgenau getrennten REAR/FRONT-Ebenen**. Daher derzeit nur als statische Künstler-Grafikoptionen auswählbar; die Galerie zeigt für sie bewusst keine irreführende Papiermasken-Vorschau. Die Truhen/Kisten sind nicht zwingend offene Behälter. Für die endgültige 100%-Abnahme müssen aus den originalen GLB-Dateien echte hochauflösende gemeinsame Render-Passes erzeugt werden. Die früheren selbst gezeichneten Vektor-Experimente sind im Archiv, nicht als menschliche Künstlergrafiken gekennzeichnet.
