package scotlandyard;

public interface MapQueue<X, Y> {

    Entry<X, Y> pop();

    Entry<X, Y> peek();

    Y get(X key);

    void remove(X key);

    void put(X key, Y value);

    interface Entry<X, Y> {

        X getKey();

        Y getValue();

    }

}
