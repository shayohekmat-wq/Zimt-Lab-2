package de.zimt.eimerliste.ui

import android.os.SystemClock
import android.view.KeyEvent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import de.zimt.eimerliste.MainActivity
import de.zimt.eimerliste.data.BucketEntity
import de.zimt.eimerliste.data.EimerDatabase
import de.zimt.eimerliste.data.DraftPreferences
import de.zimt.eimerliste.domain.PaperDrawingCodec
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposerDeviceTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)
    private val context = instrumentation.targetContext
    private val dao = EimerDatabase.get(context).dao()
    private val text = "Erster Absatz\nZweiter Absatz"

    private fun find(selector: BySelector) =
        checkNotNull(device.wait(Until.findObject(selector), 10000)) { "Missing UI element: $selector" }

    private fun assertDraftText(stage: String) {
        val deadline = SystemClock.uptimeMillis() + 5000
        while (DraftPreferences.load(context).text != text && SystemClock.uptimeMillis() < deadline)
            SystemClock.sleep(100)
        assertEquals(stage, text, DraftPreferences.load(context).text)
    }

    @Test fun writeDrawReturnFoldAndTapBucketPreserveBothInputs() {
        check(context.packageName.endsWith(".debug"))
        val bucket = BucketEntity("composer-bucket", "Composer Eimer", "wood", 100, 100)
        runBlocking { dao.addBucket(bucket) }
        ActivityScenario.launch(MainActivity::class.java).use {
            // Inject actual key input, not an accessibility SET_TEXT request that
            // older Android/Compose combinations may silently decline.
            find(By.desc("Notiztext")).click()
            device.executeShellCommand("input text Erster%sAbsatz")
            assertTrue(device.pressKeyCode(KeyEvent.KEYCODE_ENTER))
            device.executeShellCommand("input text Zweiter%sAbsatz")
            assertDraftText("Text field including Enter/newline")
            find(By.desc("Skizze auf dem Papier zeichnen oder bearbeiten")).click()
            assertTrue(device.wait(Until.hasObject(By.text("Dein Zeichenpapier")), 10000))
            assertDraftText("Opening drawing mode must not change text")
            SystemClock.sleep(650)
            val bounds = find(By.desc("Zeichenfläche")).visibleBounds
            assertTrue(bounds.width() > 50 && bounds.height() > 50)
            assertTrue(device.swipe(bounds.centerX() - 25, bounds.centerY(),
                bounds.centerX() + 25, bounds.centerY() + 25, 18))
            find(By.desc("Zur Textbearbeitung wechseln und Skizze behalten")).click()
            find(By.desc("Notiztext"))
            assertDraftText("Returning from drawing mode")
            assertTrue("Completed stroke must already belong to the draft",
                PaperDrawingCodec.hasInk(DraftPreferences.load(context).drawing))
            find(By.desc("Zettel dreimal falten")).click()
            assertTrue(device.wait(Until.hasObject(By.text("ZIEH DEINEN ZETTEL IN EINEN EIMER")), 10000))
            find(By.descStartsWith("Composer Eimer,")).click()
            val deadline = SystemClock.uptimeMillis() + 5000
            var saved = runBlocking { dao.snapshotNotes().firstOrNull() }
            while (saved == null && SystemClock.uptimeMillis() < deadline) {
                SystemClock.sleep(100)
                saved = runBlocking { dao.snapshotNotes().firstOrNull() }
            }
            val note = checkNotNull(saved) { "Composer did not save the note" }
            assertEquals(bucket.id, note.bucketId)
            assertEquals(text, note.text)
            assertTrue(PaperDrawingCodec.hasInk(note.drawingPayload))
        }
    }
}
