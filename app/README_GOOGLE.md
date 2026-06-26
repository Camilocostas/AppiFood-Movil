# Google / Firebase configuration (Maps, Analytics, Auth)

This file explains how developers should handle the Firebase / Google Services configuration locally and in CI.

Important points
- DO NOT commit app/google-services.json to the repository. It may contain API keys and project identifiers.
- Each developer must download their own google-services.json from Firebase Console and place it in app/ locally.
- The repository contains app/google-services.json (sanitized) and app/google-services.json.example as templates.

Local setup (recommended)
1. Download google-services.json from Firebase Console for project "appifood-cc357".
2. Place it in the app/ directory of your local clone (app/google-services.json). Do NOT commit this file.
3. Ensure your local .gitignore contains /app/google-services.json (was added in the repo). If not, add it.

CI setup (recommended)
- Store secrets in GitHub Actions (Settings → Secrets and variables → Actions).
  - Option A: Store the entire JSON as FIREBASE_JSON. In your workflow, write it to app/google-services.json before the build:

    - name: Restore Firebase config
      run: |
        echo "${{ secrets.FIREBASE_JSON }}" > app/google-services.json

  - Option B: Store only MAPS_API_KEY and other individual keys, and use manifestPlaceholders to inject into the manifest.

Rotating keys
- If a key is compromised, rotate it in Google Cloud Console / Firebase and update the secret in GitHub.
- Restrict keys by Android package name and SHA-1 fingerprint to reduce risk.

If you're uncertain about how to obtain the SHA-1 fingerprint for release builds, run:

  ./gradlew signingReport

