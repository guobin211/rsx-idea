package com.github.guobin211.rsxidea.parser

import com.github.guobin211.rsxidea.lexer.RsxLexerAdapter
import com.github.guobin211.rsxidea.psi.RsxFile
import com.github.guobin211.rsxidea.psi.RsxTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.github.guobin211.rsxidea.language.RsxLanguage

class RsxParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(RsxLanguage)
    }

    override fun createLexer(project: Project?): Lexer = RsxLexerAdapter()

    override fun createParser(project: Project?): PsiParser = RsxParser()

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = RsxTokenTypes.COMMENTS

    override fun getStringLiteralElements(): TokenSet = RsxTokenTypes.STRINGS

    override fun createElement(node: ASTNode): PsiElement {
        throw UnsupportedOperationException("Not implemented")
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile = RsxFile(viewProvider)
}
