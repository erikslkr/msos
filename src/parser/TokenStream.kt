package parser

import lexer.Token

class TokenStream(private val tokens: List<Token>) {
    private var position = 0

    fun peek(): Token = tokens.getOrNull(position) ?: Token.EOF
    fun next(): Token = tokens.getOrNull(position++) ?: Token.EOF

    fun expect(token: Token): Token {
        return tokens.getOrNull(position++).let {
            when (it) {
                token -> it
                null -> throw ParserException("Missing '$token'")
                else -> throw ParserException("Expected '$token', but found $it")
            }
        }
    }
}
