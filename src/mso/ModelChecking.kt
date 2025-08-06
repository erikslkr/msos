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
            when (formula.quantifier) {
                Quantifier.EXISTENTIAL -> {
                    when (formula.variable) {
                        is SetVariable -> {
                            for (vertexSet in graph.vertexSets()) {
                                assignment.assignSet(formula.variable, vertexSet)
                                modelCheck(graph, formula.innerFormula, assignment).let {
                                    assignment.unassignSet(formula.variable)
                                    if (it) {
                                        return true
                                    }
                                }
                            }
                            false
                        }
                        is ElementVariable -> {
                            for (vertex in graph.vertices) {
                                assignment.assignElement(formula.variable, vertex)
                                modelCheck(graph, formula.innerFormula, assignment).let {
                                    assignment.unassignElement(formula.variable)
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
                    when (formula.variable) {
                        is SetVariable -> {
                            for (vertexSet in graph.vertexSets()) {
                                assignment.assignSet(formula.variable, vertexSet)
                                modelCheck(graph, formula.innerFormula, assignment).let {
                                    assignment.unassignSet(formula.variable)
                                    if (!it) {
                                        return false
                                    }
                                }
                            }
                            true
                        }
                        is ElementVariable -> {
                            for (vertex in graph.vertices) {
                                assignment.assignElement(formula.variable, vertex)
                                modelCheck(graph, formula.innerFormula, assignment).let {
                                    assignment.unassignElement(formula.variable)
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
            when (formula.connective) {
                BinaryConnective.CONJUNCTION -> {
                    val left = modelCheck(graph, formula.left, assignment)
                    if (!left) {
                        return false
                    }
                    modelCheck(graph, formula.right, assignment)
                }
                BinaryConnective.DISJUNCTION -> {
                    val left = modelCheck(graph, formula.left, assignment)
                    if (left) {
                        return true
                    }
                    modelCheck(graph, formula.right, assignment)
                }
                BinaryConnective.IMPLICATION -> {
                    val left = modelCheck(graph, formula.left, assignment)
                    if (!left) {
                        return true
                    }
                    modelCheck(graph, formula.right, assignment)
                }
                BinaryConnective.BIIMPLICATION -> {
                    val left = modelCheck(graph, formula.left, assignment)
                    val right = modelCheck(graph, formula.right, assignment)
                    left == right
                }
            }
        }
        is Formula.UnaryFormula -> {
            when (formula.connective) {
                UnaryConnective.NEGATION -> {
                    !modelCheck(graph, formula.innerFormula, assignment)
                }
            }
        }
        is Formula.BinaryPredicate -> {
            when (formula.left) {
                is ElementVariable -> {
                    when (formula.right) {
                        is ElementVariable -> {
                            when (formula.operator) {
                                BinaryOperator.EQ -> assignment.getElement(formula.left) == assignment.getElement(formula.right)
                                BinaryOperator.NEQ -> assignment.getElement(formula.left) != assignment.getElement(formula.right)
                            }
                        }
                        is SetVariable -> throw RuntimeException("'=' and '≠' are not supported between set variables and element variables")
                    }
                }
                is SetVariable -> {
                    when (formula.right) {
                        is ElementVariable -> throw RuntimeException("'=' and '≠' are not supported between set variables and element variables")
                        is SetVariable -> {
                            when (formula.operator) {
                                BinaryOperator.EQ -> assignment.getSet(formula.left) == assignment.getSet(formula.right)
                                BinaryOperator.NEQ -> assignment.getSet(formula.left) != assignment.getSet(formula.right)
                            }
                        }
                    }
                }
            }
        }
        is Formula.RelationPredicate -> {
            when (formula.relation) {
                EdgeRelation -> {
                    if (formula.arguments.size != 2) {
                        throw RuntimeException("Expected 2 arguments for 'E', but got ${formula.arguments.size}")
                    }
                    val vertices = formula.arguments.map { assignment.getElement(it as ElementVariable) }.toSet()
                    graph.edges.map(Edge::toSet).any { it == vertices }
                }
                is SetVariable -> {
                    if (formula.arguments.size != 1) {
                        throw RuntimeException("Expected 1 argument for '${formula.relation.name}', but got ${formula.arguments.size}")
                    }
                    val value = assignment.getElement(formula.arguments[0] as ElementVariable)
                    value in assignment.getSet(formula.relation)
                }
            }
        }
    }
}
