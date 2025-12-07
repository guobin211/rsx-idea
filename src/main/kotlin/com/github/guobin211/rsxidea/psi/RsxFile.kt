package com.github.guobin211.rsxidea.psi

import com.github.guobin211.rsxidea.filetype.RsxFileType
import com.github.guobin211.rsxidea.language.RsxLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class RsxFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, RsxLanguage) {
    override fun getFileType(): FileType = RsxFileType

    override fun toString(): String = "RSX File"
}
