import graph.*;
import org.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14N11Method;

import java.util.*;

public class Prim {
    private List<Node<Integer>> visitedNodes = new ArrayList<Node<Integer>>();
    private List<Edge<Integer, Integer>> usedEdges = new ArrayList<Edge<Integer, Integer>>();

    public Prim(Graph<Integer, Integer> graph) {
        //TODO: Using the passed in graph, implement Prims algorithm in this
        // class.
        Edge tentativeWeight = graph.getEdgesFrom(graph.getNode(1)).get(0);//ArrayList<Integer>(graph.getNodes().size());
        Node startNode = graph.getNode(0);
        visitedNodes.add(startNode);
        while(!(visitedNodes.equals(graph.getNodes()))){
            for(Node node:visitedNodes){
                List<Edge<Integer, Integer>> tempEdges = graph.getEdgesFrom(node);
                Edge temp = new Edge(new Node(1),new Node(1),999);
                for(Edge edge:tempEdges){
                    if(usedEdges.contains(edge)){
                    }else{
                        if(Integer.parseInt((String) edge.getData()) < Integer.parseInt((String) temp.getData())){
                            temp = edge;
                        }
                    }
                }
                if(Integer.parseInt((String) tentativeWeight.getData()) > Integer.parseInt((String)temp.getData()) && !visitedNodes.contains(temp.getTarget())){
                    tentativeWeight = temp;
                }

            }
            visitedNodes.add(tentativeWeight.getTarget());
            usedEdges.add(tentativeWeight);

        }
    }

    public Graph<Integer, Integer> getMinimumSpanningTree() {
        //TODO: You should return a new graph that represents the minimum
        // spanning tree of the graph.
        Graph minimumST = new UndirectedGraph<>();
        for(Node node:visitedNodes){
            minimumST.add(node);
        }
        for(Edge edge:usedEdges){
            minimumST.add((edge));
        }

        return minimumST;
    }

}
