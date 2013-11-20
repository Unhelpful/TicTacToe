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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Board implements Serializable {
    private static final long serialVersionUID = 1;
    private int state;
    private static final int contentsMask = 0x3FFFF;
    private static final String playerChars = " XO";
    private static final String playerCharsRep = "-XO#";

    public Board(Board board) {
        state = board.state;
    }

    public Board() {
        state = 0;
    }

    public Board(int state) {
        this.state = state;
    }

    public int getContents() {
        return state & contentsMask;
    }

    private int getRowStart(int rowNum) {
        switch (rowNum) {
            case 0:
                return 0;
            case 1:
                return 6;
            case 2:
                return 12;
            case 3:
                return 0;
            case 4:
                return 2;
            case 5:
                return 4;
            case 6:
                return 0;
            case 7:
                return 4;
        }
        return -1;
    }

    private int getRowInc(int rowNum) {
        switch (rowNum) {
            case 0:
            case 1:
            case 2:
                return 2;
            case 3:
            case 4:
            case 5:
                return 6;
            case 6:
                return 8;
            case 7:
                return 4;
        }
        return -1;
    }

    public byte countRow(int rowNum, int player) {
        byte count = 0;
        int offset = getRowStart(rowNum);
        int inc = getRowInc(rowNum);
        for (int i = 0; i < 3; i++) {
            if ((((state >> offset) & 3) & player) != 0)
                count++;
            offset += inc;
        }
        return count;
    }

    public byte countPlayer(int player) {
        int playerMask = 0x15555;
        if (player == 2)
            playerMask <<= 1;
        return (byte) Integer.bitCount(getContents() & playerMask);
    }

    public boolean checkWin(int player) {
        for (int i = 0; i < 8; i++)
            if (countRow(i, player) == 3)
                return true;
        return false;
    }

    public int countMoves() {
        return Integer.bitCount(getContents());
    }

    private static int getOffset(int x, int y) {
        if (x < 0 || x > 2 || y < 0 || y > 2)
            throw new IllegalArgumentException("Coordinates off board");
        return (x << 1) + (y * 6);
    }

    private Point getCoord(int offset) {
        return new Point((offset % 6) / 2, offset / 6);
    }

    public void play(int x, int y, int player) {
        int offset = getOffset(x, y);
        if (((state >> offset) & 3) != 0)
            throw new IllegalArgumentException("Space already occupied");
        state |= player << offset;
    }

    public void play(Point point, int player) {
        play(point.x, point.y, player);
    }

    public Point[] getLegalMoves() {
        Point[] result = new Point[9];
        int count = 0;
        for (int offset = 0; offset < 18; offset += 2) {
            if (((state >> offset) & 3) == 0)
                result[count++] = getCoord(offset);
        }
        return Arrays.copyOf(result, count);
    }

    public Point[] getCanonicalLegalMoves() {
        Set<Integer> seen = new HashSet<Integer>();
        Point[] result = new Point[9];
        int count = 0;
        for (int offset = 0; offset < 18; offset += 2) {
            if (((state >> offset) & 3) != 0) continue;
            int newBoard = getContents() | (3 << offset);
            int key = toCanonical(newBoard) & contentsMask;
            if (seen.contains(key)) continue;
            result[count++] = getCoord(offset);
            seen.add(key);
        }
        return Arrays.copyOf(result, count);
    }

    public static int get(int state, int x, int y) {
        return (state >> getOffset(x, y)) & 3;
    }
    public int get(int x, int y) {
        return get(state, x, y);
    }

    public String toString() {
        return toString(getContents());
    }

    public static String toString(int state) {
        StringBuffer result = new StringBuffer();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                result.append(playerCharsRep.charAt(get(state, x, y)));
            }
        }
        return result.toString();
    }

    private static int rotateCW(int state) {
        return (state & 0x300) |
                ((state & 0x3) << 4) |
                ((state & 0xc) << 8) |
                ((state & 0x30) << 12) |
                ((state & 0xc00) << 4) |
                ((state & 0x30000) >> 4) |
                ((state & 0xc000) >> 8) |
                ((state & 0x3000) >> 12) |
                ((state & 0xc0) >> 4);
    }

    private static int flipVertical(int state) {
        return (state & 0xfc0) |
                ((state & 0x3f) << 12) |
                ((state & 0x3f000) >> 12);
    }

    private static int flipHorizontal(int state) {
        return (state & 0xc30c) |
                ((state & 0x30c3) << 4) |
                ((state & 0x30c30) >> 4);
    }

    private static int applyTransform(int state, int transform) {
        if ((transform & 1) == 1)
            state = rotateCW(state);
        if ((transform & 2) == 2)
            state = flipVertical(state);
        if ((transform & 4) == 4)
            state = flipHorizontal(state);
        return state;
    }

    private static int reverseTransform(int state, int transform) {
        if ((transform & 1) == 1 && (((transform & 2) ^ ((transform & 4) >> 1)) == 0))
            transform ^= 6;
        return applyTransform(state, transform);
    }

    private static int toCanonical(int state) {
        state &= contentsMask;
        int minState = state;
        int transform = 0;
        for (int i = 0; i < 8; i++) {
            int curState = applyTransform(state, i);
            if (curState < minState) {
                minState = curState;
                transform = i;
            }
        }
        return minState | (transform << 18);
    }

    public int stateID() {
        return toCanonical(getContents()) & contentsMask;
    }

    public static void countBoards(Board board, Set<Integer> states, int turn) {
        Point moves[] = board.getLegalMoves();
        int player = (turn & 1) + 1;
        for (int i = 0; i < moves.length; i++) {
            Board cur = new Board(board);
            cur.play(moves[i], player);
            states.add(toCanonical(cur.state) & contentsMask);
            if (turn < 7 && !board.checkWin(player))
                countBoards(cur, states, turn + 1);
        }
    }

}

