package com.example.chess.entities;

public class CastlingMove extends Move {

    public int rookInitialPosition;
    public int rookFinalPosition;

    public CastlingMove(int initialSquareIndex, int targetSquareIndex) {
        super(initialSquareIndex, targetSquareIndex);
    }
}
