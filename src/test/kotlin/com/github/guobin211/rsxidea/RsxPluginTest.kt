package com.github.guobin211.rsxidea

import com.github.guobin211.rsxidea.filetype.RsxFileType
import com.github.guobin211.rsxidea.language.RsxLanguage
import com.github.guobin211.rsxidea.lexer.RsxLexer
import com.github.guobin211.rsxidea.psi.RsxFile
import com.github.guobin211.rsxidea.psi.RsxTokenTypes
import com.intellij.psi.TokenType
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class RsxPluginTest : BasePlatformTestCase() {

    fun testRsxFileTypeRegistered() {
        // Test that file type is properly configured
        assertEquals("RSX", RsxFileType.name)
        assertEquals("rsx", RsxFileType.defaultExtension)
        assertEquals(RsxLanguage, RsxFileType.language)
    }

    fun testLexerBasicStructure() {
        val lexer = RsxLexer()
        val code = """---
use rsx::Request;
---"""

        lexer.start(code, 0, code.length, 0)

        // Verify first token is RUST_DELIMITER
        assertEquals(RsxTokenTypes.RUST_DELIMITER, lexer.tokenType)
    }

    fun testLexerInterpolation() {
        val lexer = RsxLexer()
        val code = "<template>{{ user.name }}</template>"

        lexer.start(code, 0, code.length, 0)

        // Find interpolation tokens
        var foundInterpolation = false
        while (lexer.tokenType != null) {
            if (lexer.tokenType == RsxTokenTypes.INTERPOLATION_OPEN) {
                foundInterpolation = true
                break
            }
            lexer.advance()
        }

        assertTrue("Should find interpolation open token", foundInterpolation)
    }

    fun testLexerDirectives() {
        val lexer = RsxLexer()
        val code = "<template>{{@if condition}}content{{/if}}</template>"

        lexer.start(code, 0, code.length, 0)

        var foundDirectiveIf = false
        while (lexer.tokenType != null) {
            if (lexer.tokenType == RsxTokenTypes.DIRECTIVE_IF) {
                foundDirectiveIf = true
                break
            }
            lexer.advance()
        }

        assertTrue("Should find @if directive token", foundDirectiveIf)
    }

    fun testLexerHtmlTags() {
        val lexer = RsxLexer()
        val code = "<template><div class=\"container\">text</div></template>"

        lexer.start(code, 0, code.length, 0)

        var foundTagName = false
        var foundAttrName = false
        var foundAttrValue = false

        while (lexer.tokenType != null) {
            when (lexer.tokenType) {
                RsxTokenTypes.HTML_TAG_NAME -> foundTagName = true
                RsxTokenTypes.HTML_ATTR_NAME -> foundAttrName = true
                RsxTokenTypes.HTML_ATTR_VALUE -> foundAttrValue = true
            }
            lexer.advance()
        }

        assertTrue("Should find HTML tag name", foundTagName)
        assertTrue("Should find HTML attribute name", foundAttrName)
        assertTrue("Should find HTML attribute value", foundAttrValue)
    }

    fun testLexerExpressionOperators() {
        val lexer = RsxLexer()
        val code = "<template>{{ a > b && c <= d }}</template>"

        lexer.start(code, 0, code.length, 0)

        var foundGT = false
        var foundAND = false
        var foundLE = false

        while (lexer.tokenType != null) {
            when (lexer.tokenType) {
                RsxTokenTypes.GT -> foundGT = true
                RsxTokenTypes.AND -> foundAND = true
                RsxTokenTypes.LE -> foundLE = true
            }
            lexer.advance()
        }

        assertTrue("Should find > operator", foundGT)
        assertTrue("Should find && operator", foundAND)
        assertTrue("Should find <= operator", foundLE)
    }

    fun testLexerWhitespace() {
        val lexer = RsxLexer()
        val code = "<template>  <div>  </div>  </template>"

        lexer.start(code, 0, code.length, 0)

        var foundWhitespace = false
        while (lexer.tokenType != null) {
            if (lexer.tokenType == TokenType.WHITE_SPACE) {
                foundWhitespace = true
                break
            }
            lexer.advance()
        }

        assertTrue("Should find WHITE_SPACE token", foundWhitespace)
    }

    fun testLexerNoGaps() {
        val lexer = RsxLexer()
        val code = "<template><div class=\"test\">{{ message }}</div></template>"

        lexer.start(code, 0, code.length, 0)

        var lastEnd = 0
        while (lexer.tokenType != null) {
            assertEquals("Token should start where previous ended at position $lastEnd", lastEnd, lexer.tokenStart)
            lastEnd = lexer.tokenEnd
            lexer.advance()
        }

        assertEquals("Should cover entire input", code.length, lastEnd)
    }

    fun testLexerElseIfDirective() {
        val lexer = RsxLexer()
        val code = "<template>{{:else if condition}}</template>"

        lexer.start(code, 0, code.length, 0)

        var foundElseIf = false
        while (lexer.tokenType != null) {
            if (lexer.tokenType == RsxTokenTypes.DIRECTIVE_ELSE_IF) {
                foundElseIf = true
                break
            }
            lexer.advance()
        }

        assertTrue("Should find :else if directive token", foundElseIf)
    }

    fun testLexerEachDirective() {
        val lexer = RsxLexer()
        val code = "<template>{{@each items as item}}{{ item }}{{/each}}</template>"

        lexer.start(code, 0, code.length, 0)

        var foundEach = false
        var foundAs = false
        var foundEndEach = false

        while (lexer.tokenType != null) {
            when (lexer.tokenType) {
                RsxTokenTypes.DIRECTIVE_EACH -> foundEach = true
                RsxTokenTypes.AS -> foundAs = true
                RsxTokenTypes.DIRECTIVE_END_EACH -> foundEndEach = true
            }
            lexer.advance()
        }

        assertTrue("Should find @each directive", foundEach)
        assertTrue("Should find 'as' keyword", foundAs)
        assertTrue("Should find /each directive", foundEndEach)
    }

    fun testLexerHtmlComment() {
        val lexer = RsxLexer()
        val code = "<template><!-- this is a comment --></template>"

        lexer.start(code, 0, code.length, 0)

        var foundComment = false
        while (lexer.tokenType != null) {
            if (lexer.tokenType == RsxTokenTypes.HTML_COMMENT) {
                foundComment = true
                break
            }
            lexer.advance()
        }

        assertTrue("Should find HTML comment", foundComment)
    }

    fun testLexerSelfClosingTag() {
        val lexer = RsxLexer()
        val code = "<template><img src=\"test.png\" /></template>"

        lexer.start(code, 0, code.length, 0)

        var foundSelfClose = false
        while (lexer.tokenType != null) {
            if (lexer.tokenType == RsxTokenTypes.HTML_SELF_CLOSE) {
                foundSelfClose = true
                break
            }
            lexer.advance()
        }

        assertTrue("Should find self-closing tag", foundSelfClose)
    }

    override fun getTestDataPath() = "src/test/testData"
}
