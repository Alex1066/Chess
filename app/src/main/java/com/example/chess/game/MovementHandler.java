package com.example.chess.game;

import com.example.chess.entities.Castling;
import com.example.chess.entities.CastlingMove;
import com.example.chess.entities.Move;
import com.example.chess.entities.Piece;
import com.example.chess.utility.Color;

import java.util.ArrayList;
import java.util.List;

public class MovementHandler {

    Game game;
    PrecomputedData pd;
    List<Move> legalMoves;

    public MovementHandler(Game game){
        this.game = game;
        pd = new PrecomputedData();
        legalMoves = new ArrayList<>();
    }

    public boolean isKingInCheck(List<Move> opponentResponse, int color) {
        int kingPosition = game.findKingPosition(color);
        for (Move move : opponentResponse) {
            if (move.targetSquareIndex == kingPosition) {
                return true;
            }
        }
        return false;
    }

    public void generateLegalMoves() {
        legalMoves.clear();
        List<Move> pseudoLegal = generatePseudoLegalMoves();
        for (Move moveToCheck : pseudoLegal) {
            game.executeMove(moveToCheck);
            List<Move> opponentResponses = generatePseudoLegalMoves();
            // Since we play a move in advance, now it's the other's player time to move. We need the
            // color of the initial player for which we are generating the legal moves, thus the call
            // of oppositeColor method.
            if (!isKingInCheck(opponentResponses, Color.oppositeColor(game.sideToMove))) {
                legalMoves.add(moveToCheck);
            }
            game.undo();
        }
        addCastling();
    }

    public List<Move> generatePseudoLegalMoves() {
        List<Move> pseudoLegalMoves = new ArrayList<>();
        for (int i = 0; i < 64; i ++) {
            int position = game.board[i].getPosition();
            int piece = game.board[i].getPiece();
            if (Piece.pieceColor(piece) == game.sideToMove) {
                List<Move> moves = getPiecePseudoLegalMoves(position, piece);
                pseudoLegalMoves.addAll(moves);
            }
        }
        return pseudoLegalMoves;
    }
    /**
     * Generates all pseudo-legal moves for a given piece (moves that do not consider checks or pins).
     */
    public List<Move> getPiecePseudoLegalMoves(int position, int piece) {
        List<Move> pseudoLegalMoves = new ArrayList<>();
        if (Piece.isSlidingPiece(piece)) {
            pseudoLegalMoves = getPseudoLegalMovesForSlidingPiece(position);
        } else if (Piece.isPieceType(piece, Piece.knight)) {
            pseudoLegalMoves = getPseudoLegalMovesForKnight(position);
        } else if (Piece.isPieceType(piece, Piece.king)) {
            pseudoLegalMoves = getPseudoLegalMovesForKing(position);
        } else if (Piece.isPieceType(piece, Piece.pawn)) {
            if (Piece.pieceColor(piece) == Color.black) {
                pseudoLegalMoves = getPseudoLegalMovesForPawn(position, pd.arrBlackPawnPushes,
                        pd.arrBlackPawnAttacks);
            }
            else {
                pseudoLegalMoves = getPseudoLegalMovesForPawn(position, pd.arrWhitePawnPushes,
                        pd.arrWhitePawnAttacks);
            }
        }
        return pseudoLegalMoves;
    }

    /**
     * This method is called to compute the available moves for pieces that slide on the board
     * i.e the rook, the bishop and the queen.
     *
     * @param initialSquareIndex the square of the board for which we want to compute the available moves. This
     *               variable holds the index of the board square and the piece situated on the square.
     */
    public List<Move> getPseudoLegalMovesForSlidingPiece(int initialSquareIndex) {
        List<Move> pseudoLegalMoves = new ArrayList<>();
        // Rook can walk only in the first 4 directions.
        // Bishop can walk only in the last 4 directions.
        // Queen is unrestricted.
        int startDirection = Piece.isPieceType(game.board[initialSquareIndex].getPiece(), Piece.bishop) ? 4 : 0;
        int endDirection = Piece.isPieceType(game.board[initialSquareIndex].getPiece(), Piece.rook) ? 4 : 8;
        for (int direction = startDirection; direction < endDirection; direction++) {
            for (int i = 0; i < pd.squaresToTheEdge[initialSquareIndex][direction]; i++) {
                int targetSquareIndex = initialSquareIndex + PrecomputedData.directionOffset[direction] * (i + 1);
                int targetPiece = game.board[targetSquareIndex].getPiece();

                // If it encounters a friendly piece then it can't move further in this direction.
                if (Piece.pieceColor(targetPiece) == game.sideToMove) {
                    break;
                }
                pseudoLegalMoves.add(new Move(initialSquareIndex, targetSquareIndex));

                // If the square had an enemy piece on it, then no other moves are available in
                // this direction.
                if (Piece.pieceColor(targetPiece) == Color.oppositeColor(game.sideToMove)) {
                    break;
                }
            }
        }
        return pseudoLegalMoves;
    }

    public List<Move> getPseudoLegalMovesForKnight(int initialSquareIndex) {
        List<Move> pseudoLegalMoves = new ArrayList<>();
        for (int targetIndex : pd.arrKnightAttacks.get(initialSquareIndex)) {
            int targetPiece = game.board[targetIndex].getPiece();
            // If the target is a friendly piece, then skip this one.
            if (Piece.pieceColor(targetPiece) == game.sideToMove) {
                continue;
            }
            pseudoLegalMoves.add(new Move(initialSquareIndex, targetIndex));
        }
        return pseudoLegalMoves;
    }

    public List<Move> getPseudoLegalMovesForKing(int initialSquareIndex) {
        List<Move> pseudoLegalMoves = new ArrayList<>();
        for (int targetIndex : pd.arrKingAttacks.get(initialSquareIndex)) {
            int targetPiece = game.board[targetIndex].getPiece();
            // If the target is a friendly piece, then skip this one.
            if (Piece.pieceColor(targetPiece) == game.sideToMove) {
                continue;
            }
            pseudoLegalMoves.add(new Move(initialSquareIndex, targetIndex));
        }
        return pseudoLegalMoves;
    }

    public List<Move> getPseudoLegalMovesForPawn(int initialSquareIndex, List<List<Integer>> pawnPushes, List<List<Integer>> pawnAttacks) {
        List<Move> pseudoLegalMoves = new ArrayList<>();
        for (int targetIndex : pawnPushes.get(initialSquareIndex)) {
            int targetPiece = game.board[targetIndex].getPiece();
            // If the target is not a free space, the pawn cannot push, thus further search is futile.
            if (targetPiece != Piece.empty) {
                break;
            }
            Move move = new Move(initialSquareIndex, targetIndex);

            // Double pawn push
            if (Math.abs(initialSquareIndex - targetIndex) == 16) {
                move.markAsDoubleSquarePawnMove();
            }

            // Check if it is a promotion move.
            if ((targetIndex >> 3 == 7 && game.sideToMove == Color.white) ||
                    (targetIndex >> 3 == 0 && game.sideToMove == Color.black)) {
                move.markAsPromotionMove();
            }
            pseudoLegalMoves.add(move);
        }
        for (int targetIndex : pawnAttacks.get(initialSquareIndex)) {
            int targetPiece = game.board[targetIndex].getPiece();

            // Check if enPassant is available for this pawn.
            if (game.enPassant != null && game.enPassant == targetIndex) {
                pseudoLegalMoves.add(new Move(initialSquareIndex, targetIndex));
            }

            // If the target is not an enemy piece, the pawn cannot attack.
            if (Piece.pieceColor(targetPiece) != Color.oppositeColor(game.sideToMove)) {
                continue;
            }

            Move move = new Move(initialSquareIndex, targetIndex);
            // Check if it is a promotion move.
            if ((targetIndex >> 3 == 7 && game.sideToMove == Color.white) ||
                    (targetIndex >> 3 == 0 && game.sideToMove == Color.black)) {
                move.markAsPromotionMove();
            }
            pseudoLegalMoves.add(move);
        }
        return pseudoLegalMoves;
    }


    public boolean checkWhiteKingSideCastling(int kingPosition) {
        return game.white_king_side_castling &&
                noPiecesBetweenKingAndRook(kingPosition, 2, 1) &&
                isCastlingLegal(kingPosition, 1);
    }

    public boolean checkBlackKingSideCastling(int kingPosition) {
        return game.black_king_side_castling &&
                noPiecesBetweenKingAndRook(kingPosition, 2, 1) &&
                isCastlingLegal(kingPosition, 1);
    }

    public boolean checkWhiteQueenSideCastling(int kingPosition) {
        return game.white_queen_side_castling &&
                noPiecesBetweenKingAndRook(kingPosition, 3, -1) &&
                isCastlingLegal(kingPosition, -1);
    }

    public  boolean checkBlackQueenSideCastling(int kingPosition) {
        return game.black_queen_side_castling &&
                noPiecesBetweenKingAndRook(kingPosition, 3, -1) &&
                isCastlingLegal(kingPosition, -1);
    }

    public boolean noPiecesBetweenKingAndRook(int kingPosition, int numberOfSpaces, int directionSign) {
        for (int i = 1; i <= numberOfSpaces; i++) {
            if (game.board[kingPosition + i * directionSign].getPiece() != Piece.empty) {
                return false;
            }
        }
        return true;
    }
    /**
        directionSign is -1 for queen-side castling and 1 for king-side castling
     */
    public boolean isCastlingLegal(int kingPosition, int directionSign) {
        for (int i = 0; i <= 2; i++) {
            game.executeMove(new Move(kingPosition, kingPosition + directionSign * i));
            List<Move> opponentResponses = generatePseudoLegalMoves();
            // Since we play a move in advance, now it's the other's player time to move. We need the
            // color of the initial player for which we are generating the legal moves, thus the call
            // of oppositeColor method.
            if (isKingInCheck(opponentResponses, Color.oppositeColor(game.sideToMove))) {
                game.undo();
                return false;
            }
            game.undo();
        }
        return true;
    }

    public void addCastling() {
        CastlingMove castling;
        if (game.getSideToMove() == Color.white) {
            if (checkWhiteKingSideCastling(Castling.whiteIndex + Castling.kingInitialPosition)) {
                castling = new CastlingMove(Castling.whiteIndex + Castling.kingInitialPosition,
                        Castling.whiteIndex + Castling.kingPositionAfterShortCastling);
                castling.rookInitialPosition = Castling.whiteIndex + Castling.kingSideRookInitialPosition;
                castling.rookFinalPosition = Castling.whiteIndex + Castling.kingSideRookFinalPosition;
                castling.markAsCastling();
                legalMoves.add(castling);
            }
            if (checkWhiteQueenSideCastling(Castling.whiteIndex + Castling.kingInitialPosition)) {
                castling = new CastlingMove(Castling.whiteIndex + Castling.kingInitialPosition,
                        Castling.whiteIndex + Castling.kingPositionAfterLongCastling);
                castling.rookInitialPosition = Castling.whiteIndex + Castling.queenSideRookInitialPosition;
                castling.rookFinalPosition = Castling.whiteIndex + Castling.queenSideRookFinalPosition;
                castling.markAsCastling();
                legalMoves.add(castling);
            }
        }
        else {
            if (checkBlackKingSideCastling(Castling.blackIndex + Castling.kingInitialPosition)) {
                castling = new CastlingMove(Castling.blackIndex + Castling.kingInitialPosition,
                        Castling.blackIndex + Castling.kingPositionAfterShortCastling);
                castling.rookInitialPosition = Castling.blackIndex + Castling.kingSideRookInitialPosition;
                castling.rookFinalPosition = Castling.blackIndex + Castling.kingSideRookFinalPosition;
                castling.markAsCastling();
                legalMoves.add(castling);
            }
            if (checkBlackQueenSideCastling(Castling.blackIndex + Castling.kingInitialPosition)) {
                castling = new CastlingMove(Castling.blackIndex + Castling.kingInitialPosition,
                        Castling.blackIndex + Castling.kingPositionAfterLongCastling);
                castling.rookInitialPosition = Castling.blackIndex + Castling.queenSideRookInitialPosition;
                castling.rookFinalPosition = Castling.blackIndex + Castling.queenSideRookFinalPosition;
                castling.markAsCastling();
                legalMoves.add(castling);
            }
        }
    }
}
