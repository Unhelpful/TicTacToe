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

package us.looking_glass.tictactoe.cli;


import us.looking_glass.tictactoe.Game;
import us.looking_glass.tictactoe.Player;
import us.looking_glass.util.Serializer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final Map<String, WeakReference<Player>> cache = new HashMap<String, WeakReference<Player>>();
    private static Serializer serializer = new Serializer();


    public static Player fromClass(String playerClassName) {
        Class<Player> playerClass;
        try {
            playerClass = (Class<Player>) Class.forName(playerClassName);
        } catch (ClassNotFoundException e) {
            try {
                playerClass = (Class<Player>) Class.forName(Player.class.getPackage().getName() + "." + playerClassName);
            } catch (ClassNotFoundException en) {
                e.printStackTrace();
                en.printStackTrace();
                return null;
            }
        }
        try {
            return playerClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public static String getFilename(String playerInfo) {
        String[] args = playerInfo.split(":", 2);
        if (args.length != 2)
            return null;
        else if (args[0].compareToIgnoreCase("file") == 0)
            return args[1];
        else return null;
    }

    public static Player getPlayer(String playerInfo) {
        Player result = null;
        WeakReference<Player> ref = cache.get(playerInfo);
        if (ref != null) {
            result = ref.get();
            if (result != null)
                return result;
            else
                cache.remove(playerInfo);
        }
        String[] args = playerInfo.split(":", 2);
        if (args.length != 2)
            return null;
        if (args[0].compareToIgnoreCase("class") == 0)
            result = fromClass(args[1]);
        else if (args[0].compareToIgnoreCase("file") == 0)
            try {
                result = fromFile(args[1]);
            } catch (IOException e) {
            }
        if (result != null)
            cache.put(playerInfo, new WeakReference<Player>(result));
        return result;
    }

    public static Player fromFile(String filename) throws IOException {
        Player result = (Player) serializer.fromFile(filename);
        return result;
    }

    public static void toFile(String filename, Player player) throws IOException {
        serializer.toFile(filename, player);
    }


    public static void main(String[] args) throws IOException {
        Player player1 = getPlayer(args[0]);
        Player player2 = player1;
        if (args[0].compareTo(args[1]) != 0)
            player2 = getPlayer(args[1]);
        int count = 1;
        if (args.length > 2)
            count = Integer.parseInt(args[2]);
        int[] results = new int[3];
        for (int i = 1; i < count + 1; i++) {
            Game game = new Game(player1, player2);
            byte status;
            while (Game.PLAYING == (status = game.run())) ;
            //System.out.printf("Game %d result: %s\n", i, game.status());
            switch (status) {
                case Game.DRAW:
                    results[0]++;
                    break;
                case Game.PLAYING:
                    break;
                case Game.P1_WIN:
                    results[1]++;
                    break;
                case Game.P2_WIN:
                    results[2]++;
                    break;
            }
        }
        System.out.printf("Total results:\nDraw:   %3d\nP1 win: %3d\nP2 win: %3d\n", results[0], results[1], results[2]);
        System.out.println(player1);
        System.out.println(player2);
        if (getFilename(args[0]) != null) {
            toFile(getFilename(args[0]), player1);
        }
        if (player1 != player2 && getFilename(args[1]) != null)
            toFile(getFilename(args[1]), player2);
    }
}
