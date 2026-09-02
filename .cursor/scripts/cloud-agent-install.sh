#!/usr/bin/env bash
set -euo pipefail

export ANDROID_HOME="${ANDROID_HOME:-/opt/android-sdk}"
export ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
export PATH="${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${PATH}"

yes | sdkmanager --licenses >/dev/null || true
sdkmanager \
    "platform-tools" \
    "platforms;android-36" \
    "build-tools;35.0.0" \
    "build-tools;34.0.0"

cd /workspace
chmod +x gradlew
./gradlew --no-daemon :app:assembleDebug
