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


    public double xScore(Move move, int xLocation, int dLocation, double currentDistance, Colour detective) {
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();
        double weight = 0;
        double newDistance;

        for (Transport transport : Transport.values()) {
            ticketMap.put(transport, view.getPlayerTickets(detective, Ticket.fromTransport(transport)));
        }

        newDistance = dijkstra.getRoute(dLocation, fetchTarget(xLocation, move), ticketMap).size();
        weight = weight + (newDistance - currentDistance) * 10;

        return weight;
    }

    public double dScore(Move move, int xlocation,int dlocation,double currentDistance) {
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();
        double weight = 0;
        double newDistance;
        int numEdges = 0;
        for (Transport transport : Transport.values()) {
            ticketMap.put(transport, view.getPlayerTickets(view.getCurrentPlayer(), Ticket.fromTransport(transport)));
        }
        if (xlocation != 0) {
            newDistance = dijkstra.getRoute(fetchTarget(dlocation, move), xlocation, ticketMap).size();
            weight = (currentDistance - newDistance)*10;
          //  System.out.println("BIG WEIGHT=     "+weight);
        }
        numEdges = graph.getEdgesFrom(graph.getNode(fetchTarget(dlocation, move))).size();
        weight = weight + numEdges;
        if (view.getRounds().get(view.getRound()) && fetchTarget(dlocation, move) == xlocation) {
            return 999;
        }
        return weight;

    }

    public Node testTree(List<Move> moves, int depth, int location, int opLocation, Move value, Colour opponent, double currentDistance,double alpha, double beta, long time) {
        Node bigNode = new Node(new WeightedMove(value, -999));
        double bestWeight = -999;
        double weight = 0;
        if (depth < 2) {
            if (depth % 2 == 0) {
                bigNode.fetchValue().changeWeight(alpha);
                for (Move move : moves) {
                    Node node = testTree(graph.generateMoves(opponent, opLocation), depth + 1, fetchTarget(location, move),
                            opLocation, move, opponent,currentDistance,bigNode.fetchValue().fetchWeight(),beta, time);
                    bigNode.addChild(node);
                    if (node.fetchValue().fetchWeight() > bigNode.fetchValue().fetchWeight()) {
                        bigNode.fetchValue().changeWeight(node.fetchValue().fetchWeight());
                        if(depth == 0){
                            bigNode.fetchValue().changeMove(node.fetchValue().fetchMove());
                        }
                    }
                    if(beta <= bigNode.fetchValue().fetchWeight()){
                        break;
                    }

                }
            }
            else {
                bigNode.fetchValue().changeWeight(beta);
                for (Move move : moves) {
                    Node node = testTree(graph.generateMoves(view.getCurrentPlayer(), location), depth+1, location,
                            fetchTarget(opLocation, move), move, opponent,currentDistance,alpha,bigNode.fetchValue().fetchWeight(), time);
                    bigNode.addChild(node);
                    if (node.fetchValue().fetchWeight() < bigNode.fetchValue().fetchWeight()) {
                        bigNode.fetchValue().changeWeight(node.fetchValue().fetchWeight());
                    }
                    if (bigNode.fetchValue().fetchWeight() <= alpha){
                        break;
                    }
                }
            }
        }
        else {
            for (Move move : moves) {
                Node n;
                if (view.getCurrentPlayer() == Colour.Black) {
                    if ((System.currentTimeMillis() - time) < 13000) {
                        weight = xScore(move, location, opLocation, currentDistance, opponent);
                    }
                    else {
                        weight = -999;
                    }
                    n = new Node(new WeightedMove(move, weight));
                }
                else {
                    weight = dScore(move, opLocation, location,currentDistance);
                    n = new Node(new WeightedMove(move, weight));
                }
                bigNode.addChild(n);
                if (weight > bestWeight) bestWeight = weight;
                if(beta <= bestWeight){
                    break;
                }
            }
            bigNode.fetchValue().changeWeight(bestWeight);
        }
        return bigNode;
    }


    public Colour getClosestDetective(int location) {
        Colour closestDetective = view.getPlayers().get(1);
        int closestDetectiveDistance = 999;
        int newDistance = 0;
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();

        for (Colour player:view.getPlayers()) {
            if (player != Colour.Black) {
                ticketMap = new HashMap<Transport, Integer>();
                for (Transport transport : Transport.values()) {
                    ticketMap.put(transport, view.getPlayerTickets(player, Ticket.fromTransport(transport)));
                }
                newDistance = dijkstra.getRoute(view.getPlayerLocation(player), location,
                        ticketMap).size();
               // System.out.println("@@@@@@@@@@@@    distance: "+newDistance);
                if (newDistance < closestDetectiveDistance) {
                    closestDetective = player;
                    closestDetectiveDistance = newDistance;
                }
            }
        }
        return closestDetective;
    }

    @Override
    public void notify(int location, List<Move> moves, Integer token, Receiver receiver) {
        //TODO: Some clever AI here ...
        Move bestMove = moves.get(0);
        double bestMoveScore = -999;
        double newScore;
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();

        //System.out.println("Playing random move: " + moves.get(0));
        if(view.getCurrentPlayer().equals((Colour.Black))){
            for (Transport transport : Transport.values()) {
                ticketMap.put(transport, view.getPlayerTickets(getClosestDetective(location), Ticket.fromTransport(transport)));
            }
            Node root = testTree(moves, 0, location, view.getPlayerLocation(getClosestDetective(location)), moves.get(0),
                    getClosestDetective(location), dijkstra.getRoute(view.getPlayerLocation(getClosestDetective(location)), location, ticketMap).size(),
                    (double) -999,(double) 999, System.currentTimeMillis());
            bestMove = root.fetchValue().fetchMove();
            bestMoveScore = root.fetchValue().fetchWeight();
        }else{
            for (Transport transport : Transport.values()) {
                ticketMap.put(transport, view.getPlayerTickets(view.getCurrentPlayer(), Ticket.fromTransport(transport)));
            }
            if(view.getPlayerLocation(Colour.Black) != 0){
                Node root = testTree(moves, 0, location, view.getPlayerLocation(Colour.Black), moves.get(0), Colour.Black,
                        dijkstra.getRoute(location, view.getPlayerLocation(Colour.Black), ticketMap).size(),-999,999, System.currentTimeMillis());
                bestMove = root.fetchValue().fetchMove();
                bestMoveScore = root.fetchValue().fetchWeight();
            }else{
                for (Move move:moves) {
                    newScore = dScore(move,view.getPlayerLocation(Colour.Black),location,999);
                    if (newScore > bestMoveScore) {
                        bestMove = move;
                        bestMoveScore = newScore;
                    }
                }
            }


        }

        System.out.println("[CHOSENMOVE]: "+bestMove+" [WEIGHT}: "+ bestMoveScore);
        System.out.println("CLOSEST DETECTIVE = " +getClosestDetective(location));
        receiver.playMove(bestMove, token);
    }
}
