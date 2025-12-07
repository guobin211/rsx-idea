package com.github.guobin211.rsxidea

import com.github.guobin211.rsxidea.lexer.RsxLexer
import com.github.guobin211.rsxidea.psi.RsxTokenTypes
import com.intellij.psi.TokenType
import org.junit.Assert.*
import org.junit.Test

class RsxLexerTest {

    private fun tokenize(code: String): List<Pair<String, String>> {
        val lexer = RsxLexer()
        val tokens = mutableListOf<Pair<String, String>>()
        
        lexer.start(code, 0, code.length, 0)
        while (lexer.tokenType != null) {
            val tokenText = code.substring(lexer.tokenStart, lexer.tokenEnd)
            tokens.add(lexer.tokenType.toString() to tokenText)
            lexer.advance()
        }
        return tokens
    }

    @Test
    fun testRustSection() {
        val code = """---
use rsx::Request;
---"""
        val tokens = tokenize(code)
        
        assertTrue("Should have RUST_DELIMITER", tokens.any { it.first.contains("RUST_DELIMITER") })
        assertTrue("Should have RUST_CODE", tokens.any { it.first.contains("RUST_CODE") })
    }

    @Test
    fun testTemplateSection() {
        val code = "<template><div>Hello</div></template>"
        val tokens = tokenize(code)
        
        assertTrue("Should have TEMPLATE_OPEN", tokens.any { it.first.contains("TEMPLATE_OPEN") })
        assertTrue("Should have HTML_TAG_NAME", tokens.any { it.first.contains("HTML_TAG_NAME") })
        assertTrue("Should have TEMPLATE_CLOSE", tokens.any { it.first.contains("TEMPLATE_CLOSE") })
    }

    @Test
    fun testInterpolation() {
        val code = "<template>{{ message }}</template>"
        val tokens = tokenize(code)
        
        assertTrue("Should have INTERPOLATION_OPEN", tokens.any { it.first.contains("INTERPOLATION_OPEN") })
        assertTrue("Should have IDENTIFIER", tokens.any { it.first.contains("IDENTIFIER") })
        assertTrue("Should have INTERPOLATION_CLOSE", tokens.any { it.first.contains("INTERPOLATION_CLOSE") })
    }

    @Test
    fun testDirectiveIf() {
        val code = "<template>{{@if condition}}content{{/if}}</template>"
        val tokens = tokenize(code)
        
        assertTrue("Should have DIRECTIVE_IF", tokens.any { it.first.contains("DIRECTIVE_IF") })
        assertTrue("Should have DIRECTIVE_END_IF", tokens.any { it.first.contains("DIRECTIVE_END_IF") })
    }

    @Test
    fun testDirectiveEach() {
        val code = "<template>{{@each items as item}}{{ item }}{{/each}}</template>"
        val tokens = tokenize(code)
        
        assertTrue("Should have DIRECTIVE_EACH", tokens.any { it.first.contains("DIRECTIVE_EACH") })
        assertTrue("Should have AS keyword", tokens.any { it.first.contains("AS") })
        assertTrue("Should have DIRECTIVE_END_EACH", tokens.any { it.first.contains("DIRECTIVE_END_EACH") })
    }

    @Test
    fun testHtmlAttributes() {
        val code = "<template><div class=\"container\" id=\"main\">text</div></template>"
        val tokens = tokenize(code)
        
        assertTrue("Should have HTML_ATTR_NAME", tokens.any { it.first.contains("HTML_ATTR_NAME") })
        assertTrue("Should have HTML_ATTR_VALUE", tokens.any { it.first.contains("HTML_ATTR_VALUE") })
    }

    @Test
    fun testExpressionOperators() {
        val code = "<template>{{ a > b && c <= d }}</template>"
        val tokens = tokenize(code)
        
        assertTrue("Should have GT", tokens.any { it.first.contains("GT") })
        assertTrue("Should have AND", tokens.any { it.first.contains("AND") })
        assertTrue("Should have LE", tokens.any { it.first.contains("LE") })
    }

    @Test
    fun testTernaryExpression() {
        val code = "<template>{{ condition ? 'yes' : 'no' }}</template>"
        val tokens = tokenize(code)
        
        assertTrue("Should have QUESTION", tokens.any { it.first.contains("QUESTION") })
        assertTrue("Should have COLON", tokens.any { it.first.contains("COLON") })
        assertTrue("Should have STRING", tokens.any { it.first.contains("STRING") })
    }

    @Test
    fun testHtmlComment() {
        val code = "<template><!-- comment --></template>"
        val tokens = tokenize(code)
        
        assertTrue("Should have HTML_COMMENT", tokens.any { it.first.contains("HTML_COMMENT") })
    }

    @Test
    fun testSelfClosingTag() {
        val code = "<template><img src=\"test.png\" /></template>"
        val tokens = tokenize(code)
        
        assertTrue("Should have HTML_SELF_CLOSE", tokens.any { it.first.contains("HTML_SELF_CLOSE") })
    }

    @Test
    fun testScriptSection() {
        val code = "<script>const x = 1;</script>"
        val tokens = tokenize(code)
        
        assertTrue("Should have SCRIPT_OPEN", tokens.any { it.first.contains("SCRIPT_OPEN") })
        assertTrue("Should have SCRIPT_CODE", tokens.any { it.first.contains("SCRIPT_CODE") })
        assertTrue("Should have SCRIPT_CLOSE", tokens.any { it.first.contains("SCRIPT_CLOSE") })
    }

    @Test
    fun testStyleSection() {
        val code = "<style>.class { color: red; }</style>"
        val tokens = tokenize(code)
        
        assertTrue("Should have STYLE_OPEN", tokens.any { it.first.contains("STYLE_OPEN") })
        assertTrue("Should have STYLE_CODE", tokens.any { it.first.contains("STYLE_CODE") })
        assertTrue("Should have STYLE_CLOSE", tokens.any { it.first.contains("STYLE_CLOSE") })
    }

    @Test
    fun testCompleteFile() {
        val code = """---
use rsx::Request;
---

<script>
const x = 1;
</script>

<template>
    <div class="container">
        {{ message }}
        {{@if condition}}
            <p>Yes</p>
        {{/if}}
    </div>
</template>

<style>
.container { padding: 20px; }
</style>"""
        
        val tokens = tokenize(code)
        
        // Verify all section types are present
        assertTrue("Should have RUST_DELIMITER", tokens.any { it.first.contains("RUST_DELIMITER") })
        assertTrue("Should have SCRIPT_OPEN", tokens.any { it.first.contains("SCRIPT_OPEN") })
        assertTrue("Should have TEMPLATE_OPEN", tokens.any { it.first.contains("TEMPLATE_OPEN") })
        assertTrue("Should have STYLE_OPEN", tokens.any { it.first.contains("STYLE_OPEN") })
        
        // Verify template content
        assertTrue("Should have HTML_TAG_NAME", tokens.any { it.first.contains("HTML_TAG_NAME") })
        assertTrue("Should have INTERPOLATION_OPEN", tokens.any { it.first.contains("INTERPOLATION_OPEN") })
        assertTrue("Should have DIRECTIVE_IF", tokens.any { it.first.contains("DIRECTIVE_IF") })
    }
}
