package com.github.guobin211.rsxidea.language

import com.intellij.lang.Language

object RsxLanguage : Language("RSX") {
    private fun readResolve(): Any = RsxLanguage

    override fun getDisplayName(): String = "RSX"

    override fun isCaseSensitive(): Boolean = true
}
