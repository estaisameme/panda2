package scotlandyard;

import java.io.IOException;
import java.util.*;

/**
 * A class to perform all of the game logic.
 */

public class ScotlandYard implements ScotlandYardView, Receiver {

    protected MapQueue<Integer, Token> queue;
    protected Integer gameId;
    protected Random random;
    protected Integer numberOfDetectives;
    protected List<Boolean> rounds;
    protected ScotlandYardGraph graph;
    protected List<PlayerData> listOfPlayerData = new ArrayList<PlayerData>();
    protected Colour currentPlayer;
    protected Integer lastKnownLocOfMrX;
    protected int roundsMrXPlayed;
    /**
     * Constructs a new ScotlandYard object. This is used to perform all of the game logic.
     *
     * @param numberOfDetectives the number of detectives in the game.
     * @param rounds the List of booleans determining at which rounds Mr X is visible.
     * @param graph the graph used to represent the board.
     * @param queue the Queue used to put pending moves onto.
     * @param gameId the id of this game.
     */
    public ScotlandYard(Integer numberOfDetectives, List<Boolean> rounds, ScotlandYardGraph graph, MapQueue<Integer, Token> queue, Integer gameId) {
        this.queue = queue;
        this.gameId = gameId;
        this.random = new Random();
        this.numberOfDetectives = numberOfDetectives;
        this.rounds = rounds;
        this.graph = graph;
        this.lastKnownLocOfMrX = 0;
        this.roundsMrXPlayed = 0;
        this.currentPlayer = Colour.Black;

    }

    /**
     * Starts playing the game.
     */
    public void startRound() {
        if (isReady() && !isGameOver()) {
            turn();
        }
    }

    /**
     * Notifies a player when it is their turn to play.
     */
    public void turn() {
        Integer token = getSecretToken();
        queue.put(gameId, new Token(token, getCurrentPlayer(), System.currentTimeMillis()));
        notifyPlayer(getCurrentPlayer(), token);
    }

    /**
     * Plays a move sent from a player.
     *
     * @param move the move chosen by the player.
     * @param token the secret token which makes sure the correct player is making the move.
     */
    public void playMove(Move move, Integer token) {
        Token secretToken = queue.get(gameId);
        if (secretToken != null && token == secretToken.getToken()) {
            queue.remove(gameId);
            play(move);
            nextPlayer();
            startRound();
        }
    }

    /**
     * Returns a random integer. This is used to make sure the correct player
     * plays the move.
     * @return a random integer.
     */
    private Integer getSecretToken() {
        return random.nextInt();
    }

    /**
     * Notifies a player with the correct list of valid moves.
     *
     * @param colour the colour of the player to be notified.
     * @param token the secret token for the move.
     */
    private void notifyPlayer(Colour colour, Integer token) {
        //TODO:
        for(PlayerData player: listOfPlayerData){
            if(player.getColour().equals((colour))){
                player.getPlayer().notify(player.getLocation(), validMoves(colour), token, this);
            }
        }
    }

    /**
     * Passes priority onto the next player whose turn it is to play.
     */
    protected void nextPlayer() {
        Boolean updatePlayer = false;

        for(PlayerData player:listOfPlayerData){
            if(updatePlayer.equals(true)){
                currentPlayer = player.getColour();
                break;
            }
            if(player.getColour().equals(currentPlayer)){
                if(player.getColour().equals(Colour.Black)){
                    roundsMrXPlayed++;
                }
                if(listOfPlayerData.indexOf(player) == (listOfPlayerData.size() - 1)){
                    currentPlayer = Colour.Black;
                }
                updatePlayer = true;
            }
        }
    }

    /**
     * Allows the game to play a given move.
     *
     * @param move the move that is to be played.
     */
    protected void play(Move move) {
        if (move instanceof MoveTicket) play((MoveTicket) move);
        else if (move instanceof MoveDouble) play((MoveDouble) move);
        else if (move instanceof MovePass) play((MovePass) move);
    }

    /**
     * Plays a MoveTicket.
     *
     * @param move the MoveTicket to play.
     */
    protected void play(MoveTicket move) {
        //TODO:
        if(move.colour.equals(currentPlayer)){
            PlayerData currentDetective = getActualPlayer(currentPlayer);
            PlayerData mrX = getActualPlayer(Colour.Black);

            if(move.colour.equals(Colour.Black)){//Mr X
                mrX.removeTicket(move.ticket);
                mrX.setLocation(move.target);

            }else{ //Detectives
                currentDetective.removeTicket(move.ticket);
                mrX.addTicket(move.ticket);
                currentDetective.setLocation(move.target);
            }
        }
    }

    /**
     * Plays a MoveDouble.
     *
     * @param move the MoveDouble to play.
     */
    protected void play(MoveDouble move) {
        //TODO:
        if(move.colour.equals(currentPlayer)){
            PlayerData currentDetective = getActualPlayer(currentPlayer);
            PlayerData mrX = getActualPlayer(Colour.Black);
            if(move.colour.equals(Colour.Black)){//Mr X
                roundsMrXPlayed++;
                mrX.removeTicket(move.move1.ticket);
                mrX.removeTicket(move.move2.ticket);
                mrX.removeTicket(Ticket.Double);
                mrX.setLocation(move.move2.target);
            }
        }
    }


    public PlayerData getActualPlayer(Colour colour){
        for(PlayerData player: listOfPlayerData){
            if(player.getColour().equals(colour)){
                return player;
            }
        }
        return null;
    }

    /**
     * Plays a MovePass.
     *
     * @param move the MovePass to play.
     */
    protected void play(MovePass move) {

    }

    /**
     * Returns the list of valid moves for a given player.
     *
     * @param player the player whose moves we want to see.
     * @return the list of valid moves for a given player.
     */
    public List<Move> validMoves(Colour player) {
        //TODO:
        List<Move> listOfMoves = new ArrayList<Move>();
        for(PlayerData lstPlayer:listOfPlayerData){
            if(lstPlayer.getColour().equals(player)){
                graph.generateMoves(graph,lstPlayer,listOfMoves,listOfPlayerData);
            }
        }
        return listOfMoves;
    }

    /**
     * Allows spectators to join the game. They can only observe as if they
     * were a detective: only MrX's revealed locations can be seen.
     *
     * @param spectator the spectator that wants to be notified when a move is made.
     */
    public void spectate(Spectator spectator) {
        //TODO:
    }

    /**
     * Allows players to join the game with a given starting state. When the
     * last player has joined, the game must ensure that the first player to play is Mr X.
     *
     * @param player the player that wants to be notified when he must make moves.
     * @param colour the colour of the player.
     * @param location the starting location of the player.
     * @param tickets the starting tickets for that player.
     * @return true if the player has joined successfully.
     */
    public boolean join(Player player, Colour colour, int location, Map<Ticket, Integer> tickets) {
        try {
            listOfPlayerData.add(new PlayerData(player,colour,location,tickets));
        }catch (Exception e){return false;}
        return true;
    }

    /**
     * A list of the colours of players who are playing the game in the initial order of play.
     * The length of this list should be the number of players that are playing,
     * the first element should be Colour.Black, since Mr X always starts.
     *
     * @return The list of players.
     */
    public List<Colour> getPlayers() {
        List<Colour> ethnicConflict = new ArrayList<Colour>();
        for(PlayerData player: listOfPlayerData){
            ethnicConflict.add(player.getColour());
        }
        return ethnicConflict;
    }

    /**
     * Returns the colours of the winning players. If Mr X it should contain a single
     * colour, else it should send the list of detective colours
     *
     * @return A set containing the colours of the winning players
     */
    public Set<Colour> getWinningPlayers() {
        //TODO:
        return new HashSet<Colour>();
    }

    /**
     * The location of a player with a given colour in its last known location.
     *
     * @param colour The colour of the player whose location is requested.
     * @return The location of the player whose location is requested.
     * If Black, then this returns 0 if MrX has never been revealed,
     * otherwise returns the location of MrX in his last known location.
     * MrX is revealed in round n when {@code rounds.get(n)} is true.
     */
    public int getPlayerLocation(Colour colour) {
        PlayerData player = getActualPlayer(colour);
        if(player.getColour().equals(Colour.Black)){
            if(rounds.get(getRound()).equals(true)){
                lastKnownLocOfMrX = player.getLocation();
            }else{
            }
            return lastKnownLocOfMrX;
        }else {
            return player.getLocation();
        }
    }

    /**
     * The number of a particular ticket that a player with a specified colour has.
     *
     * @param colour The colour of the player whose tickets are requested.
     * @param ticket The type of tickets that is being requested.
     * @return The number of tickets of the given player.
     */
    public int getPlayerTickets(Colour colour, Ticket ticket) {
        for(PlayerData player: listOfPlayerData){
            if(player.getColour().equals(colour)){
                return player.getTickets().get(ticket);
            }
        }
        return -1;
    }

    /**
     * The game is over when MrX has been found or the agents are out of
     * tickets. See the rules for other conditions.
     *
     * @return true when the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return false;
    }

    /**
     * A game is ready when all the required players have joined.
     *
     * @return true when the game is ready to be played, false otherwise.
     */
    public boolean isReady() {
        if(this.numberOfDetectives.equals(listOfPlayerData.size() - 1)){
            return true;
        }
        return false;
    }

    /**
     * The player whose turn it is.
     *
     * @return The colour of the current player.
     */
    public Colour getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * The round number is determined by the number of moves MrX has played.
     * Initially this value is 0, and is incremented for each move MrX makes.
     * A double move counts as two moves.
     *
     * @return the number of moves MrX has played.
     */
    public int getRound() {

        return roundsMrXPlayed;
    }

    /**
     * A list whose length-1 is the maximum number of moves that MrX can play in a game.
     * The getRounds().get(n) is true when MrX reveals the target location of move n,
     * and is false otherwise.
     * Thus, if getRounds().get(0) is true, then the starting location of MrX is revealed.
     *
     * @return a list of booleans that indicate the turns where MrX reveals himself.
     */
    public List<Boolean> getRounds() {
        return this.rounds;

    }

}
