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

import us.looking_glass.util.Well1024a;

import java.io.Serializable;
import java.util.Random;

public class Player implements Serializable {
    public static final Well1024a prng;
    private static final long serialVersionUID = 1;

    static {
        Random random;
        random = new Random();
        int[] seed = new int[32];
        for (int i = 0; i < 32; i++) {
            seed[i] = random.nextInt();
        }
        prng = new Well1024a();
        prng.setSeed(seed);
    }

    public PlayerInstance newPlayer(Game game, int player) {
        return new PlayerInstance(game, player);
    }

    public boolean saveable() {
        return false;
    }

    public class PlayerInstance implements Serializable {
        private final Game game;
        private final byte player;

        PlayerInstance(Game game, int player) {
            this.game = game;
            this.player = (byte) player;
        }

        public final Game game() {
            return game;
        }

        public final int player() {
            return player;
        }

        public Point getMove() {
            return null;
        }

        public void finish(int status) {
        }

        public Player parent() {
            return Player.this;
        }
    }

}
