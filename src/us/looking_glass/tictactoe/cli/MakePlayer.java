package us.looking_glass.tictactoe.cli;

import us.looking_glass.tictactoe.Player;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: chshrcat
 * Date: 11/10/13
 * Time: 1:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class MakePlayer {
    public static void main (String[] args) throws IOException {
        Player player = Main.fromClass(args[0]);
        Main.toFile(args[1], player);
    }
}
