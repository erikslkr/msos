package lexer

/**
 * A span representing an interval of character positions.
 * @param start start position of the span (inclusive)
 * @param end end position of the span (exclusive)
 */
data class Span(val start: Int, val end: Int) {
    infix fun join(other: Span): Span {
        return Span(minOf(this.start, other.start), maxOf(this.end, other.end))
    }

    companion object {
        val EMPTY = Span(0, 0)
    }
}
