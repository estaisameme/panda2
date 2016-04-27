package player;

import scotlandyard.*;
import swing.algorithms.Dijkstra;

import java.io.IOException;
import java.util.*;
import graph.Edge;

/**
 * Created by User on 21/04/2016.
 */
public class RealPlayer implements Player{
    Dijkstra dijkstra;
    ScotlandYardView view;
    ScotlandYardGraph graph;

    public RealPlayer(ScotlandYardView sView, String graphFileName) throws IOException {
        //TODO: A better AI makes use of `view` and `graphFilename`.
        dijkstra = new Dijkstra(graphFileName);
        view = sView;
        ScotlandYardGraphReader graphReader = new ScotlandYardGraphReader();
        graph = graphReader.readGraph(graphFileName);

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
                    //weight = weight + dijkstra.getRoute(view.getPlayerLocation(player), fetchTarget(location, move), ticketMap).size();
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


    @Override
    public void notify(int location, List<Move> moves, Integer token, Receiver receiver) {
        //TODO: Some clever AI here ...
        Move bestMove = moves.get(0);
        double bestMoveScore = -999;
        double newScore;
        //System.out.println("Playing random move: " + moves.get(0));
        for (Move move:moves) {
            newScore = xScore(move, location);
            if (newScore > bestMoveScore) {
                bestMove = move;
                bestMoveScore = newScore;
            }
        }
        System.out.println("[CHOSENMOVE]: "+bestMove+" [WEIGHT}: "+ bestMoveScore);
        receiver.playMove(bestMove, token);
    }
}
