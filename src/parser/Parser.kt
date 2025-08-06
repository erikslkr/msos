package parser

import ast.*
import lexer.Token
import mso.EdgeRelation
import mso.ElementVariable
import mso.SetVariable
import mso.Variable

class Parser(private val tokens: TokenStream) {
    fun parseFormula(): Formula {
        return parseBinary()
    }

    fun parseBinary(): Formula {
        var left = parseUnary()
        while (true) {
            val token = tokens.peek()
            val connective = when (token) {
                Token.And -> BinaryConnective.CONJUNCTION
                Token.Or -> BinaryConnective.DISJUNCTION
                Token.Implies -> BinaryConnective.IMPLICATION
                Token.Iff -> BinaryConnective.BIIMPLICATION
                else -> return left
            }
            tokens.next()
            val right = parseUnary()
            left = Formula.BinaryFormula(connective, left, right)
        }
    }

    fun parseUnary(): Formula {
        val token = tokens.peek()
        return when (token) {
            Token.Not -> {
                tokens.next()
                val innerFormula = parseUnary()
                Formula.UnaryFormula(UnaryConnective.NEGATION, innerFormula)
            }
            else -> parseQuantified()
        }
    }

    fun parseQuantified(): Formula {
        val token = tokens.peek()
        return when (token) {
            Token.Exists, Token.Forall -> {
                tokens.next()
                val quantifier = if (token == Token.Exists) Quantifier.EXISTENTIAL else Quantifier.UNIVERSAL
                val variable = parseVariable()
                val inner = parseQuantified()
                Formula.QuantifiedFormula(quantifier, variable, inner)
            }
            else -> parsePredicate()
        }
    }

    fun parsePredicate(): Formula {
        val token = tokens.peek()
        return when (token) {
            Token.E -> {
                tokens.next()
                tokens.expect(Token.LParen)
                val arguments = parseGenericList(Token.Comma, Token.RParen) { parseVariable() }
                Formula.RelationPredicate(EdgeRelation, arguments)
            }
            is Token.SetVariable -> {
                val leftVariable = parseVariable() as SetVariable
                val token2 = tokens.next()
                when (token2) {
                    Token.Eq, Token.Neq -> {
                        val operator = if (token2 == Token.Eq) BinaryOperator.EQ else BinaryOperator.NEQ
                        val rightVariable = parseVariable()
                        Formula.BinaryPredicate(operator, leftVariable, rightVariable)
                    }
                    Token.LParen -> {
                        val arguments = parseGenericList(Token.Comma, Token.RParen) { parseVariable() }
                        Formula.RelationPredicate(leftVariable, arguments)
                    }
                    else -> throw ParserException("Expected '(' or binary operator, but found '$token2'")
                }
            }
            is Token.ElementVariable -> {
                val leftVariable = parseVariable()
                val token2 = tokens.next()
                when (token2) {
                    Token.Eq, Token.Neq -> {
                        val operator = if (token2 == Token.Eq) BinaryOperator.EQ else BinaryOperator.NEQ
                        val rightVariable = parseVariable()
                        Formula.BinaryPredicate(operator, leftVariable, rightVariable)
                    }
                    else -> throw ParserException("Expected binary operator, but found '$token2'")
                }
            }
            else -> parseParenthesized()
        }
    }

    fun parseParenthesized(): Formula {
        tokens.expect(Token.LParen)
        val formula = parseFormula()
        tokens.expect(Token.RParen)
        return formula
    }

    fun parseVariable(): Variable {
        val token = tokens.next()
        return when (token) {
            is Token.SetVariable -> SetVariable(token.identifier)
            is Token.ElementVariable -> ElementVariable(token.identifier)
            else -> throw ParserException("Expected variable, but found '$token'")
        }
    }

    fun <T> parseGenericList(
        separator: Token,
        terminator: Token,
        listEntrySupplier: () -> T,
    ): List<T> {
        val elements = mutableListOf<T>()
        var token = tokens.peek()
        while (token != Token.EOF) {
            val element = listEntrySupplier()
            elements.add(element)
            when (tokens.next()) {
                separator -> {
                    token = tokens.peek()
                    continue
                }
                terminator -> return elements
                Token.EOF -> break
                else -> throw ParserException("Expected '$separator' or '$terminator', but found '$token'")
            }
        }
        throw ParserException("Missing '$terminator'")
    }
}
