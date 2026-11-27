# Spanish Verb Trainer B2

Offline-first Android app project (Kotlin + Jetpack Compose), based on the supplied B2 grammar table.

## What's included
- Sentence input and offline recognition of conjugated forms from a built-in verb set.
- Conjugation tables for the forms in the supplied table: Indefinido, Perfecto, Imperfecto, Pluscuamperfecto, four Subjuntivo groups (including -ra/-se variants), Futuro Simple, Futuro Compuesto, Condicional Simple/Compuesto, and Gerundio.
- Russian infinitive glosses and Russian helper text.
- Short multiple-choice practice.
- No network permission or server dependency.

## Important scope notes
- This is a starter offline dictionary, not an unrestricted Spanish NLP parser or full machine translation engine. Recognition is limited to the included verb lexicon and generated forms.
- Russian sentence translation is currently a basic helper/gloss, not a complete context-aware translator.
- The initial verb set is intentionally extendable. Irregular verbs have individual overrides for common present and preterite forms; rare spelling/stem changes should be reviewed before study use.
- The source image's bottom-row “Futuro Compuesto” example says “comeré”; the correct compound form is “habré comido”. The app uses the standard compound construction.
- The table has 13 distinct outputs when Condicional Simple and Condicional Compuesto are separated and both subjunctive -ra/-se variants are shown.

## Build APK
1. Open this folder in Android Studio (Giraffe or newer).
2. Allow Gradle sync and install Android SDK 35 if prompted.
3. Choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. Debug APK output: `app/build/outputs/apk/debug/app-debug.apk`.

The repository also includes a GitHub Actions workflow (`.github/workflows/build-apk.yml`) that builds a debug APK on push when the project is uploaded to GitHub.
