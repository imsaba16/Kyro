# Kyro

Kyro is an **experimental, educational scripting language** inspired by **Kotlin** and **Python**, designed as a **structured alternative to shell scripting**.  
Kyro compiles into **POSIX-compliant shell (`/bin/sh`)**, allowing Kyro scripts to run anywhere a standard Unix shell is available.

This project was created for **learning, experimentation, and hobby purposes**, focusing on compiler design, language tooling, and safe scripting ergonomics.

---

## ✨ Why Kyro?

Shell scripts are powerful but often:
- Hard to read
- Error-prone
- Difficult to maintain
- Lacking structure and diagnostics

Kyro aims to:
- Provide **clean, structured syntax**
- Keep **shell semantics**
- Generate **portable shell scripts**
- Offer **better error messages**
- Avoid runtime dependencies

Kyro is **not** a shell replacement.  
It is a **compiler that generates shell scripts**.

---

## 🧠 Key Concepts

- **Source Language**: Kyro (`.ky`)
- **Target Language**: POSIX shell (`sh`)
- **Implementation Language**: Kotlin
- **Execution Model**:

---

> Kyro (.ky) → Compiler → Shell Script (.sh) → /bin/sh


### Kyro programs do **not** require:
- JVM
- Java
- Kotlin
- Gradle

Only the **Kyro compiler** needs these during development or compilation.

---

## 🧩 Language Features

### Variables
```ky
val message = "Hello"
print(message)
```

### Command Execution
```commandline
run "echo" "Hello from Kyro"
```

### Built-in print
```kotlin
print("Hello World")
```
### Compiles to:
```commandline
echo "Hello World"
```
### Conditionals
```kyro
if exists("build") {
    run "rm", "-rf", "build"
}
```
### Loops
```kyro
for file in glob("*.txt") {
    print(file)
}
```
### Pipelines
```kyro
pipe {
    run "cat", "file.txt"
    run "grep", "error"
    run "wc", "-l"
}
```

## CLI Usage
### Compile only
```commandline
kyro compile script.ky
```
### Compile and run
```commandline
kyro run script.ky
```
## Error Diagnostics
**Kyro provides line and column aware error messages.**
```text
Kyro error at 1:9 → Unterminated string literal
```
---

### Project Structure

```text
src/
 ├─ lexer/     # Tokenization & source positions
 ├─ parser/    # AST construction
 ├─ ast/       # Language model
 ├─ emit/      # Shell code generation
 ├─ cli/       # Command-line interface
 └─ util/      # Error handling
```
> The project is intentionally structured like a real compiler.
---

## Project Goals
**This project is meant to:**
Learn language design

- Learn lexer & parser implementation
- Explore DSL design
- Understand code generation
- Build a real compiler pipeline
- Improve developer ergonomics for scripting

**It is not intended to:**
- Replace Bash
- Compete with existing shells
- Be production-ready (yet)

---

## Distribution
**Currently:**
Kyro is built and run using Gradle (development only)

**Planned:**
- Fat JAR distribution
- Native binary via Kotlin/Native

**Kyro scripts themselves are always pure shell.**

---

## Acknowledgements
**Kyro is inspired by:**
- Shell scripting
- Kotlin DSL design
- Python readability
- Compiler construction literature

---

## Status
**Educational / Experimental / Hobby Project**

Active development, learning-focused, and evolving.

---

## Contributions

Contributions, ideas, and discussions are welcome.

Since this is a learning project, clarity and simplicity are preferred over complexity.

---

## Disclaimer

Kyro is an experimental language.

Use it for learning, experimentation, and personal tooling.