package com.github.guobin211.rsxidea.psi

import com.github.guobin211.rsxidea.language.RsxLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class RsxElementType(@NonNls debugName: String) : IElementType(debugName, RsxLanguage) {
    override fun toString(): String = "RsxElementType.${super.toString()}"
}
