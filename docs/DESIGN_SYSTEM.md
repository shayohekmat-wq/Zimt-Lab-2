# Designsystem · charmingX4 × Eimer-Liste

## Leitgefühl
Warm, handwerklich, haptisch, liebevoll unperfekt. Charmanter Skeuomorphismus – **nicht** überladenes Spielzeug. Viel Detail an den Dingen, viel Ruhe um sie herum. Papier, Eimer und Regalbretter bilden das Interface; die Bedienung bleibt selbsterklärend.

## Materialpalette
- Hintergrund: tiefe, warme Holz-/Anthrazittöne; kein schwarzes Loch
- Primärfläche: helles, ruhiges Ahorn-/Pergamentpapier mit Fasern, leicht unregelmäßigem Rand
- Akzent: gealtertes Messing, warmes Licht, kein ständiges Glitzern
- Eimer: Zink/Metall, geflochtenes Weidenholz, Papierkorb, Holz, Emaille, Messing, Vintage
- Text: tiefe Tinte statt reines Schwarz, klare humanistische Schrift für Lesbarkeit
- Ausgewählter Eimer: sanfter Lichtsaum und subtile Anhebung, **nicht** aggressive Skalierung

## Bildaufbau
- Oben: ruhiger, kleiner Titel „Eimer-Liste“; darunter Eimer als Objekte in einem Regal / auf einer Bühne.
- Eimer haben **lesbare physische Etiketten** mit ihren Namen.
- Unten: immer sichtbarer **echter Papierfetzen als Textfeld**, der beim Tippen minimal aufgerichtet wird; rechts ein dezenter Papierflieger-/Falt-Button.
- Der Papierfetzen darf großzügig wachsen, wenn die Tastatur erscheint.
- Eimer offen: Blick in den Eimer mit einzeln greifbaren Zetteln; große, lesbare Einzelansicht statt unlesbarem Mikroschriftfeld.

## Motion-Story
1. Papier beschriften: feinste Höhenänderung / Fokus.
2. Absenden: Schreibzustand friert ein; persistieren.
3. **Vier unterscheidbare Faltungen** an derselben Notiz, nicht vier austauschbare Sprites.
4. Wurf in den **tatsächlich ausgewählten** Eimer, zartes Metall-/Papiergeräusch; kein Verlust der UI-Kontrolle.
5. Neuer Papierfetzen erscheint erst nach erfolgreichem Abschluss.
6. Bei deaktivierten Systemanimationen: direkte Zustandsänderung mit verständlichem Feedback.

## Barrierefreiheit und Ergonomie
- Inhalt und Aktionen für TalkBack beschriften; die Faltanimation ist dekorativ.
- Touchflächen mindestens 48 dp, reguläre Android-Texteingabe mit IME.
- Schrift 1,3× bis 2×, abgeschnittene Namen vermeiden.
- Haptik optional; kein essenzieller Ablauf hängt von Sensoren oder Audio ab.
- Kleine Displays und große Schriften sind Abnahmekriterien, keine spätere Nacharbeit.

## Später
Pinselbutton vergrößert denselben Zettel in einen Zeichenmodus (auch nur Zeichnung). Schüttelgeste hat visuell zugängliche Alternative „Zufallszettel ziehen“.
