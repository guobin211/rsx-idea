package com.github.guobin211.rsxidea.lexer

import com.github.guobin211.rsxidea.psi.RsxTokenTypes
import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

/**
 * RSX Lexer - Handles lexical analysis for RSX files
 * Supports four sections: Rust (---...---), Script (<script>), Template (<template>), Style (<style>)
 */
class RsxLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var bufferEnd: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var currentToken: IElementType? = null
    private var state: Int = STATE_INITIAL

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
    }

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.bufferEnd = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        this.state = initialState
        advance()
    }

    override fun getState(): Int = state

    override fun getTokenType(): IElementType? = currentToken

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
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

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = bufferEnd

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
            currentChar().isWhitespace() -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace()) {
                    tokenEnd++
                }
                currentToken = RsxTokenTypes.WHITE_SPACE
            }
            else -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.BAD_CHARACTER
            }
        }
    }

    private fun lexRust() {
        when {
            lookingAt("---") -> {
                tokenEnd = tokenStart + 3
                currentToken = RsxTokenTypes.RUST_DELIMITER
                state = STATE_INITIAL
            }
            else -> {
                val closeIdx = findPattern("---")
                tokenEnd = if (closeIdx >= 0) closeIdx else bufferEnd
                if (tokenEnd > tokenStart) {
                    currentToken = RsxTokenTypes.RUST_CODE
                } else {
                    tokenEnd = tokenStart + 1
                    currentToken = RsxTokenTypes.RUST_CODE
                }
            }
        }
    }

    private fun lexScript() {
        when {
            lookingAt("</script>") -> {
                tokenEnd = tokenStart + 9
                currentToken = RsxTokenTypes.SCRIPT_CLOSE
                state = STATE_INITIAL
            }
            else -> {
                val closeIdx = findPattern("</script>")
                tokenEnd = if (closeIdx >= 0) closeIdx else bufferEnd
                if (tokenEnd > tokenStart) {
                    currentToken = RsxTokenTypes.SCRIPT_CODE
                } else {
                    tokenEnd = tokenStart + 1
                    currentToken = RsxTokenTypes.SCRIPT_CODE
                }
            }
        }
    }

    private fun lexStyle() {
        when {
            lookingAt("</style>") -> {
                tokenEnd = tokenStart + 8
                currentToken = RsxTokenTypes.STYLE_CLOSE
                state = STATE_INITIAL
            }
            else -> {
                val closeIdx = findPattern("</style>")
                tokenEnd = if (closeIdx >= 0) closeIdx else bufferEnd
                if (tokenEnd > tokenStart) {
                    currentToken = RsxTokenTypes.STYLE_CODE
                } else {
                    tokenEnd = tokenStart + 1
                    currentToken = RsxTokenTypes.STYLE_CODE
                }
            }
        }
    }

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
                    if (lookingAtOffset(tokenEnd, "</template>") || 
                        lookingAtOffset(tokenEnd, "<") || 
                        lookingAtOffset(tokenEnd, "{{")) {
                        break
                    }
                    tokenEnd++
                }
                if (tokenEnd == tokenStart) tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.WHITE_SPACE
            }
            else -> {
                // HTML text content - read until we hit special chars
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd) {
                    val c = buffer[tokenEnd]
                    if (c == '<' || lookingAtOffset(tokenEnd, "{{")) {
                        break
                    }
                    if (c.isWhitespace()) {
                        break
                    }
                    tokenEnd++
                }
                if (tokenEnd == tokenStart) tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.HTML_TEXT
            }
        }
    }

    private fun lexTemplateTag() {
        when {
            currentChar().isWhitespace() -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace()) {
                    tokenEnd++
                }
                currentToken = RsxTokenTypes.WHITE_SPACE
            }
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
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != quote) {
                    tokenEnd++
                }
                if (tokenEnd < bufferEnd) tokenEnd++ // closing quote
                currentToken = RsxTokenTypes.HTML_ATTR_VALUE
            }
            isIdentifierStart(currentChar()) -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && isTagNameChar(buffer[tokenEnd])) {
                    tokenEnd++
                }
                // Check if this is followed by = (attribute) or not (tag name)
                var nextIdx = tokenEnd
                while (nextIdx < bufferEnd && buffer[nextIdx].isWhitespace()) {
                    nextIdx++
                }
                currentToken = if (nextIdx < bufferEnd && buffer[nextIdx] == '=') {
                    RsxTokenTypes.HTML_ATTR_NAME
                } else if (nextIdx < bufferEnd && (buffer[nextIdx] == '>' || buffer[nextIdx] == '/')) {
                    RsxTokenTypes.HTML_TAG_NAME
                } else if (nextIdx < bufferEnd && isIdentifierStart(buffer[nextIdx])) {
                    // Another identifier follows - first one is tag name
                    RsxTokenTypes.HTML_TAG_NAME
                } else {
                    RsxTokenTypes.HTML_TAG_NAME
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
            currentChar().isWhitespace() -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace()) {
                    tokenEnd++
                }
                currentToken = RsxTokenTypes.WHITE_SPACE
            }
            lookingAt(">") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.HTML_TAG_CLOSE
                state = STATE_TEMPLATE
            }
            isIdentifierStart(currentChar()) -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && isTagNameChar(buffer[tokenEnd])) {
                    tokenEnd++
                }
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
            currentChar().isWhitespace() -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace()) {
                    tokenEnd++
                }
                currentToken = RsxTokenTypes.WHITE_SPACE
            }
            lookingAt("\"") -> {
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != '"') {
                    tokenEnd++
                }
                if (tokenEnd < bufferEnd) tokenEnd++ // closing quote
                currentToken = RsxTokenTypes.HTML_ATTR_VALUE
                state = STATE_TEMPLATE_TAG
            }
            lookingAt("'") -> {
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != '\'') {
                    tokenEnd++
                }
                if (tokenEnd < bufferEnd) tokenEnd++ // closing quote
                currentToken = RsxTokenTypes.HTML_ATTR_VALUE
                state = STATE_TEMPLATE_TAG
            }
            lookingAt("{{") -> {
                // Unquoted interpolation as attribute value
                val closeInterp = findPattern("}}")
                tokenEnd = if (closeInterp >= 0) closeInterp + 2 else bufferEnd
                currentToken = RsxTokenTypes.HTML_ATTR_VALUE
                state = STATE_TEMPLATE_TAG
            }
            else -> {
                // Unquoted attribute value
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && !buffer[tokenEnd].isWhitespace() && 
                       buffer[tokenEnd] != '>' && buffer[tokenEnd] != '/') {
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

    private fun lexInterpolation() {
        when {
            currentChar().isWhitespace() -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace()) {
                    tokenEnd++
                }
                currentToken = RsxTokenTypes.WHITE_SPACE
            }
            lookingAt("}}") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.INTERPOLATION_CLOSE
                state = STATE_TEMPLATE
            }
            lookingAt("===") -> {
                tokenEnd = tokenStart + 3
                currentToken = RsxTokenTypes.EQEQEQ
            }
            lookingAt("!==") -> {
                tokenEnd = tokenStart + 3
                currentToken = RsxTokenTypes.NEE
            }
            lookingAt("==") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.EQEQ
            }
            lookingAt("!=") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.NE
            }
            lookingAt(">=") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.GE
            }
            lookingAt("<=") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.LE
            }
            lookingAt("&&") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.AND
            }
            lookingAt("||") -> {
                tokenEnd = tokenStart + 2
                currentToken = RsxTokenTypes.OR
            }
            lookingAt(">") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.GT
            }
            lookingAt("<") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.LT
            }
            lookingAt("+") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.PLUS
            }
            lookingAt("-") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.MINUS
            }
            lookingAt("*") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.MUL
            }
            lookingAt("/") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.DIV
            }
            lookingAt("%") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.MOD
            }
            lookingAt("!") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.NOT
            }
            lookingAt("?") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.QUESTION
            }
            lookingAt(":") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.COLON
            }
            lookingAt(".") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.DOT
            }
            lookingAt(",") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.COMMA
            }
            lookingAt("(") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.LPAREN
            }
            lookingAt(")") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.RPAREN
            }
            lookingAt("[") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.LBRACKET
            }
            lookingAt("]") -> {
                tokenEnd = tokenStart + 1
                currentToken = RsxTokenTypes.RBRACKET
            }
            lookingAt("'") || lookingAt("\"") -> {
                val quote = currentChar()
                tokenEnd = tokenStart + 1
                while (tokenEnd < bufferEnd && buffer[tokenEnd] != quote) {
                    if (buffer[tokenEnd] == '\\' && tokenEnd + 1 < bufferEnd) {
                        tokenEnd++
                    }
                    tokenEnd++
                }
                if (tokenEnd < bufferEnd) tokenEnd++ // closing quote
                currentToken = RsxTokenTypes.STRING
            }
            currentChar().isDigit() -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && (buffer[tokenEnd].isDigit() || buffer[tokenEnd] == '.')) {
                    tokenEnd++
                }
                currentToken = RsxTokenTypes.NUMBER
            }
            isIdentifierStart(currentChar()) -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && isIdentifierPart(buffer[tokenEnd])) {
                    tokenEnd++
                }
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
            currentChar().isWhitespace() -> {
                tokenEnd = tokenStart
                while (tokenEnd < bufferEnd && buffer[tokenEnd].isWhitespace()) {
                    tokenEnd++
                }
                currentToken = RsxTokenTypes.WHITE_SPACE
            }
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
                // Fall back to interpolation parsing
                state = STATE_INTERPOLATION
                lexInterpolation()
            }
        }
    }

    // Helper methods
    private fun currentChar(): Char = if (tokenStart < bufferEnd) buffer[tokenStart] else '\u0000'

    private fun charAtOffset(offset: Int): Char {
        val idx = tokenStart + offset
        return if (idx < bufferEnd) buffer[idx] else '\u0000'
    }

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

    private fun findEndOf(s: String): Int {
        val idx = findPattern(s)
        return if (idx >= 0) idx else -1
    }

    private fun isIdentifierStart(c: Char): Boolean = c.isLetter() || c == '_' || c == '$'

    private fun isIdentifierPart(c: Char): Boolean = c.isLetterOrDigit() || c == '_' || c == '$'

    private fun isTagNameChar(c: Char): Boolean = c.isLetterOrDigit() || c == '-' || c == '_' || c == ':'
}
