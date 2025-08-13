import graph.Graph
import graph.GraphVertex
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class GraphIsTreeTest {
    @Test
    fun `K_1 is a tree`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        assertTrue(graph.isTree())
    }

    @Test
    fun `N_2 is not a tree`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addVertex(GraphVertex("d"))
        assertFalse(graph.isTree())
    }

    @Test
    fun `P_5 is a tree`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addVertex(GraphVertex("d"))
        graph.addVertex(GraphVertex("e"))
        graph.addEdge(GraphVertex("a"), GraphVertex("b"))
        graph.addEdge(GraphVertex("b"), GraphVertex("c"))
        graph.addEdge(GraphVertex("c"), GraphVertex("d"))
        graph.addEdge(GraphVertex("d"), GraphVertex("e"))
        assertTrue(graph.isTree())
    }

    @Test
    fun `K_3 is not a tree`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addEdge(GraphVertex("a"), GraphVertex("b"))
        graph.addEdge(GraphVertex("b"), GraphVertex("c"))
        graph.addEdge(GraphVertex("c"), GraphVertex("a"))
        assertFalse(graph.isTree())
    }

    @Test
    fun `two disjoint P_3's are not a tree`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addVertex(GraphVertex("d"))
        graph.addVertex(GraphVertex("e"))
        graph.addVertex(GraphVertex("f"))
        graph.addEdge(GraphVertex("a"), GraphVertex("b"))
        graph.addEdge(GraphVertex("b"), GraphVertex("c"))
        graph.addEdge(GraphVertex("d"), GraphVertex("e"))
        graph.addEdge(GraphVertex("e"), GraphVertex("f"))
        assertFalse(graph.isTree())
    }

    @Test
    fun `T_{2,3} (binary tree of depth 3) is a tree`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addVertex(GraphVertex("d"))
        graph.addVertex(GraphVertex("e"))
        graph.addVertex(GraphVertex("f"))
        graph.addVertex(GraphVertex("g"))
        graph.addEdge(GraphVertex("a"), GraphVertex("b"))
        graph.addEdge(GraphVertex("a"), GraphVertex("c"))
        graph.addEdge(GraphVertex("b"), GraphVertex("d"))
        graph.addEdge(GraphVertex("b"), GraphVertex("e"))
        graph.addEdge(GraphVertex("c"), GraphVertex("f"))
        graph.addEdge(GraphVertex("c"), GraphVertex("g"))
        assertTrue(graph.isTree())
    }

    @Test
    fun `T_{2,3} + one edge is not a tree`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addVertex(GraphVertex("d"))
        graph.addVertex(GraphVertex("e"))
        graph.addVertex(GraphVertex("f"))
        graph.addVertex(GraphVertex("g"))
        graph.addEdge(GraphVertex("a"), GraphVertex("b"))
        graph.addEdge(GraphVertex("a"), GraphVertex("c"))
        graph.addEdge(GraphVertex("b"), GraphVertex("d"))
        graph.addEdge(GraphVertex("b"), GraphVertex("e"))
        graph.addEdge(GraphVertex("c"), GraphVertex("f"))
        graph.addEdge(GraphVertex("c"), GraphVertex("g"))
        graph.addEdge(GraphVertex("d"), GraphVertex("g"))
        assertFalse(graph.isTree())
    }
}
