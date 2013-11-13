package us.looking_glass.tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: chshrcat
 * Date: 11/12/13
 * Time: 12:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class RankManagingPlayer extends Player {
    private static final long serialVersionUID = 1;

    protected static byte[] ranks(Board board) {
        byte[] ranks = new byte[7];
        ranks[0] = (byte)board.countPlayer(1);
        ranks[1] = (byte)board.countPlayer(2);
        for (int i = 0; i < 8; i++) {
            int count1 = board.countRow(i, 1);
            int count2 = board.countRow(i, 2);
            if (count2 == 0) {
                switch(count1) {
                    case 3:
                        ranks[2]++;
                        break;
                    case 2:
                        ranks[1]++;
                        break;
                    case 1:
                        ranks[0]++;
                        break;
                    default:
                        break;
                }
            } else if (count1 == 0) {
                switch(count2) {
                    case 3:
                        ranks[5]++;
                        break;
                    case 2:
                        ranks[4]++;
                        break;
                    case 1:
                        ranks[3]++;
                        break;
                    default:
                        break;
                }
                if ((i == 1 || i == 4) && count2 > 1)   {
                    ranks[6]++;
                }
            }
        }
        return ranks;
    }

    @Override
    public Player.PlayerInstance newPlayer(Game game, int player) {
        return new PlayerInstance(game, player);
    }

    public final class PlayerInstance extends Player.PlayerInstance {
        private static final long serialVersionUID = 1;
        private Board[] history = new Board[9];
        private byte turn = 0;
        PlayerInstance(Game game, int player) {
            super(game, player);
        }

        private final int score (Board board) {
            byte[] ranks = ranks(board);
            int score = 0;
            int myBase = (player() - 1) * 3;
            int theirBase = 3 - myBase;
            score |= ranks[2 + myBase] << 16;
            score |= (8 - ranks[1 + theirBase]) << 12;
            score |= (8 - ranks[theirBase]) << 8;
            score |= (ranks[1 + myBase]) << 4;
            score |= ranks[myBase];
            if (player() == 2)
                score += ranks[6] << 9;
            return score;
        }

        @Override
        public Point getMove() {
            Board board = game().board();
            if (turn < game().turn()) {
  //              System.out.printf("%d %s\n", turn, board);
                history[turn++] = new Board(board);
            }
            int player = player();
            Point[] moves = board.getLegalMoves();
            Point bestMove = null;
            int bestScore = -1;
            for (int i = 0; i < moves.length; i++) {
                int rnd = prng.nextInt(moves.length - i) + i;
                if (rnd > i) {
                    Point tmp = moves[i];
                    moves[i] = moves[rnd];
                    moves[rnd] = tmp;
                }
                Board cur = new Board(board);
                cur.play(moves[i], player());
                int score = score(cur);
//                System.out.printf("%s %08x\n", cur, score);
                if (score > bestScore) {
                    history[turn] = cur;
                    bestMove = moves[i];
                    bestScore = score;
                }
            }
//            System.out.printf("%d %s\n", turn, history[turn]);
            turn++;
            return bestMove;
        }

        @Override
        public void finish(int status) {
            if (status != -1)
                return;
            if (turn < game().turn())
                history[turn++] = game().board();
            System.out.printf("%d %s\n", turn, Arrays.toString(Arrays.copyOf(history,turn)));
        }
    }
}
