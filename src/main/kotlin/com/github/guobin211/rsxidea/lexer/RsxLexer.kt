package com.github.guobin211.rsxidea.lexer

import com.github.guobin211.rsxidea.psi.RsxTokenTypes
import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

/**
 * RSX Lexer with full syntax highlighting for Rust, TypeScript, HTML, and CSS
 */
class RsxLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var currentToken: IElementType? = null
    private var state: Int = STATE_INITIAL

    // Context tracking for better highlighting
    private var lastToken: IElementType? = null
    private var lastTokenText: String = ""
    private var lastNonWhitespaceToken: IElementType? = null

    companion object {
        const val STATE_INITIAL = 0
        const val STATE_RUST = 1
        const val STATE_SCRIPT = 2
        const val STATE_TEMPLATE = 3
        const val STATE_STYLE = 4
        const val STATE_TEMPLATE_TAG = 5
        const val STATE_TEMPLATE_TAG_ATTR = 6
        const val STATE_INTERPOLATION = 7
        const val STATE_DIRECTIVE = 8
        const val STATE_TEMPLATE_CLOSE_TAG = 9

        // Rust keywords
        private val RUST_KEYWORDS = setOf(
            "as", "async", "await", "break", "const", "continue", "crate", "dyn",
            "else", "enum", "extern", "false", "fn", "for", "if", "impl", "in",
            "let", "loop", "match", "mod", "move", "mut", "pub", "ref", "return",
            "static", "struct", "super", "trait", "true", "type",
            "unsafe", "use", "where", "while"
        )
        private val RUST_SELF = setOf("self", "Self")
        private val RUST_PRIMITIVE_TYPES = setOf(
            "bool", "char", "str", "u8", "u16", "u32", "u64", "u128", "usize",
            "i8", "i16", "i32", "i64", "i128", "isize", "f32", "f64"
        )
        private val RUST_STD_TYPES = setOf(
            "String", "Vec", "Option", "Result", "Box", "Rc", "Arc",
            "HashMap", "HashSet", "BTreeMap", "BTreeSet",
            "Cell", "RefCell", "Mutex", "RwLock",
            "Cow", "Pin", "PhantomData"
        )

        // TypeScript keywords
        private val TS_KEYWORDS = setOf(
            "abstract", "any", "as", "async", "await", "boolean", "break", "case",
            "catch", "class", "const", "constructor", "continue", "debugger", "declare",
            "default", "delete", "do", "else", "enum", "export", "extends", "false",
            "finally", "for", "from", "function", "get", "if", "implements", "import",
            "in", "infer", "instanceof", "interface", "is", "keyof", "let", "module",
            "namespace", "never", "new", "null", "number", "object", "of", "package",
            "private", "protected", "public", "readonly", "require", "return", "set",
            "static", "string", "super", "switch", "symbol", "this", "throw", "true",
            "try", "type", "typeof", "undefined", "unique", "unknown", "var", "void",
            "while", "with", "yield"
        )
        private val TS_TYPES = setOf(
            "Array", "Boolean", "Date", "Error", "Function", "JSON", "Map", "Math",
            "Number", "Object", "Promise", "RegExp", "Set", "String", "Symbol", "WeakMap", "WeakSet"
        )

        // CSS at-rules
        private val CSS_AT_RULES = setOf(
            "@import", "@media", "@keyframes", "@font-face", "@charset", "@supports",
            "@namespace", "@page", "@viewport", "@counter-style", "@font-feature-values"
        )
    }

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        this.state = initialState
        this.lastToken = null
        this.lastTokenText = ""
        this.lastNonWhitespaceToken = null
        advance()
    }

    override fun getState(): Int = state
    override fun getTokenType(): IElementType? = currentToken
    override fun getTokenStart(): Int = tokenStart
    override fun getTokenEnd(): Int = tokenEnd
    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = bufferEnd

    override fun advance() {
        // Save last token info before advancing
        if (currentToken != null && currentToken != RsxTokenTypes.WHITE_SPACE) {
            lastToken = currentToken
            lastTokenText = buffer.subSequence(tokenStart, tokenEnd).toString()
            lastNonWhitespaceToken = currentToken
        }

        tokenStart = tokenEnd
        if (tokenStart >= bufferEnd) {
            currentToken = null
            return
        }

        when (state) {
            STATE_INITIAL -> lexInitial()
            STATE_RUST -> lexRust()
            STATE_SCRIPT -> lexScript()
            STATE_TEMPLATE -> lexTemplate()
            STATE_STYLE -> lexStyle()
            STATE_TEMPLATE_TAG -> lexTemplateTag()
            STATE_TEMPLATE_TAG_ATTR -> lexTemplateTagAttr()
            STATE_TEMPLATE_CLOSE_TAG -> lexTemplateCloseTag()
            STATE_INTERPOLATION -> lexInterpolation()
            STATE_DIRECTIVE -> lexDirective()
            else -> lexInitial()
        }
    }

    // ==================== Initial State ====================
    private fun lexInitial() {
        when {
            lookingAt("---") -> {
                tokenEnd = tokenStart + 3
                currentToken = RsxTokenTypes.RUST_DELIMITER
                state = STATE_RUST
            }
            lookingAt("<script") -> {
                val endTag = findEndOf(">")
                tokenEnd = if (endTag >= tokenStart) endTag + 1 else tokenStart + 7
                currentToken = RsxTokenTypes.SCRIPT_OPEN
                state = STATE_SCRIPT
            }
            lookingAt("<template") -> {
                val endTag = findEndOf(">")
                tokenEnd = if (endTag >= tokenStart) endTag + 1 else tokenStart + 9
                currentToken = RsxTokenTypes.TEMPLATE_OPEN
                state = STATE_TEMPLATE
            }
            lookingAt("<style") -> {
                val endTag = findEndOf(">")
                tokenEnd = if (endTag >= tokenStart) endTag + 1 else tokenStart + 6
                currentToken = RsxTokenTypes.STYLE_OPEN
                state = STATE_STYLE
            }
            currentChar().isWhitespace() -> lexWhitespace()
            else -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.BAD_CHARACTER
            }
        }
    }

    // ==================== Rust Lexer ====================
    private fun lexRust() {
        when {
            lookingAt("---") -> {
                tokenEnd = tokenStart + 3
                currentToken = RsxTokenTypes.RUST_DELIMITER
                state = STATE_INITIAL
            }
            currentChar().isWhitespace() -> lexWhitespace()
            lookingAt("//") -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != '\n') tokenEnd++
                currentToken = RsxTokenTypes.RUST_COMMENT
            }
            lookingAt("/*") -> {
                tokenEnd = tokenStart + 2
                while (tokenEnd < bufferEnd - 1 && !(buffer[tokenEnd] == '*' && buffer[tokenEnd + 1] == '/')) {
                    tokenEnd++
                }
                if (tokenEnd < bufferEnd - 1) tokenEnd += 2
                currentToken = RsxTokenTypes.RUST_COMMENT
            }
            lookingAt("#[") || lookingAt("#![") -> {
                tokenEnd = tokenStart
                var depth = 0
                while (tokenEnd < bufferEnd) {
                    if (buffer[tokenEnd] == '[') depth++
                    if (buffer[tokenEnd] == ']') depth--
                    tokenEnd++
                    if (depth == 0) break
                }
                currentToken = RsxTokenTypes.RUST_ATTRIBUTE
            }
            lookingAt("::") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.RUST_NAMESPACE
            }
            lookingAt("->") || lookingAt("=>") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.RUST_OPERATOR
            }
            currentChar() == '\'' && tokenStart + 1 < bufferEnd && buffer[tokenStart + 1].isLetter() -> {
                // Lifetime 'a
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && isIdentifierPart(buffer[tokenEnd])) tokenEnd++
                currentToken = RsxTokenTypes.RUST_LIFETIME
            }
            currentChar() == '"' -> lexRustString()
            currentChar() == '\'' -> lexRustChar()
            currentChar().isDigit() -> lexRustNumber()
            isIdentifierStart(currentChar()) -> lexRustIdentifier()
            isRustOperator(currentChar()) -> {
                tokenEnd = tokenStart + 1
                // Handle multi-char operators
                if (tokenEnd < bufferEnd) {
                    val two = buffer.subSequence(tokenStart, tokenEnd + 1).toString()
                    if (two in listOf("&&", "||", "==", "!=", "<=", ">=", "<<", ">>", "+=", "-=", "*=", "/=")) {
                        tokenEnd++
                    }
                }
                currentToken = RsxTokenTypes.RUST_OPERATOR
            }
            currentChar() in "{}[]();,.:" -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.RUST_PUNCTUATION
            }
            else -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.BAD_CHARACTER
            }
        }
    }

    private fun lexRustIdentifier() {
        tokenEnd = tokenStart
        while (tokenEnd < bufferEnd && isIdentifierPart(buffer[tokenEnd])) tokenEnd++
        val text = buffer.subSequence(tokenStart, tokenEnd).toString()

        // Check for macro (ends with !)
        if (tokenEnd < bufferEnd && buffer[tokenEnd] == '!') {
            tokenEnd++
            currentToken = RsxTokenTypes.RUST_MACRO
            return
        }

        // Look ahead for context
        val nextChar = peekNextNonWhitespace()

        currentToken = when {
            // Keywords
            text in RUST_KEYWORDS -> RsxTokenTypes.RUST_KEYWORD
            text in RUST_SELF -> RsxTokenTypes.RUST_SELF
            // Primitive types
            text in RUST_PRIMITIVE_TYPES -> RsxTokenTypes.RUST_TYPE
            // Standard library types
            text in RUST_STD_TYPES -> RsxTokenTypes.RUST_TYPE
            // SCREAMING_SNAKE_CASE = constant
            text.all { it.isUpperCase() || it == '_' } && text.any { it.isLetter() } -> RsxTokenTypes.RUST_CONSTANT
            // PascalCase after use/struct/enum/impl/trait/:/->/</, or { (for use statements like use foo::{Bar, Baz})
            text[0].isUpperCase() && (lastTokenText in listOf("use", "struct", "enum", "impl", "trait", "type", ":", "->", "<", ",", "{")) -> RsxTokenTypes.RUST_STRUCT
            // PascalCase followed by :: or { or < = type/struct
            text[0].isUpperCase() && nextChar in listOf(':', '{', '<') -> RsxTokenTypes.RUST_STRUCT
            // PascalCase in general = likely a type
            text[0].isUpperCase() -> RsxTokenTypes.RUST_TYPE
            // After fn = function definition
            lastTokenText == "fn" -> RsxTokenTypes.RUST_FUNCTION
            // Followed by ( = function call
            nextChar == '(' -> RsxTokenTypes.RUST_FUNCTION_CALL
            // After :: = module path or associated item
            lastToken == RsxTokenTypes.RUST_NAMESPACE -> {
                if (nextChar == '(' || (tokenEnd < bufferEnd && buffer[tokenEnd] == '!')) {
                    RsxTokenTypes.RUST_FUNCTION_CALL
                } else if (text[0].isUpperCase()) {
                    RsxTokenTypes.RUST_STRUCT
                } else {
                    RsxTokenTypes.RUST_MODULE
                }
            }
            // Before :: = module
            nextChar == ':' && tokenEnd + 1 < bufferEnd && buffer[tokenEnd + 1] == ':' -> RsxTokenTypes.RUST_MODULE
            // After . = field or method
            lastToken == RsxTokenTypes.RUST_PUNCTUATION && lastTokenText == "." -> {
                if (nextChar == '(') RsxTokenTypes.RUST_FUNCTION_CALL else RsxTokenTypes.RUST_FIELD
            }
            else -> RsxTokenTypes.RUST_IDENTIFIER
        }
    }

    private fun peekNextNonWhitespace(): Char? {
        var i = tokenEnd
        while (i < bufferEnd && buffer[i].isWhitespace()) i++
        return if (i < bufferEnd) buffer[i] else null
    }

    private fun lexRustString() {
        tokenEnd = tokenStart + 1
        if (lookingAt("r#") || lookingAt("r\"")) {
            // Raw string
            if (buffer[tokenStart + 1] == '#') {
                var hashes = 0
                while (tokenEnd < bufferEnd && buffer[tokenEnd] == '#') {
                    hashes++
                    tokenEnd++
                }
                if (tokenEnd < bufferEnd && buffer[tokenEnd] == '"') {
                    tokenEnd++
                    val closePattern = "\"" + "#".repeat(hashes)
                    while (tokenEnd < bufferEnd) {
                        if (lookingAtOffset(tokenEnd, closePattern)) {
                            tokenEnd += closePattern.length
                            break
                        }
                        tokenEnd++
                    }
                }
            }
        } else {
            while (tokenEnd < bufferEnd && buffer[tokenEnd] != '"') {
                if (buffer[tokenEnd] == '\\' && tokenEnd + 1 < bufferEnd) tokenEnd++
                tokenEnd++
            }
            if (tokenEnd < bufferEnd) tokenEnd++
        }
        currentToken = RsxTokenTypes.RUST_STRING
    }

    private fun lexRustChar() {
        tokenEnd = tokenStart + 1
        if (tokenEnd < bufferEnd && buffer[tokenEnd] == '\\') {
            tokenEnd += 2
        } else if (tokenEnd < bufferEnd) {
            tokenEnd++
        }
        if (tokenEnd < bufferEnd && buffer[tokenEnd] == '\'') tokenEnd++
        currentToken = RsxTokenTypes.RUST_STRING
    }

    private fun lexRustNumber() {
        tokenEnd = tokenStart
        if (lookingAt("0x") || lookingAt("0X")) {
            tokenEnd += 2
            while (tokenEnd < bufferEnd && (buffer[tokenEnd].isDigit() || buffer[tokenEnd] in 'a'..'f' || buffer[tokenEnd] in 'A'..'F' || buffer[tokenEnd] == '_')) tokenEnd++
        } else if (lookingAt("0b") || lookingAt("0B")) {
            tokenEnd += 2
            while (tokenEnd < bufferEnd && (buffer[tokenEnd] == '0' || buffer[tokenEnd] == '1' || buffer[tokenEnd] == '_')) tokenEnd++
        } else if (lookingAt("0o") || lookingAt("0O")) {
            tokenEnd += 2
            while (tokenEnd < bufferEnd && (buffer[tokenEnd] in '0'..'7' || buffer[tokenEnd] == '_')) tokenEnd++
        } else {
            while (tokenEnd < bufferEnd && (buffer[tokenEnd].isDigit() || buffer[tokenEnd] == '_')) tokenEnd++
            if (tokenEnd < bufferEnd && buffer[tokenEnd] == '.') {
                tokenEnd++
                while (tokenEnd < bufferEnd && (buffer[tokenEnd].isDigit() || buffer[tokenEnd] == '_')) tokenEnd++
            }
            if (tokenEnd < bufferEnd && (buffer[tokenEnd] == 'e' || buffer[tokenEnd] == 'E')) {
                tokenEnd++
                if (tokenEnd < bufferEnd && (buffer[tokenEnd] == '+' || buffer[tokenEnd] == '-')) tokenEnd++
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) tokenEnd++
            }
        }
        // Type suffix
        while (tokenEnd < bufferEnd && (buffer[tokenEnd].isLetter() || buffer[tokenEnd].isDigit())) tokenEnd++
        currentToken = RsxTokenTypes.RUST_NUMBER
    }

    private fun isRustOperator(c: Char) = c in "+-*/%&|^!<>=@#$?~"

    // ==================== TypeScript Lexer ====================
    private fun lexScript() {
        when {
            lookingAt("</script>") -> {
                tokenEnd = tokenStart + 9
                currentToken = RsxTokenTypes.SCRIPT_CLOSE
                state = STATE_INITIAL
            }
            currentChar().isWhitespace() -> lexWhitespace()
            lookingAt("//") -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != '\n') tokenEnd++
                currentToken = RsxTokenTypes.TS_COMMENT
            }
            lookingAt("/*") -> {
                tokenEnd = tokenStart + 2
                while (tokenEnd < bufferEnd - 1 && !(buffer[tokenEnd] == '*' && buffer[tokenEnd + 1] == '/')) {
                    tokenEnd++
                }
                if (tokenEnd < bufferEnd - 1) tokenEnd += 2
                currentToken = RsxTokenTypes.TS_COMMENT
            }
            currentChar() == '"' || currentChar() == '\'' || currentChar() == '`' -> lexTsString()
            currentChar().isDigit() -> lexTsNumber()
            isIdentifierStart(currentChar()) -> lexTsIdentifier()
            isTsOperator(currentChar()) -> {
                tokenEnd = tokenStart + 1
                if (tokenEnd < bufferEnd) {
                    val two = buffer.subSequence(tokenStart, tokenEnd + 1).toString()
                    if (two in listOf("=>", "&&", "||", "==", "!=", "<=", ">=", "++", "--", "+=", "-=", "**", "??", "?.")) {
                        tokenEnd++
                        if (tokenEnd < bufferEnd) {
                            val three = buffer.subSequence(tokenStart, tokenEnd + 1).toString()
                            if (three in listOf("===", "!==", "...")) tokenEnd++
                        }
                    }
                }
                currentToken = RsxTokenTypes.TS_OPERATOR
            }
            currentChar() in "{}[]();,." -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.TS_PUNCTUATION
            }
            else -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.BAD_CHARACTER
            }
        }
    }

    private fun lexTsIdentifier() {
        tokenEnd = tokenStart
        while (tokenEnd < bufferEnd && isIdentifierPart(buffer[tokenEnd])) tokenEnd++
        val text = buffer.subSequence(tokenStart, tokenEnd).toString()

        val nextChar = peekNextNonWhitespace()

        currentToken = when {
            text in TS_KEYWORDS -> RsxTokenTypes.TS_KEYWORD
            text in TS_TYPES -> RsxTokenTypes.TS_TYPE
            // SCREAMING_SNAKE_CASE = constant
            text.all { it.isUpperCase() || it == '_' } && text.any { it.isLetter() } -> RsxTokenTypes.TS_CONSTANT
            // PascalCase after class/interface/type/extends/implements = class/type
            text[0].isUpperCase() && (lastTokenText in listOf("class", "interface", "type", "extends", "implements", "new", ":", "<", ",")) -> RsxTokenTypes.TS_CLASS
            // PascalCase followed by < = generic type
            text[0].isUpperCase() && nextChar == '<' -> RsxTokenTypes.TS_CLASS
            // PascalCase in general = likely a type
            text[0].isUpperCase() -> RsxTokenTypes.TS_TYPE
            // After function = function definition
            lastTokenText == "function" -> RsxTokenTypes.TS_FUNCTION
            // Followed by ( = function call
            nextChar == '(' -> RsxTokenTypes.TS_FUNCTION_CALL
            // After . = property or method
            lastToken == RsxTokenTypes.TS_PUNCTUATION && lastTokenText == "." -> {
                if (nextChar == '(') RsxTokenTypes.TS_FUNCTION_CALL else RsxTokenTypes.TS_PROPERTY
            }
            else -> RsxTokenTypes.TS_IDENTIFIER
        }
    }

    private fun lexTsString() {
        val quote = currentChar()
        tokenEnd = tokenStart + 1
        if (quote == '`') {
            // Template literal
            while (tokenEnd < bufferEnd && buffer[tokenEnd] != '`') {
                if (buffer[tokenEnd] == '\\' && tokenEnd + 1 < bufferEnd) tokenEnd++
                tokenEnd++
            }
        } else {
            while (tokenEnd < bufferEnd && buffer[tokenEnd] != quote) {
                if (buffer[tokenEnd] == '\\' && tokenEnd + 1 < bufferEnd) tokenEnd++
                tokenEnd++
            }
        }
        if (tokenEnd < bufferEnd) tokenEnd++
        currentToken = RsxTokenTypes.TS_STRING
    }

    private fun lexTsNumber() {
        tokenEnd = tokenStart
        if (lookingAt("0x") || lookingAt("0X")) {
            tokenEnd += 2
            while (tokenEnd < bufferEnd && (buffer[tokenEnd].isDigit() || buffer[tokenEnd] in 'a'..'f' || buffer[tokenEnd] in 'A'..'F')) tokenEnd++
        } else if (lookingAt("0b") || lookingAt("0B")) {
            tokenEnd += 2
            while (tokenEnd < bufferEnd && (buffer[tokenEnd] == '0' || buffer[tokenEnd] == '1')) tokenEnd++
        } else if (lookingAt("0o") || lookingAt("0O")) {
            tokenEnd += 2
            while (tokenEnd < bufferEnd && buffer[tokenEnd] in '0'..'7') tokenEnd++
        } else {
            while (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) tokenEnd++
            if (tokenEnd < bufferEnd && buffer[tokenEnd] == '.') {
                tokenEnd++
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) tokenEnd++
            }
            if (tokenEnd < bufferEnd && (buffer[tokenEnd] == 'e' || buffer[tokenEnd] == 'E')) {
                tokenEnd++
                if (tokenEnd < bufferEnd && (buffer[tokenEnd] == '+' || buffer[tokenEnd] == '-')) tokenEnd++
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isDigit()) tokenEnd++
            }
        }
        if (tokenEnd < bufferEnd && buffer[tokenEnd] == 'n') tokenEnd++ // BigInt
        currentToken = RsxTokenTypes.TS_NUMBER
    }

    private fun isTsOperator(c: Char) = c in "+-*/%&|^!<>=?:~"

    // ==================== CSS Lexer ====================
    private fun lexStyle() {
        when {
            lookingAt("</style>") -> {
                tokenEnd = tokenStart + 8
                currentToken = RsxTokenTypes.STYLE_CLOSE
                state = STATE_INITIAL
            }
            currentChar().isWhitespace() -> lexWhitespace()
            lookingAt("/*") -> {
                tokenEnd = tokenStart + 2
                while (tokenEnd < bufferEnd - 1 && !(buffer[tokenEnd] == '*' && buffer[tokenEnd + 1] == '/')) {
                    tokenEnd++
                }
                if (tokenEnd < bufferEnd - 1) tokenEnd += 2
                currentToken = RsxTokenTypes.CSS_COMMENT
            }
            currentChar() == '@' -> {
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && (buffer[tokenEnd].isLetter() || buffer[tokenEnd] == '-')) tokenEnd++
                currentToken = RsxTokenTypes.CSS_AT_RULE
            }
            currentChar() == '#' -> {
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && (buffer[tokenEnd].isLetterOrDigit() || buffer[tokenEnd] in "-_")) tokenEnd++
                // Check if it's a color
                val len = tokenEnd - tokenStart - 1
                if (len == 3 || len == 4 || len == 6 || len == 8) {
                    val hex = buffer.subSequence(tokenStart + 1, tokenEnd).toString()
                    if (hex.all { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }) {
                        currentToken = RsxTokenTypes.CSS_COLOR
                        return
                    }
                }
                currentToken = RsxTokenTypes.CSS_SELECTOR
            }
            currentChar() == '.' && tokenStart + 1 < bufferEnd && (buffer[tokenStart + 1].isLetter() || buffer[tokenStart + 1] == '-' || buffer[tokenStart + 1] == '_') -> {
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && (buffer[tokenEnd].isLetterOrDigit() || buffer[tokenEnd] in "-_")) tokenEnd++
                currentToken = RsxTokenTypes.CSS_SELECTOR
            }
            currentChar() == '"' || currentChar() == '\'' -> {
                val quote = currentChar()
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != quote) {
                    if (buffer[tokenEnd] == '\\' && tokenEnd + 1 < bufferEnd) tokenEnd++
                    tokenEnd++
                }
                if (tokenEnd < bufferEnd) tokenEnd++
                currentToken = RsxTokenTypes.CSS_VALUE
            }
            currentChar().isDigit() || (currentChar() == '.' && tokenStart + 1 < bufferEnd && buffer[tokenStart + 1].isDigit()) -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && (buffer[tokenEnd].isDigit() || buffer[tokenEnd] == '.')) tokenEnd++
                // Unit suffix
                val unitStart = tokenEnd
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isLetter()) tokenEnd++
                if (tokenEnd > unitStart) {
                    currentToken = RsxTokenTypes.CSS_NUMBER
                } else {
                    currentToken = RsxTokenTypes.CSS_NUMBER
                }
            }
            currentChar().isLetter() || currentChar() == '-' || currentChar() == '_' -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && (buffer[tokenEnd].isLetterOrDigit() || buffer[tokenEnd] in "-_")) tokenEnd++
                val text = buffer.subSequence(tokenStart, tokenEnd).toString()
                // Check if followed by : (property) or not (selector/value)
                var nextIdx = tokenEnd
                while (nextIdx < bufferEnd && buffer[nextIdx].isWhitespace()) nextIdx++
                currentToken = when {
                    nextIdx < bufferEnd && buffer[nextIdx] == ':' -> RsxTokenTypes.CSS_PROPERTY
                    nextIdx < bufferEnd && buffer[nextIdx] == '{' -> RsxTokenTypes.CSS_SELECTOR
                    text.endsWith("px") || text.endsWith("em") || text.endsWith("rem") ||
                    text.endsWith("vh") || text.endsWith("vw") || text.endsWith("%") -> RsxTokenTypes.CSS_NUMBER
                    else -> RsxTokenTypes.CSS_VALUE
                }
            }
            currentChar() in "{}();:," -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.CSS_PUNCTUATION
            }
            else -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.BAD_CHARACTER
            }
        }
    }

    // ==================== Template Lexer ====================
    private fun lexTemplate() {
        when {
            lookingAt("</template>") -> {
                tokenEnd = tokenStart + 11
                currentToken = RsxTokenTypes.TEMPLATE_CLOSE
                state = STATE_INITIAL
            }
            lookingAt("{{@") || lookingAt("{{:") || lookingAt("{{/") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.INTERPOLATION_OPEN
                state = STATE_DIRECTIVE
            }
            lookingAt("{{") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.INTERPOLATION_OPEN
                state = STATE_INTERPOLATION
            }
            lookingAt("<!--") -> {
                val closeIdx = findPattern("-->")
                tokenEnd = if (closeIdx >= 0) closeIdx + 3 else bufferEnd
                currentToken = RsxTokenTypes.HTML_COMMENT
            }
            lookingAt("</") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.HTML_TAG_END_OPEN
                state = STATE_TEMPLATE_CLOSE_TAG
            }
            lookingAt("<") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.HTML_TAG_OPEN
                state = STATE_TEMPLATE_TAG
            }
            currentChar().isWhitespace() -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace()) {
                    if (lookingAtOffset(tokenEnd, "</template>") || lookingAtOffset(tokenEnd, "<") || lookingAtOffset(tokenEnd, "{{")) break
                    tokenEnd++
                }
                if (tokenEnd == tokenStart) tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.WHITE_SPACE
            }
            else -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd) {
                    val c = buffer[tokenEnd]
                    if (c == '<' || lookingAtOffset(tokenEnd, "{{") || c.isWhitespace()) break
                    tokenEnd++
                }
                if (tokenEnd == tokenStart) tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.HTML_TEXT
            }
        }
    }

    private fun lexTemplateTag() {
        when {
            currentChar().isWhitespace() -> lexWhitespace()
            lookingAt("/>") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.HTML_SELF_CLOSE
                state = STATE_TEMPLATE
            }
            lookingAt(">") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.HTML_TAG_CLOSE
                state = STATE_TEMPLATE
            }
            lookingAt("=") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.EQ
                state = STATE_TEMPLATE_TAG_ATTR
            }
            lookingAt("\"") || lookingAt("'") -> {
                val quote = currentChar()
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != quote) tokenEnd++
                if (tokenEnd < bufferEnd) tokenEnd++
                currentToken = RsxTokenTypes.HTML_ATTR_VALUE
            }
            isIdentifierStart(currentChar()) -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && isTagNameChar(buffer[tokenEnd])) tokenEnd++
                var nextIdx = tokenEnd
                while (nextIdx < bufferEnd && buffer[nextIdx].isWhitespace()) nextIdx++
                // Use lastNonWhitespaceToken to handle whitespace between < and tag name
                val prevToken = lastNonWhitespaceToken
                currentToken = if (prevToken == RsxTokenTypes.HTML_TAG_OPEN) {
                    RsxTokenTypes.HTML_TAG_NAME
                } else if (nextIdx < bufferEnd && buffer[nextIdx] == '=') {
                    RsxTokenTypes.HTML_ATTR_NAME
                } else if (nextIdx < bufferEnd && (buffer[nextIdx] == '>' || buffer[nextIdx] == '/')) {
                    // Could be a boolean attribute or tag name - check context
                    if (prevToken == RsxTokenTypes.HTML_TAG_NAME || prevToken == RsxTokenTypes.HTML_ATTR_VALUE || prevToken == RsxTokenTypes.HTML_ATTR_NAME) {
                        RsxTokenTypes.HTML_ATTR_NAME  // Boolean attribute
                    } else {
                        RsxTokenTypes.HTML_TAG_NAME
                    }
                } else {
                    // Default: if after tag name or attr, it's an attribute
                    if (prevToken == RsxTokenTypes.HTML_TAG_NAME || prevToken == RsxTokenTypes.HTML_ATTR_VALUE || prevToken == RsxTokenTypes.HTML_ATTR_NAME) {
                        RsxTokenTypes.HTML_ATTR_NAME
                    } else {
                        RsxTokenTypes.HTML_TAG_NAME
                    }
                }
            }
            else -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.BAD_CHARACTER
            }
        }
    }

    private fun lexTemplateCloseTag() {
        when {
            currentChar().isWhitespace() -> lexWhitespace()
            lookingAt(">") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.HTML_TAG_CLOSE
                state = STATE_TEMPLATE
            }
            isIdentifierStart(currentChar()) -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && isTagNameChar(buffer[tokenEnd])) tokenEnd++
                currentToken = RsxTokenTypes.HTML_TAG_NAME
            }
            else -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.BAD_CHARACTER
            }
        }
    }

    private fun lexTemplateTagAttr() {
        when {
            currentChar().isWhitespace() -> lexWhitespace()
            lookingAt("\"") -> {
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != '"') tokenEnd++
                if (tokenEnd < bufferEnd) tokenEnd++
                currentToken = RsxTokenTypes.HTML_ATTR_VALUE
                state = STATE_TEMPLATE_TAG
            }
            lookingAt("'") -> {
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != '\'') tokenEnd++
                if (tokenEnd < bufferEnd) tokenEnd++
                currentToken = RsxTokenTypes.HTML_ATTR_VALUE
                state = STATE_TEMPLATE_TAG
            }
            lookingAt("{{") -> {
                val closeInterp = findPattern("}}")
                tokenEnd = if (closeInterp >= 0) closeInterp + 2 else bufferEnd
                currentToken = RsxTokenTypes.HTML_ATTR_VALUE
                state = STATE_TEMPLATE_TAG
            }
            else -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && !buffer[tokenEnd].isWhitespace() && buffer[tokenEnd] != '>' && buffer[tokenEnd] != '/') {
                    tokenEnd++
                }
                if (tokenEnd > tokenStart) {
                    currentToken = RsxTokenTypes.HTML_ATTR_VALUE
                } else {
                    state = STATE_TEMPLATE_TAG
                    lexTemplateTag()
                    return
                }
                state = STATE_TEMPLATE_TAG
            }
        }
    }

    // ==================== Interpolation Lexer ====================
    private fun lexInterpolation() {
        when {
            currentChar().isWhitespace() -> lexWhitespace()
            lookingAt("}}") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.INTERPOLATION_CLOSE
                state = STATE_TEMPLATE
            }
            lookingAt("===") -> { tokenEnd = tokenStart + 3; currentToken = RsxTokenTypes.EQEQEQ }
            lookingAt("!==") -> { tokenEnd = tokenStart + 3; currentToken = RsxTokenTypes.NEE }
            lookingAt("==") -> { tokenEnd = tokenStart + 2; currentToken = RsxTokenTypes.EQEQ }
            lookingAt("!=") -> { tokenEnd = tokenStart + 2; currentToken = RsxTokenTypes.NE }
            lookingAt(">=") -> { tokenEnd = tokenStart + 2; currentToken = RsxTokenTypes.GE }
            lookingAt("<=") -> { tokenEnd = tokenStart + 2; currentToken = RsxTokenTypes.LE }
            lookingAt("&&") -> { tokenEnd = tokenStart + 2; currentToken = RsxTokenTypes.AND }
            lookingAt("||") -> { tokenEnd = tokenStart + 2; currentToken = RsxTokenTypes.OR }
            lookingAt(">") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.GT }
            lookingAt("<") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.LT }
            lookingAt("+") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.PLUS }
            lookingAt("-") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.MINUS }
            lookingAt("*") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.MUL }
            lookingAt("/") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.DIV }
            lookingAt("%") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.MOD }
            lookingAt("!") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.NOT }
            lookingAt("?") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.QUESTION }
            lookingAt(":") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.COLON }
            lookingAt(".") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.DOT }
            lookingAt(",") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.COMMA }
            lookingAt("(") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.LPAREN }
            lookingAt(")") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.RPAREN }
            lookingAt("[") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.LBRACKET }
            lookingAt("]") -> { tokenEnd = tokenStart + 1; currentToken = RsxTokenTypes.RBRACKET }
            lookingAt("'") || lookingAt("\"") -> {
                val quote = currentChar()
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != quote) {
                    if (buffer[tokenEnd] == '\\' && tokenEnd + 1 < bufferEnd) tokenEnd++
                    tokenEnd++
                }
                if (tokenEnd < bufferEnd) tokenEnd++
                currentToken = RsxTokenTypes.STRING
            }
            currentChar().isDigit() -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && (buffer[tokenEnd].isDigit() || buffer[tokenEnd] == '.')) tokenEnd++
                currentToken = RsxTokenTypes.NUMBER
            }
            isIdentifierStart(currentChar()) -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && isIdentifierPart(buffer[tokenEnd])) tokenEnd++
                val text = buffer.subSequence(tokenStart, tokenEnd).toString()
                currentToken = when (text) {
                    "true", "false" -> RsxTokenTypes.BOOLEAN
                    "as" -> RsxTokenTypes.AS
                    else -> RsxTokenTypes.IDENTIFIER
                }
            }
            else -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.BAD_CHARACTER
            }
        }
    }

    private fun lexDirective() {
        when {
            currentChar().isWhitespace() -> lexWhitespace()
            lookingAt("}}") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.INTERPOLATION_CLOSE
                state = STATE_TEMPLATE
            }
            lookingAt("@if") && !isIdentifierPart(charAtOffset(3)) -> {
                tokenEnd = tokenStart + 3
                currentToken = RsxTokenTypes.DIRECTIVE_IF
                state = STATE_INTERPOLATION
            }
            lookingAt("@each") && !isIdentifierPart(charAtOffset(5)) -> {
                tokenEnd = tokenStart + 5
                currentToken = RsxTokenTypes.DIRECTIVE_EACH
                state = STATE_INTERPOLATION
            }
            lookingAt("@html") && !isIdentifierPart(charAtOffset(5)) -> {
                tokenEnd = tokenStart + 5
                currentToken = RsxTokenTypes.DIRECTIVE_HTML
                state = STATE_INTERPOLATION
            }
            lookingAt(":else if") -> {
                tokenEnd = tokenStart + 8
                currentToken = RsxTokenTypes.DIRECTIVE_ELSE_IF
                state = STATE_INTERPOLATION
            }
            lookingAt(":elseif") -> {
                tokenEnd = tokenStart + 7
                currentToken = RsxTokenTypes.DIRECTIVE_ELSE_IF
                state = STATE_INTERPOLATION
            }
            lookingAt(":else") && !isIdentifierPart(charAtOffset(5)) -> {
                tokenEnd = tokenStart + 5
                currentToken = RsxTokenTypes.DIRECTIVE_ELSE
                state = STATE_INTERPOLATION
            }
            lookingAt("/if") && !isIdentifierPart(charAtOffset(3)) -> {
                tokenEnd = tokenStart + 3
                currentToken = RsxTokenTypes.DIRECTIVE_END_IF
                state = STATE_INTERPOLATION
            }
            lookingAt("/each") && !isIdentifierPart(charAtOffset(5)) -> {
                tokenEnd = tokenStart + 5
                currentToken = RsxTokenTypes.DIRECTIVE_END_EACH
                state = STATE_INTERPOLATION
            }
            else -> {
                state = STATE_INTERPOLATION
                lexInterpolation()
            }
        }
    }

    // ==================== Helper Methods ====================
    private fun lexWhitespace() {
        tokenEnd = tokenStart
        while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace()) tokenEnd++
        currentToken = RsxTokenTypes.WHITE_SPACE
    }

    private fun currentChar(): Char = if (tokenStart < bufferEnd) buffer[tokenStart] else '\u0000'
    private fun charAtOffset(offset: Int): Char = if (tokenStart + offset < bufferEnd) buffer[tokenStart + offset] else '\u0000'

    private fun lookingAt(s: String): Boolean {
        if (tokenStart + s.length > bufferEnd) return false
        for (i in s.indices) {
            if (buffer[tokenStart + i] != s[i]) return false
        }
        return true
    }

    private fun lookingAtOffset(offset: Int, s: String): Boolean {
        if (offset + s.length > bufferEnd) return false
        for (i in s.indices) {
            if (buffer[offset + i] != s[i]) return false
        }
        return true
    }

    private fun findPattern(pattern: String): Int {
        var i = tokenStart
        while (i + pattern.length <= bufferEnd) {
            if (lookingAtOffset(i, pattern)) return i
            i++
        }
        return -1
    }

    private fun findEndOf(s: String): Int = findPattern(s)

    private fun isIdentifierStart(c: Char) = c.isLetter() || c == '_' || c == '$'
    private fun isIdentifierPart(c: Char) = c.isLetterOrDigit() || c == '_' || c == '$'
    private fun isTagNameChar(c: Char) = c.isLetterOrDigit() || c == '-' || c == '_' || c == ':'
}
