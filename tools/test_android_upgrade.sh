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
run_test() {
  local class="$1" name="$2"
  adb shell am instrument -w -r -e class "$class" "$runner" | tee "$evidence/$name.txt"
  grep -Eq '^OK \(1 test[s]?\)' "$evidence/$name.txt"
}
run_test de.zimt.eimerliste.data.UpgradeFixtureTest#seedOldInstallation seed
adb install -r "$new" | tee "$evidence/install-upgrade.txt"
run_test de.zimt.eimerliste.data.UpgradeFixtureTest#verifyAfterUpgrade verify-upgrade
run_test de.zimt.eimerliste.data.EimerPersistenceTest persistence
run_test de.zimt.eimerliste.data.DraftPersistenceTest draft
# Use a clean test installation for UI gestures. The update fixture above has
# already been verified, and this is the ephemeral debug emulator only.
adb shell pm clear de.zimt.eimerliste.debug
run_test de.zimt.eimerliste.ui.NoteGestureDeviceTest gestures
