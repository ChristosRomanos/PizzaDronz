# Git Large File Issue - Solution

## Problem
```
remote: error: File ilp_submission_image.tar is 237.77 MB
remote: error: This exceeds GitHub's file size limit of 100.00 MB
```

## Solution Steps

### Step 1: Remove Large File from Git History

Run these commands in PowerShell from your project directory:

```powershell
cd C:\Users\cmrom\PizzaDronz

# Option A: If you just committed the file (easiest)
git reset --soft HEAD~1
git reset HEAD ilp_submission_image.tar
git commit -m "Removed large tar file"

# Option B: Remove from all history using BFG (recommended)
# Download BFG: https://rtyley.github.io/bfg-repo-cleaner/
java -jar bfg.jar --delete-files ilp_submission_image.tar
git reflog expire --expire=now --all
git gc --prune=now --aggressive

# Option C: Using git filter-branch (slower but works)
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch ilp_submission_image.tar" \
  --prune-empty --tag-name-filter cat -- --all

git reflog expire --expire=now --all
git gc --prune=now --aggressive
```

### Step 2: Add to .gitignore

Create or update `.gitignore`:

```bash
# Large files
ilp_submission_image.tar
*.tar

# Build artifacts
target/
*.class

# IDE
.idea/
*.iml

# OS files
.DS_Store
desktop.ini
Thumbs.db
```

### Step 3: Force Push (CAUTION)

```powershell
# This will overwrite remote history!
git push origin --force --all
git push origin --force --tags
```

### Step 4: Verify

```powershell
# Check repo size
git count-objects -vH

# Verify file is gone
git log --all --full-history -- ilp_submission_image.tar
```

## Alternative: Use Git LFS for Large Files

If you need to keep the large file:

```powershell
# Install Git LFS
git lfs install

# Track large files
git lfs track "*.tar"

# Add the file
git add .gitattributes
git add ilp_submission_image.tar
git commit -m "Add large file with LFS"
git push
```

## Quick Fix (Recommended)

If you just want to push without the large file:

```powershell
cd C:\Users\cmrom\PizzaDronz

# Remove the file
Remove-Item ilp_submission_image.tar -ErrorAction SilentlyContinue
Remove-Item PizzaDronz/ilp_submission_image.tar -ErrorAction SilentlyContinue

# Add to gitignore
Add-Content .gitignore "`nilp_submission_image.tar`n*.tar"

# Commit the removal
git rm --cached ilp_submission_image.tar -ErrorAction SilentlyContinue
git rm --cached PizzaDronz/ilp_submission_image.tar -ErrorAction SilentlyContinue
git add .gitignore
git commit -m "Remove large tar file and update gitignore"

# If still failing, reset history
git reset --hard HEAD~1
git commit --amend -m "Portfolio complete without large files"

# Force push
git push -f origin main
```

## What to Include in Git

### ✅ Include:
- Source code (.java files)
- Test files
- Configuration (pom.xml, application.properties)
- Documentation (.md files, portofolio.tex)
- Small assets

### ❌ Don't Include:
- Large binary files (>100MB)
- Docker images (.tar files)
- Build artifacts (target/ directory)
- IDE files (.idea/, *.iml)
- OS files (desktop.ini, .DS_Store)

## Files You Should Push

```
PizzaDronz/
├── src/
│   ├── main/java/...
│   └── test/java/...
├── pom.xml
├── portofolio.tex
├── README.md
├── FINAL_STATUS.md
├── .github/workflows/ci-cd.yml
└── .gitignore
```

## If All Else Fails: Fresh Start

```powershell
# Backup your code
Copy-Item -Recurse src/ ../src_backup/
Copy-Item portofolio.tex ../
Copy-Item pom.xml ../

# Remove git history
Remove-Item -Recurse -Force .git/

# Reinitialize
git init
git add src/ pom.xml portofolio.tex README.md .github/
git add -f .gitignore
git commit -m "Initial commit - testing portfolio"
git branch -M main
git remote add origin https://github.com/ChristosRomanos/PizzaDronz.git
git push -u origin main --force
```

## After Fixing

Once you've resolved the large file issue, push your portfolio:

```powershell
git add portofolio.tex src/ pom.xml
git commit -m "Complete testing portfolio with 867 tests"
git push origin main
```
