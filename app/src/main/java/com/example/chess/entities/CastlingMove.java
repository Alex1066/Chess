package com.example.chess.entities;

public class CastlingMove extends Move {

    public int rookInitialPosition;
    public int rookFinalPosition;
    public int kingInitialPosition;
    public int kingFinalPosition;

    public CastlingMove(int initialSquareIndex, int targetSquareIndex) {
        super(initialSquareIndex, targetSquareIndex);
    }

    public CastlingMove(Move move) {
        super(move);
    }
}
