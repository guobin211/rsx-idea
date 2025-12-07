package com.github.guobin211.rsxidea.parser

import com.github.guobin211.rsxidea.psi.RsxElementType
import com.github.guobin211.rsxidea.psi.RsxTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType

/**
 * RSX Parser - Builds PSI tree from tokens
 */
class RsxParser : PsiParser {
    companion object {
        val RSX_FILE = RsxElementType("RSX_FILE")
        val RUST_SECTION = RsxElementType("RUST_SECTION")
        val SCRIPT_SECTION = RsxElementType("SCRIPT_SECTION")
        val TEMPLATE_SECTION = RsxElementType("TEMPLATE_SECTION")
        val STYLE_SECTION = RsxElementType("STYLE_SECTION")
        val HTML_ELEMENT = RsxElementType("HTML_ELEMENT")
        val HTML_ATTRIBUTE = RsxElementType("HTML_ATTRIBUTE")
        val INTERPOLATION = RsxElementType("INTERPOLATION")
        val DIRECTIVE = RsxElementType("DIRECTIVE")
        val EXPRESSION = RsxElementType("EXPRESSION")
    }

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        
        while (!builder.eof()) {
            when (builder.tokenType) {
                RsxTokenTypes.RUST_DELIMITER -> parseRustSection(builder)
                RsxTokenTypes.SCRIPT_OPEN -> parseScriptSection(builder)
                RsxTokenTypes.TEMPLATE_OPEN -> parseTemplateSection(builder)
                RsxTokenTypes.STYLE_OPEN -> parseStyleSection(builder)
                else -> builder.advanceLexer()
            }
        }
        
        rootMarker.done(root)
        return builder.treeBuilt
    }

    private fun parseRustSection(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.advanceLexer() // consume opening ---
        
        while (!builder.eof() && builder.tokenType != RsxTokenTypes.RUST_DELIMITER) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == RsxTokenTypes.RUST_DELIMITER) {
            builder.advanceLexer() // consume closing ---
        }
        
        marker.done(RUST_SECTION)
    }

    private fun parseScriptSection(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.advanceLexer() // consume <script>
        
        while (!builder.eof() && builder.tokenType != RsxTokenTypes.SCRIPT_CLOSE) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == RsxTokenTypes.SCRIPT_CLOSE) {
            builder.advanceLexer()
        }
        
        marker.done(SCRIPT_SECTION)
    }

    private fun parseTemplateSection(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.advanceLexer() // consume <template>
        
        while (!builder.eof() && builder.tokenType != RsxTokenTypes.TEMPLATE_CLOSE) {
            when (builder.tokenType) {
                RsxTokenTypes.HTML_TAG_OPEN -> parseHtmlElement(builder)
                RsxTokenTypes.INTERPOLATION_OPEN -> parseInterpolation(builder)
                else -> builder.advanceLexer()
            }
        }
        
        if (builder.tokenType == RsxTokenTypes.TEMPLATE_CLOSE) {
            builder.advanceLexer()
        }
        
        marker.done(TEMPLATE_SECTION)
    }

    private fun parseStyleSection(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.advanceLexer() // consume <style>
        
        while (!builder.eof() && builder.tokenType != RsxTokenTypes.STYLE_CLOSE) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == RsxTokenTypes.STYLE_CLOSE) {
            builder.advanceLexer()
        }
        
        marker.done(STYLE_SECTION)
    }

    private fun parseHtmlElement(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.advanceLexer() // consume <
        
        // Tag name
        if (builder.tokenType == RsxTokenTypes.HTML_TAG_NAME) {
            builder.advanceLexer()
        }
        
        // Attributes
        while (!builder.eof() && 
               builder.tokenType != RsxTokenTypes.HTML_TAG_CLOSE &&
               builder.tokenType != RsxTokenTypes.HTML_SELF_CLOSE) {
            if (builder.tokenType == RsxTokenTypes.HTML_ATTR_NAME) {
                parseHtmlAttribute(builder)
            } else {
                builder.advanceLexer()
            }
        }
        
        // Close tag
        if (builder.tokenType == RsxTokenTypes.HTML_TAG_CLOSE ||
            builder.tokenType == RsxTokenTypes.HTML_SELF_CLOSE) {
            builder.advanceLexer()
        }
        
        marker.done(HTML_ELEMENT)
    }

    private fun parseHtmlAttribute(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.advanceLexer() // consume attr name
        
        if (builder.tokenType == RsxTokenTypes.EQ) {
            builder.advanceLexer()
            if (builder.tokenType == RsxTokenTypes.HTML_ATTR_VALUE) {
                builder.advanceLexer()
            }
        }
        
        marker.done(HTML_ATTRIBUTE)
    }

    private fun parseInterpolation(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.advanceLexer() // consume {{
        
        // Check for directive
        val isDirective = builder.tokenType in listOf(
            RsxTokenTypes.DIRECTIVE_IF,
            RsxTokenTypes.DIRECTIVE_ELSE_IF,
            RsxTokenTypes.DIRECTIVE_ELSE,
            RsxTokenTypes.DIRECTIVE_EACH,
            RsxTokenTypes.DIRECTIVE_HTML
        )
        
        while (!builder.eof() && builder.tokenType != RsxTokenTypes.INTERPOLATION_CLOSE) {
            builder.advanceLexer()
        }
        
        if (builder.tokenType == RsxTokenTypes.INTERPOLATION_CLOSE) {
            builder.advanceLexer()
        }
        
        marker.done(if (isDirective) DIRECTIVE else INTERPOLATION)
    }
}
