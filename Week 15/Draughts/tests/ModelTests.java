import draughts.*;

import java.awt.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

public class ModelTests {

    private class TestPlayer implements Player {

        @Override
        public Move notify(Set<Move> moves) {
            if (moves.iterator().hasNext()) return moves.iterator().next();
            return null;
        }

    }

    public class TestModel extends DraughtsModel {

      public TestModel(String gameName, Player player, Colour currentPlayer, Set<Piece> pieces) {
          super(gameName, player, currentPlayer, pieces);
      }

      public TestModel(String gameName, Player player) {
          super(gameName, player);
      }

      public boolean removePieceInModel(Point position, Point destination) {
          return removePiece(position, destination);
      }

      public void turnInModel() {
          turn();
      }

    }

    @Test
    public void testGameNameIsCorrect() throws Exception {
        DraughtsModel model = new DraughtsModel("Test", null);

        assertEquals("The game name should be the same as the one passed in", "Test", model.getGameName());
    }

    @Test
    public void testCurrentPlayerIsRedAtStartOfGame() throws Exception {
        DraughtsModel model = new DraughtsModel("Game", null);

        assertEquals("The red player should be the current player at the beginning of the game.", Colour.Red, model.getCurrentPlayer());
    }

    @Test
    public void testCurrentPlayerUpdatesCorrectly() throws Exception {
        TestModel model = new TestModel("Test", new TestPlayer());

        assertEquals("The current player should be red initially", Colour.Red, model.getCurrentPlayer());

        model.turnInModel();
        assertEquals("The current player should be white after one turn", Colour.White, model.getCurrentPlayer());

        model.turnInModel();
        assertEquals("The current player should be red after two turns", Colour.Red, model.getCurrentPlayer());
    }

    @Test
    public void testCorrectPieceIsReturned() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece piece = new Piece(Colour.Red, 3, 5);
        pieces.add(piece);

        DraughtsModel model = new DraughtsModel("Test", null, Colour.Red, pieces);

        assertEquals("The correct piece should be returned", piece, model.getPiece(3, 5));
    }

    @Test
    public void testOutOfBoundPieceIsNotReturned() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece piece = new Piece(Colour.Red, 8, 5);
        pieces.add(piece);

        DraughtsModel model = new DraughtsModel("Test", null, Colour.Red, pieces);

        assertEquals("The piece should not be returned as it is out of bounds.", null, model.getPiece(3, 5));
    }

    @Test
    public void testRemovePieceWorksAsIntended() throws Exception {
        TestModel model = new TestModel("Test", new TestPlayer());
        Point location = new Point(3,5);
        Point destination = new Point(1, 5);
        assertEquals("The up/left jump is not being recognised correctly",true,model.removePieceInModel(location,destination));
        destination.x = 1;
        destination.y = 3;
        assertEquals("The down/left jump is not being recognised correctly",true,model.removePieceInModel(location,destination));
        destination.x = 5;
        destination.y = 3;
        assertEquals("The down/right jump is not being recognised correctly",true,model.removePieceInModel(location,destination));
        destination.x = 5;
        destination.y = 5;
        assertEquals("The up/right jump is not being recognised correctly",true,model.removePieceInModel(location,destination));
    }
}
