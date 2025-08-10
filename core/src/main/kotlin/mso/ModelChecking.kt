package mso

import ast.BinaryConnective
import ast.BinaryOperator
import ast.Formula
import ast.Quantifier
import ast.UnaryConnective
import graph.Edge
import graph.Graph

fun modelCheck(graph: Graph, formula: Formula): Boolean {
    return modelCheck(graph, formula, Assignment())
}

fun modelCheck(graph: Graph, formula: Formula, assignment: Assignment): Boolean {
    return when (formula) {
        is Formula.QuantifiedFormula -> {
            when (formula.quantifier.first) {
                Quantifier.EXISTENTIAL -> {
                    val variable = formula.variable.first
                    when (variable) {
                        is SetVariable -> {
                            for (vertexSet in graph.vertexSets()) {
                                assignment.assignSet(variable, vertexSet)
                                modelCheck(graph, formula.innerFormula.first, assignment).let {
                                    assignment.unassignSet(variable)
                                    if (it) {
                                        return true
                                    }
                                }
                            }
                            false
                        }
                        is ElementVariable -> {
                            for (vertex in graph.vertices) {
                                assignment.assignElement(variable, vertex)
                                modelCheck(graph, formula.innerFormula.first, assignment).let {
                                    assignment.unassignElement(variable)
                                    if (it) {
                                        return true
                                    }
                                }
                            }
                            false
                        }
                    }
                }
                Quantifier.UNIVERSAL -> {
                    val variable = formula.variable.first
                    when (variable) {
                        is SetVariable -> {
                            for (vertexSet in graph.vertexSets()) {
                                assignment.assignSet(variable, vertexSet)
                                modelCheck(graph, formula.innerFormula.first, assignment).let {
                                    assignment.unassignSet(variable)
                                    if (!it) {
                                        return false
                                    }
                                }
                            }
                            true
                        }
                        is ElementVariable -> {
                            for (vertex in graph.vertices) {
                                assignment.assignElement(variable, vertex)
                                modelCheck(graph, formula.innerFormula.first, assignment).let {
                                    assignment.unassignElement(variable)
                                    if (!it) {
                                        return false
                                    }
                                }
                            }
                            true
                        }
                    }
                }
            }
        }
        is Formula.BinaryFormula -> {
            when (formula.connective.first) {
                BinaryConnective.CONJUNCTION -> {
                    val left = modelCheck(graph, formula.left.first, assignment)
                    if (!left) {
                        return false
                    }
                    modelCheck(graph, formula.right.first, assignment)
                }
                BinaryConnective.DISJUNCTION -> {
                    val left = modelCheck(graph, formula.left.first, assignment)
                    if (left) {
                        return true
                    }
                    modelCheck(graph, formula.right.first, assignment)
                }
                BinaryConnective.IMPLICATION -> {
                    val left = modelCheck(graph, formula.left.first, assignment)
                    if (!left) {
                        return true
                    }
                    modelCheck(graph, formula.right.first, assignment)
                }
                BinaryConnective.BIIMPLICATION -> {
                    val left = modelCheck(graph, formula.left.first, assignment)
                    val right = modelCheck(graph, formula.right.first, assignment)
                    left == right
                }
            }
        }
        is Formula.UnaryFormula -> {
            when (formula.connective.first) {
                UnaryConnective.NEGATION -> {
                    !modelCheck(graph, formula.innerFormula.first, assignment)
                }
            }
        }
        is Formula.BinaryPredicate -> {
            val leftVariable = formula.left.first
            when (leftVariable) {
                is ElementVariable -> {
                    val rightVariable = formula.right.first
                    when (rightVariable) {
                        is ElementVariable -> {
                            when (formula.operator.first) {
                                BinaryOperator.EQ -> assignment.getElement(leftVariable) == assignment.getElement(rightVariable)
                                BinaryOperator.NEQ -> assignment.getElement(leftVariable) != assignment.getElement(rightVariable)
                            }
                        }
                        is SetVariable -> throw RuntimeException("'=' and '≠' are not supported between set variables and element variables")
                    }
                }
                is SetVariable -> {
                    val rightVariable = formula.right.first
                    when (rightVariable) {
                        is ElementVariable -> throw RuntimeException("'=' and '≠' are not supported between set variables and element variables")
                        is SetVariable -> {
                            when (formula.operator.first) {
                                BinaryOperator.EQ -> assignment.getSet(leftVariable) == assignment.getSet(rightVariable)
                                BinaryOperator.NEQ -> assignment.getSet(leftVariable) != assignment.getSet(rightVariable)
                            }
                        }
                    }
                }
            }
        }
        is Formula.RelationPredicate -> {
            val relation = formula.relation.first
            when (relation) {
                EdgeRelation -> {
                    val vertices = formula.arguments.map { assignment.getElement(it.first as ElementVariable) }.toSet()
                    graph.edges.map(Edge::toSet).any { it == vertices }
                }
                is SetVariable -> {
                    val value = assignment.getElement(formula.arguments[0].first as ElementVariable)
                    value in assignment.getSet(relation)
                }
            }
        }
    }
}
