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

import java.io.Serializable;


public class Game implements Serializable {
    private static final long serialVersionUID = 1;
    public static final byte PLAYING = 127;
    public static final byte P1_WIN = 1;
    public static final byte DRAW = 0;
    public static final byte P2_WIN = -1;
    protected int board = 0;
    protected byte status = PLAYING;
    protected byte currentPlayer = 1;
    protected byte turn = 0;
    final Player.PlayerInstance player1, player2;

    public Game(Player player1, Player player2) {
        this.player1 = player1 == null ? null : player1.newPlayer(this, 1);
        this.player2 = player2 == null ? null : player2.newPlayer(this, 2);
    }

    public byte status() {
        return status;
    }

    public int turn() {
        return turn;
    }

    public byte getCurrentPlayer() {
        return currentPlayer;
    }

    public Player.PlayerInstance getPlayer() {
        if (status() != PLAYING)
            return null;
        else
            return currentPlayer == 1 ? player1 : player2;
    }

    public Player.PlayerInstance getPlayer(int player) {
        return player == 1 ? player1 : player2;
    }

    public void play(int x, int y, int player) {
        if (player != currentPlayer)
            throw new IllegalArgumentException("Play out of turn");
        board = Board.play(board, x, y, player);
        board = Board.markWin(board, player);
        if (Board.isMarked(board))
            status = player == 1 ? P1_WIN : P2_WIN;
        turn++;
        currentPlayer = (byte) (3 - currentPlayer);
        if (status == PLAYING && turn == 9)
            status = DRAW;
        if (status != PLAYING) {
            int result = 0;
            switch (status()) {
                case P1_WIN:
                    result = 1;
                    break;
                case P2_WIN:
                    result = -1;
                    break;
            }
            if (player1 != null)
                player1.finish(result);
            if (player2 != null)
                player2.finish(-result);
        }
    }

    public void play(int move, int player) {
        play(Point.x(move), Point.y(move), player);
    }

    public byte run(int turns) {
        while (status() == PLAYING && turns > 0) {
            Player.PlayerInstance state = currentPlayer == 1 ? player1 : player2;
            int move = -1;
            if (state != null)
                move = state.getMove();
            if (move == -1)
                return status();
            play(move, currentPlayer);
            turns--;
        }
        return status();
    }

    public byte run() {
        return run(9);
    }

    public int board() {
        return board;
    }

    @Override
    public String toString() {
        return String.format("Game(Board[%s] Turn[%d] Status[%d] P1[%s] P2[%s]", Board.toString(board()), turn(), status(), player1, player2);
    }
}
