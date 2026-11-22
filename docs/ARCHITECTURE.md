# Architekturentscheidung · Eimer-Liste

## Ziele
- Eigenständiges Android-Lab, später als ZIMT-Feature integrierbar.
- Offline-first: jede Eingabe wird lokal gespeichert; Synchronisierung ist nachgeordnet.
- Unveränderliche IDs, explizite Datenmigration und überprüfbare Updates.
- UI folgt Zustand; Animation ist ein Effekt **nach** erfolgreicher Speicherung.

## Module / Verantwortlichkeiten
`app` – Android-Startpunkt und Composition Root. Die eigentlichen Konzepte liegen nach Möglichkeit in getrennten Paketen `domain`, `data`, `ui`, `sync`. Bei wachsender Größe in Gradle-Module aufteilen.

```text
UI (Compose) → ViewModel/StateFlow → Use Cases/Repository → Room-Datenbank
                                                ↓
                                         Sync-Outbox (später)
                                                ↓
                                Auth + private Realtime-Datenbank
```

## Minimalmodell
- **Bucket**: UUID, ownerId (lokale Identität), name, materialKey, createdAt, updatedAt, deletedAt?, syncState.
- **Note**: UUID, bucketId, authorId, text?, drawingPayloadVersion?, drawingPayload?, createdAt, updatedAt, deletedAt?, syncState.
- **Member** (später): bucketId, userId, role, invitationState.
- **OutboxEvent** (später): eventId, entityId, action, payloadVersion, queuedAt, retryCount.
- Alle IDs: UUIDs, nicht lokale SQLite-Row-IDs als externe Referenzen.
- Timestamps dienen der Sortierung/Diagnose, **nicht allein** zur Konfliktentscheidung.
- Nach dem Start keine stillen Feldänderungen ohne Migration.

## Sync-Vertrag (vor Live-Betrieb spezifizieren)
- Jeder Schreibvorgang hat eine eindeutige operationId: idempotentes Replay.
- Server autorisiert jeden Zugriff aus Mitgliedschaften; Einladungen serverseitig einmalig/ablaufend.
- Änderungen tragen revisionsbasierte Versionen; konkurrierende Edits liefern Konflikt-/Merge-Status statt verdecktem Last-Writer-Wins.
- Löschungen werden zunächst als Tombstones synchronisiert; keine Geisterzettel bei Offline-Reconnect.
- Mehrpersonen-Tests mit Gerät A/B, Flugmodus, Zeitabweichung, Löschen, App-Absturz.

## Rendering & Performance
Native Jetpack Compose: UI-Logik und Geometrie getrennt, ressourcenschonende statische Texturen, selektive Animation statt dauerndem Neuzeichnen. Für das Falten dokumentierte, testbare Zustandsmaschine `idle → writing → saving → folding1..4 → flying → idle`. Fehler beim Speichern stoppen den Wurf und erhalten den Text.

## Spätere Integration
Kein direktes Teilen einer internen App-Datenbank zwischen zwei Paketkennungen. ZIMT importiert versionierte Datensätze mit deduplizierender ID-Zuordnung. Authentitäts-/Mitgliedschaftswechsel und Zeichnungsformat werden bewusst modelliert.
