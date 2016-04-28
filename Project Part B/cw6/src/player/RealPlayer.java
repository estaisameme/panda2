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
    Map<Double,Move> weightedMove;
    Map<Double,Move> newUserObject;
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
        int numEdges = 0;
        for (Transport transport : Transport.values()) {
            ticketMap.put(transport, view.getPlayerTickets(view.getCurrentPlayer(), Ticket.fromTransport(transport)));
        }
        if (xlocation != 0) {
            System.out.println("HERE WE GO " + xlocation);
            currentDistance = dijkstra.getRoute(xlocation, dlocation, ticketMap).size();
            newDistance = dijkstra.getRoute(xlocation, fetchTarget(dlocation, move), ticketMap).size();
            weight = weight - (((newDistance - currentDistance) / newDistance) * 10.0);
        }
        numEdges = graph.getEdgesFrom(graph.getNode(fetchTarget(dlocation, move))).size();
        weight = weight + numEdges;
        System.out.println("[MOVE]:"+ move + " [WEIGHT]: "+weight);

        return weight;
    }

    public DefaultMutableTreeNode makeGameTree(int xlocation,int dlocation){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("gameTree");
        if(view.getCurrentPlayer().equals(Colour.Black)){
            recursiveGameTree(xlocation,7,root,0,true);
        }else{
            recursiveGameTree(xlocation,dlocation,root,0,false);
        }
        return root;
    }

    public void recursiveGameTree(int dlocation,int xlocation,DefaultMutableTreeNode parent,int depth,Boolean black){
        Double weight = (double)0;
        if(depth == 2){
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
        }else{
            if(black){
                if(depth % 2 == 0){
                    weight =  (double) -999;
                }else{
                    weight = (double) 999;
                }
                List<Move> moves = graph.generateMoves(Colour.Black,xlocation);
                for(Move move: moves){
                    recursiveGameTree(xlocation,fetchTarget(dlocation,move), makeNode(weight,move,parent),depth + 1,true);
                }
                if(depth % 2 == 0){
                    weight =  (double) -999;
                }else{
                    weight = (double) 999;
                }

            }else{
                if(depth % 2 == 0){
                    weight =  (double) -999;
                }else{
                    weight = (double) 999;
                }
                List<Move> moves = graph.generateMoves(view.getCurrentPlayer(),dlocation);
                for(Move move: moves){
                    recursiveGameTree(fetchTarget(xlocation,move),dlocation, makeNode(weight,move,parent),depth + 1,false);
                }
                if(depth % 2 == 0){
                    Double temp = (double) 0,min = (double) 0;

                    for(int i = parent.getChildCount();i > 0;i--){
                        DefaultMutableTreeNode child = parent.getNextNode();
                        Map<Double,Move> tempmap = (Map<Double,Move>) child.getUserObject();
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
                        Map<Double,Move> tempmap = (Map<Double,Move>) parent.getUserObject();
                        Iterator iter = tempmap.values().iterator();
                        while (iter.hasNext()) {
                            Move temporaryMove = (Move) iter.next();
                            newUserObject.put(min,temporaryMove);
                        }
                    }


                    parent.setUserObject(newUserObject);

                }else{
                    Double temp = (double) 0,max = (double) 0;
                    for(int i = parent.getChildCount();i > 0;i--){
                        DefaultMutableTreeNode child = parent.getNextNode();
                        Map<Double,Move> tempmap = (Map<Double,Move>) child.getUserObject();
                        Iterator iter = tempmap.keySet().iterator();
                        while (iter.hasNext()) {
                            temp = (double) iter.next();
                        }
                        if(temp > max){
                            max = temp;
                        }

                    }
                    newUserObject.clear();
                    if(depth == 0){
                        newUserObject.put(max,verytemporaryMove);
                    }else{
                        Map<Double,Move> tempmap = (Map<Double,Move>) parent.getUserObject();
                        Iterator iter = tempmap.values().iterator();
                        while (iter.hasNext()) {
                            Move temporaryMove = (Move) iter.next();
                            newUserObject.put(max,temporaryMove);
                        }
                    }

                    parent.setUserObject(newUserObject);
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
            newUserObject.clear();
            newUserObject = (Map<Double,Move>) makeGameTree(view.getPlayerLocation(Colour.Black),view.getPlayerLocation(view.getCurrentPlayer())).getUserObject();
            Iterator iter = newUserObject.values().iterator();
            while (iter.hasNext()) {
               bestMove = (Move) iter.next();

            }
            Iterator iter1 = newUserObject.keySet().iterator();
            while (iter.hasNext()) {
                bestMoveScore = (double) iter.next();
            }

        }

        System.out.println("[CHOSENMOVE]: "+bestMove+" [WEIGHT}: "+ bestMoveScore);
        receiver.playMove(bestMove, token);
    }
}
