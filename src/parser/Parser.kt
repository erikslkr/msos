package parser

import ast.*
import lexer.Span
import lexer.TokenType
import mso.EdgeRelation
import mso.ElementVariable
import mso.SetVariable
import mso.Variable

class Parser(private val tokens: TokenStream) {
    fun parseFormula(): Pair<Formula, Span> {
        return parseBinary()
    }

    fun parseBinary(): Pair<Formula, Span> {
        var (left, leftSpan) = parseUnary()
        while (true) {
            val token = tokens.peek()
            val connective = when (token.tokenType) {
                TokenType.And -> BinaryConnective.CONJUNCTION
                TokenType.Or -> BinaryConnective.DISJUNCTION
                TokenType.Implies -> BinaryConnective.IMPLICATION
                TokenType.Iff -> BinaryConnective.BIIMPLICATION
                else -> return left to leftSpan
            } to token.span
            tokens.next()
            val (right, rightSpan) = parseUnary()
            left = Formula.BinaryFormula(connective, left to leftSpan, right to rightSpan)
            leftSpan = leftSpan join rightSpan
        }
    }

    fun parseUnary(): Pair<Formula, Span> {
        val token = tokens.peek()
        return when (token.tokenType) {
            TokenType.Not -> {
                tokens.next()
                val (innerFormula, innerSpan) = parseUnary()
                Formula.UnaryFormula(UnaryConnective.NEGATION to token.span, innerFormula to innerSpan) to (token.span join innerSpan)
            }
            else -> parseQuantified()
        }
    }

    fun parseQuantified(): Pair<Formula, Span> {
        val token = tokens.peek()
        return when (token.tokenType) {
            TokenType.Exists, TokenType.Forall -> {
                tokens.next()
                val quantifier = if (token.tokenType == TokenType.Exists) {
                    Quantifier.EXISTENTIAL
                } else {
                    Quantifier.UNIVERSAL
                } to token.span
                val variableWithSpan = parseVariable()
                val (inner, innerSpan) = parseQuantified()
                Formula.QuantifiedFormula(quantifier, variableWithSpan, inner to innerSpan) to (token.span join innerSpan)
            }
            else -> parsePredicate()
        }
    }

    fun parsePredicate(): Pair<Formula, Span> {
        val token = tokens.peek()
        return when (token.tokenType) {
            TokenType.E -> {
                tokens.next()
                tokens.expect(TokenType.LParen)
                val (arguments, parenSpan) = parseGenericList(TokenType.Comma, TokenType.RParen) { parseVariable() }
                Formula.RelationPredicate(EdgeRelation to token.span, arguments) to (token.span join parenSpan)
            }
            is TokenType.SetVariable -> {
                val (leftVariable, leftSpan) = parseVariable()
                val token2 = tokens.next()
                when (token2.tokenType) {
                    TokenType.Eq, TokenType.Neq -> {
                        val operator = if (token2.tokenType == TokenType.Eq) {
                            BinaryOperator.EQ
                        } else {
                            BinaryOperator.NEQ
                        } to token2.span
                        val (rightVariable, rightSpan) = parseVariable()
                        Formula.BinaryPredicate(operator, leftVariable to leftSpan, rightVariable to rightSpan) to (leftSpan join rightSpan)
                    }
                    TokenType.LParen -> {
                        val (arguments, parenSpan) = parseGenericList(TokenType.Comma, TokenType.RParen) { parseVariable() }
                        Formula.RelationPredicate((leftVariable as SetVariable) to leftSpan, arguments) to (token.span join parenSpan)
                    }
                    else -> throw ParserException("Expected '(' or binary operator, but found '${token2.tokenType}'")
                }
            }
            is TokenType.ElementVariable -> {
                val (leftVariable, leftSpan) = parseVariable()
                val token2 = tokens.next()
                when (token2.tokenType) {
                    TokenType.Eq, TokenType.Neq -> {
                        val operator = if (token2.tokenType == TokenType.Eq) {
                            BinaryOperator.EQ
                        } else {
                            BinaryOperator.NEQ
                        } to token2.span
                        val (rightVariable, rightSpan) = parseVariable()
                        Formula.BinaryPredicate(operator, leftVariable to leftSpan, rightVariable to rightSpan) to (leftSpan join rightSpan)
                    }
                    else -> throw ParserException("Expected binary operator, but found '${token2.tokenType}'")
                }
            }
            else -> parseParenthesized()
        }
    }

    fun parseParenthesized(): Pair<Formula, Span> {
        val lparen = tokens.expect(TokenType.LParen)
        val (formula, _) = parseFormula()
        val rparen = tokens.expect(TokenType.RParen)
        return formula to (lparen.span join rparen.span)
    }

    fun parseVariable(): Pair<Variable, Span> {
        val token = tokens.next()
        return when (token.tokenType) {
            is TokenType.SetVariable -> SetVariable(token.tokenType.identifier) to token.span
            is TokenType.ElementVariable -> ElementVariable(token.tokenType.identifier) to token.span
            else -> throw ParserException("Expected variable, but found '$token'")
        }
    }

    fun <T> parseGenericList(
        separator: TokenType,
        terminator: TokenType,
        listEntrySupplier: () -> Pair<T, Span>,
    ): Pair<List<Pair<T, Span>>, Span> {
        val elements = mutableListOf<Pair<T, Span>>()
        var token = tokens.peek()
        while (token != TokenType.EOF) {
            val element = listEntrySupplier()
            elements.add(element)
            val token2 = tokens.next()
            when (token2.tokenType) {
                separator -> {
                    token = tokens.peek()
                    continue
                }
                terminator -> {
                    return elements to token2.span
                }
                TokenType.EOF -> break
                else -> throw ParserException("Expected '$separator' or '$terminator', but found '${token2.tokenType}'")
            }
        }
        throw ParserException("Missing '$terminator'")
    }
}
