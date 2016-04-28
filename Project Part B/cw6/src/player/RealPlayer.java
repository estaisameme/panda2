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

    public double dScore(Move move, int location) {
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();
        double weight = 0;
        double currentDistance;
        double newDistance;
        int numEdges = 0;
        for (Transport transport : Transport.values()) {
            ticketMap.put(transport, view.getPlayerTickets(view.getCurrentPlayer(), Ticket.fromTransport(transport)));
        }
        if (view.getPlayerLocation(Colour.Black) != 0) {
            System.out.println("HERE WE GO " + view.getPlayerLocation(Colour.Black));
            currentDistance = dijkstra.getRoute(view.getPlayerLocation(Colour.Black), location, ticketMap).size();
            newDistance = dijkstra.getRoute(view.getPlayerLocation(Colour.Black), fetchTarget(location, move), ticketMap).size();
            weight = weight - (((newDistance - currentDistance) / newDistance) * 10.0);
        }
        numEdges = graph.getEdgesFrom(graph.getNode(fetchTarget(location, move))).size();
        weight = weight + numEdges;
        System.out.println("[MOVE]:"+ move + " [WEIGHT]: "+weight);

        return weight;
    }

    public void makeGameTree(List<Move> moves,int location){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("gameTree");
        DefaultMutableTreeNode current_parent;
        current_parent = root;
        for(int i = 0;i < 2;i++){
            for(Move move: moves){
                Double weight = xScore(move, location);
                makeNode(weight,move,current_parent);
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
        for (Move move:moves) {
            if (view.getCurrentPlayer() == Colour.Black) {
                newScore = xScore(move, location);
            }
            else {
                newScore = dScore(move, location);
            }
            if (newScore > bestMoveScore) {
                bestMove = move;
                bestMoveScore = newScore;
            }
        }
        for (Transport transport:Transport.values()) {
            System.out.println("AYY LMAO" +view.getPlayerTickets(Colour.Black, Ticket.fromTransport(transport)));
        }


        System.out.println("[CHOSENMOVE]: "+bestMove+" [WEIGHT}: "+ bestMoveScore);
        receiver.playMove(bestMove, token);
    }
}
