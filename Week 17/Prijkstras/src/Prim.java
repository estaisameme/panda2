import graph.*;
import org.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14N11Method;

import java.util.*;

public class Prim {
    private List<Node<Integer>> visitedNodes = new ArrayList<Node<Integer>>();
    private List<Edge<Integer, Integer>> usedEdges = new ArrayList<Edge<Integer, Integer>>();

    public Prim(Graph<Integer, Integer> graph) {
        //TODO: Using the passed in graph, implement Prims algorithm in this
        // class.
        //ArrayList<Integer>(graph.getNodes().size());
        Node startNode = graph.getNode(1);
        visitedNodes.add(startNode);
        while(visitedNodes.size() < (graph.getNodes().size())){
            Edge tentativeWeight = graph.getEdgesFrom(graph.getNode(1)).get(0);
            for(Node node:visitedNodes){
                List<Edge<Integer, Integer>> tempEdges = graph.getEdgesFrom(node);
                Edge temp = new Edge(new Node(1),new Node(1),999);
                for(Edge edge:tempEdges){
                    boolean contains = false;
                    for(Edge usededge: usedEdges){
                        if((String.valueOf(edge.getSource()).equals(String.valueOf(usededge.getSource())) && String.valueOf(edge.getTarget()).equals(String.valueOf(usededge.getTarget())) && String.valueOf(edge.getData()).equals(String.valueOf(usededge.getData())))||(String.valueOf(edge.getSource()).equals(String.valueOf(usededge.getTarget())) && String.valueOf(edge.getTarget()).equals(String.valueOf(usededge.getSource())) && String.valueOf(edge.getData()).equals(String.valueOf(usededge.getData())))){
                            contains = true;
                        }
                    }
                    if(contains == true){
                    }else{
                        if(Integer.parseInt(String.valueOf(edge.getData()) ) < Integer.parseInt(String.valueOf(temp.getData()))){
                            temp = edge;
                        }
                    }
                }
                boolean containsagain = false;
                for(Node nodel:visitedNodes){
                    if(Integer.parseInt(toString().valueOf(nodel.getIndex())) == Integer.parseInt(String.valueOf(temp.getTarget()))){
                        containsagain = true;
                    }
                }
                if(Integer.parseInt(String.valueOf(tentativeWeight.getData())) > Integer.parseInt(String.valueOf(temp.getData())) && containsagain == false){
                    tentativeWeight = temp;
                }

            }
            visitedNodes.add(tentativeWeight.getTarget());
            usedEdges.add(new Edge(tentativeWeight.getSource(),tentativeWeight.getTarget(),tentativeWeight.getData()));
           // usedEdges.add(new Edge(tentativeWeight.getTarget(),tentativeWeight.getSource(),tentativeWeight.getData()));

        }
    }

    public Graph<Integer, Integer> getMinimumSpanningTree() {
        //TODO: You should return a new graph that represents the minimum
        // spanning tree of the graph.
        Graph minimumST = new UndirectedGraph<>();
        for(Node node:this.visitedNodes){
            minimumST.add(node);
        }
        for(Edge edge:this.usedEdges){
            minimumST.add((edge));
        }

        return minimumST;
    }

}
