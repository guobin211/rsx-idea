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
        // Section delimiters
        val DELIMITER = createTextAttributesKey("RSX_DELIMITER", DefaultLanguageHighlighterColors.KEYWORD)
        val SECTION_TAG = createTextAttributesKey("RSX_SECTION_TAG", DefaultLanguageHighlighterColors.KEYWORD)

        // ============ Rust highlighting ============
        val RUST_KEYWORD = createTextAttributesKey("RSX_RUST_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val RUST_TYPE = createTextAttributesKey("RSX_RUST_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME)
        val RUST_IDENTIFIER = createTextAttributesKey("RSX_RUST_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        val RUST_FUNCTION = createTextAttributesKey("RSX_RUST_FUNCTION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
        val RUST_FUNCTION_CALL = createTextAttributesKey("RSX_RUST_FUNCTION_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL)
        val RUST_STRUCT = createTextAttributesKey("RSX_RUST_STRUCT", DefaultLanguageHighlighterColors.CLASS_NAME)
        val RUST_MODULE = createTextAttributesKey("RSX_RUST_MODULE", DefaultLanguageHighlighterColors.CLASS_REFERENCE)
        val RUST_CONSTANT = createTextAttributesKey("RSX_RUST_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT)
        val RUST_FIELD = createTextAttributesKey("RSX_RUST_FIELD", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        val RUST_STRING = createTextAttributesKey("RSX_RUST_STRING", DefaultLanguageHighlighterColors.STRING)
        val RUST_NUMBER = createTextAttributesKey("RSX_RUST_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val RUST_COMMENT = createTextAttributesKey("RSX_RUST_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val RUST_LIFETIME = createTextAttributesKey("RSX_RUST_LIFETIME", DefaultLanguageHighlighterColors.METADATA)
        val RUST_MACRO = createTextAttributesKey("RSX_RUST_MACRO", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL)
        val RUST_ATTRIBUTE = createTextAttributesKey("RSX_RUST_ATTRIBUTE", DefaultLanguageHighlighterColors.METADATA)
        val RUST_OPERATOR = createTextAttributesKey("RSX_RUST_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val RUST_PUNCTUATION = createTextAttributesKey("RSX_RUST_PUNCTUATION", DefaultLanguageHighlighterColors.SEMICOLON)
        val RUST_SELF = createTextAttributesKey("RSX_RUST_SELF", DefaultLanguageHighlighterColors.KEYWORD)
        val RUST_NAMESPACE = createTextAttributesKey("RSX_RUST_NAMESPACE", DefaultLanguageHighlighterColors.OPERATION_SIGN)

        // ============ TypeScript highlighting ============
        val TS_KEYWORD = createTextAttributesKey("RSX_TS_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val TS_TYPE = createTextAttributesKey("RSX_TS_TYPE", DefaultLanguageHighlighterColors.CLASS_NAME)
        val TS_IDENTIFIER = createTextAttributesKey("RSX_TS_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        val TS_FUNCTION = createTextAttributesKey("RSX_TS_FUNCTION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
        val TS_FUNCTION_CALL = createTextAttributesKey("RSX_TS_FUNCTION_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL)
        val TS_CLASS = createTextAttributesKey("RSX_TS_CLASS", DefaultLanguageHighlighterColors.CLASS_NAME)
        val TS_CONSTANT = createTextAttributesKey("RSX_TS_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT)
        val TS_PROPERTY = createTextAttributesKey("RSX_TS_PROPERTY", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        val TS_STRING = createTextAttributesKey("RSX_TS_STRING", DefaultLanguageHighlighterColors.STRING)
        val TS_NUMBER = createTextAttributesKey("RSX_TS_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val TS_COMMENT = createTextAttributesKey("RSX_TS_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val TS_OPERATOR = createTextAttributesKey("RSX_TS_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val TS_PUNCTUATION = createTextAttributesKey("RSX_TS_PUNCTUATION", DefaultLanguageHighlighterColors.SEMICOLON)

        // ============ CSS highlighting ============
        val CSS_SELECTOR = createTextAttributesKey("RSX_CSS_SELECTOR", DefaultLanguageHighlighterColors.CLASS_NAME)
        val CSS_PROPERTY = createTextAttributesKey("RSX_CSS_PROPERTY", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        val CSS_VALUE = createTextAttributesKey("RSX_CSS_VALUE", DefaultLanguageHighlighterColors.STRING)
        val CSS_NUMBER = createTextAttributesKey("RSX_CSS_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val CSS_COLOR = createTextAttributesKey("RSX_CSS_COLOR", DefaultLanguageHighlighterColors.NUMBER)
        val CSS_COMMENT = createTextAttributesKey("RSX_CSS_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
        val CSS_AT_RULE = createTextAttributesKey("RSX_CSS_AT_RULE", DefaultLanguageHighlighterColors.KEYWORD)
        val CSS_PUNCTUATION = createTextAttributesKey("RSX_CSS_PUNCTUATION", DefaultLanguageHighlighterColors.SEMICOLON)

        // ============ HTML highlighting ============
        val HTML_TAG = createTextAttributesKey("RSX_HTML_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG)
        val HTML_TAG_NAME = createTextAttributesKey("RSX_HTML_TAG_NAME", DefaultLanguageHighlighterColors.MARKUP_TAG)
        val HTML_ATTR_NAME = createTextAttributesKey("RSX_HTML_ATTR_NAME", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE)
        val HTML_ATTR_VALUE = createTextAttributesKey("RSX_HTML_ATTR_VALUE", DefaultLanguageHighlighterColors.STRING)
        val HTML_TEXT = createTextAttributesKey("RSX_HTML_TEXT", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR)
        val HTML_COMMENT = createTextAttributesKey("RSX_HTML_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)

        // ============ Template/Interpolation highlighting ============
        val INTERPOLATION_BRACES = createTextAttributesKey("RSX_INTERPOLATION_BRACES", DefaultLanguageHighlighterColors.BRACES)
        val DIRECTIVE = createTextAttributesKey("RSX_DIRECTIVE", DefaultLanguageHighlighterColors.KEYWORD)
        val IDENTIFIER = createTextAttributesKey("RSX_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
        val NUMBER = createTextAttributesKey("RSX_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val STRING = createTextAttributesKey("RSX_STRING", DefaultLanguageHighlighterColors.STRING)
        val KEYWORD = createTextAttributesKey("RSX_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val OPERATOR = createTextAttributesKey("RSX_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
        val BRACKETS = createTextAttributesKey("RSX_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        val PARENTHESES = createTextAttributesKey("RSX_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
        val DOT = createTextAttributesKey("RSX_DOT", DefaultLanguageHighlighterColors.DOT)
        val COMMA = createTextAttributesKey("RSX_COMMA", DefaultLanguageHighlighterColors.COMMA)
        val BAD_CHARACTER = createTextAttributesKey("RSX_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        // Key arrays
        private val DELIMITER_KEYS = arrayOf(DELIMITER)
        private val SECTION_TAG_KEYS = arrayOf(SECTION_TAG)

        private val RUST_KEYWORD_KEYS = arrayOf(RUST_KEYWORD)
        private val RUST_TYPE_KEYS = arrayOf(RUST_TYPE)
        private val RUST_IDENTIFIER_KEYS = arrayOf(RUST_IDENTIFIER)
        private val RUST_FUNCTION_KEYS = arrayOf(RUST_FUNCTION)
        private val RUST_FUNCTION_CALL_KEYS = arrayOf(RUST_FUNCTION_CALL)
        private val RUST_STRUCT_KEYS = arrayOf(RUST_STRUCT)
        private val RUST_MODULE_KEYS = arrayOf(RUST_MODULE)
        private val RUST_CONSTANT_KEYS = arrayOf(RUST_CONSTANT)
        private val RUST_FIELD_KEYS = arrayOf(RUST_FIELD)
        private val RUST_STRING_KEYS = arrayOf(RUST_STRING)
        private val RUST_NUMBER_KEYS = arrayOf(RUST_NUMBER)
        private val RUST_COMMENT_KEYS = arrayOf(RUST_COMMENT)
        private val RUST_LIFETIME_KEYS = arrayOf(RUST_LIFETIME)
        private val RUST_MACRO_KEYS = arrayOf(RUST_MACRO)
        private val RUST_ATTRIBUTE_KEYS = arrayOf(RUST_ATTRIBUTE)
        private val RUST_OPERATOR_KEYS = arrayOf(RUST_OPERATOR)
        private val RUST_PUNCTUATION_KEYS = arrayOf(RUST_PUNCTUATION)
        private val RUST_SELF_KEYS = arrayOf(RUST_SELF)
        private val RUST_NAMESPACE_KEYS = arrayOf(RUST_NAMESPACE)

        private val TS_KEYWORD_KEYS = arrayOf(TS_KEYWORD)
        private val TS_TYPE_KEYS = arrayOf(TS_TYPE)
        private val TS_IDENTIFIER_KEYS = arrayOf(TS_IDENTIFIER)
        private val TS_FUNCTION_KEYS = arrayOf(TS_FUNCTION)
        private val TS_FUNCTION_CALL_KEYS = arrayOf(TS_FUNCTION_CALL)
        private val TS_CLASS_KEYS = arrayOf(TS_CLASS)
        private val TS_CONSTANT_KEYS = arrayOf(TS_CONSTANT)
        private val TS_PROPERTY_KEYS = arrayOf(TS_PROPERTY)
        private val TS_STRING_KEYS = arrayOf(TS_STRING)
        private val TS_NUMBER_KEYS = arrayOf(TS_NUMBER)
        private val TS_COMMENT_KEYS = arrayOf(TS_COMMENT)
        private val TS_OPERATOR_KEYS = arrayOf(TS_OPERATOR)
        private val TS_PUNCTUATION_KEYS = arrayOf(TS_PUNCTUATION)

        private val CSS_SELECTOR_KEYS = arrayOf(CSS_SELECTOR)
        private val CSS_PROPERTY_KEYS = arrayOf(CSS_PROPERTY)
        private val CSS_VALUE_KEYS = arrayOf(CSS_VALUE)
        private val CSS_NUMBER_KEYS = arrayOf(CSS_NUMBER)
        private val CSS_COLOR_KEYS = arrayOf(CSS_COLOR)
        private val CSS_COMMENT_KEYS = arrayOf(CSS_COMMENT)
        private val CSS_AT_RULE_KEYS = arrayOf(CSS_AT_RULE)
        private val CSS_PUNCTUATION_KEYS = arrayOf(CSS_PUNCTUATION)

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
            TokenType.WHITE_SPACE -> EMPTY_KEYS

            // Section delimiters
            RsxTokenTypes.RUST_DELIMITER -> DELIMITER_KEYS
            RsxTokenTypes.SCRIPT_OPEN, RsxTokenTypes.SCRIPT_CLOSE,
            RsxTokenTypes.TEMPLATE_OPEN, RsxTokenTypes.TEMPLATE_CLOSE,
            RsxTokenTypes.STYLE_OPEN, RsxTokenTypes.STYLE_CLOSE -> SECTION_TAG_KEYS

            // Rust tokens
            RsxTokenTypes.RUST_KEYWORD -> RUST_KEYWORD_KEYS
            RsxTokenTypes.RUST_TYPE -> RUST_TYPE_KEYS
            RsxTokenTypes.RUST_IDENTIFIER -> RUST_IDENTIFIER_KEYS
            RsxTokenTypes.RUST_FUNCTION -> RUST_FUNCTION_KEYS
            RsxTokenTypes.RUST_FUNCTION_CALL -> RUST_FUNCTION_CALL_KEYS
            RsxTokenTypes.RUST_STRUCT -> RUST_STRUCT_KEYS
            RsxTokenTypes.RUST_MODULE -> RUST_MODULE_KEYS
            RsxTokenTypes.RUST_CONSTANT -> RUST_CONSTANT_KEYS
            RsxTokenTypes.RUST_FIELD -> RUST_FIELD_KEYS
            RsxTokenTypes.RUST_STRING -> RUST_STRING_KEYS
            RsxTokenTypes.RUST_NUMBER -> RUST_NUMBER_KEYS
            RsxTokenTypes.RUST_COMMENT -> RUST_COMMENT_KEYS
            RsxTokenTypes.RUST_LIFETIME -> RUST_LIFETIME_KEYS
            RsxTokenTypes.RUST_MACRO -> RUST_MACRO_KEYS
            RsxTokenTypes.RUST_ATTRIBUTE -> RUST_ATTRIBUTE_KEYS
            RsxTokenTypes.RUST_OPERATOR -> RUST_OPERATOR_KEYS
            RsxTokenTypes.RUST_PUNCTUATION -> RUST_PUNCTUATION_KEYS
            RsxTokenTypes.RUST_SELF -> RUST_SELF_KEYS
            RsxTokenTypes.RUST_NAMESPACE -> RUST_NAMESPACE_KEYS

            // TypeScript tokens
            RsxTokenTypes.TS_KEYWORD -> TS_KEYWORD_KEYS
            RsxTokenTypes.TS_TYPE -> TS_TYPE_KEYS
            RsxTokenTypes.TS_IDENTIFIER -> TS_IDENTIFIER_KEYS
            RsxTokenTypes.TS_FUNCTION -> TS_FUNCTION_KEYS
            RsxTokenTypes.TS_FUNCTION_CALL -> TS_FUNCTION_CALL_KEYS
            RsxTokenTypes.TS_CLASS -> TS_CLASS_KEYS
            RsxTokenTypes.TS_CONSTANT -> TS_CONSTANT_KEYS
            RsxTokenTypes.TS_PROPERTY -> TS_PROPERTY_KEYS
            RsxTokenTypes.TS_STRING -> TS_STRING_KEYS
            RsxTokenTypes.TS_NUMBER -> TS_NUMBER_KEYS
            RsxTokenTypes.TS_COMMENT -> TS_COMMENT_KEYS
            RsxTokenTypes.TS_OPERATOR -> TS_OPERATOR_KEYS
            RsxTokenTypes.TS_PUNCTUATION -> TS_PUNCTUATION_KEYS

            // CSS tokens
            RsxTokenTypes.CSS_SELECTOR -> CSS_SELECTOR_KEYS
            RsxTokenTypes.CSS_PROPERTY -> CSS_PROPERTY_KEYS
            RsxTokenTypes.CSS_VALUE -> CSS_VALUE_KEYS
            RsxTokenTypes.CSS_NUMBER -> CSS_NUMBER_KEYS
            RsxTokenTypes.CSS_COLOR -> CSS_COLOR_KEYS
            RsxTokenTypes.CSS_COMMENT -> CSS_COMMENT_KEYS
            RsxTokenTypes.CSS_AT_RULE -> CSS_AT_RULE_KEYS
            RsxTokenTypes.CSS_PUNCTUATION -> CSS_PUNCTUATION_KEYS

            // HTML tokens
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
            RsxTokenTypes.LBRACKET, RsxTokenTypes.RBRACKET,
            RsxTokenTypes.LBRACE, RsxTokenTypes.RBRACE -> BRACKETS_KEYS
            RsxTokenTypes.LPAREN, RsxTokenTypes.RPAREN -> PARENTHESES_KEYS

            // Punctuation
            RsxTokenTypes.DOT -> DOT_KEYS
            RsxTokenTypes.COMMA -> COMMA_KEYS
            RsxTokenTypes.SEMICOLON -> RUST_PUNCTUATION_KEYS

            // Bad character
            TokenType.BAD_CHARACTER -> BAD_CHARACTER_KEYS

            else -> EMPTY_KEYS
        }
    }
}
