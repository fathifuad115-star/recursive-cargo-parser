
package org.example

/**
 * File: ContainerParser.kt
 *
 * ============================================================================
 * JVM CALL STACK & RECURSION MEMORY EXECUTION TRACE
 * ============================================================================
 *
 * This parser uses Pure Structural Recursion. Instead of using a global mutable
 * list, the execution state is implicitly managed by the JVM Call Stack.
 * Every recursive call creates a new Stack Frame.
 *
 * 1. Stack Frame Creation:
 *    When `parseRecursive` calls itself, the JVM allocates a new stack frame
 *    in memory. This frame holds the current function arguments and local variables.
 *
 * 2. Deep Nesting / Stack Push Phase:
 *    As the parser enters nested brackets like '[ [ ] ]', new frames are pushed
 *    onto the call stack, moving the execution deeper into memory.
 *
 * 3. Stack Frame Destruction & Merging Phase:
 *    When a closing bracket ']' is reached, the current stack frame finishes,
 *    returns its accumulated sub-list, and is popped (destroyed) off the stack.
 *    The parent frame then merges this sub-list into its own results.
 */

/**
 * Custom domain exception thrown when brackets are mathematically unbalanced.
 */
class StructuralMismatchException(message: String) : Exception(message)

// A global counter used ONLY tracking the index position across the single recursive pass.
private var currentIndex = 0

/**
 * Main entry point to parse nested cargo packing strings.
 */
fun parse(input: String): List<String> {
    // 1. Validate balance first to catch mismatched brackets early
    validateBracketBalance(input)

    // 2. Reset the index counter before starting
    currentIndex = 0

    // 3. Start the recursive processing
    return parseRecursive(input)
}

/**
 * Simplified structural recursive function.
 */
private fun parseRecursive(input: String): List<String> {
    val accumulatedIds = mutableListOf<String>()
    var currentToken = ""

    while (currentIndex < input.length) {
        val char = input[currentIndex]
        currentIndex++ // Move to the next character for the next iteration

        when (char) {
            '[' -> {
                // If we hit an opening bracket, first save any token we were building
                if (currentToken.startsWith("PKG-") && currentToken.length > 4) {
                    accumulatedIds.add(currentToken)
                }
                currentToken = "" // Reset token

                // PUSH NEW FRAME: Call recursively to handle the inner structure
                val childResults = parseRecursive(input)
                accumulatedIds.addAll(childResults)
            }
            ']' -> {
                // If we hit a closing bracket, save the final token of this level
                if (currentToken.startsWith("PKG-") && currentToken.length > 4) {
                    accumulatedIds.add(currentToken)
                }
                // POP FRAME: Return current level results back to the caller
                return accumulatedIds
            }
            ',' -> {
                // Commas split tokens. Save what we have and reset.
                if (currentToken.startsWith("PKG-") && currentToken.length > 4) {
                    accumulatedIds.add(currentToken)
                }
                currentToken = ""
            }
            else -> {
                // Ignore whitespace, otherwise build the raw text token character by character
                if (!char.isWhitespace()) {
                    currentToken += char
                }
            }
        }
    }

    // Capture any trailing token at the very end of the string
    if (currentToken.startsWith("PKG-") && currentToken.length > 4) {
        accumulatedIds.add(currentToken)
    }

    return accumulatedIds
}

/**
 * Simple linear scan to ensure all brackets open and close properly.
 */
private fun validateBracketBalance(input: String) {
    var balance = 0
    for (i in input.indices) {
        if (input[i] == '[') balance++
        if (input[i] == ']') balance--

        if (balance < 0) {
            throw StructuralMismatchException("Structural Mismatch: Found extra ']' at position $i")
        }
    }
    if (balance > 0) {
        throw StructuralMismatchException("Structural Mismatch: Missing $balance closing bracket(s) ']'")
    }
}

fun main() {
    try {
        val testInput = " Crate [ Box [ PKG-101 ] , PKG-102, Crate[Box[], PKG-202] ] "
        println("Testing input: $testInput")

        val result = parse(testInput)
        println("==================================================")
        println("Parsed Package IDs: $result")
        println("==================================================")



    } catch (e: StructuralMismatchException) {
        println("==================================================")
        println("Error caught: ${e.message}")
        println("==================================================")


    }
}

