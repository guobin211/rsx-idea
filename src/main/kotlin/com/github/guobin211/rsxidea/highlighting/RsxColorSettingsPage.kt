package com.github.guobin211.rsxidea.highlighting

import com.github.guobin211.rsxidea.filetype.RsxIcons
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class RsxColorSettingsPage : ColorSettingsPage {
    companion object {
        private val DESCRIPTORS = arrayOf(
            // Section
            AttributesDescriptor("Section//Delimiter (---)", RsxSyntaxHighlighter.DELIMITER),
            AttributesDescriptor("Section//Section tag", RsxSyntaxHighlighter.SECTION_TAG),

            // Rust
            AttributesDescriptor("Rust//Keyword", RsxSyntaxHighlighter.RUST_KEYWORD),
            AttributesDescriptor("Rust//Type", RsxSyntaxHighlighter.RUST_TYPE),
            AttributesDescriptor("Rust//Identifier", RsxSyntaxHighlighter.RUST_IDENTIFIER),
            AttributesDescriptor("Rust//String", RsxSyntaxHighlighter.RUST_STRING),
            AttributesDescriptor("Rust//Number", RsxSyntaxHighlighter.RUST_NUMBER),
            AttributesDescriptor("Rust//Comment", RsxSyntaxHighlighter.RUST_COMMENT),
            AttributesDescriptor("Rust//Lifetime", RsxSyntaxHighlighter.RUST_LIFETIME),
            AttributesDescriptor("Rust//Macro", RsxSyntaxHighlighter.RUST_MACRO),
            AttributesDescriptor("Rust//Attribute", RsxSyntaxHighlighter.RUST_ATTRIBUTE),
            AttributesDescriptor("Rust//Operator", RsxSyntaxHighlighter.RUST_OPERATOR),
            AttributesDescriptor("Rust//Punctuation", RsxSyntaxHighlighter.RUST_PUNCTUATION),

            // TypeScript
            AttributesDescriptor("TypeScript//Keyword", RsxSyntaxHighlighter.TS_KEYWORD),
            AttributesDescriptor("TypeScript//Type", RsxSyntaxHighlighter.TS_TYPE),
            AttributesDescriptor("TypeScript//Identifier", RsxSyntaxHighlighter.TS_IDENTIFIER),
            AttributesDescriptor("TypeScript//String", RsxSyntaxHighlighter.TS_STRING),
            AttributesDescriptor("TypeScript//Number", RsxSyntaxHighlighter.TS_NUMBER),
            AttributesDescriptor("TypeScript//Comment", RsxSyntaxHighlighter.TS_COMMENT),
            AttributesDescriptor("TypeScript//Operator", RsxSyntaxHighlighter.TS_OPERATOR),
            AttributesDescriptor("TypeScript//Punctuation", RsxSyntaxHighlighter.TS_PUNCTUATION),

            // CSS
            AttributesDescriptor("CSS//Selector", RsxSyntaxHighlighter.CSS_SELECTOR),
            AttributesDescriptor("CSS//Property", RsxSyntaxHighlighter.CSS_PROPERTY),
            AttributesDescriptor("CSS//Value", RsxSyntaxHighlighter.CSS_VALUE),
            AttributesDescriptor("CSS//Number", RsxSyntaxHighlighter.CSS_NUMBER),
            AttributesDescriptor("CSS//Color", RsxSyntaxHighlighter.CSS_COLOR),
            AttributesDescriptor("CSS//Comment", RsxSyntaxHighlighter.CSS_COMMENT),
            AttributesDescriptor("CSS//At-rule", RsxSyntaxHighlighter.CSS_AT_RULE),
            AttributesDescriptor("CSS//Punctuation", RsxSyntaxHighlighter.CSS_PUNCTUATION),

            // HTML
            AttributesDescriptor("HTML//Tag brackets", RsxSyntaxHighlighter.HTML_TAG),
            AttributesDescriptor("HTML//Tag name", RsxSyntaxHighlighter.HTML_TAG_NAME),
            AttributesDescriptor("HTML//Attribute name", RsxSyntaxHighlighter.HTML_ATTR_NAME),
            AttributesDescriptor("HTML//Attribute value", RsxSyntaxHighlighter.HTML_ATTR_VALUE),
            AttributesDescriptor("HTML//Text", RsxSyntaxHighlighter.HTML_TEXT),
            AttributesDescriptor("HTML//Comment", RsxSyntaxHighlighter.HTML_COMMENT),

            // Template
            AttributesDescriptor("Template//Interpolation braces", RsxSyntaxHighlighter.INTERPOLATION_BRACES),
            AttributesDescriptor("Template//Directive", RsxSyntaxHighlighter.DIRECTIVE),

            // Expression
            AttributesDescriptor("Expression//Identifier", RsxSyntaxHighlighter.IDENTIFIER),
            AttributesDescriptor("Expression//Number", RsxSyntaxHighlighter.NUMBER),
            AttributesDescriptor("Expression//String", RsxSyntaxHighlighter.STRING),
            AttributesDescriptor("Expression//Keyword", RsxSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Expression//Operator", RsxSyntaxHighlighter.OPERATOR),
            AttributesDescriptor("Expression//Brackets", RsxSyntaxHighlighter.BRACKETS),
            AttributesDescriptor("Expression//Parentheses", RsxSyntaxHighlighter.PARENTHESES),
            AttributesDescriptor("Expression//Dot", RsxSyntaxHighlighter.DOT),
            AttributesDescriptor("Expression//Comma", RsxSyntaxHighlighter.COMMA),

            // Other
            AttributesDescriptor("Bad character", RsxSyntaxHighlighter.BAD_CHARACTER),
        )
    }

    override fun getIcon(): Icon = RsxIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = RsxSyntaxHighlighter()

    override fun getDemoText(): String = """---
// Rust section with syntax highlighting
use rsx::{Request, Response};

#[derive(Debug)]
struct User {
    name: String,
    age: u32,
}

async fn get_server_side_props(req: Request) -> Response<'static> {
    let message = "Hello, World!";
    let count = 42;
    println!("Count: {}", count);
    Response::json!({ "message": message })
}
---

<script>
// TypeScript section with syntax highlighting
import { ref, computed } from 'vue'

interface Props {
    message: string
    count: number
}

const { message, count } = defineProps<Props>()
const items: Array<string> = ['a', 'b', 'c']
const isLoggedIn = ref(true)
const user = { name: 'John', age: 25 }

function handleClick(): void {
    console.log('clicked')
}
</script>

<template>
    <div class="container">
        <!-- This is an HTML comment -->
        <h1>{{ message }}</h1>
        <p id="intro">Welcome to RSX!</p>
        {{@if isLoggedIn}}
            <span>Hello, {{ user.name }}!</span>
        {{:else if count > 0}}
            <span>Count: {{ count }}</span>
        {{:else}}
            <span>Please log in.</span>
        {{/if}}
        {{@each items as item, index}}
            <li>{{ index + 1 }}: {{ item }}</li>
        {{/each}}
        <button onclick="handleClick">Click me</button>
        <input type="text" value="{{ message }}" />
        <img src="/logo.png" alt="Logo" />
    </div>
</template>

<style>
/* CSS section with syntax highlighting */
@import url('https://fonts.googleapis.com/css');

.container {
    padding: 20px;
    margin: 0 auto;
    max-width: 800px;
    background-color: #f5f5f5;
    border-radius: 8px;
}

#intro {
    color: #333;
    font-size: 1.5rem;
    line-height: 1.6;
}

h1 {
    color: #2196F3;
    font-weight: bold;
}

button:hover {
    background: rgba(0, 0, 0, 0.1);
}
</style>"""

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "RSX"
}
