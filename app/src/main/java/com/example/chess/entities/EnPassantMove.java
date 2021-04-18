package com.example.chess.entities;

public class EnPassantMove extends Move{
    public int pawnToCapture;

    public EnPassantMove(int initialSquareIndex, int targetSquareIndex) {
        super(initialSquareIndex, targetSquareIndex);
    }

    public EnPassantMove(Move move) {
        super(move);
    }
}
