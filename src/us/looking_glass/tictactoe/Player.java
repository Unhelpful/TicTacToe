package us.looking_glass.tictactoe;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well1024a;
import us.looking_glass.util.Serializer;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: chshrcat
 * Date: 10/16/13
 * Time: 4:48 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Player implements Serializable {
    public static final RandomGenerator prng;
    private static final long serialVersionUID = 1;
    static {
        Random random;
        random = new Random();
        int[] seed = new int[32];
        for (int i = 0; i < 32; i++) {
            seed[i] = random.nextInt();
        }
        prng = new Well1024a(seed);

    }
    public PlayerInstance newPlayer(Game game, int player) {
        return new PlayerInstance(game, player);
    }

    public PlayerInstance newPlayer(Game game, int player, ObjectInput oi) {
        return  newPlayer(game, player);
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
