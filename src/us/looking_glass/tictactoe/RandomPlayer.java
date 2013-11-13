package us.looking_glass.tictactoe;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: chshrcat
 * Date: 10/18/13
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class RandomPlayer extends Player {
    private static final long serialVersionUID = 1;

    public PlayerInstance newPlayer (Game game, int player) {
        return new PlayerInstance(game, player);
    }

    public class PlayerInstance extends Player.PlayerInstance {
        private static final long serialVersionUID = 1;
        PlayerInstance(Game game, int player) {
            super(game, player);
        }

        @Override
        public Point getMove() {
            Point[] moves = game().board().getLegalMoves();
            return moves[prng.nextInt(moves.length)];
        }
    }
}
