# CPMAI Prep App (Unofficial)

Independent, **unofficial** study aid related to the PMI-CPMAI™ certification.

**Not affiliated with, endorsed by, or sponsored by Project Management Institute, Inc.** PMI® and PMI-CPMAI™ are marks of PMI.

Practice questions are original study items, not PMI exam questions.

## Features

- Full module notes (readable in-app)
- Top 10 exam takeaways, tips, and knowledge checks per topic
- 120 flashcards with flip, shuffle, bookmarks, and mastery tracking
- 40 original scenario MCQs plus extra trap questions
- Exam simulator (20 mixed questions)
- Pattern lab + “which phase?” drills
- Glossary search and study streak / weak-spot tracking

## APK

A signed release APK is in [`dist/CPMAI-Study.apk`](dist/CPMAI-Study.apk). On your phone: enable install from this source if prompted, then open the file.

Minimum Android: **8.0 (API 26)**.

## Build

```bash
export ANDROID_HOME=/path/to/android-sdk
./gradlew :app:assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`
