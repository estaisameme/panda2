package player;

import scotlandyard.*;
import swing.algorithms.Dijkstra;
import java.util.*;

/**
 * Created by User on 21/04/2016.
 */
public class RealPlayer implements Player{


    public RealPlayer(ScotlandYardView view, String graphFilename) {
        //TODO: A better AI makes use of `view` and `graphFilename`.
        Dijkstra dijkstra = new Dijkstra(graphFilename);
        PlayerData player = view.getPlayer(Colour.Black);
        Map tickets = player.getTickets();
        view.
    }

    public int score(List<Move> moves,Map<Transport,Integer> tickets,Dijkstra dijkstra){
        List<Integer> scores;
        scores = dijkstra.getRoute(1, 2,tickets );
        return 0;
    }

    @Override
    public void notify(int location, List<Move> moves, Integer token, Receiver receiver) {
        //TODO: Some clever AI here ...
        System.out.println("Getting random move");
        // Collections.shuffle(moves);
        // System.out.println("Moves: " + moves);
        System.out.println("Playing random move: " + moves.get(0));
        receiver.playMove(moves.get(0), token);
    }
}
