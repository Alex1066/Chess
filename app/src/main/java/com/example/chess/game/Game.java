package com.example.chess.game;

import com.example.chess.entities.Castling;
import com.example.chess.entities.CastlingMove;
import com.example.chess.entities.Move;
import com.example.chess.entities.Piece;
import com.example.chess.entities.Square;
import com.example.chess.utility.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This is the game class responsible for controlling only the information that describes
 * the state of the game.
 */
public class Game {

    public Square[] board = new Square[64];
    public int sideToMove;
    public Map<String, Boolean> castlingAbility = new HashMap<>();
    public boolean white_queen_side_castling = false;
    public boolean white_king_side_castling = false;
    public boolean black_queen_side_castling = false;
    public boolean black_king_side_castling = false;
    public Integer enPassant = null;
    public int halfmoveClock;
    public int fullmoveCounter;
    private Fen FEN;

    // En Passant is not available.
    // EnPassant should be available only 1 turn.
    public List<String> gameHistory = new ArrayList<>();

    /**
     * The constructor of the class.
     *
     * @param FEN a string representation of the current state of the game
     */
    public Game(Fen FEN) {
        this.FEN = FEN;
        gameHistory.add(FEN.toString());
        castlingAbility.put("white_king_side", true);
        castlingAbility.put("white_queen_side", true);
        castlingAbility.put("black_king_side", true);
        castlingAbility.put("black_queen_side", true);

        clearBoard();
    }

    public void clearBoard() {
        for (int i = 0; i < 64; i++) {
            board[i] = new Square(i, 0);
        }
    }

    public int findKingPosition(int color) {
        for (Square square : board) {
            if (square.getPiece() == ((color << 3) + Piece.king)) {
                return square.getPosition();
            }
        }
        return 0;
    }


    /**
     * This method will load the positions of the pieces on the board according to the fen.
     * Fen is a string that contains all the information needed for initializing a game in any
     * state (after any number of moves).
     */
    public void loadGameStateFromFen() {
        board = FEN.getPiecePlacement();
        enPassant = FEN.getEnPassantSquareTarget();
        sideToMove = FEN.getSideToMove();
        updateCastling(FEN.getCastlingAbility());

    }

    public void updateCastling(String castling) {
        for (int i = 0; i < castling.length(); i++) {
            char castlingSymbol = castling.charAt(i);
            switch (castlingSymbol) {
                case 'K':
                    white_king_side_castling = true;
                    break;
                case 'Q':
                    white_queen_side_castling = true;
                    break;
                case 'k':
                    black_king_side_castling = true;
                case 'q':
                    black_queen_side_castling = true;
            }
        }
    }
    
    public void checkIfMoveDisablesCastling(int piecePosition) {
        // If the queen-side white rook is moved and white long castling is still available, then disable it.
        if (piecePosition == (Castling.whiteIndex + Castling.queenSideRookInitialPosition) && white_queen_side_castling) {
            white_queen_side_castling = false;
        }
        // If the king-side white rook is moved and white short castling is still available, then disable it.
        if (piecePosition == (Castling.whiteIndex + Castling.kingSideRookInitialPosition) && white_king_side_castling) {
            white_king_side_castling = false;
        }
        // If the queen-side black rook is moved and black long castling is still available, then disable it.
        if (piecePosition == (Castling.blackIndex + Castling.queenSideRookInitialPosition) && black_queen_side_castling) {
            black_queen_side_castling = false;
        }
        // If the king-side black rook is moved and black short castling is still available, then disable it.
        if (piecePosition == (Castling.blackIndex + Castling.kingSideRookInitialPosition) && black_king_side_castling) {
            black_king_side_castling = false;
        }
        // If the white king is moved and any white castling is still available, then disable it.
        if (piecePosition == (Castling.whiteIndex + Castling.kingInitialPosition) && (white_king_side_castling || white_queen_side_castling)) {
            white_queen_side_castling = white_king_side_castling = false;
        }
        // If the black king is moved and any black castling is still available, then disable it.
        if (piecePosition == (Castling.blackIndex + Castling.kingInitialPosition) && (black_king_side_castling || black_queen_side_castling)) {
            black_queen_side_castling = black_king_side_castling = false;
        }
    }

    public void doCastling(CastlingMove castlingMove) {
        int rookToMove;
        int rookGoesTo;
        rookToMove = castlingMove.rookInitialPosition;
        rookGoesTo = castlingMove.rookFinalPosition;
        board[rookGoesTo].setPiece(board[rookToMove].getPiece());
        board[rookToMove].setPiece(Piece.empty);
    }
    
    /**
     * @// TODO: 2/28/2021
     * Here computer move should be added after endAction();
     */
    public void executeMove(Move move) {
        int pieceToMove = board[move.initialSquareIndex].getPiece();
        board[move.initialSquareIndex].setPiece(Piece.empty);
        board[move.targetSquareIndex].setPiece(pieceToMove);

        if (move instanceof CastlingMove) {
            doCastling((CastlingMove) move);
        }

        if (Piece.isPieceType(pieceToMove, Piece.king) || Piece.isPieceType(pieceToMove, Piece.rook)) {
            checkIfMoveDisablesCastling(move.initialSquareIndex);
        }

        /*
        If the player makes the enPassant move, then the pawn that has to be removed from the
        game is either above or below the clickedSquare depending of the color of the player.
         */
        if (enPassant != null &&  enPassant == move.targetSquareIndex) {
            if (sideToMove == Color.black) {
                board[move.targetSquareIndex+8].setPiece(Piece.empty);
            }
            else {
                board[move.targetSquareIndex-8].setPiece(Piece.empty);
            }
        }
        if (move.isDoublePawnPush) {
            if (sideToMove == Color.white) {
                enPassant = move.targetSquareIndex - 8;
            }
            else {
                enPassant = move.targetSquareIndex + 8;
            }
        }
        else {
            if (enPassant != null) {
                enPassant = null;
            }
        }

        sideToMove = Color.oppositeColor(sideToMove);
        FEN.updateFen(this);
        gameHistory.add(FEN.toString());

    }

    /**
     * This method will undo the last move done by the human player.
     * If the game mode is human vs human, then only one undo is required.
     * If the game move is human vs AI, then the game should undo both the human
     * move and the response from the AI.
     *
     * @param depth the number of half moves to be undo
     */
    public void undo(int depth) {
        if (gameHistory.size() > depth) {
            for (int i = 0; i < depth; i++) {
                gameHistory.remove(gameHistory.size() - 1);
            }
            FEN = new Fen(gameHistory.get(gameHistory.size() - 1));
            loadGameStateFromFen();
        }
    }

    public void promotePawn(int pawnPosition, int promotedTo) {
        board[pawnPosition].setPiece(promotedTo);
        FEN.updateFen(this);
        gameHistory.remove(gameHistory.size() - 1);
        gameHistory.add(FEN.toString());

    }


    public int getSideToMove() {
        return sideToMove;
    }
    public Square[] getBoard() {
        return board;
    }

}
