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

## Freemium

Free: Core Concepts + Phase I. Full version: remaining phases, 7 Patterns, exam simulator.

### Sideload APK (not Play Store)

Customers enter a license code (`PREP-XXXXXX-XXXX`):

```bash
python3 tools/generate_license_keys.py 10
./gradlew :app:assembleSideloadRelease
```

### Google Play

Play builds **must** sell unlock through Google Play Billing (product id `full_unlock`).

1. Play Console → your app (`com.cpmai.studylab`) → Monetize → In-app products → create **Managed product** `full_unlock` (one-time, e.g. ₹499).
2. Add your Gmail under Settings → License testing.
3. Upload an internal-testing **AAB**:

```bash
./gradlew :app:bundlePlayRelease
```

Output: `app/build/outputs/bundle/playRelease/app-play-release.aab`

The Play app restores purchases on launch. Do not offer UPI/license codes in the Play listing.

A signed release APK is in [`dist/CPMAI-Study.apk`](dist/CPMAI-Study.apk). On your phone: enable install from this source if prompted, then open the file.

Minimum Android: **8.0 (API 26)**.

## Build

```bash
./gradlew :app:assembleSideloadRelease
```

Sideload APK: `app/build/outputs/apk/sideload/release/`
