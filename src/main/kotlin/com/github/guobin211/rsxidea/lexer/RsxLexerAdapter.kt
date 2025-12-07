package com.github.guobin211.rsxidea.lexer

import com.intellij.lexer.LexerBase

class RsxLexerAdapter : LexerBase() {
    private val delegate = RsxLexer()

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        delegate.start(buffer, startOffset, endOffset, initialState)
    }

    override fun getState(): Int = delegate.state

    override fun getTokenType() = delegate.tokenType

    override fun getTokenStart(): Int = delegate.tokenStart

    override fun getTokenEnd(): Int = delegate.tokenEnd

    override fun advance() = delegate.advance()

    override fun getBufferSequence(): CharSequence = delegate.bufferSequence

    override fun getBufferEnd(): Int = delegate.bufferEnd
}
