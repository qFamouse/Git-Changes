# Git Changes Viewer

IntelliJ IDEA plugin that copies uncommitted Git changes to clipboard in a readable format.

## Features

- View uncommitted changes (added, modified, deleted files)
- Works with selected files in Commit tool window
- Automatically copies formatted output to clipboard
- Shows relative paths from Git repository root

## Installation

1. Download the latest release `.zip` file
2. Open IntelliJ IDEA
3. Go to **Settings → Plugins → ⚙️ → Install Plugin from Disk**
4. Select the downloaded `.zip` file
5. Restart IDE

## Usage

1. Select files in the Commit tool window (left panel)
2. Right-click on selected files
3. Choose **"Copy Changes to Clipboard"**
4. Changes are now in your clipboard

## Output Format

**Modified files:**
```
Было (path/to/file):
<old content>

Стало:
<new content>

---
```

**Added files:**
```
Добавлено (path/to/file):
<file content>

---
```

**Deleted files:**
```
Удалено (path/to/file):
<file content>

---
```

## Build from Source
```bash
./gradlew buildPlugin
```

Output: `build/distributions/Git-Changes-1.0-SNAPSHOT.zip`

## Requirements

- IntelliJ IDEA 2025.1.4+
- Git version control

## License

MIT
