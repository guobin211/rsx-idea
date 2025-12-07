package com.github.guobin211.rsxidea.folding

import com.github.guobin211.rsxidea.psi.RsxTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

class RsxFoldingBuilder : FoldingBuilderEx(), DumbAware {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()
        val text = document.text

        // Fold Rust section (---...---)
        foldRustSections(text, descriptors, root)

        // Fold Script section (<script>...</script>)
        foldSection(text, "<script", "</script>", "script", descriptors, root)

        // Fold Template section (<template>...</template>)
        foldSection(text, "<template", "</template>", "template", descriptors, root)

        // Fold Style section (<style>...</style>)
        foldSection(text, "<style", "</style>", "style", descriptors, root)

        // Fold HTML comments
        foldHtmlComments(text, descriptors, root)

        // Fold directive blocks (@if...{{/if}}, @each...{{/each}})
        foldDirectiveBlocks(text, descriptors, root)

        return descriptors.toTypedArray()
    }

    private fun foldRustSections(text: String, descriptors: MutableList<FoldingDescriptor>, root: PsiElement) {
        var startIndex = 0
        while (true) {
            val start = text.indexOf("---", startIndex)
            if (start < 0) break

            val end = text.indexOf("---", start + 3)
            if (end < 0) break

            val endOffset = end + 3
            if (endOffset - start > 6) { // More than just "------"
                val range = TextRange(start, endOffset)
                descriptors.add(FoldingDescriptor(root.node, range, FoldingGroup.newGroup("rust")))
            }
            startIndex = endOffset
        }
    }

    private fun foldSection(
        text: String,
        openTag: String,
        closeTag: String,
        groupName: String,
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement
    ) {
        var startIndex = 0
        while (true) {
            val start = text.indexOf(openTag, startIndex)
            if (start < 0) break

            // Find the end of opening tag
            val tagEnd = text.indexOf(">", start)
            if (tagEnd < 0) break

            val end = text.indexOf(closeTag, tagEnd)
            if (end < 0) break

            val endOffset = end + closeTag.length
            if (endOffset - start > openTag.length + closeTag.length + 2) {
                val range = TextRange(start, endOffset)
                descriptors.add(FoldingDescriptor(root.node, range, FoldingGroup.newGroup(groupName)))
            }
            startIndex = endOffset
        }
    }

    private fun foldHtmlComments(text: String, descriptors: MutableList<FoldingDescriptor>, root: PsiElement) {
        var startIndex = 0
        while (true) {
            val start = text.indexOf("<!--", startIndex)
            if (start < 0) break

            val end = text.indexOf("-->", start + 4)
            if (end < 0) break

            val endOffset = end + 3
            if (endOffset - start > 7) { // More than just "<!---->"
                val range = TextRange(start, endOffset)
                descriptors.add(FoldingDescriptor(root.node, range, FoldingGroup.newGroup("comment")))
            }
            startIndex = endOffset
        }
    }

    private fun foldDirectiveBlocks(text: String, descriptors: MutableList<FoldingDescriptor>, root: PsiElement) {
        // Fold @if...{{/if}} blocks
        foldDirective(text, "{{@if", "{{/if}}", "if", descriptors, root)
        
        // Fold @each...{{/each}} blocks
        foldDirective(text, "{{@each", "{{/each}}", "each", descriptors, root)
    }

    private fun foldDirective(
        text: String,
        openDirective: String,
        closeDirective: String,
        groupName: String,
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement
    ) {
        var startIndex = 0
        var depth = 0
        val starts = mutableListOf<Int>()

        var i = 0
        while (i < text.length) {
            when {
                text.regionMatches(i, openDirective, 0, openDirective.length) -> {
                    if (depth == 0 || starts.isEmpty()) {
                        starts.add(i)
                    }
                    depth++
                    i += openDirective.length
                }
                text.regionMatches(i, closeDirective, 0, closeDirective.length) -> {
                    depth--
                    if (depth == 0 && starts.isNotEmpty()) {
                        val start = starts.removeAt(starts.size - 1)
                        val endOffset = i + closeDirective.length
                        if (endOffset - start > openDirective.length + closeDirective.length) {
                            val range = TextRange(start, endOffset)
                            descriptors.add(FoldingDescriptor(root.node, range, FoldingGroup.newGroup(groupName)))
                        }
                    }
                    i += closeDirective.length
                }
                else -> i++
            }
        }
    }

    override fun getPlaceholderText(node: ASTNode): String {
        val text = node.text
        return when {
            text.startsWith("---") -> "--- ... ---"
            text.startsWith("<script") -> "<script>...</script>"
            text.startsWith("<template") -> "<template>...</template>"
            text.startsWith("<style") -> "<style>...</style>"
            text.startsWith("<!--") -> "<!-- ... -->"
            text.startsWith("{{@if") -> "{{@if ...}}...{{/if}}"
            text.startsWith("{{@each") -> "{{@each ...}}...{{/each}}"
            else -> "..."
        }
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean = false
}
