package com.github.guobin211.rsxidea.brace

import com.github.guobin211.rsxidea.psi.RsxTokenTypes
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class RsxBraceMatcher : PairedBraceMatcher {
    companion object {
        private val PAIRS = arrayOf(
            // Interpolation braces
            BracePair(RsxTokenTypes.INTERPOLATION_OPEN, RsxTokenTypes.INTERPOLATION_CLOSE, true),
            // Parentheses
            BracePair(RsxTokenTypes.LPAREN, RsxTokenTypes.RPAREN, false),
            // Brackets
            BracePair(RsxTokenTypes.LBRACKET, RsxTokenTypes.RBRACKET, false),
            // HTML tags
            BracePair(RsxTokenTypes.HTML_TAG_OPEN, RsxTokenTypes.HTML_TAG_CLOSE, true),
            BracePair(RsxTokenTypes.HTML_TAG_OPEN, RsxTokenTypes.HTML_SELF_CLOSE, true),
        )
    }

    override fun getPairs(): Array<BracePair> = PAIRS

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset
}
