package parser

import lexer.Token
import lexer.TokenType

class TokenStream(private val tokens: List<Token>) {
    private var position = 0

    fun peek(): Token = tokens.getOrNull(position) ?: Token.EOF
    fun next(): Token = tokens.getOrNull(position++) ?: Token.EOF

    fun expect(tokenType: TokenType): Token {
        return tokens.getOrNull(position++).let {
            when (it?.tokenType) {
                tokenType -> it
                null -> throw ParserException("Missing '$tokenType'")
                else -> throw ParserException("Expected '$tokenType', but found '${it.tokenType}'")
            }
        }
    }
}
