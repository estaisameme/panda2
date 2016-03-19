package scotlandyard;

import graph.Edge;
import graph.UndirectedGraph;

import java.util.List;

public class ScotlandYardGraph extends UndirectedGraph<Integer, Transport> {

    public List<Move> generateMoves(ScotlandYardGraph graph,PlayerData lstPlayer,List<Move> listOfMoves,List<PlayerData> listOfPLayerData) {
        //TODO: Use this to help generate valid moves if you would like.
        Boolean doubleYes = false;
        Boolean detectiveCollision = false;
        Boolean boat = false;
        int numSecrets = lstPlayer.getTickets().get(Ticket.Secret);

        if(lstPlayer.getTickets().get(Ticket.Double) > 0){doubleYes = true;}

        for(Edge<Integer, scotlandyard.Transport> edge: graph.getEdgesFrom(graph.getNode(lstPlayer.getLocation()))){
            detectiveCollision = false;
            for(PlayerData players:listOfPLayerData){
                if(edge.getTarget().getIndex().equals(players.getLocation()) && !(players.getColour().equals(Colour.Black))){
                    detectiveCollision = true;
                    break;
                }
            }
            if(detectiveCollision){}else {

                if (lstPlayer.getTickets().get(Ticket.fromTransport(edge.getData())) > 0 || (numSecrets > 0 && edge.getData().equals(Transport.Boat))) {

                    if (doubleYes) {

                        if(!(edge.getData().equals(Transport.Boat))){
                            Move move = MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex());
                            listOfMoves.add(move);
                        }else{
                            boat = true;
                        }
                        if (numSecrets > 0 ||(edge.getData().equals(Transport.Boat))) {
                            Move secretMove = MoveTicket.instance(lstPlayer.getColour(), Ticket.Secret, edge.getTarget().getIndex());
                            listOfMoves.add(secretMove);
                        }
                        for (Edge<Integer, scotlandyard.Transport> edge1 : graph.getEdgesFrom(edge.getTarget())) {
                            for (PlayerData players : listOfPLayerData) {
                                if (edge1.getTarget().getIndex().equals(players.getLocation()) && !(players.getColour().equals(Colour.Black))) {
                                    detectiveCollision = true;
                                    break;
                                }
                            }
                            if (detectiveCollision) {
                            } else {
                                if (numSecrets > 1 || ((edge1.getData().equals(Transport.Boat)) && boat)) {
                                    Move secretMove = MoveDouble.instance(lstPlayer.getColour(), MoveTicket.instance(lstPlayer.getColour(), Ticket.Secret, edge.getTarget().getIndex()), MoveTicket.instance(lstPlayer.getColour(), Ticket.Secret, edge1.getTarget().getIndex()));
                                    listOfMoves.add(secretMove);
                                }
                                if(!(edge1.getData().equals(Transport.Boat))){
                                    if(boat){
                                        if (lstPlayer.getTickets().get(Ticket.fromTransport(edge1.getData())) > 0) {
                                            Move move3 = MoveDouble.instance(lstPlayer.getColour(), MoveTicket.instance(lstPlayer.getColour(), Ticket.Secret, edge.getTarget().getIndex()), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge1.getData()), edge1.getTarget().getIndex()));
                                            listOfMoves.add(move3);
                                        }
                                    }
                                    else {
                                        if (edge.getData().equals(edge1.getData())) {
                                            if (lstPlayer.getTickets().get(Ticket.fromTransport(edge.getData())) > 1) {
                                                Move move3 = MoveDouble.instance(lstPlayer.getColour(), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex()), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge1.getData()), edge1.getTarget().getIndex()));
                                                listOfMoves.add(move3);
                                                if(numSecrets > 0){
                                                    Move anotherGoddamnMove = MoveDouble.instance(lstPlayer.getColour(), MoveTicket.instance(lstPlayer.getColour(), Ticket.Secret, edge.getTarget().getIndex()), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge1.getData()), edge1.getTarget().getIndex()));
                                                    Move yetAnotherGoddamnMove = MoveDouble.instance(lstPlayer.getColour(), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex()), MoveTicket.instance(lstPlayer.getColour(), Ticket.Secret, edge1.getTarget().getIndex()));
                                                    listOfMoves.add(anotherGoddamnMove);
                                                    listOfMoves.add(yetAnotherGoddamnMove);
                                                }
                                            }
                                        } else {
                                            if (lstPlayer.getTickets().get(Ticket.fromTransport(edge1.getData())) > 0) {
                                                Move move3 = MoveDouble.instance(lstPlayer.getColour(), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex()), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge1.getData()), edge1.getTarget().getIndex()));
                                                listOfMoves.add(move3);
                                                if(numSecrets > 0){
                                                    Move anotherGoddamnMove = MoveDouble.instance(lstPlayer.getColour(), MoveTicket.instance(lstPlayer.getColour(), Ticket.Secret, edge.getTarget().getIndex()), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge1.getData()), edge1.getTarget().getIndex()));
                                                    Move yetAnotherGoddamnMove = MoveDouble.instance(lstPlayer.getColour(), MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex()), MoveTicket.instance(lstPlayer.getColour(), Ticket.Secret, edge1.getTarget().getIndex()));
                                                    listOfMoves.add(anotherGoddamnMove);
                                                    listOfMoves.add(yetAnotherGoddamnMove);
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    if(!boat){
                                        if(numSecrets > 0){
                                            Move goddamnBoatMove = MoveDouble.instance(lstPlayer.getColour(),MoveTicket.instance(lstPlayer.getColour(),Ticket.fromTransport(edge.getData()),edge.getTarget().getIndex()),MoveTicket.instance(lstPlayer.getColour(),Ticket.Secret,edge1.getTarget().getIndex()));
                                            listOfMoves.add((goddamnBoatMove));
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        if(!(edge.getData().equals(Transport.Boat))){
                            Move move = MoveTicket.instance(lstPlayer.getColour(), Ticket.fromTransport(edge.getData()), edge.getTarget().getIndex());
                            listOfMoves.add(move);
                        }
                        if (numSecrets > 0 ||(edge.getData().equals(Transport.Boat))) {
                            Move secretMove = MoveTicket.instance(lstPlayer.getColour(), Ticket.Secret, edge.getTarget().getIndex());
                            listOfMoves.add(secretMove);
                        }
                    }
                }
            }
        }
        if (listOfMoves.size() == 0 && lstPlayer.getColour() != Colour.Black) {
            Move move = MovePass.instance(lstPlayer.getColour());
            listOfMoves.add(move);
        }
        return listOfMoves;
    }

}
