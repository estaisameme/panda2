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

      public void playInModel(Move move) {
          play(move);
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
        Point destination = new Point(1, 3);
        assertEquals("The up/left jump is not being recognised correctly",true,model.removePieceInModel(location,destination));
        destination.x = 1;
        destination.y = 7;
        assertEquals("The down/left jump is not being recognised correctly",true,model.removePieceInModel(location,destination));
        destination.x = 5;
        destination.y = 7;
        assertEquals("The down/right jump is not being recognised correctly",true,model.removePieceInModel(location,destination));
        destination.x = 5;
        destination.y = 3;
        assertEquals("The up/right jump is not being recognised correctly",true,model.removePieceInModel(location,destination));
    }

    @Test
    public void testMovePiece() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece piece = new Piece(Colour.Red, 3, 3);
        pieces.add(piece);
        TestModel model = new TestModel("Test", null, Colour.Red, pieces);
        Move move = new Move(piece, 2, 2);

        model.playInModel(move);
        assertEquals("up/left move not played correctly", piece, model.getPiece(2,2));

        move = new Move(piece, 3, 1);
        model.playInModel(move);
        assertEquals("up/right move not played correctly", piece, model.getPiece(3,1));

        move = new Move(piece, 2, 2);
        model.playInModel(move);
        assertEquals("down/left move not played correctly", piece, model.getPiece(2,2));

        move = new Move(piece, 3, 3);
        model.playInModel(move);
        assertEquals("down/right move not played correctly", piece, model.getPiece(3,3));
    }

    @Test
    public void testJumpRemove() {
        Set<Piece> pieces = new HashSet<Piece>();
        Piece pieceA = new Piece(Colour.Red, 3, 3);
        Piece pieceB = new Piece(Colour.White, 4, 4);
        pieces.add(pieceA);
        pieces.add(pieceB);
        TestModel model = new TestModel("Test", null, Colour.Red, pieces);

        Move move = new Move(pieceA, 5, 5);
        model.playInModel(move);
        assertEquals("down/right jump not played correctly", null, model.getPiece(4,4));

    }

    @Test
    public void testInitialise() {
        Set<Piece> testPieces = new HashSet<Piece>();
        Piece pieceWhite = new Piece(Colour.White, 0, 0);
        Piece pieceRed = new Piece(Colour.Red, 0, 0);
        for (int j = 0; j < 3; j ++) {
            for (int i = 0; i < 8; i ++) {
                if ((i + j) % 2 != 0) {
                    pieceWhite.setX(i);
                    pieceWhite.setY(j);
                    testPieces.add(pieceWhite);
                }
            }
        }
        for (int j = 5; j < 8; j ++) {
            for (int i = 0; i < 8; i ++) {
                if ((i + j) % 2 != 0) {
                    pieceRed.setX(i);
                    pieceRed.setY(j);
                    testPieces.add(pieceRed);
                }
            }
        }
        DraughtsModel model = new DraughtsModel("This is a test",null);
        assertEquals("Board not set up correctly", testPieces, model.getPieces());
    }

}
