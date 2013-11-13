package us.looking_glass.tictactoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: chshrcat
 * Date: 10/18/13
 * Time: 3:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class CLIPlayer extends Player implements Serializable {
    private final static String playerChars = " XO";

    public CLIPlayer() {
    }

    private static void prompt(Board board, int player) {
        System.out.println("  0 1 2\n");
        for (int y = 0; y < 3; y++) {
            System.out.printf("%d ", y);
            for (int x = 0; x < 3; x++) {
                System.out.print(playerChars.charAt(board.get(x, y)));
                System.out.print(x == 2 ? "\n" : "|");
            }
            if (y == 2)
                System.out.printf("%s's turn, enter number of row, then column, separated by space: ", playerChars.charAt(player));
            else
                System.out.println("  -+-+-");

        }
    }



    public PlayerInstance newPlayer(Game game, int player) {
        return new PlayerInstance(game, player) {
            @Override
            public Point getMove() {
                Board board = game().board();
                int player = player();
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                prompt(board, player);
                String[] input = new String[0];
                try {
                    input = in.readLine().split("\\s+");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                if (input.length != 2) return null;
                try {
                    int y = Integer.parseInt(input[0]);
                    int x = Integer.parseInt(input[1]);
                    if (board.get(x, y) != 0)
                        return null;
                    System.out.printf("Playing at %d,%d\n", x, y);
                    return new Point(x, y);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        };
    }
}
