package com.example.chess.game;

import com.example.chess.entities.Piece;
import com.example.chess.entities.Square;
import com.example.chess.utility.Color;

import java.util.HashMap;
import java.util.Map;

public class Fen {
    private String piecePlacement;
    private String sideToMove;
    private String castlingAbility;
    private String enPassantTargetSquare;
    private String halfmoveClock;
    private String fullmoveCounter;

    private final Map<Character, Integer> pieceFromSymbol;
    private final Map<Integer, String> symbolFromPiece;
    private final Map<Character, Integer> fileCharacterToIndex;
    private final Map<Integer, String> fileIndexToString;

    public Fen(String FEN) {
        piecePlacement = FEN.split(" ")[0];
        sideToMove = FEN.split(" ")[1];
        castlingAbility = FEN.split(" ")[2];
        enPassantTargetSquare = FEN.split(" ")[3];
        halfmoveClock = FEN.split(" ")[4];
        fullmoveCounter = FEN.split(" ")[5];

        pieceFromSymbol = new HashMap<>();
        symbolFromPiece = new HashMap<>();
        fileCharacterToIndex = new HashMap<>();
        fileIndexToString = new HashMap<>();

        fillDictionaries();

    }

    /**
     * This method creates dictionaries in order to have a quick translation between piece
     * representation as a character in FEN and as a integer on the board.
     */
    public void fillDictionaries() {
        pieceFromSymbol.put('k', Piece.black + Piece.king);
        pieceFromSymbol.put('q', Piece.black + Piece.queen);
        pieceFromSymbol.put('r', Piece.black + Piece.rook);
        pieceFromSymbol.put('b', Piece.black + Piece.bishop);
        pieceFromSymbol.put('n', Piece.black + Piece.knight);
        pieceFromSymbol.put('p', Piece.black + Piece.pawn);
        pieceFromSymbol.put('K', Piece.white + Piece.king);
        pieceFromSymbol.put('Q', Piece.white + Piece.queen);
        pieceFromSymbol.put('R', Piece.white + Piece.rook);
        pieceFromSymbol.put('B', Piece.white + Piece.bishop);
        pieceFromSymbol.put('N', Piece.white + Piece.knight);
        pieceFromSymbol.put('P', Piece.white + Piece.pawn);

        symbolFromPiece.put(Piece.black + Piece.king, "k");
        symbolFromPiece.put(Piece.black + Piece.queen, "q");
        symbolFromPiece.put(Piece.black + Piece.rook, "r");
        symbolFromPiece.put(Piece.black + Piece.bishop, "b");
        symbolFromPiece.put(Piece.black + Piece.knight, "n");
        symbolFromPiece.put(Piece.black + Piece.pawn, "p");
        symbolFromPiece.put(Piece.white + Piece.king, "K");
        symbolFromPiece.put(Piece.white + Piece.queen, "Q");
        symbolFromPiece.put(Piece.white + Piece.rook, "R");
        symbolFromPiece.put(Piece.white + Piece.bishop, "B");
        symbolFromPiece.put(Piece.white + Piece.knight, "N");
        symbolFromPiece.put(Piece.white + Piece.pawn, "P");

        fileCharacterToIndex.put('a', 0);
        fileCharacterToIndex.put('b', 1);
        fileCharacterToIndex.put('c', 2);
        fileCharacterToIndex.put('d', 3);
        fileCharacterToIndex.put('e', 4);
        fileCharacterToIndex.put('f', 5);
        fileCharacterToIndex.put('g', 6);
        fileCharacterToIndex.put('h', 7);

        fileIndexToString.put(0, "a");
        fileIndexToString.put(1, "b");
        fileIndexToString.put(2, "c");
        fileIndexToString.put(3, "d");
        fileIndexToString.put(4, "e");
        fileIndexToString.put(5, "f");
        fileIndexToString.put(6, "g");
        fileIndexToString.put(7, "h");
    }

    public void updateFen(Game game) {
        updatePiecePlacement(game.board);
        updateSideToMove(game.sideToMove);
        updateCastlingAbility(game.white_king_side_castling, game.white_queen_side_castling, game.black_king_side_castling, game.black_queen_side_castling);
        updateEnPassantSquareTarget(game.enPassant);
    }
    /**
     * After a move is made the FEN is updated.
     */
    private void updatePiecePlacement(Square[] board) {
        int numberEmptySquares = 0;
        StringBuilder positionsFen = new StringBuilder();
        int piece;
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                piece = board[rank * 8 + file].getPiece();
                if (piece == Piece.empty) {
                    numberEmptySquares++;
                } else {
                    if (numberEmptySquares != 0) {
                        positionsFen.append(numberEmptySquares);
                        numberEmptySquares = 0;
                    }
                    positionsFen.append(symbolFromPiece.get(piece));
                }
            }
            if (numberEmptySquares != 0) {
                positionsFen.append(numberEmptySquares);
                numberEmptySquares = 0;
            }
            if (rank != 0) {
                positionsFen.append("/");

            }
        }
        piecePlacement = positionsFen.toString();
    }

    private void updateSideToMove(int sideToMove) {
        if (sideToMove == Color.white) {
            this.sideToMove = "w";
        }
        else {
            this.sideToMove = "b";
        }
    }

    private void updateCastlingAbility(boolean w_k_side, boolean w_q_side, boolean b_k_side, boolean b_q_side) {
        castlingAbility = "";
        if (w_k_side) {
            castlingAbility += "K";
        }
        if (w_q_side) {
            castlingAbility += "Q";
        }
        if (b_k_side) {
            castlingAbility += "k";
        }
        if (b_q_side) {
            castlingAbility += "q";
        }
        if (castlingAbility.equals("")) {
            castlingAbility += "-";
        }

    }

    private void updateEnPassantSquareTarget(Integer enPassant) {
        if (enPassant != null) {
            int rank = enPassant >> 3;
            int file = enPassant % 8;
            enPassantTargetSquare = fileIndexToString.get(file) + rank;
        }
        else {
            enPassantTargetSquare = "-";
        }
    }

    public Square[] getPiecePlacement() {
        Square[] board = new Square[64];
        // Create an empty board.
        for (int i = 0; i < 64; i++) {
            board[i] = new Square(i, 0);
        }
        int file = 0, rank = 7, squareIndex;
        // Fill the board with pieces.
        for (int i = 0; i < piecePlacement.length(); i++) {
            char symbol = piecePlacement.charAt(i);
            if (symbol == '/') {
                rank -= 1;
                file = 0;
            } else {
                if (Character.isDigit(symbol)) {
                    file += Character.getNumericValue(symbol);
                } else {
                    squareIndex = (rank) * 8 + file;
                    Integer checkIfExists = pieceFromSymbol.get(symbol);
                    int piece = checkIfExists != null ? checkIfExists : 0;
                    board[squareIndex] = new Square(squareIndex, piece);
                    file++;
                }
            }
        }
        return board;
    }

    public int getSideToMove() {
        if (sideToMove.equals("w")) {
            return Color.white;
        }
        return Color.black;
    }

    public Integer getEnPassantSquareTarget() {
        Integer enPassant;
        if (enPassantTargetSquare.equals("-")) {
            enPassant = null;
        }
        else {
            Integer file = fileCharacterToIndex.get(enPassantTargetSquare.charAt(0));
            int rank = Character.getNumericValue(enPassantTargetSquare.charAt(1));
            enPassant = file != null ? rank * 8 + file : null;
        }
        return enPassant;
    }

    public String getCastlingAbility() {
        return castlingAbility;
    }

    public String toString() {
        String FEN;
        FEN = piecePlacement + " " + sideToMove + " " + castlingAbility + " " +
                enPassantTargetSquare + " " + halfmoveClock + " " + fullmoveCounter;
        return FEN;
    }

}
