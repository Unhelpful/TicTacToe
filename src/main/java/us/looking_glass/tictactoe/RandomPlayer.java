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

public class RandomPlayer extends Player {
    private static final long serialVersionUID = 1;

    public PlayerInstance newPlayer(Game game, int player) {
        return new PlayerInstance(game, player);
    }

    public class PlayerInstance extends Player.PlayerInstance {
        private static final long serialVersionUID = 1;

        PlayerInstance(Game game, int player) {
            super(game, player);
        }

        @Override
        public int getMove() {
            int[] moves = Board.getLegalMoves(game().board());
            return moves[prng.nextInt(moves.length)];
        }
    }
}
