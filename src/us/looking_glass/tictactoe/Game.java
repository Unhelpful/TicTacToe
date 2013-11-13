package us.looking_glass.tictactoe;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: chshrcat
 * Date: 10/17/13
 * Time: 3:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class Game implements Serializable {
    private static final long serialVersionUID = 1;
    public static final byte PLAYING = 127;
    public static final byte P1_WIN = 1;
    public static final byte DRAW = 0;
    public static final byte P2_WIN = -1;
    protected Board board = new Board();
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
        board.play(x, y, player);
        for (int i = 0; i < 8; i++) {
            if (board.countRow(i, player) == 3) {
                status = player == 1 ? P1_WIN : P2_WIN;
                break;
            }
        }
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

    public void play(Point move, int player) {
        play(move.x , move.y, player);
    }

    public byte run(int turns) {
        while (status() == PLAYING && turns > 0) {
            Player.PlayerInstance state = currentPlayer == 1 ? player1 : player2;
            Point move = null;
            if (state != null)
                move = state.getMove();
            if (move == null)
                return status();
            play(move, currentPlayer);
            turns--;
        }
        return status();
    }

    public byte run() {
        return run(9);
    }

    public Board board() {
        return board;
    }

    @Override
    public String toString() {
        return String.format("Game(Board[%s] Turn[%d] Status[%d] P1[%s] P2[%s]", board(), turn(), status(), player1, player2);
    }
}
