package com.example.chess.entities;

public class Square {

    private int position;
    private int piece;

    public Square(int position, int piece) {
        this.position = position;
        this.piece = piece;
    }

    public Square(Square square) {
        this.position = square.position;
        this.piece = square.piece;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPiece() {
        return piece;
    }

    public void setPiece(int piece) {
        this.piece = piece;
    }
}
