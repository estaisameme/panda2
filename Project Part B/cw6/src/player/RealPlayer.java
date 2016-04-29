package player;

import scotlandyard.*;
import swing.algorithms.Dijkstra;

import java.io.IOException;
import java.util.*;
import graph.Edge;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by User on 21/04/2016.
 */
public class RealPlayer implements Player{
    Dijkstra dijkstra;
    ScotlandYardView view;
    ScotlandYardGraph graph;
    HashMap<Double,Move> weightedMove =  new HashMap<Double,Move>();
    HashMap<Double,Move> newUserObject = new HashMap<Double,Move>();
    Move verytemporaryMove;

    public RealPlayer(ScotlandYardView sView, String graphFileName) {
        //TODO: A better AI makes use of `view` and `graphFilename`.
        dijkstra = new Dijkstra(graphFileName);
        view = sView;
        ScotlandYardGraphReader graphReader = new ScotlandYardGraphReader();
        try{
            graph = graphReader.readGraph(graphFileName);
        }
        catch (Exception e) {

        }
    }

    public int fetchTarget(int location, Move move) {
        try {
            return ((MoveDouble)move).move2.target;
        }
        catch (Exception e) {
        }
        try {
            return ((MoveTicket)move).target;
        }
        catch (Exception e) {
        }
        return location;
    }

    public double xScore(Move move, int location) {
        List <Colour> players = view.getPlayers();
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();
        double weight = 0;
        double currentDistance;
        double newDistance;
        int numEdges = 0;
        for (Colour player:players) {
            if (player != Colour.Black) {

                for (Transport transport : Transport.values()) {
                    ticketMap.put(transport, view.getPlayerTickets(player, Ticket.fromTransport(transport)));
                }
                currentDistance = dijkstra.getRoute(view.getPlayerLocation(player), location, ticketMap).size();
                newDistance = dijkstra.getRoute(view.getPlayerLocation(player), fetchTarget(location, move), ticketMap).size();
                weight = weight + (((newDistance - currentDistance)/newDistance)*10.0);
                //System.out.println(weight);
            }
        }


        numEdges = graph.getEdgesFrom(graph.getNode(fetchTarget(location, move))).size();
        weight = weight + numEdges;

        System.out.println("[MOVE]:"+ move + " [WEIGHT]: "+weight);

        return weight;
    }

    public double dScore(Move move, int xlocation,int dlocation) {
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();
        double weight = 0;
        double currentDistance;
        double newDistance;
        //int numEdges = 0;
        for (Transport transport : Transport.values()) {
            ticketMap.put(transport, view.getPlayerTickets(view.getCurrentPlayer(), Ticket.fromTransport(transport)));
        }
        if (xlocation != 0) {
            currentDistance = dijkstra.getRoute(xlocation, dlocation, ticketMap).size();
            newDistance = dijkstra.getRoute(xlocation, fetchTarget(dlocation, move), ticketMap).size();
            weight = weight + (currentDistance-newDistance);
        }
        //numEdges = graph.getEdgesFrom(graph.getNode(fetchTarget(dlocation, move))).size();
        //weight = weight + numEdges;
        //System.out.println("[MOVE]:"+ move + " [WEIGHT]: "+weight);

        return weight;
    }

    /*public DefaultMutableTreeNode makeGameTree(int xlocation,int dlocation){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("gameTree");
        if(view.getCurrentPlayer().equals(Colour.Black)){
            recursiveGameTree(xlocation,7,root,0,true);
        }else{
            recursiveGameTree(xlocation,dlocation,root,0,false);
        }
        return root;
    }

    public void recursiveGameTree(int xlocation,int dlocation,DefaultMutableTreeNode parent,int depth,Boolean black){
        Double weight = (double) 0;
        if(depth == 2){
            System.out.println("we've hit the bottom");
            if(black){
                List<Move> moves = graph.generateMoves(Colour.Black,xlocation);
                for(Move move: moves){
                    weight = xScore(move, xlocation);
                    makeNode(weight,move,parent);
                }
            }else{
                List<Move> moves = graph.generateMoves(view.getCurrentPlayer(),dlocation);
                for(Move move: moves){
                    weight = dScore(move, xlocation, dlocation);
                    makeNode(weight,move,parent);
                }
            }
            if(depth % 2 == 0) {
                Double temp = (double) 0, max = (double) 0;
                System.out.println("we've entered the loop");
                for (int i = parent.getChildCount(); i > 0; i--) {
                    DefaultMutableTreeNode child = parent.getNextNode();
                    HashMap<Double, Move> tempmap = (HashMap<Double, Move>) child.getUserObject();
                    Iterator iter = tempmap.keySet().iterator();
                    while (iter.hasNext()) {
                        temp = (double) iter.next();
                    }
                    if (temp > max) {
                        max = temp;
                        verytemporaryMove = tempmap.get(max);
                    }

                }
                newUserObject.clear();
                if (depth == 0) {
                    System.out.println("please boss");
                    newUserObject.put(max, verytemporaryMove);
                } else {
                    HashMap<Double, Move> tempmap = (HashMap<Double, Move>) parent.getUserObject();
                    Iterator iter = tempmap.values().iterator();
                    while (iter.hasNext()) {
                        Move temporaryMove = (Move) iter.next();
                        newUserObject.put(max, temporaryMove);
                    }
                }


                parent.setUserObject(newUserObject);
                parent.removeAllChildren();
            }
        }else{
            if(black){
                if(depth % 2 == 0){
                    weight =  (double) -999;
                }else{
                    weight = (double) 999;
                }
                List<Move> moves = graph.generateMoves(Colour.Black,xlocation);
                for(Move move: moves){
                    recursiveGameTree(fetchTarget(xlocation,move),dlocation, makeNode(weight,move,parent),depth + 1,false);
                }
                System.out.println("Done recurring");
                if(depth % 2 == 0) {
                    Double temp = (double) 0, min = (double) 0;

                    for (int i = parent.getChildCount(); i > 0; i--) {
                        DefaultMutableTreeNode child = parent.getNextNode();
                        HashMap<Double, Move> tempmap = (HashMap<Double, Move>) child.getUserObject();
                        Iterator iter = tempmap.keySet().iterator();
                        while (iter.hasNext()) {
                            temp = (double) iter.next();
                        }
                        if (temp < min) {
                            min = temp;
                            verytemporaryMove = tempmap.get(min);
                        }

                    }
                    newUserObject.clear();
                    if (depth == 0) {
                        System.out.println("please boss");
                        newUserObject.put(min, verytemporaryMove);
                    } else {
                        HashMap<Double, Move> tempmap = (HashMap<Double, Move>) parent.getUserObject();
                        Iterator iter = tempmap.values().iterator();
                        while (iter.hasNext()) {
                            Move temporaryMove = (Move) iter.next();
                            newUserObject.put(min, temporaryMove);
                        }
                    }


                    parent.setUserObject(newUserObject);
                    parent.removeAllChildren();
                }

            }else{
                if(depth % 2 == 0){
                    weight =  (double) -999;
                }else{
                    weight = (double) 999;
                }
                List<Move> moves = graph.generateMoves(view.getCurrentPlayer(),dlocation);
                for(Move move: moves){
                    recursiveGameTree(xlocation,fetchTarget(dlocation,move), makeNode(weight,move,parent),depth + 1,true);
                }
                if(depth % 2 == 0){
                    Double temp = (double) 0,min = (double) 0;

                    for(int i = parent.getChildCount();i > 0;i--){
                        DefaultMutableTreeNode child = parent.getNextNode();
                        HashMap<Double,Move> tempmap = (HashMap<Double,Move>) child.getUserObject();
                        Iterator iter = tempmap.keySet().iterator();
                        while (iter.hasNext()) {
                             temp = (double) iter.next();
                        }
                        if(temp < min){
                            min = temp;
                            verytemporaryMove = tempmap.get(min);
                        }

                    }
                    newUserObject.clear();
                    if(depth == 0){
                        newUserObject.put(min,verytemporaryMove);
                    }else{
                        HashMap<Double,Move> tempmap = (HashMap<Double,Move>) parent.getUserObject();
                        Iterator iter = tempmap.values().iterator();
                        while (iter.hasNext()) {
                            Move temporaryMove = (Move) iter.next();
                            newUserObject.put(min,temporaryMove);
                        }
                    }


                    parent.setUserObject(newUserObject);
                    parent.removeAllChildren();

                }else{
                    Double temp = (double) 0,max = (double) 0;
                    for(int i = parent.getChildCount();i > 0;i--){
                        DefaultMutableTreeNode child = parent.getNextNode();
                        HashMap<Double,Move> tempmap = (HashMap<Double,Move>) child.getUserObject();
                        Iterator iter = tempmap.keySet().iterator();
                        while (iter.hasNext()) {
                            temp = (double) iter.next();
                        }
                        if(temp > max){
                            max = temp;
                            verytemporaryMove = tempmap.get(max);
                        }

                    }
                    newUserObject.clear();
                    if(depth == 0){
                        System.out.println("Please boss");
                        newUserObject.put(max,verytemporaryMove);
                    }else{
                        HashMap<Double,Move> tempmap = (HashMap<Double,Move>) parent.getUserObject();
                        Iterator iter = tempmap.values().iterator();
                        while (iter.hasNext()) {
                            Move temporaryMove = (Move) iter.next();
                            newUserObject.put(max,temporaryMove);
                        }
                    }

                    parent.setUserObject(newUserObject);
                    parent.removeAllChildren();
                }
            }
        }
    }

    private DefaultMutableTreeNode makeNode(Double weight, Move move, DefaultMutableTreeNode parent)
    {
        weightedMove.put(weight,move);
        DefaultMutableTreeNode new_node;
        new_node = new DefaultMutableTreeNode(weightedMove);
        parent.add(new_node);
        return new_node;
    }*/

    public Node testTree(List<Move> moves, int depth, int location, int opLocation, Move value) {
        Node bigNode = new Node(new WeightedMove(value, 999));
        if (depth < 2) {
            if (depth % 2 == 0) {
                for (Move move : moves) {
                    bigNode.fetchValue().changeWeight(-999);
                    bigNode.addChild(testTree(graph.generateMoves(view.getCurrentPlayer(), location), depth+1, opLocation,
                            fetchTarget(location, move), move));
                }
                for (Node node:bigNode.fetchChildren()) {
                    if (node.fetchValue().fetchWeight() > bigNode.fetchValue().fetchWeight()) {
                        bigNode.fetchValue().changeWeight(node.fetchValue().fetchWeight());
                    }
                }
            }
            else {
                for (Move move : moves) {
                    bigNode.fetchValue().changeWeight(999);
                    bigNode.addChild(testTree(graph.generateMoves(view.getCurrentPlayer(), location), depth+1, opLocation,
                            fetchTarget(location, move), move));
                }
                for (Node node:bigNode.fetchChildren()) {
                    if (node.fetchValue().fetchWeight() < bigNode.fetchValue().fetchWeight()) {
                        bigNode.fetchValue().changeWeight(node.fetchValue().fetchWeight());
                    }
                }
            }
        }
        else {
            for (Move move : moves) {
                Node n;
                if (view.getCurrentPlayer() == Colour.Black) {
                    n = new Node(new WeightedMove(move, dScore(move, opLocation, location)));
                }
                else {
                    n = new Node(new WeightedMove(move, dScore(move, opLocation, location)));
                }
                bigNode.addChild(n);
                for (Node node:bigNode.fetchChildren()) {
                    if (node.fetchValue().fetchWeight() < bigNode.fetchValue().fetchWeight()) {
                        bigNode.fetchValue().changeWeight(node.fetchValue().fetchWeight());
                        //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@  "+
                         //       bigNode.fetchValue().fetchWeight());
                    }
                }
                System.out.println(n.fetchValue().fetchMove()+ "  "+n.fetchValue().fetchWeight());
            }
        }
        return bigNode;
    }



    @Override
    public void notify(int location, List<Move> moves, Integer token, Receiver receiver) {
        //TODO: Some clever AI here ...
        Move bestMove = moves.get(0);
        double bestMoveScore = -999;
        double newScore;
        //System.out.println("Playing random move: " + moves.get(0));
        if(view.getCurrentPlayer().equals((Colour.Black))){
            for (Move move:moves) {
                newScore = xScore(move, location);
                if (newScore > bestMoveScore) {
                    bestMove = move;
                    bestMoveScore = newScore;
                }
            }
            for (Transport transport:Transport.values()) {
                System.out.println("AYY LMAO" +view.getPlayerTickets(Colour.Black, Ticket.fromTransport(transport)));
            }
        }else{
            //newUserObject.clear();
            if(view.getPlayerLocation(Colour.Black)!= 0){
                Node root = testTree(moves, 0, location, view.getPlayerLocation(Colour.Black), moves.get(0));
                for (Node node:root.fetchChildren()) {
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    System.out.println(node.fetchValue().fetchMove()+ "     "+ node.fetchValue().fetchWeight());
                    if (node.fetchValue().fetchWeight() > bestMoveScore) {
                        bestMove = node.fetchValue().fetchMove();
                        bestMoveScore = node.fetchValue().fetchWeight();
                    }
                    //System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@      " + be);
                }
            }else{
                for (Move move:moves) {
                    newScore = dScore(move,view.getPlayerLocation(Colour.Black),location);
                    if (newScore > bestMoveScore) {
                        bestMove = move;
                        bestMoveScore = newScore;
                    }
                }
                for (Transport transport:Transport.values()) {
                    System.out.println("AYY LMAO" +view.getPlayerTickets(Colour.Black, Ticket.fromTransport(transport)));
                }
            }


        }

        System.out.println("[CHOSENMOVE]: "+bestMove+" [WEIGHT}: "+ bestMoveScore);
        receiver.playMove(bestMove, token);
    }
}
