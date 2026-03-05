# Git Changes Viewer

IntelliJ IDEA plugin that copies uncommitted Git changes to clipboard in a readable format.

## Features

- View uncommitted changes (added, modified, deleted files)
- Works with selected files in Commit tool window
- Three copy modes: full content, only changed parts, paths only
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
3. Choose **"Copy Changes"** → select one of the modes:
    - **Full Content** — full before/after file content
    - **Only Changed Parts** — only modified lines with line numbers
    - **Paths Only** — only file paths with change type
4. Changes are now in your clipboard

## Output Format

### Full Content

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

### Only Changed Parts

**Modified files:**
```
Изменено (path/to/file):

- Было (строки 3-5):
  <old lines>
+ Стало (строки 3-6):
  <new lines>

---
```

**Added/Deleted files:** same as Full Content mode.

### Paths Only

```
Изменено: path/to/file
Добавлено: path/to/file
Удалено: path/to/file
```

## Build from Source
```bash
./gradlew buildPlugin
```

Output: `build/distributions/Git-Changes-<version>.zip`

## Requirements

- IntelliJ IDEA 2023.3+
- Git version control

## License

MIT