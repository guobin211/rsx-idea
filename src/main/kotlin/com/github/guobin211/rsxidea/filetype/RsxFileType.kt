package com.github.guobin211.rsxidea.filetype

import com.github.guobin211.rsxidea.language.RsxLanguage
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object RsxFileType : LanguageFileType(RsxLanguage) {
    @JvmStatic
    val INSTANCE: RsxFileType = this

    override fun getName(): String = "RSX"

    override fun getDescription(): String = "RSX language file"

    override fun getDefaultExtension(): String = "rsx"

    override fun getIcon(): Icon = RsxIcons.FILE
}
