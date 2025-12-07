package com.github.guobin211.rsxidea.completion

import com.github.guobin211.rsxidea.psi.RsxTokenTypes
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class RsxCompletionContributor : CompletionContributor() {
    init {
        // Directive completions after {{@
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(RsxTokenTypes.IDENTIFIER),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    // Add directive completions
                    result.addElement(LookupElementBuilder.create("if")
                        .withTypeText("Conditional directive")
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, " condition}}\n\n{{/if}}")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset - 14)
                        })
                    
                    result.addElement(LookupElementBuilder.create("each")
                        .withTypeText("Loop directive")
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, " items as item}}\n\n{{/each}}")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset - 18)
                        })
                    
                    result.addElement(LookupElementBuilder.create("html")
                        .withTypeText("Raw HTML directive")
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, " content}}")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset - 2)
                        })
                }
            }
        )

        // HTML tag completions
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(RsxTokenTypes.HTML_TAG_NAME),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    // Common HTML tags
                    val tags = listOf(
                        "div", "span", "p", "a", "img", "ul", "ol", "li",
                        "h1", "h2", "h3", "h4", "h5", "h6",
                        "header", "footer", "main", "nav", "section", "article", "aside",
                        "form", "input", "button", "label", "select", "option", "textarea",
                        "table", "thead", "tbody", "tr", "th", "td",
                        "br", "hr", "pre", "code", "blockquote",
                        "strong", "em", "b", "i", "u", "s",
                        "video", "audio", "canvas", "svg", "iframe"
                    )
                    
                    tags.forEach { tag ->
                        result.addElement(LookupElementBuilder.create(tag)
                            .withTypeText("HTML tag"))
                    }
                }
            }
        )

        // HTML attribute completions
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(RsxTokenTypes.HTML_ATTR_NAME),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    // Common HTML attributes
                    val attrs = listOf(
                        "id", "class", "style", "title", "name", "value",
                        "href", "src", "alt", "type", "placeholder",
                        "disabled", "readonly", "required", "checked", "selected",
                        "data-", "aria-",
                        "onclick", "onchange", "onsubmit", "onload",
                        "client" // RSX-specific: client component marker
                    )
                    
                    attrs.forEach { attr ->
                        result.addElement(LookupElementBuilder.create(attr)
                            .withTypeText("HTML attribute")
                            .withInsertHandler { ctx, _ ->
                                ctx.document.insertString(ctx.tailOffset, "=\"\"")
                                ctx.editor.caretModel.moveToOffset(ctx.tailOffset - 1)
                            })
                    }
                }
            }
        )
    }
}
