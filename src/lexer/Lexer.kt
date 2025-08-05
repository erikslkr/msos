package lexer

class Lexer(private val input: String) {
    private var position = 0

    private val singleCharTokens = mapOf(
        '(' to Token.LParen,
        ')' to Token.RParen,
        ',' to Token.Comma,
        '=' to Token.Eq,
    )

    @Throws(LexerException::class)
    fun peekToken(): Token {
        val previousPosition = position
        val token = nextToken()
        position = previousPosition
        return token
    }

    @Throws(LexerException::class)
    fun nextToken(): Token {
        skipWhitespace()
        val char = peekChar()
        singleCharTokens[char]?.let {
            nextChar()
            return it
        }
        return when (char) {
            '\\' -> {
                nextChar()
                readKeyword()
            }
            '!' -> {
                nextChar()
                if (peekChar() == '=') {
                    nextChar()
                    Token.Neq
                } else {
                    Token.Not
                }
            }
            null -> Token.EOF
            else -> {
                val identifier = readIdentifier() ?: throw LexerException("Illegal character '${char}'")
                if (identifier == "E") {
                    Token.E
                } else if (identifier[0].isLowerCase()) {
                    Token.ElementVariable(identifier)
                } else {
                    Token.SetVariable(identifier)
                }
            }
        }
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

    private fun readKeyword(): Token {
        val identifier = readIdentifier() ?: throw LexerException("Expected keyword after '\\'")
        return when (identifier) {
            "exists" -> Token.Exists
            "forall" -> Token.Forall
            "land" -> Token.And
            "lor" -> Token.Or
            "implies" -> Token.Implies
            "iff" -> Token.Iff
            "eq" -> Token.Eq
            "neq" -> Token.Neq
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
