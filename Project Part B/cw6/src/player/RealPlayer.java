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

    public double xScore(Move move, int xLocation, int dLocation, int dStartingLocaton, int xStartingLocation, Colour detective) {
        List <Colour> players = view.getPlayers();
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();
        double weight = 0;
        double currentDistance;
        double newDistance;
        int numEdges = 0;

        for (Transport transport : Transport.values()) {
            ticketMap.put(transport, view.getPlayerTickets(detective, Ticket.fromTransport(transport)));
        }
        currentDistance = dijkstra.getRoute(dStartingLocaton, xStartingLocation, ticketMap).size();
        newDistance = dijkstra.getRoute(dLocation, fetchTarget(xLocation, move), ticketMap).size();
        weight = (newDistance - currentDistance) * 10;
        //System.out.println(weight);

        numEdges = graph.getEdgesFrom(graph.getNode(fetchTarget(xLocation, move))).size();
        //weight = weight + numEdges;

        System.out.println("[MOVE]:"+ move + " [WEIGHT]: "+weight);
        Random random = new Random();
        return weight;
    }

    public double dScore(Move move, int xlocation,int dlocation,int dStartingLoc,int xStartingLoc) {
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();
        double weight = 0;
        double currentDistance;
        double newDistance;
        int numEdges = 0;
        for (Transport transport : Transport.values()) {
            ticketMap.put(transport, view.getPlayerTickets(view.getCurrentPlayer(), Ticket.fromTransport(transport)));
        }
        if (xlocation != 0) {
            currentDistance = dijkstra.getRoute(dStartingLoc, xStartingLoc, ticketMap).size();
            newDistance = dijkstra.getRoute(fetchTarget(dlocation, move), xlocation, ticketMap).size();
            weight = (currentDistance - newDistance)*10;
          //  System.out.println("BIG WEIGHT=     "+weight);
        }
        numEdges = graph.getEdgesFrom(graph.getNode(fetchTarget(dlocation, move))).size();
        //weight = weight + numEdges;

        return weight;
    }

    public Node testTree(List<Move> moves, int depth, int location, int opLocation, Move value, Colour opponent,int dStartingLoc,int xStartingLoc,double alpha, double beta) {
        Node bigNode = new Node(new WeightedMove(value, -999));
        double bestWeight = -999;
        double weight = 0;
        if (depth < 2) {
            if (depth % 2 == 0) {
                bigNode.fetchValue().changeWeight(alpha);
                for (Move move : moves) {
                    bigNode.addChild(testTree(graph.generateMoves(opponent, opLocation), depth + 1, fetchTarget(location, move),
                            opLocation, move, opponent,dStartingLoc,xStartingLoc,bigNode.fetchValue().fetchWeight(),beta));

                }
                for (Node node:bigNode.fetchChildren()) {
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
                    bigNode.addChild(testTree(graph.generateMoves(view.getCurrentPlayer(), location), depth+1, location,
                            fetchTarget(opLocation, move), move, opponent,dStartingLoc,xStartingLoc,alpha,bigNode.fetchValue().fetchWeight()));
                }
                for (Node node:bigNode.fetchChildren()) {
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
                    weight = xScore(move, location, opLocation, dStartingLoc, xStartingLoc, opponent);
                    n = new Node(new WeightedMove(move, weight));
                }
                else {
                    weight = dScore(move, opLocation, location,dStartingLoc,xStartingLoc);
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

        //System.out.println("Playing random move: " + moves.get(0));
        if(view.getCurrentPlayer().equals((Colour.Black))){
            Node root = testTree(moves, 0, location, view.getPlayerLocation(getClosestDetective(location)), moves.get(0), getClosestDetective(location), view.getPlayerLocation(getClosestDetective(location)),location,(double) -999,(double) 999);
            bestMove = root.fetchValue().fetchMove();
            bestMoveScore = root.fetchValue().fetchWeight();
        }else{
            if(view.getPlayerLocation(Colour.Black) != 0){
                Node root = testTree(moves, 0, location, view.getPlayerLocation(Colour.Black), moves.get(0), Colour.Black,location,view.getPlayerLocation(Colour.Black),-999,999);
                /*for (Node node:root.fetchChildren()) {
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    System.out.println(node.fetchValue().fetchMove()+ "     "+ node.fetchValue().fetchWeight());
                    if (node.fetchValue().fetchWeight() > bestMoveScore) {
                       // bestMove = node.fetchValue().fetchMove();
                      //  bestMoveScore = node.fetchValue().fetchWeight();
                    }
                }
                System.out.println("old method:-" + bestMoveScore);*/
                bestMove = root.fetchValue().fetchMove();
                bestMoveScore = root.fetchValue().fetchWeight();
            }else{
                for (Move move:moves) {
                    newScore = dScore(move,view.getPlayerLocation(Colour.Black),location,location,view.getPlayerLocation(Colour.Black));
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
