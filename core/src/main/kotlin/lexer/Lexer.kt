package lexer

import parser.TokenStream

class Lexer(private val input: String) {
    private var position = 0

    private val singleCharTokens = mapOf(
        '(' to TokenType.LParen,
        ')' to TokenType.RParen,
        ',' to TokenType.Comma,
        '=' to TokenType.Eq,
    )

    fun tokenize(): TokenStream {
        val tokens = mutableListOf<Token>()
        while (true) {
            val token = nextToken()
            tokens.add(token)
            if (token.tokenType == TokenType.EOF) {
                break
            }
        }
        return TokenStream(tokens)
    }

    fun peekToken(): Token {
        val previousPosition = position
        val token = nextToken()
        position = previousPosition
        return token
    }

    fun nextToken(): Token {
        skipWhitespace()
        val char = peekChar()
        singleCharTokens[char]?.let {
            val span = Span(position, position + 1)
            nextChar()
            return Token(it, span)
        }
        val startPosition = position
        val tokenType = when (char) {
            '\\' -> {
                nextChar()
                readKeyword()
            }
            '!' -> {
                nextChar()
                if (peekChar() == '=') {
                    nextChar()
                    TokenType.Neq
                } else {
                    TokenType.Not
                }
            }
            null -> TokenType.EOF
            else -> {
                val identifier = readIdentifier() ?: throw LexerException("Illegal character '${char}'")
                if (identifier == "E") {
                    TokenType.E
                } else if (identifier[0].isLowerCase()) {
                    TokenType.ElementVariable(identifier)
                } else {
                    TokenType.SetVariable(identifier)
                }
            }
        }
        return Token(tokenType, Span(startPosition, position))
    }

    private fun nextChar(): Char? {
        return if (position < input.length) {
            input[position++]
        } else {
            null
        }
    }

    private fun peekChar(): Char? {
        return if (position < input.length) {
            input[position]
        } else {
            null
        }
    }

    private fun skipWhitespace() {
        while (peekChar()?.isWhitespace() == true) {
            nextChar()
        }
    }

    private fun readKeyword(): TokenType {
        val identifier = readIdentifier() ?: throw LexerException("Expected keyword after '\\'")
        return when (identifier) {
            "exists" -> TokenType.Exists
            "forall" -> TokenType.Forall
            "land" -> TokenType.And
            "lor" -> TokenType.Or
            "implies" -> TokenType.Implies
            "iff" -> TokenType.Iff
            "eq" -> TokenType.Eq
            "neq" -> TokenType.Neq
            "neg" -> TokenType.Not
            else -> throw LexerException("Unknown keyword '$identifier'")
        }
    }

    private fun readIdentifier(): String? {
        val startPosition = position
        if (peekChar()?.isLetter() != true) {
            return null
        }
        while (peekChar()?.isLetter() == true) {
            nextChar()
        }
        while (peekChar()?.isDigit() == true) {
            nextChar()
        }
        return input.substring(startPosition, position)
    }
}
