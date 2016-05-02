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
        dijkstra = new Dijkstra(graphFileName);
        view = sView;
        ScotlandYardGraphReader graphReader = new ScotlandYardGraphReader();
        try{
            graph = graphReader.readGraph(graphFileName);
        }
        catch (Exception e) {

        }
    }

    //Used to split up types of moves, and return the appropriate target, for use in getting updated positions
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

    //Returns the score/weight of a move made by Mr X
    public double xScore(Move move, int xLocation, int dLocation, double currentDistance, Colour detective) {
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();
        double weight = 0;
        double newDistance;
        //for loop that generates the map of moves of the closest detective, for use with the dijkstra's getRoute function
        for (Transport transport : Transport.values()) {
            ticketMap.put(transport, view.getPlayerTickets(detective, Ticket.fromTransport(transport)));
        }
        //Calculates the distance between Mr X and the closest detective, after movement
        newDistance = dijkstra.getRoute(dLocation, fetchTarget(xLocation, move), ticketMap).size();
        weight = weight + (newDistance - currentDistance) * 10;

        return weight;
    }

    //Returns the score/weight of a move made by a detective
    public double dScore(Move move, int xlocation,int dlocation,double currentDistance) {
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();
        double weight = 0;
        double newDistance;
        int numEdges = 0;
        //for loop that generates the map of moves of the current detective, for use with the dijkstra's getRoute function
        for (Transport transport : Transport.values()) {
            ticketMap.put(transport, view.getPlayerTickets(view.getCurrentPlayer(), Ticket.fromTransport(transport)));
        }
        //If Mr X has not been seen yet, weigh the move based solely on how connected the possible target nodes are
        if (xlocation != 0) {
            //weigh the move using the net change of distance between the current detective and Mr X
            newDistance = dijkstra.getRoute(fetchTarget(dlocation, move), xlocation, ticketMap).size();
            weight = (currentDistance - newDistance)*10;
        }
        //Add the number of edges the target node has coming from it to the weight, this helps when differentiating between identical weight moves
        numEdges = graph.getEdgesFrom(graph.getNode(fetchTarget(dlocation, move))).size();
        weight = weight + numEdges;
        return weight;

    }

    //Recursive function that builds the game tree by creating child nodes off of a 'bignode'
    public Node buildGameTree(List<Move> moves, int depth, int location, int opLocation, Move value, Colour opponent, double currentDistance,double alpha, double beta, long time) {
        Node bigNode = new Node(new WeightedMove(value, -999));//The 'bignode' is the parent node, from which child nodes are birthed, the weighted move is a custom object made up of a weight and a move
        double bestWeight = -999;
        double weight = 0;

        if (depth < 2) { //If we are not at terminal depth
            if (depth % 2 == 0) { //If it is the maximising player's layer of the tree
                bigNode.fetchValue().changeWeight(alpha); //Sets the bignode's starting weight to that of alpha

                for (Move move : moves) {
                    //Creates the child node, by recursively calling buildGameTree, but for the minimising player
                    Node node = buildGameTree(graph.generateMoves(opponent, opLocation), depth + 1, fetchTarget(location, move),
                            opLocation, move, opponent,currentDistance,bigNode.fetchValue().fetchWeight(),beta, time);
                    bigNode.addChild(node);
                    //Updates the parent node with the best child weight(in this case the biggest)
                    if (node.fetchValue().fetchWeight() > bigNode.fetchValue().fetchWeight()) { //
                        bigNode.fetchValue().changeWeight(node.fetchValue().fetchWeight());
                        //If at the top level update the parent node with the child move as well
                        if(depth == 0){
                            bigNode.fetchValue().changeMove(node.fetchValue().fetchMove());
                        }
                    }
                    //Prune if the bignode's weight is above/equal to the beta weight
                    if(beta <= bigNode.fetchValue().fetchWeight()){
                        break;
                    }

                }
            }
            else { //If minimising player
                bigNode.fetchValue().changeWeight(beta); //Sets the bignode's starting weight to that of beta
                for (Move move : moves) {
                    //Creates the child node, by recursively calling buildGameTree, but for the maximising player
                    Node node = buildGameTree(graph.generateMoves(view.getCurrentPlayer(), location), depth+1, location,
                            fetchTarget(opLocation, move), move, opponent,currentDistance,alpha,bigNode.fetchValue().fetchWeight(), time);
                    bigNode.addChild(node);
                    //Updates the parent node with the best child weight(in this case the smallest)
                    if (node.fetchValue().fetchWeight() < bigNode.fetchValue().fetchWeight()) {
                        bigNode.fetchValue().changeWeight(node.fetchValue().fetchWeight());
                    }
                    //Prune if the bignode's weight is below/equal to the alpha weight
                    if (bigNode.fetchValue().fetchWeight() <= alpha){
                        break;
                    }
                }
            }
        }
        else {//If at the terminal depth
            for (Move move : moves) {
                Node n;
                if (view.getCurrentPlayer() == Colour.Black) {
                    if ((System.currentTimeMillis() - time) < 13000) {//As long as less than 13 seconds have elapsed, score as normal(This is generally only relevant to Mr X as his move possibilities are far greater than that of a detective)
                        weight = xScore(move, location, opLocation, currentDistance, opponent);
                    }
                    else { //else give massive negative weight(effectively discards the rest of the tree)
                        weight = -999;
                    }
                    n = new Node(new WeightedMove(move, weight));//Create terminal node
                }
                else {
                    weight = dScore(move, opLocation, location,currentDistance);
                    n = new Node(new WeightedMove(move, weight));
                }
                bigNode.addChild(n);
                if (weight > bestWeight) bestWeight = weight;//Change the bignode weight to the best child weight
                if(beta <= bestWeight){//Prune if greater than or equal to beta weight
                    break;
                }
            }
            bigNode.fetchValue().changeWeight(bestWeight);
        }
        return bigNode;
    }

    //Function to return the nearest detective to a given location, currently used for Mr X
    public Colour getClosestDetective(int location) {
        Colour closestDetective = view.getPlayers().get(1);
        int closestDetectiveDistance = 999;
        int newDistance = 0;
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();
        //Iterates through each player to determine which is closest
        for (Colour player:view.getPlayers()) {
            if (player != Colour.Black) {
                ticketMap = new HashMap<Transport, Integer>();
                for (Transport transport : Transport.values()) {
                    ticketMap.put(transport, view.getPlayerTickets(player, Ticket.fromTransport(transport)));
                }
                newDistance = dijkstra.getRoute(view.getPlayerLocation(player), location,
                        ticketMap).size();
               // The closest detective is changed if the next detective to be compared is closer than the previous
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

        Move bestMove = moves.get(0);
        double bestMoveScore = -999;
        double newScore;
        Map <Transport, Integer> ticketMap = new HashMap<Transport, Integer>();

        //If the current player is Mr X
        if(view.getCurrentPlayer().equals((Colour.Black))){
            for (Transport transport : Transport.values()) {
                ticketMap.put(transport, view.getPlayerTickets(getClosestDetective(location), Ticket.fromTransport(transport)));
            }
            //Create the root node of the gametree and following nodes
            Node root = buildGameTree(moves, 0, location, view.getPlayerLocation(getClosestDetective(location)), moves.get(0),
                    getClosestDetective(location), dijkstra.getRoute(view.getPlayerLocation(getClosestDetective(location)), location, ticketMap).size(),
                    (double) -999,(double) 999, System.currentTimeMillis());
            //Set the chosen move and chosen move score to the ones stored in the root node
            bestMove = root.fetchValue().fetchMove();
            bestMoveScore = root.fetchValue().fetchWeight();
        }else{//If a detective
            for (Transport transport : Transport.values()) {
                ticketMap.put(transport, view.getPlayerTickets(view.getCurrentPlayer(), Ticket.fromTransport(transport)));
            }
            //If Mr X has been revealed
            if(view.getPlayerLocation(Colour.Black) != 0){
                //Build the gametree
                Node root = buildGameTree(moves, 0, location, view.getPlayerLocation(Colour.Black), moves.get(0), Colour.Black,
                        dijkstra.getRoute(location, view.getPlayerLocation(Colour.Black), ticketMap).size(),-999,999, System.currentTimeMillis());
                //Search the top layer of tree, this is done instead of just pulling from the root node so that some custom weighting can be implemented
                for(Node node:root.fetchChildren()){
                    //If the move will result in a detective win immediately, do it
                    if (view.getRounds().get(view.getRound()) && fetchTarget(location, node.fetchValue().fetchMove()) == view.getPlayerLocation(Colour.Black)) {
                        bestMove = node.fetchValue().fetchMove();
                        bestMoveScore = node.fetchValue().fetchWeight();
                        break;
                    }else{
                        //Else evaluate against current best move
                        if (node.fetchValue().fetchWeight() > bestMoveScore) {
                            bestMove = node.fetchValue().fetchMove();
                            bestMoveScore = node.fetchValue().fetchWeight();
                        }
                    }
                }
            }else{//If Mr X has not been revealed yet
                for (Move move:moves) {
                    //Create weights based on interconnectivity of neighbour nodes
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
        receiver.playMove(bestMove, token);//Send move to server
    }
}
