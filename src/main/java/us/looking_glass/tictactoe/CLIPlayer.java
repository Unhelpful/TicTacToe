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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;


public class CLIPlayer extends Player implements Serializable {
    private final static String playerChars = " XO";

    public CLIPlayer() {
    }

    private static void prompt(int board, int player) {
        System.out.println("  0 1 2\n");
        for (int y = 0; y < 3; y++) {
            System.out.printf("%d ", y);
            for (int x = 0; x < 3; x++) {
                System.out.print(playerChars.charAt(Board.get(board, x, y)));
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
            public int getMove() {
                int board = game().board();
                int player = player();
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                prompt(board, player);
                String[] input = new String[0];
                try {
                    input = in.readLine().split("\\s+");
                } catch (IOException e) {
                    e.printStackTrace();
                    return -1;
                }
                if (input.length != 2) return -1;
                try {
                    int y = Integer.parseInt(input[0]);
                    int x = Integer.parseInt(input[1]);
                    if (Board.get(board, x, y) != 0)
                        return -1;
                    System.out.printf("Playing at %d,%d\n", x, y);
                    return Point.point(x, y);
                } catch (IllegalArgumentException e) {
                    return -1;
                }
            }
        };
    }
}
