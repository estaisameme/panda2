package scotlandyard;

import graph.*;

import java.util.*;
import java.util.function.BooleanSupplier;

public class ScotlandYardGraph extends UndirectedGraph<Integer, Transport> {

    public List<Move> generateMoves(ScotlandYardGraph graph,PlayerData lstPlayer,List<Move> listOfMoves,List<PlayerData> listOfPLayerData) {
        //TODO: Use this to help generate valid moves if you would like.
        Boolean doubleYes = false;
        Boolean detectiveCollision = false;
        if(lstPlayer.getTickets().get(Ticket.Double) > 0){doubleYes = true;}

        for(Edge<Integer, scotlandyard.Transport> edge: graph.getEdgesFrom(graph.getNode(lstPlayer.getLocation()))){
            for(PlayerData players:listOfPLayerData){
                if(edge.getTarget().getIndex().equals(players.getLocation()) && !(players.getColour().equals(Colour.Black))){
                    detectiveCollision = true;
                    break;
                }
            }
            if(detectiveCollision){}else {
                if (lstPlayer.getTickets().get(Ticket.fromTransport(edge.getData())) > 0) {
                    if (doubleYes) {
                        for (Edge<Integer, scotlandyard.Transport> edge1 : graph.getEdgesFrom(edge.getTarget())) {
                            for (PlayerData players : listOfPLayerData) {
                                if (edge.getTarget().getIndex().equals(players.getLocation()) && !(players.getColour().equals(Colour.Black))) {
                                    detectiveCollision = true;
                                    break;
                                }
                            }
                            if (detectiveCollision) {
                            } else {
                                if (edge.getData().equals(edge1.getData())) {
                                    if (lstPlayer.getTickets().get(Ticket.fromTransport(edge.getData())) + lstPlayer.getTickets().get(Ticket.fromTransport(edge1.getData())) > 1) {
                                        Move move3 = MoveDouble.instance(lstPlayer.getColour(), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex()), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge1.getData()), edge1.getTarget().getIndex()));
                                        listOfMoves.add(move3);
                                    }
                                } else {
                                    if (lstPlayer.getTickets().get(Ticket.fromTransport(edge1.getData())) > 0) {
                                        Move move3 = MoveDouble.instance(lstPlayer.getColour(), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex()), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge1.getData()), edge1.getTarget().getIndex()));
                                        listOfMoves.add(move3);
                                    }
                                }
                            }
                        }
                    }else{
                        Move move = MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex());
                        listOfMoves.add(move);
                    }
                }
            }
        }
        return listOfMoves;
    }

}
