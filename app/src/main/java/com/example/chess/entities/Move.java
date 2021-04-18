package com.example.chess.entities;


public class Move {
    public int initialSquareIndex;
    public int targetSquareIndex;
    public boolean isPromotionMove;
    public boolean isDoublePawnPush;
    public boolean isCastling;

    public Move(int initialSquareIndex, int targetSquareIndex) {
        this.initialSquareIndex = initialSquareIndex;
        this.targetSquareIndex = targetSquareIndex;
        isPromotionMove = false;
        isDoublePawnPush = false;
        isCastling = false;
    }

    public Move(Move move) {
        initialSquareIndex = move.initialSquareIndex;
        targetSquareIndex = move.targetSquareIndex;
        isPromotionMove = move.isPromotionMove;
        isDoublePawnPush = move.isDoublePawnPush;
    }

    public boolean equals(Move otherMove){
        return (initialSquareIndex == otherMove.initialSquareIndex) &&
                (targetSquareIndex == otherMove.targetSquareIndex);
    }

    public void markAsPromotionMove() {
        isPromotionMove = true;
    }

    public void markAsDoubleSquarePawnMove() {
        isDoublePawnPush = true;
    }

    public void markAsCastling() {
        isCastling = true;
    }
}
