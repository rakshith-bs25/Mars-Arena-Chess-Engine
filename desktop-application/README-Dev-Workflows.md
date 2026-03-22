# Team Mars – Git & Release Standards

This document describes how we collaborate on this repo. It’s a lightweight GitFlow based process with Conventional Commits, code reviews, CI checks, and tagged releases.

## TL;DR

* Work from **feature/** branches off **develop**
* Use **Conventional Commits** for messages (e.g., `feat: add Mars theme`)
* Open a **PR → develop**; require 1–2 reviews and green CI
* Squash-merge PRs
* Release flow: `develop` → `release/x.y.z` → PR to **main** → tag **vX.Y.Z**
* Hotfixes branch from **main**: `hotfix/x.y.z`

---

## 1) Branching Model

### Long-lived branches

* **main**: Production code only (tagged releases).
* **develop**: Integration branch for the next release.

### Short-lived branches

* **feature/<topic>** → from `develop` (e.g., `feature/mars-theme`)
* **fix/<topic>** → from `develop` (bugfix for next release)
* **release/x.y.z** → from `develop` when stabilizing a version
* **hotfix/x.y.z** → from `main` for urgent production fixes

> Use kebab-case for `<topic>`; keep it short and meaningful.

---

## 2) Daily Developer Flow

```bash
# 1) Sync
git checkout develop
git pull --ff-only

# 2) Branch
git checkout -b feature/<topic>

# 3) Work
./gradlew run
./gradlew test

# 4) Commit (Conventional Commits)
git add -A
git commit -m "feat: short present-tense summary"

# 5) Push and open PR to develop
git push -u origin feature/<topic>
```

Keep your branch up to date:

```bash
git fetch origin
git rebase origin/develop   # recommended for a linear history
# resolve conflicts, then:
git push --force-with-lease
```

---

## 3) Commit Message Rules (Conventional Commits)

**Format**

```
<type>(optional-scope): <summary>
```

**Types**

* `feat:` new functionality
* `fix:` bug fix
* `refactor:` code change that doesn’t add a feature or fix a bug
* `docs:`, `test:`, `chore:`, `style:`, `perf:`, `ci:` …

**Examples**

* `feat: add Mars theme assets`
* `fix: prevent NPE when loading config on first run`
* `refactor: extract board drawing into helper`

> Include context in the PR description (screenshots, before/after, testing notes).

---

## 4) Pull Requests

* **Target**: `develop` (except releases/hotfixes)
* **Title**: mirrors main commit (Conventional Commit style)
* **Description**: what/why, how tested, breaking changes, screenshots if UI
* **Size**: Prefer small, focused PRs
* **Reviews**: At least **1 approval** (2 for risky changes)
* **CI**: All checks **must pass**
* **Merge method**: **Squash and merge** (keeps history clean)

**PR Checklist**

* [ ] Branch up to date with `develop`
* [ ] Builds locally (`./gradlew clean build` or `shadowJar`)
* [ ] Tests pass (`./gradlew test`) and/or manual test notes included
* [ ] Follows code style and null-safety best practices
* [ ] No commented-out code or debug prints
* [ ] No binaries or secrets committed

---

## 5) Release Process (SemVer)

1. **Cut a release branch**

   ```bash
   git checkout develop
   git pull
   git checkout -b release/x.y.z
   # bump version in build.gradle if needed
   git commit -m "chore(release): prepare x.y.z"
   git push -u origin release/x.y.z
   ```
2. **Stabilize** on the release branch (only fixes/docs).
3. **PR → main**, get approvals, **merge**.
4. **Tag** and push:

   ```bash
   git checkout main
   git pull
   git tag -a vX.Y.Z -m "Java Open Chess CCD X.Y.Z"
   git push origin vX.Y.Z
   ```
5. **Back-merge** main → develop:

   ```bash
   git checkout develop
   git pull
   git merge --no-ff main -m "chore: back-merge main after vX.Y.Z"
   git push
   ```

> CI builds the fat jar (`*-all.jar`). Optionally upload it as a release asset.

---

## 6) Hotfix Process

For urgent production issues after a release:

```bash
git checkout main
git pull
git checkout -b hotfix/x.y.(z+1)
# fix + commit
git push -u origin hotfix/x.y.(z+1)
```

* PR to **main**, tag **vX.Y.(Z+1)** after merge
* Back-merge **main → develop** to keep branches aligned

---

## 7) Code Style & Clean Code

* Prefer **small functions**, clear names, and **early returns**
* Handle **nulls** defensively (no NPEs)
* No dead/commented code; avoid large classes
* UI code: no blocking work on EDT; use SwingWorker / background threads
* Keep resource loading via classpath; avoid absolute paths

---

## 8) Repository Hygiene

* Don’t commit local IDE settings or build outputs
* Keep `.gitignore` current (build/, .gradle/, .vscode/, etc.)
* Use **`.gitattributes`** to normalize line endings (already present)
* Never commit secrets or API keys

---

## 9) CI/CD (GitHub Actions)

* Every push/PR runs CI with **Java 17**
* CI must be green before merging
* Release tags can trigger publish steps (opt-in)

---

## 10) Handling Merge Conflicts

* Prefer **rebase** onto `develop` to resolve conflicts locally
* Use IDE merge tools; test after resolving
* Never use `--force`; use `--force-with-lease`
* If stuck, push a `conflict/` helper branch and ask for help

---

## 11) Access & Protections (Repo Settings)

Recommended branch protections:

* **main**: require PR, require approvals, require green checks, disallow force-push
* **develop**: require PR + green checks

---

## 12) Useful Commands

```bash
# create feature branch
git checkout -b feature/mars-theme

# update from origin
git fetch --all --prune
git rebase origin/develop

# push safely after rebase
git push --force-with-lease

# run app
./gradlew run

# build fat jar
./gradlew shadowJar
```