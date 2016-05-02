package player;

import scotlandyard.Move;

/**
 * Created by William on 28/04/2016.
 */
//This is the weighted move class, the object used to create the gametree
public class WeightedMove {
    private Move move;
    private double weight;

    public WeightedMove(Move m, double w) {
        move = m;
        weight = w;
    }

    public Move fetchMove() {
        return move;
    }

    public double fetchWeight(){
       return weight;
    }

    public void changeWeight(double w) {
        weight = w;
    }

    public void changeMove(Move m) {
        move = m;
    }
}
