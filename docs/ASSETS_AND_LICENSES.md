# Bildressourcen, Marken- und Lizenzhygiene

Stand: 22.11.2026.

## Build 0.2.0

Die sechs Eimer-Varianten und Papiere sind **programmatisch in Jetpack Compose Canvas** gezeichnet. Das Repository enthält für diese Objekte keine heruntergeladenen Fotos, fremden Bucket-Icons, Stockmaterialien, Font-Dateien, sonstige gekaufte Assetpacks oder undokumentierte Bilder.

| Material | Quelle | Technik | Externe Lizenz |
|---|---|---|---|
| Zink-Eimer | `ui/PaperAndBucket.kt` | Eigene Vektorformen/Verläufe/Patina | Nicht erforderlich |
| Geflecht-Eimer | `ui/PaperAndBucket.kt` | Eigene Über-/Unter-Webstruktur | Nicht erforderlich |
| Papierkorb | `ui/PaperAndBucket.kt` | Eigene Formen/Falzlinien | Nicht erforderlich |
| Holzeimer | `ui/PaperAndBucket.kt` | Eigene Linien/Materialverläufe | Nicht erforderlich |
| Emaille-Eimer | `ui/PaperAndBucket.kt` | Eigene Formen/Materialverläufe | Nicht erforderlich |
| Messing-Eimer | `ui/PaperAndBucket.kt` | Eigene Formen/Materialverläufe | Nicht erforderlich |
| Papierzettel | `ui/PaperAndBucket.kt` | Eigener unregelmäßiger Pfad, Schatten | Nicht erforderlich |

**Für neue Assets:** Bevor ein fremdes Foto/Icon/3D-Modell eingecheckt wird, schriftlich überprüfbar dokumentieren: Urheber/Provider, Original-URL, Lizenzversion, Recht zur kommerziellen App-Verteilung/Modifikation, notwendige Attribution, Datum der Prüfung, ggf. Quelle der Rohdatei. „Im Internet gefunden“, „frei downloadbar“, Pinterest und bloßes Verlinken sind keine Nutzungsberechtigung. Ungeklärte Ressourcen bleiben draußen. Eine bloße Optik-Ähnlichkeit wird nicht als Lizenznachweis interpretiert.

## Prioritäten für 0.3

Originalmaterialien weiter verbessern, **ohne** die App durch große Bilddateien, ruckelige Effekte oder fremde Asset-Lizenzen zu belasten. Für eine schärfere Objekthaptik lieber gezielte Vektor-Schatten, Lichtkanten und Material-Details als hochauflösende Echtzeittexturen.
