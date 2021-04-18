package com.example.chess.utility;

public class SquareColor {
    public static final int darkSquare = 0;
    public static final int lightSquare = 1;

    public static int findColorFromIndex(int index){
        int rank = index / 8;
        int file = index % 8;
        if (rank % 2 == file % 2) {
            return darkSquare;
        }
        return lightSquare;
    }
}
