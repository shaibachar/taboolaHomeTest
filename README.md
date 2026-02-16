# Text-Based Calculator - Java Implementation

A comprehensive Java implementation of a text-based calculator/mini interpreter that supports variable assignments, 
arithmetic expressions, and increment/decrement operators with proper precedence and associativity.

## Table of Contents

- [Requirements](#requirements)
- [Constraints](#constraints)
- [Project Structure](#project-structure)
- [Features](#features)
- [Usage](#usage)
- [Build & Test](#build--test)
- [Test Coverage](#test-coverage)
- [CI/CD](#cicd)
- [Diagrams](#diagrams)
- [Design Decisions](#design-decisions)
- [Known Limitations](#known-limitations)

---

## Requirements

### Input/Output Specification

**Input:**
- A sequence of lines, each containing an **assignment statement**
- Each assignment statement uses a subset of Java numeric expression syntax
- Empty lines are allowed and ignored
- Whitespace (spaces, tabs) can appear anywhere and should be ignored (except inside identifiers/numbers)

**Output:**
- After evaluating all input lines in order, print the final values of variables
- Format: `(a=1,b=2,x=10)` — parentheses, comma-separated `name=value`
- Variable order: **insertion order** (first time a variable is assigned) using a `LinkedHashMap`

### Example

**Input:**
```
i = 0
j = ++i
x = i++ + 5
y = (5 + 3) * 10
i += y
```

**Output:**
```
(i=82,j=1,x=6,y=80)
```

### Supported Language Features

#### 1. Variables
- Identifier grammar: `IDENT = [A-Za-z_][A-Za-z0-9_]*`
- Variables store **numeric values** via Java `Number` (long or double depending on literal/operator usage)
- Variables must be defined before use (throw `EvalException` if undefined)

#### 2. Literals
- Numeric literals: `NUMBER = [0-9]+(\.[0-9]+)?`
- Base-10 parsing only (no hex, binary, or underscores)
- Integer literals are parsed as `long`; decimal literals are parsed as `double`
- Overflow detection for invalid long/double literals (throws `ParseException`)

#### 3. Operators

**Arithmetic Binary:**
- `+` (addition), `-` (subtraction), `*` (multiplication), `/` (division), `%` (modulo)

**Unary:**
- Unary plus: `+x`
- Unary minus: `-x`
- Pre-increment: `++x`
- Pre-decrement: `--x`

**Increment/Decrement:**
- Post-increment: `x++`
- Post-decrement: `x--`

**Assignment:**
- Simple: `=`
- Compound: `+=`, `-=`, `*=`, `/=`, `%=`

**Parentheses:**
- `( expr )` for grouping and precedence override

#### 4. Operator Precedence & Associativity

| Precedence | Operator(s)              | Associativity |
|-----------|--------------------------|---------------|
| 1 (Highest) | Postfix: `x++`, `x--`    | Left          |
| 2         | Prefix: `++x`, `--x`, `+x`, `-x` | Right |
| 3         | Multiplicative: `*`, `/`, `%`  | Left          |
| 4 (Lowest) | Additive: `+`, `-`      | Left          |

#### 5. Evaluation Order

- **Binary expressions evaluate left operand first, then right operand** (left-to-right)
- This is critical for correct semantics with side effects from `++`/`--`
- **Compound assignments:** The left-hand target's current value is saved before evaluating the RHS (Java Language Spec behavior)

---

## Constraints

### Non-Goals / Not Supported

- Do NOT use Rhino, Nashorn, JavaScript engines, or any "eval" style solutions
- No hex/binary/octal literals
- No string literals or character literals
- No comparison operators (`<`, `>`, `==`, `!=`,  `<=`, `>=`)
- No logical operators (`&&`, `||`, `!`)
- No bitwise operators (`&`, `|`, `^`, `~`, `<<`, `>>`)
- No ternary operator (`? :`)
- No function calls or method invocations
- No variable declarations (implicit declaration on first assignment)
- No comments or multi-statement lines

### Minimum Requirements

- **Language:** Java 17 or higher
- **Build Tool:** Maven 3.6+
- **Testing:** JUnit 5 (Jupiter)
- **Scope:** Single-file expressions per line; no multi-line statements

---

## Project Structure

```
Taboola-Home-Test/
├── pom.xml                          # Maven configuration
├── README.md                        # This file
├── src/
│   ├── main/java/com/shaibachar/calc/
│   │   ├── CalculatorApp.java       # Main entry point
│   │   ├── eval/
│   │   │   ├── Environment.java     # Variable storage (LinkedHashMap)
│   │   │   ├── EvalException.java   # Runtime evaluation errors
│   │   │   └── Evaluator.java       # Expression evaluation engine
│   │   ├── exceptions/
│   │   │   └── ParseException.java  # Parsing errors
│   │   ├── lexer/
│   │   │   ├── Lexer.java          # Tokenizer
│   │   │   ├── Token.java          # Token data class (POJO)
│   │   │   └── TokenType.java      # Token type enum with descriptions
│   │   └── parser/
│   │       ├── Parser.java          # Recursive descent parser
│   │       ├── AssignOp.java        # Assignment operator enum
│   │       ├── BinaryOp.java        # Binary operator enum
│   │       ├── UnaryOp.java         # Unary operator enum
│   │       ├── PostfixOp.java       # Postfix operator enum
│   │       ├── expr/                # Expression AST node types
│   │       └── stmt/                # Statement AST node types
│   │
│   └── test/java/com/shaibachar/calc/
│       ├── CalculatorExampleTest.java     # Integration example
│       ├── CompoundAssignmentTest.java    # Compound assignment semantics
│       ├── ErrorHandlingTest.java         # Error cases
│       ├── IncDecSemanticsTest.java       # Increment/decrement behavior
│       ├── PrecedenceTest.java            # Operator precedence
│       ├── eval/
│       │   └── EvaluatorTest.java         # Evaluator unit tests (88 tests)
│       ├── lexer/
│       │   └── LexerTest.java             # Lexer unit tests (140 tests)
│       └── parser/
│           └── ParserTest.java            # Parser unit tests (88 tests)
│
├── .github/workflows/
│   └── build-and-test.yml          # CI build + test workflow
└── target/                         # Build artifacts (generated)
```

### Architecture Overview

```
Input String
    ↓
[LEXER] → Tokens
    ↓
[PARSER] → AST (Expr, Stmt)
    ↓
[EVALUATOR] → Result (Number)
    ↓
[ENVIRONMENT] → Variable Storage (LinkedHashMap)
    ↓
Output: (var1=val1, var2=val2, ...)
```

**Key Design Patterns:**
- **AST-based evaluation:** Parse input into Abstract Syntax Tree, then recursively evaluate
- **Visitor-style evaluation:** Each expression type knows how to evaluate itself
- **Recursive descent parser:** Hand-written parser matching the grammar directly
- **POJO data model:** All AST nodes are immutable final POJOs (replaced records for compatibility)

---

## Features

### Lexer (`Lexer.java`)
- Tokenizes input into `Token` objects
- Handles multi-character operators (`++`, `--`, `+=`, etc.)
- Tracks token position for error reporting
- Validates long and decimal literals (detects overflow/invalid format)
- Rejects invalid characters with meaningful error messages

### Parser (`Parser.java`)
- Recursive descent parser implementing the formal grammar
- Respects operator precedence and associativity
- Handles parenthesized expressions
- Error recovery with informative messages
- Parses statements into assignment AST nodes

### Evaluator (`Evaluator.java`)
- Recursively evaluates expression trees
- Handles side effects (increment/decrement) in correct order
- Enforces left-to-right evaluation for binary expressions
- Implements compound assignment semantics (Java-like)
- Detects undefined variables and division by zero

### Environment (`Environment.java`)
- Stores variable bindings in `LinkedHashMap`
- Preserves insertion order (first assignment order)
- Throws `EvalException` for undefined variable access
- Supports both `get(name)` and `set(name, value)` operations

---

## Usage

### Compile
```bash
mvn clean compile
```

### Run Tests
```bash
mvn test
```

### Run Application
```bash
mvn exec:java -Dexec.mainClass="com.shaibachar.calc.CalculatorApp"
```

### API Example (Java)
```java
import com.shaibachar.calc.CalculatorApp;
import java.util.List;

List<String> lines = List.of(
    "i = 0",
    "j = ++i",
    "x = i++ + 5",
    "y = (5 + 3) * 10",
    "i += y"
);

String result = CalculatorApp.execute(lines);
System.out.println(result);  // Output: (i=82,j=1,x=6,y=80)
```

---

## Build & Test

### Prerequisites
- **JDK 17+** (recommended)
- **Maven 3.6+**

### Build Commands

**Full build and test:**
```bash
mvn clean install
```

**Compile only:**
```bash
mvn clean compile
```

**Run tests only:**
```bash
mvn test
```

**Run specific test class:**
```bash
mvn test -Dtest=ParserTest
mvn test -Dtest=LexerTest
mvn test -Dtest=EvaluatorTest
```

**Run with detailed output:**
```bash
mvn test -X
```

## Manual Run & Test

### Run the application manually

1. Build the project:

```bash
mvn clean compile
```

2. Start the app and wait for input:

```bash
mvn exec:java -Dexec.mainClass="com.shaibachar.calc.CalculatorApp"
```

Note: The `-Dexec.mainClass=...` flag must be prefixed by `-D`. If you see an error like `Unknown lifecycle phase ".mainClass=..."`, it means the `-D` flag was omitted.

3. Type your assignment lines, one per line. Each line must be a valid assignment statement (e.g., `x = 5`, `y += 2`, `z = (x + y) * 3`).

4. When you are done entering lines, close standard input so the app can execute:
- Windows (PowerShell): press `Ctrl+Z` then `Enter`
- macOS/Linux: press `Ctrl+D`

5. The app prints the final variable values in insertion order.

Example input:
```
i = 0
j = ++i
x = i++ + 5
y = (5 + 3) * 10
i += y
```

Expected output:
```
(i=82,j=1,x=6,y=80)
```

Tip: Empty lines are ignored. If you make a typo or unsupported expression, the app throws a parse/eval error with a code and message.

### Run tests manually

Run the full test suite:

```bash
mvn test
```

Run a specific test class:

```bash
mvn test -Dtest=LexerTest
mvn test -Dtest=ParserTest
mvn test -Dtest=EvaluatorTest
```

---

## Test Coverage

### Overview

| Component | Test Class | Test Count | Coverage |
|-----------|-----------|-----------|----------|
| **Lexer** | `LexerTest.java` | 140 tests | All token types, invalid chars, whitespace, positions |
| **Parser** | `ParserTest.java` | 88 tests | All expression types, precedence, error handling |
| **Evaluator** | `EvaluatorTest.java` | 88 tests | All operators, side effects, errors (via reflection) |
| **Integration** | Various | 5 tests | Full pipeline examples |
| **TOTAL** | — | **221 tests** | ✅ All passing |

### Lexer Tests (140 tests)

**Test Suites:**
1. **IdentifierTokens** (5 tests) — Single/multi-character identifiers, underscores, digits
2. **NumberTokens** (5 tests) — Single/multi-digit, zero, max int, overflow detection
3. **SingleCharacterOperators** (6 tests) — `+`, `-`, `*`, `/`, `%`, `=`
4. **TwoCharacterOperators** (7 tests) — `++`, `--`, `+=`, `-=`, `*=`, `/=`, `%=`
5. **ParenthesesTokens** (2 tests) — `(` and `)`
6. **ComplexExpressions** (6 tests) — Full assignments, nested operators, precedence
7. **WhitespaceHandling** (5 tests) — Leading/trailing, around operators, tabs/spaces
8. **TokenPositions** (4 tests) — Position tracking for each token
9. **ErrorHandling** (5 tests) — Unexpected characters, error messages
10. **EmptyAndNullInput** (2 tests) — Empty strings and null handling
11. **AllOperatorsInSequence** (2 tests) — All operators in order
12. **EdgeCases** (5 tests) — Consecutive operators, mixed identifiers
13. **InvalidCharacters** (23 tests) — 23 invalid special characters
14. **InvalidStringsAndSequences** (29 tests) — Invalid positions, unsupported operators
15. **InvalidCharacterErrorMessages** (3 tests) — Error message quality
16. **ValidCharactersAfterInvalid** (6 tests) — Invalid chars before/after valid tokens

**Coverage:** ✅ All token types, invalid characters, whitespace, position tracking

### Parser Tests (88 tests)

**Test Suites:**
1. **ParseStatementTests** (11 tests) — Assignments, all compound operators, errors
2. **AdditiveExpressionTests** (7 tests) — Add/sub, left-associativity, precedence
3. **MultiplicativeExpressionTests** (7 tests) — Mul/div/mod, precedence over addition
4. **UnaryExpressionTests** (8 tests) — Plus/minus, pre-inc/dec, double unary
5. **PostfixExpressionTests** (6 tests) — Post-inc/dec, in binary operations
6. **PrimaryExpressionTests** (11 tests) — Literals, variables, parentheses, nesting
7. **PrecedenceAndAssociativityTests** (8 tests) — Complex precedence scenarios
8. **ComplexExpressionTests** (5 tests) — Full expressions, all operators
9. **ErrorHandlingTests** (8 tests) — Missing components, unexpected tokens
10. **BinaryOperatorDirectionTests** (3 tests) — Left-associativity verification
11. **VariableExpressionTests** (4 tests) — Variables in various contexts
12. **AllOperatorsTests** (3 tests) — All operators coverage

**Coverage:** ✅ All expression types, operators, precedence, error conditions

### Evaluator Tests (88 tests, via Reflection)

**Coverage:**
- Literal evaluation
- Variable lookup and undefined errors
- Unary operators with side effects
- Postfix operators with side effects
- All binary operators and division by zero
- Left-to-right evaluation order
- Side effect timing

**Coverage:** ✅ All operators, side effects, error conditions

### Integration Tests (5 tests)

1. **CalculatorExampleTest** — Full pipeline
2. **CompoundAssignmentTest** (3 tests) — Compound semantics
3. **ErrorHandlingTest** (3 tests) — End-to-end error detection
4. **IncDecSemanticsTest** (5 tests) — Inc/dec behavior
5. **PrecedenceTest** (4 tests) — Operator precedence

---

## CI/CD

This project uses **GitHub Actions** for continuous integration.

### Workflow: Build and Test

**File:** `.github/workflows/build-and-test.yml`

**Triggers:**
- Push to `main` or `develop` branches
- Pull requests to `main` or `develop` branches

**Matrix Strategy:**
- Builds and tests on Java 17 and Java 21
- Runs on `ubuntu-latest`

**Steps:**
1. Checkout code
2. Set up JDK (17 or 21)
3. Build with Maven (`mvn clean compile`)
4. Run tests (`mvn test`)
5. Upload test reports as artifacts (if tests run)

**View Results:**
- Go to the **Actions** tab on GitHub
- Click the workflow run to see logs
- Download test reports under **Artifacts**

### Local CI Check

Before pushing, run locally:

```bash
mvn clean compile
mvn test
```

If both pass, your PR is ready.

---

## Diagrams

Simple visual flow diagrams for the pipeline (`x = i++ + 5`) are available in:

- `docs/diagrams/lexer-flow.svg`
- `docs/diagrams/parser-flow.svg`
- `docs/diagrams/evaluator-flow.svg`


---

## Design Decisions

- **Numeric model:** The evaluator uses `Number` and supports both `long` and `double` values.
- **Evaluation order:** Binary expressions evaluate left-to-right to preserve side-effect semantics for `++`/`--`.
- **Variable lifecycle:** Variables are implicitly created on first assignment and stored in insertion order.
- **Error diagnostics:** Runtime errors include line number and source-line context for easier troubleshooting.

### Recursive Descent Parser
https://www.youtube.com/watch?v=SToUyjAsaFk

This project uses a recursive descent parser, 
a top-down parsing technique where each grammar rule is implemented as a dedicated method. 
The parser consumes tokens from left to right,
with method structure reflecting operator precedence
(for example: `expression` -> `additive` -> `multiplicative` -> `unary` -> `postfix` -> `primary`). 
Each method parses the next-lower-precedence expressions and then combines them while matching the relevant operators,
which naturally enforces associativity and precedence. In this implementation, the `Parser` 
class follows that structure, creating AST nodes (`BinaryExpr`, `UnaryExpr`, `PostfixExpr`, `LiteralExpr`, `VarExpr`) 
as it recognizes tokens, and raising `ParseException` with a coded message when an expected token is missing or invalid.

## Known Limitations

- No booleans, comparisons, logical, or bitwise operators.
- No function calls or user-defined functions.
- Single-statement-per-line parser (no comments or multi-statement lines).
