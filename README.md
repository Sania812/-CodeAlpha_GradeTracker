# Student Grade Tracker

A desktop Java application (Swing GUI) for recording student marks, calculating grades, and viewing class statistics. Built as Internship Project No. 1.

![Java](https://img.shields.io/badge/Java-17%2B-orange) ![Swing](https://img.shields.io/badge/UI-Swing%20%2B%20FlatLaf-blue)

## Features

- Add, edit, and remove students (name, roll number, marks)
- Automatic grade calculation (A+ down to F) based on marks
- Live statistics: total students, average marks, highest and lowest scorer
- Search by name or roll number
- Input validation (marks must be 0–100, no duplicate roll numbers, no empty names)
- Modern flat UI theme via [FlatLaf](https://www.formdev.com/flatlaf/)
- "Load Sample Data" button for quick demoing

## Grading Scale

| Marks   | Grade |
|---------|-------|
| 90–100  | A+    |
| 80–89   | A     |
| 70–79   | B     |
| 60–69   | C     |
| 50–59   | D     |
| Below 50| F     |

## Project Structure

```
project no. 1/
├── Student.java          # Student model (name, roll number, marks, grade logic)
├── GradeTracker.java      # Manages the ArrayList<Student> — add/update/remove/search/stats
├── GradeTrackerGUI.java   # Swing GUI — the window, form, table, and all interaction logic
├── Main.java              # Entry point — sets up FlatLaf and launches the GUI
├── flatlaf.jar             # Third-party UI library (bundled for convenience)
└── README.md
```

## How to Run

### Option 1 — Compile and run from source

Requires JDK 17+.

```
javac -cp flatlaf.jar -d out *.java
java -cp "out;flatlaf.jar" Main
```

(On macOS/Linux, use `:` instead of `;` in the classpath: `java -cp "out:flatlaf.jar" Main`)

### Option 2 — Build a standalone Windows executable

Requires a JDK with `jpackage` (JDK 17+).

```powershell
javac -cp flatlaf.jar -d out *.java

# Merge FlatLaf's classes into the jar so the exe is self-contained
Copy-Item flatlaf.jar flatlaf.zip
Expand-Archive -Path flatlaf.zip -DestinationPath merged -Force
Remove-Item flatlaf.zip
Remove-Item merged\module-info.class -Force -ErrorAction SilentlyContinue
Copy-Item out\*.class merged\ -Force

New-Item -ItemType Directory -Force jarinput | Out-Null
jar --create --file jarinput\GradeTracker.jar --main-class Main -C merged .

jpackage --type app-image --input jarinput --main-jar GradeTracker.jar --main-class Main --name GradeTracker --dest dist
```

Then run `dist\GradeTracker\GradeTracker.exe`.

### IntelliJ IDEA / Eclipse / VS Code

1. Open this folder as a project.
2. Add `flatlaf.jar` to the project's classpath/libraries.
3. Run `Main.java`.

## OOP Concepts Used

- **Encapsulation** — `Student` fields are private, accessed via getters.
- **Separation of concerns** — `Student` (data), `GradeTracker` (business logic), `GradeTrackerGUI` (presentation), `Main` (entry point).
- **Collections** — `ArrayList<Student>` in `GradeTracker` for dynamic storage.
- **Constructors** — used throughout to initialize object state cleanly.

## Technologies Used

- Java (JDK 17+)
- Swing (GUI toolkit)
- FlatLaf (modern look-and-feel library)

## Learning Outcomes

- Applying encapsulation and class separation in a multi-file Java project
- Building a desktop GUI with Swing: layouts, event handling, custom table rendering
- Using ArrayList for dynamic in-memory data management
- Defensive input validation to prevent crashes on bad user input
- Packaging a Java application into a native Windows executable with `jpackage`

## Possible Future Improvements

- Persist student data to a file or database between sessions
- Export the report to CSV/PDF
- Sort the table by name or marks
- Add charts for grade distribution
