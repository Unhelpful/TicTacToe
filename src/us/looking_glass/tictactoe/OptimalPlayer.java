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

public class OptimalPlayer extends Player {
    private static final long serialVersionUID = 1;

    protected static byte[] ranks(Board board) {
        byte[] ranks = new byte[7];
        ranks[0] = (byte) board.countPlayer(1);
        ranks[1] = (byte) board.countPlayer(2);
        for (int i = 0; i < 8; i++) {
            int count1 = board.countRow(i, 1);
            int count2 = board.countRow(i, 2);
            if (count2 == 0) {
                switch (count1) {
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
                switch (count2) {
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
                if ((i == 1 || i == 4) && count2 > 1) {
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
        private byte turn = 0;

        PlayerInstance(Game game, int player) {
            super(game, player);
        }

        private final int score(Board board) {
            byte[] ranks = ranks(board);
            int score = 0;
            int myBase = (player() - 1) * 3;
            int theirBase = 3 - myBase;
            score |= ranks[2 + myBase] << 16;
            score |= (8 - ranks[1 + theirBase]) << 12;
            score |= (ranks[1 + myBase]) << 8;
            score |= (8 - ranks[theirBase]) << 4;
            score |= ranks[myBase];
            if (player() == 2)
                score += ranks[6] << 5;
            return score;
        }

        @Override
        public Point getMove() {
            Board board = game().board();
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
                if (score > bestScore) {
                    bestMove = moves[i];
                    bestScore = score;
                }
            }
            turn++;
            return bestMove;
        }

        @Override
        public void finish(int status) {
            if (status != -1)
                return;
        }
    }
}
