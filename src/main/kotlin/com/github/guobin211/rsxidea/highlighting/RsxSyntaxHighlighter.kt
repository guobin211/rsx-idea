package com.github.guobin211.rsxidea.highlighting

import com.github.guobin211.rsxidea.lexer.RsxLexerAdapter
import com.github.guobin211.rsxidea.psi.RsxTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class RsxSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        // Section delimiters - use bright keyword color
        val DELIMITER = createTextAttributesKey("RSX_DELIMITER", DefaultLanguageHighlighterColors.KEYWORD)
        val SECTION_TAG = createTextAttributesKey("RSX_SECTION_TAG", DefaultLanguageHighlighterColors.KEYWORD)

        // Rust code - use function declaration color for visibility
        val RUST_CODE = createTextAttributesKey("RSX_RUST_CODE", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)

        // Script code - use local variable color
        val SCRIPT_CODE = createTextAttributesKey("RSX_SCRIPT_CODE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE)

        // Style code - use class name color
        val STYLE_CODE = createTextAttributesKey("RSX_STYLE_CODE", DefaultLanguageHighlighterColors.CLASS_NAME)

        // HTML
        val HTML_TAG = createTextAttributesKey("RSX_HTML_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG)
        val HTML_TAG_NAME = createTextAttributesKey("RSX_HTML_TAG_NAME", DefaultLanguageHighlighterColors.MARKUP_TAG)
        val HTML_ATTR_NAME = createTextAttributesKey("RSX_HTML_ATTR_NAME", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE)
        val HTML_ATTR_VALUE = createTextAttributesKey("RSX_HTML_ATTR_VALUE", DefaultLanguageHighlighterColors.STRING)
        val HTML_TEXT = createTextAttributesKey("RSX_HTML_TEXT", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR)
        val HTML_COMMENT = createTextAttributesKey("RSX_HTML_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)

        // Interpolation - use braces color
        val INTERPOLATION_BRACES = createTextAttributesKey("RSX_INTERPOLATION_BRACES", DefaultLanguageHighlighterColors.BRACES)

        // Directives - use keyword color for visibility
        val DIRECTIVE = createTextAttributesKey("RSX_DIRECTIVE", DefaultLanguageHighlighterColors.KEYWORD)

        // Expressions
        val IDENTIFIER = createTextAttributesKey("RSX_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        val NUMBER = createTextAttributesKey("RSX_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val STRING = createTextAttributesKey("RSX_STRING", DefaultLanguageHighlighterColors.STRING)
        val KEYWORD = createTextAttributesKey("RSX_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val OPERATOR = createTextAttributesKey("RSX_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val BRACKETS = createTextAttributesKey("RSX_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        val PARENTHESES = createTextAttributesKey("RSX_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
        val DOT = createTextAttributesKey("RSX_DOT", DefaultLanguageHighlighterColors.DOT)
        val COMMA = createTextAttributesKey("RSX_COMMA", DefaultLanguageHighlighterColors.COMMA)

        // Bad character
        val BAD_CHARACTER = createTextAttributesKey("RSX_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        private val DELIMITER_KEYS = arrayOf(DELIMITER)
        private val SECTION_TAG_KEYS = arrayOf(SECTION_TAG)
        private val RUST_CODE_KEYS = arrayOf(RUST_CODE)
        private val SCRIPT_CODE_KEYS = arrayOf(SCRIPT_CODE)
        private val STYLE_CODE_KEYS = arrayOf(STYLE_CODE)
        private val HTML_TAG_KEYS = arrayOf(HTML_TAG)
        private val HTML_TAG_NAME_KEYS = arrayOf(HTML_TAG_NAME)
        private val HTML_ATTR_NAME_KEYS = arrayOf(HTML_ATTR_NAME)
        private val HTML_ATTR_VALUE_KEYS = arrayOf(HTML_ATTR_VALUE)
        private val HTML_TEXT_KEYS = arrayOf(HTML_TEXT)
        private val HTML_COMMENT_KEYS = arrayOf(HTML_COMMENT)
        private val INTERPOLATION_BRACES_KEYS = arrayOf(INTERPOLATION_BRACES)
        private val DIRECTIVE_KEYS = arrayOf(DIRECTIVE)
        private val IDENTIFIER_KEYS = arrayOf(IDENTIFIER)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val STRING_KEYS = arrayOf(STRING)
        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val OPERATOR_KEYS = arrayOf(OPERATOR)
        private val BRACKETS_KEYS = arrayOf(BRACKETS)
        private val PARENTHESES_KEYS = arrayOf(PARENTHESES)
        private val DOT_KEYS = arrayOf(DOT)
        private val COMMA_KEYS = arrayOf(COMMA)
        private val BAD_CHARACTER_KEYS = arrayOf(BAD_CHARACTER)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = RsxLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        if (tokenType == null) return EMPTY_KEYS
        
        return when (tokenType) {
            // Whitespace - no highlighting
            TokenType.WHITE_SPACE -> EMPTY_KEYS

            // Section delimiters
            RsxTokenTypes.RUST_DELIMITER -> DELIMITER_KEYS
            RsxTokenTypes.SCRIPT_OPEN, RsxTokenTypes.SCRIPT_CLOSE,
            RsxTokenTypes.TEMPLATE_OPEN, RsxTokenTypes.TEMPLATE_CLOSE,
            RsxTokenTypes.STYLE_OPEN, RsxTokenTypes.STYLE_CLOSE -> SECTION_TAG_KEYS

            // Section content
            RsxTokenTypes.RUST_CODE -> RUST_CODE_KEYS
            RsxTokenTypes.SCRIPT_CODE -> SCRIPT_CODE_KEYS
            RsxTokenTypes.STYLE_CODE -> STYLE_CODE_KEYS

            // HTML
            RsxTokenTypes.HTML_TAG_OPEN, RsxTokenTypes.HTML_TAG_CLOSE,
            RsxTokenTypes.HTML_TAG_END_OPEN, RsxTokenTypes.HTML_SELF_CLOSE -> HTML_TAG_KEYS
            RsxTokenTypes.HTML_TAG_NAME -> HTML_TAG_NAME_KEYS
            RsxTokenTypes.HTML_ATTR_NAME -> HTML_ATTR_NAME_KEYS
            RsxTokenTypes.HTML_ATTR_VALUE -> HTML_ATTR_VALUE_KEYS
            RsxTokenTypes.HTML_TEXT -> HTML_TEXT_KEYS
            RsxTokenTypes.HTML_COMMENT -> HTML_COMMENT_KEYS

            // Interpolation
            RsxTokenTypes.INTERPOLATION_OPEN, RsxTokenTypes.INTERPOLATION_CLOSE -> INTERPOLATION_BRACES_KEYS

            // Directives
            RsxTokenTypes.DIRECTIVE_IF, RsxTokenTypes.DIRECTIVE_ELSE_IF,
            RsxTokenTypes.DIRECTIVE_ELSE, RsxTokenTypes.DIRECTIVE_END_IF,
            RsxTokenTypes.DIRECTIVE_EACH, RsxTokenTypes.DIRECTIVE_END_EACH,
            RsxTokenTypes.DIRECTIVE_HTML -> DIRECTIVE_KEYS

            // Expressions
            RsxTokenTypes.IDENTIFIER -> IDENTIFIER_KEYS
            RsxTokenTypes.NUMBER -> NUMBER_KEYS
            RsxTokenTypes.STRING -> STRING_KEYS
            RsxTokenTypes.BOOLEAN, RsxTokenTypes.AS -> KEYWORD_KEYS

            // Operators
            RsxTokenTypes.GT, RsxTokenTypes.LT, RsxTokenTypes.GE, RsxTokenTypes.LE,
            RsxTokenTypes.EQEQ, RsxTokenTypes.NE, RsxTokenTypes.EQEQEQ, RsxTokenTypes.NEE,
            RsxTokenTypes.AND, RsxTokenTypes.OR, RsxTokenTypes.NOT,
            RsxTokenTypes.PLUS, RsxTokenTypes.MINUS, RsxTokenTypes.MUL,
            RsxTokenTypes.DIV, RsxTokenTypes.MOD,
            RsxTokenTypes.QUESTION, RsxTokenTypes.COLON, RsxTokenTypes.EQ -> OPERATOR_KEYS

            // Brackets
            RsxTokenTypes.LBRACKET, RsxTokenTypes.RBRACKET -> BRACKETS_KEYS
            RsxTokenTypes.LPAREN, RsxTokenTypes.RPAREN -> PARENTHESES_KEYS

            // Punctuation
            RsxTokenTypes.DOT -> DOT_KEYS
            RsxTokenTypes.COMMA -> COMMA_KEYS

            // Bad character
            TokenType.BAD_CHARACTER -> BAD_CHARACTER_KEYS

            else -> EMPTY_KEYS
        }
    }
}
