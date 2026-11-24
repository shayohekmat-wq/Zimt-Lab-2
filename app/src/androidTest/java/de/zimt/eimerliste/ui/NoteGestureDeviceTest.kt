package de.zimt.eimerliste.ui

import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import de.zimt.eimerliste.MainActivity
import de.zimt.eimerliste.data.BucketEntity
import de.zimt.eimerliste.data.EimerDatabase
import de.zimt.eimerliste.data.NoteEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Real Android input events and real uptime, not a mocked Compose gesture clock. */
@RunWith(AndroidJUnit4::class)
class NoteGestureDeviceTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)
    private val context = instrumentation.targetContext
    private val dao = EimerDatabase.get(context).dao()
    private val source = BucketEntity("gesture-source", "Gesture A", "wood", 100, 100)
    private val target = BucketEntity("gesture-target", "Gesture B", "zinc", 101, 101)
    private val note = NoteEntity("gesture-note", source.id, "GESTURE TEST", createdAt = 102, updatedAt = 102)

    private fun find(desc: String) = checkNotNull(device.wait(Until.findObject(By.desc(desc)), 10000)) {
        "Accessibility node not found: $desc"
    }

    private fun openBucket(name: String) {
        val obj = checkNotNull(device.wait(Until.findObject(By.descStartsWith("$name,")), 10000))
        obj.click()
        device.waitForIdle()
    }

    private fun currentNote() = runBlocking { dao.noteById(note.id)!! }

    private fun inject(action: Int, x: Float, y: Float, down: Long,
                       requireAccepted: Boolean = true): Boolean {
        val event = MotionEvent.obtain(down, SystemClock.uptimeMillis(), action, x, y, 0)
        event.source = InputDevice.SOURCE_TOUCHSCREEN
        return try {
            val accepted = instrumentation.uiAutomation.injectInputEvent(event, true)
            if (requireAccepted) check(accepted) { "Input injection rejected: action=$action" }
            accepted
        } finally { event.recycle() }
    }

    @Test fun shortTapDragCancelAndBurnUseRealPointerEvents() {
        check(context.packageName.endsWith(".debug"))
        runBlocking {
            dao.addBucket(source)
            dao.addBucket(target)
            dao.addNote(note)
        }
        ActivityScenario.launch(MainActivity::class.java).use {
            val destinationBounds = checkNotNull(device.wait(Until.findObject(
                By.descStartsWith(target.name + ",")), 10000)).visibleBounds
            openBucket(source.name)
            val noteDesc = "Zettel: " + note.text
            find(noteDesc).click()
            assertTrue(device.wait(Until.hasObject(By.text("Dein Wunsch")), 5000))
            device.pressBack()
            val initial = find(noteDesc).visibleBounds
            // Drag before lift must neither burn nor open a note.
            val down = SystemClock.uptimeMillis()
            inject(MotionEvent.ACTION_DOWN, initial.centerX().toFloat(), initial.centerY().toFloat(), down)
            inject(MotionEvent.ACTION_MOVE, initial.centerX().toFloat(), initial.centerY() + 65f, down)
            SystemClock.sleep(2200)
            inject(MotionEvent.ACTION_UP, initial.centerX().toFloat(), initial.centerY() + 65f, down)
            assertNull(currentNote().deletedAt)
            assertEquals(source.id, currentNote().bucketId)
            // Close a potentially scrolled sheet and reopen a clean gesture target.
            device.pressBack()
            openBucket(source.name)
            val from = find(noteDesc).visibleBounds
            assertFalse(device.hasObject(By.desc("Zieleimer: " + target.name)))
            val to = destinationBounds
            val dragDown = SystemClock.uptimeMillis()
            inject(MotionEvent.ACTION_DOWN, from.centerX().toFloat(), from.centerY().toFloat(), dragDown)
            SystemClock.sleep(850)
            // Move out of the contents. The same finger is now owned by the overview.
            inject(MotionEvent.ACTION_MOVE, from.centerX().toFloat(), from.centerY() - 35f, dragDown)
            SystemClock.sleep(300)
            for (step in 1..15) {
                val t = step / 15f
                inject(MotionEvent.ACTION_MOVE,
                    from.centerX() + (to.centerX() - from.centerX()) * t,
                    from.centerY() + (to.centerY() - from.centerY()) * t, dragDown)
                SystemClock.sleep(20)
            }
            inject(MotionEvent.ACTION_UP, to.centerX().toFloat(), to.centerY().toFloat(), dragDown)
            val until = SystemClock.uptimeMillis() + 5000
            while (currentNote().bucketId != target.id && SystemClock.uptimeMillis() < until) SystemClock.sleep(100)
            assertEquals(target.id, currentNote().bucketId)
            assertEquals(note.text, currentNote().text)
            SystemClock.sleep(800)
            openBucket(target.name)
            // A stationary lift/release carries the open note to the overview.
            val same = find(noteDesc).visibleBounds
            val sameDown = SystemClock.uptimeMillis()
            inject(MotionEvent.ACTION_DOWN, same.centerX().toFloat(), same.centerY().toFloat(), sameDown)
            SystemClock.sleep(850)
            inject(MotionEvent.ACTION_UP, same.centerX().toFloat(), same.centerY().toFloat(), sameDown)
            checkNotNull(device.wait(Until.findObject(By.descStartsWith(target.name + ",")), 10000)).click()
            SystemClock.sleep(1100)
            assertEquals(target.id, currentNote().bucketId)
            assertEquals(note.id, currentNote().id)
            openBucket(target.name)
            val burn = find(noteDesc).visibleBounds
            val burnDown = SystemClock.uptimeMillis()
            inject(MotionEvent.ACTION_DOWN, burn.centerX().toFloat(), burn.centerY().toFloat(), burnDown)
            SystemClock.sleep(3000)
            assertNull("Warning must last at least 2700ms", currentNote().deletedAt)
            SystemClock.sleep(1600)
            // With animations disabled the successful burn may already dismiss the
            // modal window. Android can reject a trailing synthetic UP in that case.
            val releaseAccepted = inject(MotionEvent.ACTION_UP, burn.centerX().toFloat(),
                burn.centerY().toFloat(), burnDown, requireAccepted = false)
            val burnUntil = SystemClock.uptimeMillis() + 5000
            while (currentNote().deletedAt == null && SystemClock.uptimeMillis() < burnUntil) SystemClock.sleep(100)
            assertNotNull("Burn must commit even if trailing UP was rejected: $releaseAccepted",
                currentNote().deletedAt)
            assertEquals(note.id, currentNote().id)
            // The persistent recovery path must restore the same row, not recreate a note.
            SystemClock.sleep(500)
            openBucket(target.name)
            val recovery = checkNotNull(device.wait(Until.findObject(
                By.textContains("Verbrannte Zettel (1)")), 10000))
            recovery.click()
            checkNotNull(device.wait(Until.findObject(By.text("↶ " + note.text)), 10000)).click()
            val restoreUntil = SystemClock.uptimeMillis() + 5000
            while (currentNote().deletedAt != null && SystemClock.uptimeMillis() < restoreUntil)
                SystemClock.sleep(100)
            assertNull(currentNote().deletedAt)
            assertEquals(note.id, currentNote().id)
            assertEquals(target.id, currentNote().bucketId)
            assertEquals(note.text, currentNote().text)
        }
    }
}
