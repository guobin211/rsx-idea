package com.github.guobin211.rsxidea.commenter

import com.intellij.lang.Commenter

class RsxCommenter : Commenter {
    // RSX uses HTML-style comments in template section
    override fun getLineCommentPrefix(): String? = null

    override fun getBlockCommentPrefix(): String = "<!--"

    override fun getBlockCommentSuffix(): String = "-->"

    override fun getCommentedBlockCommentPrefix(): String? = null

    override fun getCommentedBlockCommentSuffix(): String? = null
}
