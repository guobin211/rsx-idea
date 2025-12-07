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
            AttributesDescriptor("Section//Delimiter (---)", RsxSyntaxHighlighter.DELIMITER),
            AttributesDescriptor("Section//Section tag", RsxSyntaxHighlighter.SECTION_TAG),
            AttributesDescriptor("Section//Rust code", RsxSyntaxHighlighter.RUST_CODE),
            AttributesDescriptor("Section//Script code", RsxSyntaxHighlighter.SCRIPT_CODE),
            AttributesDescriptor("Section//Style code", RsxSyntaxHighlighter.STYLE_CODE),
            AttributesDescriptor("HTML//Tag brackets", RsxSyntaxHighlighter.HTML_TAG),
            AttributesDescriptor("HTML//Tag name", RsxSyntaxHighlighter.HTML_TAG_NAME),
            AttributesDescriptor("HTML//Attribute name", RsxSyntaxHighlighter.HTML_ATTR_NAME),
            AttributesDescriptor("HTML//Attribute value", RsxSyntaxHighlighter.HTML_ATTR_VALUE),
            AttributesDescriptor("HTML//Text", RsxSyntaxHighlighter.HTML_TEXT),
            AttributesDescriptor("HTML//Comment", RsxSyntaxHighlighter.HTML_COMMENT),
            AttributesDescriptor("Template//Interpolation braces", RsxSyntaxHighlighter.INTERPOLATION_BRACES),
            AttributesDescriptor("Template//Directive", RsxSyntaxHighlighter.DIRECTIVE),
            AttributesDescriptor("Expression//Identifier", RsxSyntaxHighlighter.IDENTIFIER),
            AttributesDescriptor("Expression//Number", RsxSyntaxHighlighter.NUMBER),
            AttributesDescriptor("Expression//String", RsxSyntaxHighlighter.STRING),
            AttributesDescriptor("Expression//Keyword", RsxSyntaxHighlighter.KEYWORD),
            AttributesDescriptor("Expression//Operator", RsxSyntaxHighlighter.OPERATOR),
            AttributesDescriptor("Expression//Brackets", RsxSyntaxHighlighter.BRACKETS),
            AttributesDescriptor("Expression//Parentheses", RsxSyntaxHighlighter.PARENTHESES),
            AttributesDescriptor("Expression//Dot", RsxSyntaxHighlighter.DOT),
            AttributesDescriptor("Expression//Comma", RsxSyntaxHighlighter.COMMA),
            AttributesDescriptor("Bad character", RsxSyntaxHighlighter.BAD_CHARACTER),
        )
    }

    override fun getIcon(): Icon = RsxIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = RsxSyntaxHighlighter()

    override fun getDemoText(): String = """---
use rsx::{Request, Response};

async fn get_server_side_props(req: Request) -> Response {
    Response::json!({ "message": "Hello" })
}
---

<script>
const { message } = defineProps<{ message: string }>()
</script>

<template>
    <div class="container">
        <!-- This is a comment -->
        <h1>{{ message }}</h1>
        {{@if isLoggedIn}}
            <p>Welcome, {{ user.name }}!</p>
        {{:else}}
            <p>Please log in.</p>
        {{/if}}
        {{@each items as item, index}}
            <li>{{ index }}: {{ item.name }}</li>
        {{/each}}
        <span>{{ count > 0 ? 'Has items' : 'Empty' }}</span>
    </div>
</template>

<style>
.container {
    padding: 20px;
    text-align: center;
}
</style>"""

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "RSX"
}
