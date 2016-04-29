package player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by William on 28/04/2016.
 */
public class Node
{
    private List<Node> children = null;
    private WeightedMove value;

    public Node(WeightedMove value)
    {
        this.children = new ArrayList<>();
        this.value = value;
    }

    public void addChild(Node child)
    {
        children.add(child);
    }

    public WeightedMove fetchValue() {
        return this.value;
    }

    public List<Node> fetchChildren() {
        return this.children;
    }

}