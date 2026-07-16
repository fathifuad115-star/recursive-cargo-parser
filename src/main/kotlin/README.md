# 📦 Recursive Cargo Container Parser (Kotlin)

A pure, structural recursive parser designed to process nested cargo container strings and extract unique, clean Package IDs (`PKG-X`) without using standard cheat-codes like `split()`, `Regex`, or global mutable states.

---

## 🚀 How It Works (Simplified Process)

Instead of relying on easy built-in methods, this parser performs **Manual Parsing** by reading the string character by character using a single index pointer (`currentIndex`).

### 1. The Core Logic:
* **Characters & Tokens:** We build tokens character by character. If a word doesn't start with `PKG-` (like `Crate` or `Box`), it is filtered out instantly.
* **Brackets `[` and `]`:**
    - Every time the parser encounters `[`, it saves what it currently has, and calls `parseRecursive()` again (**Pushing a new frame onto the JVM Call Stack**).
    - When it encounters `]`, it completes the current level and returns the list of IDs (**Popping the frame off the stack**).
* **Delimiter Handling:** Double commas `,,` or irregular spaces are naturally ignored.

### 2. Error and Balance Control:
Before running the recursion, a linear scan validates the bracket symmetry. If there is a mathematical mismatch (e.g., `[PKG-101` or `PKG-102]`), it halts and throws a custom `StructuralMismatchException` rather than crashing the JVM.

---

## 🧠 JVM Call Stack & Memory Execution Trace

This parser implements **Pure Structural Recursion**. The state is managed dynamically inside the JVM Call Stack rather than manual global lists.

### Stack Flow Diagram: