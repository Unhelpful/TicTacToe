/**
 Copyright 2013 Andrew Mahone

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package us.looking_glass.tictactoe;

import us.looking_glass.util.Serializer;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.Serializable;
import java.util.Arrays;


public abstract class LMSPlayerBase extends Player {
    private final static long serialVersionUID = 1;
    protected final int featureSize;
    protected final double[] weights;

    final static boolean debug = false;

    public LMSPlayerBase(int featureSize) {
        this.featureSize = featureSize;
        weights = new double[(this.featureSize + 1) * 2];
        Arrays.fill(weights, .1);
    }

    protected abstract byte[] features(int board);

    private int playerOffset(int player) {
        return (player - 1) * (featureSize + 1);
    }

    double score(byte[] features, int player) {
        double score = weights[playerOffset(player)];
        for (int i = 0; i < featureSize; i++)
            score += features[i] * weights[playerOffset(player) + i + 1];
        return score;
    }

    @Override
    public boolean saveable() {
        return true;
    }

    @Override
    public Player.PlayerInstance newPlayer(Game game, int player) {
        return new PlayerInstance(game, player);
    }

    public final class PlayerInstance extends Player.PlayerInstance implements Serializable {
        private final static long serialVersionUID = 1;
        private final byte[][] features;
        private final double[] scores;
        private byte turn = 0;

        PlayerInstance(Game game, int player) {
            super(game, player);
            features = new byte[5][];
            scores = new double[5];
        }

        public int getMove() {
            int board = game().board();
            int player = player();
            int[] moves = Board.getLegalMoves(board);
            double bestScore = Float.NEGATIVE_INFINITY;
            int bestMove = -1;
            byte[] bestFeatures = null;
            for (int i = 0; i < moves.length; i++) {
                int rnd = prng.nextInt(moves.length - i) + i;
                if (rnd > i) {
                    moves[i] ^= moves[rnd];
                    moves[rnd] ^= moves[i];
                    moves[i] ^= moves[rnd];
                }
                int cur = Board.play(board, moves[i], player);
                byte[] features = features(cur);
                double score = score(features, player);
                if (score > bestScore) {
                    bestScore = score;
                    bestFeatures = features;
                    bestMove = moves[i];
                }
            }
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
