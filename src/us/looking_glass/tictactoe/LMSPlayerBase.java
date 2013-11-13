package us.looking_glass.tictactoe;

import us.looking_glass.util.Serializer;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: chshrcat
 * Date: 10/18/13
 * Time: 3:38 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class LMSPlayerBase extends Player {
    private final static long serialVersionUID = 1;
    protected final int featureSize;
    protected final double[] weights;

    final static boolean debug = false;
    public LMSPlayerBase(int featureSize) {
        this.featureSize = featureSize;
        weights = new double[(this.featureSize +1) * 2];
        Arrays.fill(weights, .1);
    }

    protected abstract byte[] features(Board board);

    private int playerOffset(int player) {
        return (player - 1) * (featureSize + 1);
    }

    double score(byte[] features, int player) {
        double score = weights[playerOffset(player)];
        for (int i = 0; i < featureSize; i++)
            score += features[i] * weights[playerOffset(player) + i+1];
        return score;
    }

    @Override
    public Player.PlayerInstance newPlayer(Game game, int player) {
        return new PlayerInstance(game, player);
    }

    @Override
    public Player.PlayerInstance newPlayer(Game game, int player, ObjectInput oi) {
        try {
            return new PlayerInstance(game, player, oi);
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    public final class PlayerInstance extends Player.PlayerInstance implements Serializable {
        private final static long serialVersionUID = 1;
        private final Board[] boards;
        private final byte[][] features;
        private final double[] scores;
        private byte turn = 0;

        PlayerInstance(Game game, int player) {
            super(game, player);
            boards = new Board[5];
            features = new byte[5][];
            scores = new double[5];
        }

        PlayerInstance(Game game, int player, ObjectInput oi) throws IOException, ClassNotFoundException {
            super(game, player);
            int version = oi.readInt();
            switch (version) {
                case 1:
                    this.boards = (Board[]) oi.readObject();
                    this.features = (byte[][]) oi.readObject();
                    this.scores = (double[]) oi.readObject();
                    this.turn = oi.readByte();
                default:
                    throw new IOException("invalid version field");
            }
        }

        public Point getMove() {
            Board board = game().board();
            int player = player();
            Point[] moves = board.getLegalMoves();
            double bestScore = Float.NEGATIVE_INFINITY;
            Board bestBoard = null;
            Point bestMove = null;
            byte[] bestFeatures = null;
            for (int i = 0; i < moves.length; i++) {
                int rnd = prng.nextInt(moves.length - i) + i;
                if (rnd > i) {
                    Point tmp = moves[i];
                    moves[i] = moves[rnd];
                    moves[rnd] = tmp;
                }
                Board cur = new Board(board);
                cur.play(moves[i], player);
                byte[] features = features(cur);
                double score = score(features, player);
                if (score > bestScore) {
                    bestScore = score;
                    bestFeatures = features;
                    bestBoard = cur;
                    bestMove = moves[i];
                }
            }
            boards[turn] = bestBoard;
            features[turn] = bestFeatures;
            scores[turn] = bestScore;
            turn++;
            return bestMove;
        }

        @Override
        public void finish(int status) {
            double[] trainingScore = new double[turn];
            if (status == 0)
                return;
            trainingScore[turn - 1] = status;
            for (int i = 0; i < turn - 1; i++) {
                trainingScore[i] = scores[i + 1];
            }
            for (int i = 0; i < turn; i++) {
                weights[playerOffset(player())] += 0.01 * (trainingScore[i] - scores[i]);
                for (int j = 0; j < featureSize; j++) {
                    weights[playerOffset(player()) + j + 1] += 0.01 * (trainingScore[i] - scores[i]) * features[i][j];
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        LMSPlayerBase p = (LMSPlayerBase) new Serializer().fromFile(args[0]);
        System.out.println(Arrays.toString(p.weights));
    }
}
