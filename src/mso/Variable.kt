package mso

sealed interface Variable

sealed interface Relation

data class ElementVariable(val name: String) : Variable {
    override fun toString(): String = name
}

data class SetVariable(val name: String) : Variable, Relation {
    override fun toString(): String = name
}

object EdgeRelation : Relation {
    override fun toString(): String = "E"
}
