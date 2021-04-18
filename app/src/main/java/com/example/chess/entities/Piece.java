package com.example.chess.entities;

import com.example.chess.utility.Color;

public class Piece {
    public static final int empty = 0;
    public static final int king = 1;
    public static final int queen = 2;
    public static final int rook = 3;
    public static final int bishop = 4;
    public static final int knight = 5;
    public static final int pawn = 6;

    public static final int black = Color.black << 3;
    public static final int white = Color.white << 3;


    public static int pieceColor(int piece) {
        return piece >> 3;
    }

    public static boolean isPieceType(int piece, int type) {
        return (piece % 8) == type;
    }

    public static boolean isSlidingPiece(int piece) {
        return (piece % 8) == rook || (piece % 8) == bishop || (piece % 8) == queen;
    }

}