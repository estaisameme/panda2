import graph.*;

import java.util.*;

public class Dijkstra {
    private Graph dijkGraph ;
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
        List<Integer> tentativeWeights = new ArrayList<Integer>();
        List<Integer> permanentWeights = new ArrayList<Integer>(this.dijkGraph.getNodes().size());
        for(int i = 0;i < this.dijkGraph.getNodes().size();i++){
            tentativeWeights.add(999);
            permanentWeights.add(999);
        }
        List<Integer> shortestPathInv = new ArrayList<Integer>();
        List<Integer> shortestPath = new ArrayList<Integer>();

        while(!visitedNodes.equals(this.dijkGraph.getNodes())){
            for(Integer number:tentativeWeights){
                number = 999;
            }
            visitedNodes.add(startNode);
            permanentWeights.set(0,0);
            for(Node node: visitedNodes){
                List<Edge<Integer, Integer>> tempEdges = this.dijkGraph.getEdgesFrom(node);
                for(Edge edge:tempEdges){
                    if(visitedNodes.contains(edge.getTarget())){
                    }else{
                        if(tentativeWeights.get(Integer.parseInt((String.valueOf(edge.getTarget().getIndex())))).equals(999)){
                            tentativeWeights.set(Integer.parseInt(String.valueOf(edge.getTarget().getIndex())) ,permanentWeights.get(Integer.parseInt(String.valueOf(edge.getSource().getIndex()))) + Integer.parseInt(String.valueOf(edge.getData())));
                        }else{
                            if(tentativeWeights.get(Integer.parseInt(String.valueOf(edge.getTarget().getIndex())) ) > permanentWeights.get(Integer.parseInt(String.valueOf(edge.getSource().getIndex()))) + Integer.parseInt(String.valueOf(edge.getData()))){
                                tentativeWeights.set(Integer.parseInt(String.valueOf(edge.getTarget().getIndex())),permanentWeights.get(Integer.parseInt(String.valueOf(edge.getSource().getIndex()))) + Integer.parseInt(String.valueOf(edge.getData())));
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
        shortestPathInv.add(destination);
        List<Edge<Integer ,Integer>> workingedges = this.dijkGraph.getEdgesTo(this.dijkGraph.getNode(destination));
        int index = 999;
        while(index != 0){
            for(Edge edge:workingedges){
                if(Integer.parseInt(String.valueOf(edge.getData())) == (permanentWeights.get(Integer.parseInt(String.valueOf(edge.getTarget().getIndex()))) - permanentWeights.get(Integer.parseInt(String.valueOf(edge.getSource().getIndex()))))){
                    index = Integer.parseInt(String.valueOf(edge.getSource().getIndex()));
                    shortestPathInv.add(index);
                    break;
                }
            }
            workingedges = this.dijkGraph.getEdgesTo(this.dijkGraph.getNode(index));
        }
        for(int j = shortestPathInv.size() - 1;j >= 0;j--){
            shortestPath.add(shortestPath.get(j));
        }
        return shortestPath;
    }

}
