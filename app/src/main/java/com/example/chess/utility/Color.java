package com.example.chess.utility;


public class Color {
    public static final int black = 1;
    public static final int white = 2;

    public static int oppositeColor(int color) {
        if (color == black) {
            return white;
        }
        return black;
    }
}
