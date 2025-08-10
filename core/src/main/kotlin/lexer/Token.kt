package lexer

data class Token(val tokenType: TokenType, val span: Span) {
    companion object {
        val EOF = Token(TokenType.EOF, Span.EMPTY)
    }
}
