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

    // The offsets of the top,bottom, right, left, top-right, top-left, bottom-right,
    // bottom-left directions respectively.
    private static final int[] directionOffset = {8, -8, 1, -1, 9, 7, -7, -9};
    //The offset of the the moves for a knight.
    private static final int[][] squaresToTheEdge = new int[64][8];
    private static final List<List<Integer>> arrKnightAttacks = new ArrayList<>();
    private static final List<List<Integer>> arrKingAttacks = new ArrayList<>();
    private static final List<List<Integer>> arrBlackPawnAttacks = new ArrayList<>();
    private static final List<List<Integer>> arrWhitePawnAttacks = new ArrayList<>();


    private Square[] board = new Square[64];
    private int sideToMove;
    private Map<String, Boolean> castlingAbility = new HashMap<>();
    public boolean white_queen_side_castling = false;
    public boolean white_king_side_castling = false;
    public boolean black_queen_side_castling = false;
    public boolean black_king_side_castling = false;
    public Integer enPassant = null;
    private int halfmoveClock;
    private int fullmoveCounter;
    private Fen FEN;

    // En Passant is not available.
    // EnPassant should be available only 1 turn.
    public List<String> gameHistory = new ArrayList<>();

    public int blackKingPosition;
    public int whiteKingPosition;
    /**
     * The constructor of the class.
     *
     * @param FEN a string representation of the current state of the game
     */
    public Game(Fen FEN) {
        this.FEN = FEN;
        gameHistory.add(FEN.getFen());
        castlingAbility.put("white_king_side", true);
        castlingAbility.put("white_queen_side", true);
        castlingAbility.put("black_king_side", true);
        castlingAbility.put("black_queen_side", true);


        clearBoard();
        precomputedDistanceData();
    }

    public void clearBoard() {
        for (int i = 0; i < 64; i++) {
            board[i] = new Square(i, 0);
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

                arrWhitePawnAttacks.add(new ArrayList<Integer>());
                computeWhitePawnAttacks(squareIndex, file);
            }
        }
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
        updateKingsPosition();
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
    
    public void updateKingsPosition() {
        int piece;
        for (int i = 0; i < 64; i++) {
            piece = board[i].getPiece();
            if (Piece.isPieceType(piece, Piece.king)) {
                if (Piece.pieceColor(piece) == Color.white) {
                    whiteKingPosition = i;
                }
                else {
                    blackKingPosition = i;
                }
            }
        }
    }

    public void updateFen() {
        FEN.updatePiecePlacement(board);
        FEN.updateEnPassantSquareTarget(enPassant);
        FEN.updateSideToMove(sideToMove);
    }

    public void checkIfMoveDisablesCastling(int initialPosition) {
        if (initialPosition == 0 && white_queen_side_castling) {
            white_queen_side_castling = false;
            FEN.updateCastlingAbility(white_king_side_castling, false, black_king_side_castling, black_queen_side_castling);
        }
        if (initialPosition == 7 && white_king_side_castling) {
            white_king_side_castling = false;
            FEN.updateCastlingAbility(false, white_queen_side_castling, black_king_side_castling, black_queen_side_castling);
        }
        if (initialPosition == 56 && black_queen_side_castling) {
            black_queen_side_castling = false;
            FEN.updateCastlingAbility(white_king_side_castling, white_queen_side_castling, black_king_side_castling, false);
        }
        if (initialPosition == 63 && black_king_side_castling) {
            black_king_side_castling = false;
            FEN.updateCastlingAbility(white_king_side_castling, white_queen_side_castling, false, black_queen_side_castling);
        }
        if (initialPosition == 4 && (white_king_side_castling || white_queen_side_castling)) {
            white_queen_side_castling = white_king_side_castling = false;
            FEN.updateCastlingAbility(false, false, black_king_side_castling, black_queen_side_castling);
        }
        if (initialPosition == 60 && (black_king_side_castling || black_queen_side_castling)) {
            black_queen_side_castling = black_king_side_castling = false;
            FEN.updateCastlingAbility(white_king_side_castling, white_queen_side_castling, false, false);
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

        if (Piece.isPieceType(board[move.targetSquareIndex].getPiece(), Piece.king)) {
            if (sideToMove == Color.white) {
                whiteKingPosition = move.targetSquareIndex;
            }
            else {
                blackKingPosition = move.targetSquareIndex;
            }
        }

        checkIfMoveDisablesCastling(move.initialSquareIndex);
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
        updateFen();
        gameHistory.add(FEN.getFen());
    }

    public void undo() {
        if (gameHistory.size() > 1) {
            gameHistory.remove(gameHistory.size() - 1);
            FEN = new Fen(gameHistory.get(gameHistory.size() - 1));
            loadGameStateFromFen();
        }
    }

    public void promotePawn(int pawnPosition, int promotedTo) {
        board[pawnPosition].setPiece(promotedTo);
        FEN.updatePiecePlacement(board);
        gameHistory.remove(gameHistory.size() - 1);
        gameHistory.add(FEN.getFen());
    }


    public List<Move> generatePseudoLegalMoves() {
        List<Move> availableMoves = new ArrayList<>();
        for (int i = 0; i < 64; i ++) {
            if (Piece.pieceColor(board[i].getPiece()) == sideToMove) {
                List<Move> moves = getAvailableMoves(i);
                availableMoves.addAll(moves);
            }
        }
        return availableMoves;
    }
    /**
     * After one piece was selected, this method will be called to compute the available moves for
     * that piece.
     *
     * @// TODO: 2/28/2021
     * Instead of generating moves only for the selected piece it will be better to generate prior
     * to piece selection everything. Therefore everything will be computed while the player is
     * thinking, only the validation of move will be done after player input.
     */
    public List<Move> getAvailableMoves(int position) {
        List<Move> availableMovesForPiece = new ArrayList<>();
        int piece = board[position].getPiece();
        if (Piece.isSlidingPiece(piece)) {
            availableMovesForPiece = getAvailableMovesForSlidingPiece(position);
        } else if (Piece.isPieceType(piece, Piece.knight)) {
            availableMovesForPiece = getAvailableMovesForKnight(position);
        } else if (Piece.isPieceType(piece, Piece.king)) {
            availableMovesForPiece = getAvailableMovesForKing(position);
        } else if (Piece.isPieceType(piece, Piece.pawn)) {
            availableMovesForPiece = getAvailableMovesForPawn(position);
        }
        return availableMovesForPiece;
    }

    /**
     * This method is called to compute the available moves for pieces that slide on the board
     * i.e the rook, the bishop and the queen.
     *
     * @param initialSquareIndex the square of the board for which we want to compute the available moves. This
     *               variable holds the index of the board square and the piece situated on the square.
     */
    public List<Move> getAvailableMovesForSlidingPiece(int initialSquareIndex) {
        List<Move> moves = new ArrayList<>();
        // Rook can walk only int the first 4 directions.
        // Bishop can walk only in the last 4 directions.
        // Queen is unrestricted.
        int startDirection = Piece.isPieceType(board[initialSquareIndex].getPiece(), Piece.bishop) ? 4 : 0;
        int endDirection = Piece.isPieceType(board[initialSquareIndex].getPiece(), Piece.rook) ? 4 : 8;
        for (int direction = startDirection; direction < endDirection; direction++) {
            for (int i = 0; i < squaresToTheEdge[initialSquareIndex][direction]; i++) {
                int targetSquareIndex = initialSquareIndex + directionOffset[direction] * (i + 1);
                int targetPiece = board[targetSquareIndex].getPiece();

                // If it encounters a friendly piece then it can't move further in this direction.
                if (Piece.pieceColor(targetPiece) == sideToMove) {
                    break;
                }
                moves.add(new Move(initialSquareIndex, targetSquareIndex));

                // If the square had an enemy piece on it, then no other moves are available in
                // this direction.
                if (Piece.pieceColor(targetPiece) == Color.oppositeColor(sideToMove)) {
                    break;
                }
            }
        }
        return moves;
    }
    public List<Move> getAvailableMovesForKnight(int initialSquareIndex) {
        List<Move> moves = new ArrayList<>();
        for (int targetIndex : arrKnightAttacks.get(initialSquareIndex)) {
            int targetPiece = board[targetIndex].getPiece();
            // If the target is a friendly piece, then skip this one.
            if (Piece.pieceColor(targetPiece) == sideToMove) {
                continue;
            }
            moves.add(new Move(initialSquareIndex, targetIndex));
        }
        return moves;
    }

    /**
     * @// TODO: 3/24/2021 Add the castling move to the king if it is possible 
     * @param initialSquareIndex
     * @return
     */
    public List<Move> getAvailableMovesForKing(int initialSquareIndex) {
        List<Move> moves = new ArrayList<>();
        for (int targetIndex : arrKingAttacks.get(initialSquareIndex)) {
            int targetPiece = board[targetIndex].getPiece();
            // If the target is a friendly piece, then skip this one.
            if (Piece.pieceColor(targetPiece) == sideToMove) {
                continue;
            }
            moves.add(new Move(initialSquareIndex, targetIndex));
        }
        return moves;
//        allLegalMoves.put(initialSquareIndex, moves);
    }
    private List<Move> pawnPushes2(int squareIndex, int rank, int color) {
        List<Move> moves = new ArrayList<>();
        int forwardMoves = 1;
        int direction = 1;
        int rankOffset = 0;
        if (color == Color.black) {
            direction = -1;
            rankOffset = 5;
        }
        // This piece cannot be moved any further.
        if (rank == 0 || rank == 7) {
            forwardMoves = 0;
        }
        if (rank == 1 + rankOffset) {
            forwardMoves++;
        }
        for (int i = 0; i < forwardMoves; i++) {
            int targetIndex = squareIndex + (i+1) * 8 * direction;
            int targetPiece = board[targetIndex].getPiece();
            // If the target is a piece, then the pawn can no longer advance.
            if (targetPiece != Piece.empty) {
                break;
            }
            Move move = new Move(squareIndex, targetIndex);
            if ((rank == 6 && color == Color.white) ||  (rank == 1 && color == Color.black)) {
                move.markAsPromotionMove();
            }
            /*
            It's a move that make enPassant available.
             */
            if (i == 1) {
                move.markAsDoubleSquarePawnMove();
            }
            moves.add(new Move(move));
        }
        return moves;
    }
    private List<Move> getAvailableMovesForPawn(int initialSquareIndex) {
        List<Move> moves;
        int rank = initialSquareIndex >> 3;
        int color = Piece.pieceColor(board[initialSquareIndex].getPiece());
        moves = pawnPushes2(initialSquareIndex, rank, color);
        if (color == Color.white) {
            for (int targetIndex : arrWhitePawnAttacks.get(initialSquareIndex)) {
                int targetPiece = board[targetIndex].getPiece();
                // If the target is not an enemy then the move cannot be done.

                if (enPassant != null) {
                    if (targetIndex != enPassant && Piece.pieceColor(targetPiece) != Color.oppositeColor(color)) {
                        continue;
                    }
                }
                else {
                    if ((Piece.pieceColor(targetPiece) != Color.oppositeColor(color))) {
                        continue;
                    }
                }
                Move move = new Move(initialSquareIndex, targetIndex);
                if (rank == 6) {
                    move.markAsPromotionMove();
                }
                moves.add(new Move(move));
            }
        }
        else {
            for (int targetIndex : arrBlackPawnAttacks.get(initialSquareIndex)) {
                int targetPiece = board[targetIndex].getPiece();
                // If the target is not an enemy then the move cannot be done.
                if (enPassant != null) {
                    if (targetIndex != enPassant && Piece.pieceColor(targetPiece) != Color.oppositeColor(color)) {
                        continue;
                    }
                }
                else {
                    if ((Piece.pieceColor(targetPiece) != Color.oppositeColor(color))) {
                        continue;
                    }
                }
                Move move = new Move(initialSquareIndex, targetIndex);
                if (rank == 1) {
                    move.markAsPromotionMove();
                }
                moves.add(new Move(move));
            }
        }
        return moves;
//        allLegalMoves.put(initialSquareIndex, moves);
    }


    public int getSideToMove() {
        return sideToMove;
    }
    public Square[] getBoard() {
        return board;
    }

    public Fen getFEN(){
        return FEN;
    }

}
