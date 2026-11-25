#!/usr/bin/env bash
set -euo pipefail
evidence=app/build/upgrade-evidence
mkdir -p "$evidence"
old=$(find "$RUNNER_TEMP/eimer-baseline/app/build/outputs/apk/debug" -name '*.apk' -print -quit)
new=$(find app/build/outputs/apk/debug -name '*.apk' -print -quit)
tests=$(find app/build/outputs/apk/androidTest/debug -name '*.apk' -print -quit)
test -s "$old"; test -s "$new"; test -s "$tests"
adb install "$old" | tee "$evidence/install-baseline.txt"
adb install "$tests"
runner=de.zimt.eimerliste.debug.test/androidx.test.runner.AndroidJUnitRunner
failures=0
run_test() {
  local class="$1" name="$2"
  if ! adb shell am instrument -w -r -e class "$class" "$runner" | tee "$evidence/$name.txt" ||
     ! grep -Eq '^OK \(1 test[s]?\)' "$evidence/$name.txt"; then
    failures=$((failures+1))
    # Preserve evidence and continue independent tests, without marking this one passed.
    adb exec-out screencap -p > "$evidence/$name.png" || true
    adb shell uiautomator dump /sdcard/eimer-test-window.xml >/dev/null 2>&1 &&
      adb pull /sdcard/eimer-test-window.xml "$evidence/$name-window.xml" >/dev/null || true
  fi
}
run_test de.zimt.eimerliste.data.UpgradeFixtureTest#seedOldInstallation seed
run_test de.zimt.eimerliste.data.UpgradeFixtureTest#verifyAfterUpgrade verify-baseline-on-disk
adb install -r "$new" | tee "$evidence/install-upgrade.txt"
run_test de.zimt.eimerliste.data.UpgradeFixtureTest#verifyAfterUpgrade verify-upgrade
run_test de.zimt.eimerliste.data.EimerPersistenceTest persistence
run_test de.zimt.eimerliste.ui.BucketBitmapDeviceTest bitmap-pairs
run_test de.zimt.eimerliste.data.DraftPersistenceTest#seedDraft seed-draft
adb shell am force-stop de.zimt.eimerliste.debug
run_test de.zimt.eimerliste.data.DraftPersistenceTest#verifyDraftAfterRestart verify-draft
# Use a clean test installation for UI gestures. The update fixture above has
# already been verified, and this is the ephemeral debug emulator only.
adb shell pm clear de.zimt.eimerliste.debug
run_test de.zimt.eimerliste.ui.NoteGestureDeviceTest gestures
adb shell pm clear de.zimt.eimerliste.debug
run_test de.zimt.eimerliste.ui.ComposerDeviceTest#writeDrawReturnFoldAndTapBucketPreserveBothInputs composer
adb shell pm clear de.zimt.eimerliste.debug
run_test de.zimt.eimerliste.ui.ComposerDeviceTest#unreadableFoldedDraftKeepsDrawingUntilExplicitRemoval composer-recovery

# A failure in any independent test still fails the CI job.
test "$failures" -eq 0
