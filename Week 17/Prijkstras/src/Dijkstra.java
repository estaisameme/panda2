import graph.*;

import java.util.*;

public class Dijkstra {
    private dijkGraph Graph;
    public Dijkstra(Graph<Integer, Integer> graph) {
        //TODO: Using the passed in graph, implement Dijkstras algorithm in this
        // class.
        this.dijkGraph = graph;
    }

    public List<Integer> shortestPath(Integer origin, Integer destination) {
        //TODO: You should return an ordered list of the node the indecies you
        // visit in your shortest path from origin to destination.
        List<Node<Integer>> visitedNodes = new ArrayList<Node<Integer>>();
        Node<Integer> startNode = this.dijkGraph.getNode(origin);
        List<Integer> tentativeWeights = new ArrayList<Integer>(this.dijkGraph.getNodes().size());
        List<Integer> permanentWeights = new ArrayList<Integer>(this.dijkGraph.getNodes().size());

        for(Integer number:tentativeWeights){
            number = 999;
        }
        visitedNodes.add(startNode);
        permanentWeights.set(0,0);
        for(Node node: visitedNodes){
            List<Edge<Integer, Integer>> tempEdges = this.dijkGraph.getEdgesFrom(node);
            for(Edge edge:tempEdges){
                if(this.dijkGraph.contains(edge.getTarget())){
                }else{
                    if(tentativeWeights.get(Integer.parseInt((String) edge.getTarget().getIndex())).equals(999)){
                        tentativeWeights.set(Integer.parseInt((String) edge.getTarget().getIndex()),permanentWeights.get(Integer.parseInt((String)edge.getSource().getIndex())) + Integer.parseInt((String) edge.getData()));
                    }else{
                        if(tentativeWeights.get(Integer.parseInt((String) edge.getTarget().getIndex())) > permanentWeights.get(Integer.parseInt((String)edge.getSource().getIndex())) + Integer.parseInt((String) edge.getData())){
                            tentativeWeights.set(Integer.parseInt((String) edge.getTarget().getIndex()),permanentWeights.get(Integer.parseInt((String)edge.getSource().getIndex())) + Integer.parseInt((String) edge.getData()));
                        }
                    }
                }
            }
        }
        Integer temp = Integer.valueOf(999);
        int index = 0;
        int iterator = 0;
        for(Integer number:tentativeWeights){
            iterator++;
            if(number < temp){
                temp = number;
                index = Integer.valueOf(iterator);
            }
        }
        permanentWeights.set(index,temp);
        visitedNodes.add(this.dijkGraph.getNode(index));

    }

}
