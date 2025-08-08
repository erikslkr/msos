package mso

sealed interface Variable {
    val name: String
}

sealed interface Relation

data class ElementVariable(override val name: String) : Variable {
    override fun toString(): String = name
}

data class SetVariable(override val name: String) : Variable, Relation {
    override fun toString(): String = name
}

object EdgeRelation : Relation {
    override fun toString(): String = "E"
}
