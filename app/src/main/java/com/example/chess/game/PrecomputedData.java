package com.example.chess.game;

import java.util.ArrayList;
import java.util.List;

public class PrecomputedData {

    public int[][] squaresToTheEdge = new int[64][8];
    public List<List<Integer>> arrKnightAttacks = new ArrayList<>();
    public List<List<Integer>> arrKingAttacks = new ArrayList<>();
    public List<List<Integer>> arrBlackPawnAttacks = new ArrayList<>();
    public List<List<Integer>> arrWhitePawnAttacks = new ArrayList<>();
    public List<List<Integer>> arrWhitePawnPushes = new ArrayList<>();
    public List<List<Integer>> arrBlackPawnPushes = new ArrayList<>();

    // The offsets of the top,bottom, right, left, top-right, top-left, bottom-right,
    // bottom-left directions respectively.
    public static final int[] directionOffset = {8, -8, 1, -1, 9, 7, -7, -9};

    public PrecomputedData() {
        precomputedDistanceData();
//        for (List<Integer> moves : arrWhitePawnPushes) {
//            System.out.println(moves);
//            System.out.println();
//        }
    }

    /**
     * This method will computes and store the distance of each square from the edges, in all 8
     * directions: top, bottom, right, left, top-right, top-left, bottom-right, bottom-left.
     * This will help when generating the valid moves for a piece.
     */
    public void precomputedDistanceData() {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                int topDirection = 7 - rank;
                int bottomDirection = rank;
                int rightDirection = 7 - file;
                int leftDirection = file;
                int topRightDirection = Math.min(topDirection, rightDirection);
                int topLeftDirection = Math.min(topDirection, leftDirection);
                int bottomRightDirection = Math.min(bottomDirection, rightDirection);
                int bottomLeftDirection = Math.min(bottomDirection, leftDirection);

                int squareIndex = rank * 8 + file;

                squaresToTheEdge[squareIndex][0] = topDirection;
                squaresToTheEdge[squareIndex][1] = bottomDirection;
                squaresToTheEdge[squareIndex][2] = rightDirection;
                squaresToTheEdge[squareIndex][3] = leftDirection;
                squaresToTheEdge[squareIndex][4] = topRightDirection;
                squaresToTheEdge[squareIndex][5] = topLeftDirection;
                squaresToTheEdge[squareIndex][6] = bottomRightDirection;
                squaresToTheEdge[squareIndex][7] = bottomLeftDirection;


                arrKnightAttacks.add(new ArrayList<Integer>());
                computeKnightMoves(squareIndex, file, rank);

                arrKingAttacks.add(new ArrayList<Integer>());
                computeKingMoves(squareIndex, file, rank);

                arrBlackPawnAttacks.add(new ArrayList<Integer>());
                computeBlackPawnAttacks(squareIndex, file);

                arrBlackPawnPushes.add(new ArrayList<Integer>());
                computeBlackPawnPushes(squareIndex, rank);

                arrWhitePawnAttacks.add(new ArrayList<Integer>());
                computeWhitePawnAttacks(squareIndex, file);

                arrWhitePawnPushes.add(new ArrayList<Integer>());
                computeWhitePawnPushes(squareIndex, rank);
            }
        }
    }

    public void computeKnightMoves(int squareIndex, int file, int rank) {
        int attackIndex;
        /*
        Checks if the knight can go in the north-north-east direction (2 squares up, 1 square right).
         */
        if (rank < 6 && file < 7) {
            attackIndex = squareIndex + 17;
            arrKnightAttacks.get(squareIndex).add(attackIndex);
        }
        /*
        Checks if the knight can go in the north-east-east direction (1 square up, 2 squares right).
         */
        if (rank < 7 && file < 6) {
            attackIndex = squareIndex + 10;
            arrKnightAttacks.get(squareIndex).add(attackIndex);
        }
        /*
        Checks if the knight can go in the south-east-east direction (1 square down, 2 squares right).
         */
        if (rank > 0 && file < 6) {
            attackIndex = squareIndex - 6;
            arrKnightAttacks.get(squareIndex).add(attackIndex);
        }
        /*
        Checks if the knight can go in the south-south-east direction (2 squares down, 1 square right).
         */
        if (rank > 1 && file < 7) {
            attackIndex = squareIndex - 15;
            arrKnightAttacks.get(squareIndex).add(attackIndex);
        }
        /*
        Checks if the knight can go in the south-south-west direction (2 squares down, 1 square left).
         */
        if (rank > 1 && file > 0) {
            attackIndex = squareIndex - 17;
            arrKnightAttacks.get(squareIndex).add(attackIndex);
        }
        /*
        Checks if the knight can go in the south-west-west direction (1 square down, 2 squares left).
         */
        if (rank > 0 && file > 1) {
            attackIndex = squareIndex - 10;
            arrKnightAttacks.get(squareIndex).add(attackIndex);
        }
        /*
        Checks if the knight can go in the north-west-west direction (1 square up, 2 squares left).
         */
        if (rank < 7 && file > 1) {
            attackIndex = squareIndex + 6;
            arrKnightAttacks.get(squareIndex).add(attackIndex);
        }
        /*
        Checks if the knight can go in the north-north-west direction (2 squares up, 1 square left).
         */
        if (rank < 6 && file > 0) {
            attackIndex = squareIndex + 15;
            arrKnightAttacks.get(squareIndex).add(attackIndex);
        }
    }

    public void computeKingMoves(int squareIndex, int file, int rank) {
        int attackIndex;
        if (rank < 7) {
            attackIndex = squareIndex + 8;
            arrKingAttacks.get(squareIndex).add(attackIndex);
            if (file < 7) {
                attackIndex = squareIndex + 9;
                arrKingAttacks.get(squareIndex).add(attackIndex);
            }
            if (file > 0) {
                attackIndex = squareIndex + 7;
                arrKingAttacks.get(squareIndex).add(attackIndex);
            }
        }
        if (rank > 0) {
            attackIndex = squareIndex - 8;
            arrKingAttacks.get(squareIndex).add(attackIndex);
            if (file < 7) {
                attackIndex = squareIndex - 7;
                arrKingAttacks.get(squareIndex).add(attackIndex);
            }
            if (file > 0) {
                attackIndex = squareIndex - 9;
                arrKingAttacks.get(squareIndex).add(attackIndex);
            }
        }
        if (file > 0) {
            attackIndex = squareIndex - 1;
            arrKingAttacks.get(squareIndex).add(attackIndex);
        }
        if (file < 7) {
            attackIndex = squareIndex + 1;
            arrKingAttacks.get(squareIndex).add(attackIndex);
        }
    }

    public void computeBlackPawnAttacks(int squareIndex, int file) {
        int attackIndex;
        if (file > 0) {
            attackIndex = squareIndex - 9;
            arrBlackPawnAttacks.get(squareIndex).add(attackIndex);
        }
        if (file < 7) {
            attackIndex = squareIndex - 7;
            arrBlackPawnAttacks.get(squareIndex).add(attackIndex);
        }
    }

    public void computeWhitePawnAttacks(int squareIndex, int file) {
        int attackIndex;
        if (file > 0) {
            attackIndex = squareIndex + 7;
            arrWhitePawnAttacks.get(squareIndex).add(attackIndex);
        }
        if (file < 7) {
            attackIndex = squareIndex + 9;
            arrWhitePawnAttacks.get(squareIndex).add(attackIndex);
        }
    }

    public void computeBlackPawnPushes(int squareIndex, int rank) {
        arrBlackPawnPushes.get(squareIndex).add(squareIndex - 8);
        // If the pawn has not moved yet, then the double square push is also available for it.
        if (rank == 6) {
            arrBlackPawnPushes.get(squareIndex).add(squareIndex - 16);
        }
    }

    public void computeWhitePawnPushes(int squareIndex, int rank) {
        arrWhitePawnPushes.get(squareIndex).add(squareIndex + 8);
        // If the pawn has not moved yet, then the double square push is also available for it.
        if (rank == 1) {
            arrWhitePawnPushes.get(squareIndex).add(squareIndex + 16);
        }
    }
}
