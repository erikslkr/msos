import graph.Graph
import graph.GraphVertex
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class GraphIsAcyclicTest {
    @Test
    fun `K_1 is acyclic`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        assertTrue(graph.isAcyclic())
    }

    @Test
    fun `N_4 is acyclic`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addVertex(GraphVertex("d"))
        assertTrue(graph.isAcyclic())
    }

    @Test
    fun `P_3 is acyclic`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addEdge(GraphVertex("a"), GraphVertex("b"))
        graph.addEdge(GraphVertex("b"), GraphVertex("c"))
        assertTrue(graph.isAcyclic())
    }

    @Test
    fun `K_3 is cyclic`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addEdge(GraphVertex("a"), GraphVertex("b"))
        graph.addEdge(GraphVertex("b"), GraphVertex("c"))
        graph.addEdge(GraphVertex("c"), GraphVertex("a"))
        assertFalse(graph.isAcyclic())
    }

    @Test
    fun `C_4 is cyclic`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addVertex(GraphVertex("d"))
        graph.addEdge(GraphVertex("a"), GraphVertex("b"))
        graph.addEdge(GraphVertex("b"), GraphVertex("c"))
        graph.addEdge(GraphVertex("c"), GraphVertex("d"))
        graph.addEdge(GraphVertex("d"), GraphVertex("a"))
        assertFalse(graph.isAcyclic())
    }

    @Test
    fun `two disjoint K_2's are acyclic`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addVertex(GraphVertex("d"))
        graph.addEdge(GraphVertex("a"), GraphVertex("b"))
        graph.addEdge(GraphVertex("c"), GraphVertex("d"))
        assertTrue(graph.isAcyclic())
    }

    @Test
    fun `disjoint K_3 and K_1 are cyclic`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addVertex(GraphVertex("d"))
        graph.addEdge(GraphVertex("a"), GraphVertex("b"))
        graph.addEdge(GraphVertex("b"), GraphVertex("c"))
        graph.addEdge(GraphVertex("c"), GraphVertex("a"))
        assertFalse(graph.isAcyclic())
    }

    @Test
    fun `K_{2,3} is cyclic`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("1"))
        graph.addVertex(GraphVertex("2"))
        graph.addVertex(GraphVertex("3"))
        graph.addEdge(GraphVertex("a"), GraphVertex("1"))
        graph.addEdge(GraphVertex("a"), GraphVertex("2"))
        graph.addEdge(GraphVertex("a"), GraphVertex("3"))
        graph.addEdge(GraphVertex("b"), GraphVertex("1"))
        graph.addEdge(GraphVertex("b"), GraphVertex("2"))
        graph.addEdge(GraphVertex("b"), GraphVertex("3"))
        assertFalse(graph.isAcyclic())
    }
}
