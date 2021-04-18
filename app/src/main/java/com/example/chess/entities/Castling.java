package com.example.chess.entities;

public class Castling {
    public static final int white_king_side = 6;
    public static final int white_queen_side = 2;
    public static final int black_king_side = 62;
    public static final int black_queen_side = 58;

//    public static final int whiteKingPosition = 4;
//    public static final int blackKingPosition = 60;

    public static final int whiteQueenSideRookInitialPosition = 0;
    public static final int whiteKingSideRookInitialPosition = 7;
    public static final int whiteRookPositionAfterQueenSideCastling = 3;
    public static final int whiteRookPositionAfterKingSideCastling = 5;

    public static final int blackQueenSideRookInitialPosition = 56;
    public static final int blackKingSideRookInitialPosition = 63;
    public static final int blackRookPositionAfterQueenSideCastling = 59;
    public static final int blackRookPositionAfterKingSideCastling = 61;

    public static int rookGoesTo(int castlingCase) {
        switch (castlingCase) {
            case white_king_side:
                return white_king_side - 1;
            case white_queen_side:
                return white_queen_side + 1;
            case black_king_side:
                return black_king_side - 1;
            case black_queen_side:
                return black_queen_side + 1;
        }
        return 0;
    }

    public static int rookPosition(int castlingCase) {
        switch (castlingCase) {
            case white_king_side:
                return 7;
            case white_queen_side:
                return 0;
            case black_king_side:
                return 63;
            case black_queen_side:
                return 56;
        }
        return 0;
    }
}
