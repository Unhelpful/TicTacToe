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

import gnu.trove.map.hash.TIntShortHashMap;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.Serializable;


public class BeanCounterPlayer extends Player implements Serializable {
    private final static long serialVersionUID = 1;
    private final static short loss = -3;
    private final static short draw = -2;
    private final static short win = -1;
    private final TIntShortHashMap scores = new TIntShortHashMap(32, 0.8F, -1, (short) 0x421);

    private short getScores(int ID) {
        return scores.get(ID);
    }

    private static int sum(short beans) {
        return (beans & 0x1f) + ((beans >> 5) & 0x1f) + (beans >> 10);
    }

    private static int get(short beans, int index) {
        return (beans >> (5 * index)) & 0x1f;
    }

    private void incScore(int ID, int incIndex) {
        short beans = getScores(ID);
        beans += 1 << (5 * incIndex);
        if (beans >> (5 * incIndex) == 0x1e)
            beans = (short) (((beans + 0x421) >> 1) & ~0x4210);
        scores.put(ID, beans);
    }

    private boolean
    compareBoards(Board a, Board b) {
        if (a == null)
            return true;
        short aBeans = getScores(a.stateID());
        short bBeans = getScores(b.stateID());
        if (aBeans == win)
            return false;
        if (bBeans == win)
            return true;
        if (bBeans == loss)
            return false;
        if (aBeans == loss)
            return true;
        if (aBeans == draw)
            return true;
        if (bBeans == draw)
            return false;
        int aSum = sum(aBeans);
        int bSum = sum(bBeans);
        if (bBeans == draw)
            return get(aBeans, 0) << 1 < aSum;
        if (get(aBeans, 2) * bSum < get(bBeans, 2) * aSum)
            return true;
        if (get(aBeans, 0) * bSum > get(bBeans, 0) * aSum)
            return true;
        return false;
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
        private final int[] history;
        private boolean losing = false;
        byte turn = 0;

        PlayerInstance(Game game, int player) {
            super(game, player);
            history = new int[5];
        }

        PlayerInstance(Game game, int player, ObjectInput oi) throws IOException, ClassNotFoundException {
            super(game, player);
            int ver = oi.readInt();
            switch (ver) {
                case 1:
                    history = (int[]) oi.readObject();
                    losing = oi.readBoolean();
                    turn = oi.readByte();
                    break;
                default:
                    throw new IOException("invalid version field");
            }
        }

        @Override
        public Point getMove() {
            int player = game().getCurrentPlayer();
            Board board = game().board();
            Point[] moves = board.getLegalMoves();
            Board bestBoard = null;
            Point bestMove = null;
            int IDs[] = new int[moves.length];
            if (losing)
                return moves[prng.nextInt(moves.length)];
            for (int i = 0; i < moves.length; i++) {
                int rnd = prng.nextInt(moves.length - i) + i;
                if (rnd > i) {
                    Point tmp = moves[i];
                    moves[i] = moves[rnd];
                    moves[rnd] = tmp;
                }
                Board curBoard = new Board(board);
                IDs[i] = curBoard.stateID();
                curBoard.play(moves[i], player);
                if (compareBoards(bestBoard, curBoard)) {
                    bestBoard = curBoard;
                    bestMove = moves[i];
                }
            }
            short score = getScores(bestBoard.stateID());
            if (score == loss) {
                for (int i = 0; i < IDs.length; i++) {
                    scores.remove(IDs[i]);
                }
                losing = true;
            } else
                history[turn++] = bestBoard.stateID();
            return bestMove;
        }

        @Override
        public void finish(int status) {
            int incIndex = status + 1;
            scores.put(history[--turn], (short) (status - 2));
            for (int i = 0; i < turn; i++)
                incScore(history[i], incIndex);
        }
    }

}
