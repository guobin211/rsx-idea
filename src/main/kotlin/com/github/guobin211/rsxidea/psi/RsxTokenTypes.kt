package com.github.guobin211.rsxidea.psi

import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet

object RsxTokenTypes {
    // Section delimiters
    @JvmField val RUST_DELIMITER = RsxTokenType("RUST_DELIMITER")           // ---
    @JvmField val SCRIPT_OPEN = RsxTokenType("SCRIPT_OPEN")                 // <script>
    @JvmField val SCRIPT_CLOSE = RsxTokenType("SCRIPT_CLOSE")               // </script>
    @JvmField val TEMPLATE_OPEN = RsxTokenType("TEMPLATE_OPEN")             // <template>
    @JvmField val TEMPLATE_CLOSE = RsxTokenType("TEMPLATE_CLOSE")           // </template>
    @JvmField val STYLE_OPEN = RsxTokenType("STYLE_OPEN")                   // <style>
    @JvmField val STYLE_CLOSE = RsxTokenType("STYLE_CLOSE")                 // </style>

    // Section content
    @JvmField val RUST_CODE = RsxTokenType("RUST_CODE")
    @JvmField val SCRIPT_CODE = RsxTokenType("SCRIPT_CODE")
    @JvmField val STYLE_CODE = RsxTokenType("STYLE_CODE")

    // Template tokens
    @JvmField val HTML_TAG_OPEN = RsxTokenType("HTML_TAG_OPEN")             // <
    @JvmField val HTML_TAG_CLOSE = RsxTokenType("HTML_TAG_CLOSE")           // >
    @JvmField val HTML_TAG_END_OPEN = RsxTokenType("HTML_TAG_END_OPEN")     // </
    @JvmField val HTML_SELF_CLOSE = RsxTokenType("HTML_SELF_CLOSE")         // />
    @JvmField val HTML_TAG_NAME = RsxTokenType("HTML_TAG_NAME")
    @JvmField val HTML_ATTR_NAME = RsxTokenType("HTML_ATTR_NAME")
    @JvmField val HTML_ATTR_VALUE = RsxTokenType("HTML_ATTR_VALUE")
    @JvmField val HTML_TEXT = RsxTokenType("HTML_TEXT")
    @JvmField val HTML_COMMENT = RsxTokenType("HTML_COMMENT")

    // Interpolation
    @JvmField val INTERPOLATION_OPEN = RsxTokenType("INTERPOLATION_OPEN")   // {{
    @JvmField val INTERPOLATION_CLOSE = RsxTokenType("INTERPOLATION_CLOSE") // }}

    // Directives
    @JvmField val DIRECTIVE_IF = RsxTokenType("DIRECTIVE_IF")               // @if
    @JvmField val DIRECTIVE_ELSE_IF = RsxTokenType("DIRECTIVE_ELSE_IF")     // :else if, :elseif
    @JvmField val DIRECTIVE_ELSE = RsxTokenType("DIRECTIVE_ELSE")           // :else
    @JvmField val DIRECTIVE_END_IF = RsxTokenType("DIRECTIVE_END_IF")       // /if
    @JvmField val DIRECTIVE_EACH = RsxTokenType("DIRECTIVE_EACH")           // @each
    @JvmField val DIRECTIVE_END_EACH = RsxTokenType("DIRECTIVE_END_EACH")   // /each
    @JvmField val DIRECTIVE_HTML = RsxTokenType("DIRECTIVE_HTML")           // @html

    // Expression tokens
    @JvmField val IDENTIFIER = RsxTokenType("IDENTIFIER")
    @JvmField val NUMBER = RsxTokenType("NUMBER")
    @JvmField val STRING = RsxTokenType("STRING")
    @JvmField val BOOLEAN = RsxTokenType("BOOLEAN")

    // Operators
    @JvmField val DOT = RsxTokenType("DOT")                                 // .
    @JvmField val COMMA = RsxTokenType("COMMA")                             // ,
    @JvmField val LPAREN = RsxTokenType("LPAREN")                           // (
    @JvmField val RPAREN = RsxTokenType("RPAREN")                           // )
    @JvmField val LBRACKET = RsxTokenType("LBRACKET")                       // [
    @JvmField val RBRACKET = RsxTokenType("RBRACKET")                       // ]
    @JvmField val QUESTION = RsxTokenType("QUESTION")                       // ?
    @JvmField val COLON = RsxTokenType("COLON")                             // :
    @JvmField val EQ = RsxTokenType("EQ")                                   // =

    // Comparison operators
    @JvmField val GT = RsxTokenType("GT")                                   // >
    @JvmField val LT = RsxTokenType("LT")                                   // <
    @JvmField val GE = RsxTokenType("GE")                                   // >=
    @JvmField val LE = RsxTokenType("LE")                                   // <=
    @JvmField val EQEQ = RsxTokenType("EQEQ")                               // ==
    @JvmField val NE = RsxTokenType("NE")                                   // !=
    @JvmField val EQEQEQ = RsxTokenType("EQEQEQ")                           // ===
    @JvmField val NEE = RsxTokenType("NEE")                                 // !==

    // Logical operators
    @JvmField val AND = RsxTokenType("AND")                                 // &&
    @JvmField val OR = RsxTokenType("OR")                                   // ||
    @JvmField val NOT = RsxTokenType("NOT")                                 // !

    // Arithmetic operators
    @JvmField val PLUS = RsxTokenType("PLUS")                               // +
    @JvmField val MINUS = RsxTokenType("MINUS")                             // -
    @JvmField val MUL = RsxTokenType("MUL")                                 // *
    @JvmField val DIV = RsxTokenType("DIV")                                 // /
    @JvmField val MOD = RsxTokenType("MOD")                                 // %

    // Keywords
    @JvmField val AS = RsxTokenType("AS")                                   // as

    // Whitespace and others - use platform WHITE_SPACE
    @JvmField val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE
    @JvmField val BAD_CHARACTER: IElementType = TokenType.BAD_CHARACTER

    // Token sets
    @JvmField val COMMENTS = TokenSet.create(HTML_COMMENT)
    @JvmField val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
    @JvmField val STRINGS = TokenSet.create(STRING, HTML_ATTR_VALUE)
    @JvmField val KEYWORDS = TokenSet.create(
        DIRECTIVE_IF, DIRECTIVE_ELSE_IF, DIRECTIVE_ELSE, DIRECTIVE_END_IF,
        DIRECTIVE_EACH, DIRECTIVE_END_EACH, DIRECTIVE_HTML, AS, BOOLEAN
    )
    @JvmField val OPERATORS = TokenSet.create(
        GT, LT, GE, LE, EQEQ, NE, EQEQEQ, NEE,
        AND, OR, NOT,
        PLUS, MINUS, MUL, DIV, MOD
    )
    @JvmField val BRACKETS = TokenSet.create(
        LPAREN, RPAREN, LBRACKET, RBRACKET,
        INTERPOLATION_OPEN, INTERPOLATION_CLOSE,
        HTML_TAG_OPEN, HTML_TAG_CLOSE, HTML_TAG_END_OPEN, HTML_SELF_CLOSE
    )
}
