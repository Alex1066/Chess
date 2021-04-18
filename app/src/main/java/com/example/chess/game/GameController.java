package com.example.chess.game;

import com.example.chess.entities.Castling;
import com.example.chess.entities.CastlingMove;
import com.example.chess.entities.Move;
import com.example.chess.entities.Piece;
import com.example.chess.entities.Square;
import com.example.chess.utility.Color;

import java.util.ArrayList;
import java.util.List;


public class GameController {
    private Game game;
    private GameUI gameUI;

    private Square pieceToMove = null;
    private int promotedPawnIndex;
    private int promotedTo;
    private List<Integer> highlightedSquares = new ArrayList<>();
    private List<Move> legalMoves = new ArrayList<>();

    public GameController(Game game, GameUI gameUI) {
        this.game = game;
        this.gameUI = gameUI;
    }

    /**
     * Here it is decided what action should be done on click events.
     *
     * @param targetedSquare the index of the square of the virtual board
     */
    public void actionHandler(int targetedSquare) {
        Square clickedSquare = game.getBoard()[targetedSquare];
        int pieceColor = Piece.pieceColor(clickedSquare.getPiece());
        if (pieceToMove == null) {
            if (pieceColor == game.getSideToMove()) {
                selectPiece(targetedSquare);
            }
        } else {
            if (pieceColor == game.getSideToMove()) {
                if (pieceToMove.getPosition() != targetedSquare) {
                    endAction();
                    selectPiece(targetedSquare);
                } else {
                    endAction();
                }
            } else {
                Move wantedMove = getWantedMove(new Move(pieceToMove.getPosition(), targetedSquare));
                if (wantedMove != null) {
                    executeMove(wantedMove);
                }
                endAction();
            }
        }
    }

    public void executeMove(Move wantedMove) {
        game.executeMove(wantedMove);
        if (wantedMove.isPromotionMove) {
            gameUI.displayPromotionOptions(pieceToMove);
            // Save the position of the pawn that has to be converted in a promoted piece.
            promotedPawnIndex = wantedMove.targetSquareIndex;
            // Set the color of the piece;
            promotedTo = Piece.pieceColor(pieceToMove.getPiece()) << 3;
        } else {
            generateLegalMoves();
            if (isCheckMate()) {
                if (game.getSideToMove() == Color.white) {
                    System.out.println("BLACK HAS WOOOON");
                } else {
                    System.out.println("WHITE HAS WOOOON");
                }
            }
        }
        gameUI.drawPiecesOnBoard(game.getBoard());

    }

    public void selectPiece(int squareIndex) {
        pieceToMove = new Square(game.getBoard()[squareIndex]);
        for (Move move : legalMoves) {
            if (pieceToMove.getPosition() == move.initialSquareIndex) {
                highlightedSquares.add(move.targetSquareIndex);
            }
        }
        gameUI.highlightSquares(highlightedSquares);
    }

    public void endAction() {
        gameUI.undoHighlight(highlightedSquares);
        highlightedSquares.clear();
        pieceToMove = null;
    }

    public void undo() {
        endAction();
        game.undo();
        gameUI.drawPiecesOnBoard(game.getBoard());
        generateLegalMoves();
    }

    public void doPromotion(int promotionPiece) {
        // The color is now, now we set the promotion piece.
        promotedTo += promotionPiece;
        game.promotePawn(promotedPawnIndex, promotedTo);
        generateLegalMoves();
        if (isCheckMate()) {
            System.out.println("The game is oveeeer");
            if (game.getSideToMove() == Color.white) {
                System.out.println("BLACK HAS WOOOON");
            } else {
                System.out.println("WHITE HAS WOOOON");
            }
        }
        gameUI.removePromotionOptions();
        gameUI.drawPiecesOnBoard(game.getBoard());
    }

    public boolean isAttackingKing(List<Move> moves, int kingPosition) {
        for (Move m : moves) {
            if (m.targetSquareIndex == kingPosition) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     * @// TODO: 3/23/2021 When it is check mate display a message for the player. also add a reset button
     */
    public boolean isCheckMate() {
        return legalMoves.size() == 0;
    }

    public void generateLegalMoves() {
        legalMoves.clear();
        int kingPosition = 0;
        List<Move> pseudoLegal = game.generatePseudoLegalMoves();
        for (Move moveToCheck : pseudoLegal) {
            game.executeMove(moveToCheck);
            if (game.getSideToMove() == Color.black) {
                kingPosition = game.whiteKingPosition;
            } else {
                kingPosition = game.blackKingPosition;
            }
            List<Move> opponentResponses = game.generatePseudoLegalMoves();
            if (!isAttackingKing(opponentResponses, kingPosition)) {
                legalMoves.add(moveToCheck);
            }
            game.undo();
        }
        addCastling(kingPosition);
    }

    public boolean checkWhiteKingSideCastling(int kingPosition) {
        if (game.white_king_side_castling &&
                game.getBoard()[kingPosition + 1].getPiece() == Piece.empty &&
                game.getBoard()[kingPosition + 2].getPiece() == Piece.empty) {
            for (int i = 0; i <= 2; i++) {
                game.executeMove(new Move(kingPosition, kingPosition + i));
                List<Move> opponentResponses = game.generatePseudoLegalMoves();
                if (isAttackingKing(opponentResponses, kingPosition + i)) {
                    game.undo();
                    return false;
                }
                game.undo();
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean checkWhiteQueenSideCastling(int kingPosition) {
        if (game.white_queen_side_castling &&
                game.getBoard()[kingPosition - 1].getPiece() == Piece.empty &&
                game.getBoard()[kingPosition - 2].getPiece() == Piece.empty &&
                game.getBoard()[kingPosition - 3].getPiece() == Piece.empty) {
            for (int i = 0; i <= 3; i++) {
                game.executeMove(new Move(kingPosition, kingPosition - i));
                List<Move> opponentResponses = game.generatePseudoLegalMoves();
                if (isAttackingKing(opponentResponses, kingPosition - i)) {
                    game.undo();
                    return false;
                }
                game.undo();
            }
        } else {
            return false;
        }
        return true;
    }

    public boolean checkBlackKingSideCastling(int kingPosition) {
        if (game.black_king_side_castling &&
                game.getBoard()[kingPosition + 1].getPiece() == Piece.empty &&
                game.getBoard()[kingPosition + 2].getPiece() == Piece.empty) {
            for (int i = 0; i <= 2; i++) {
                game.executeMove(new Move(kingPosition, kingPosition + i));
                List<Move> opponentResponses = game.generatePseudoLegalMoves();
                if (isAttackingKing(opponentResponses, kingPosition + i)) {
                    game.undo();
                    return false;
                }
                game.undo();
            }
        } else {
            return false;
        }
        return true;
    }

    public  boolean checkBlackQueenSideCastling(int kingPosition) {
        if (game.black_queen_side_castling &&
                game.getBoard()[kingPosition - 1].getPiece() == Piece.empty &&
                game.getBoard()[kingPosition - 2].getPiece() == Piece.empty &&
                game.getBoard()[kingPosition - 3].getPiece() == Piece.empty) {
            for (int i = 0; i <= 3; i++) {
                game.executeMove(new Move(kingPosition, kingPosition - i));
                List<Move> opponentResponses = game.generatePseudoLegalMoves();
                if (isAttackingKing(opponentResponses, kingPosition - i)) {
                    game.undo();
                    return false;
                }
                game.undo();
            }
        } else {
            return false;
        }
        return true;
    }

    public void addCastling(int kingPosition) {
        CastlingMove castling;
        if (game.getSideToMove() == Color.white) {
            if (checkWhiteKingSideCastling(kingPosition)) {
                castling = new CastlingMove(kingPosition, kingPosition + 2);
                castling.rookInitialPosition = Castling.whiteKingSideRookInitialPosition;
                castling.rookFinalPosition = Castling.whiteRookPositionAfterKingSideCastling;
                castling.markAsCastling();
                legalMoves.add(castling);
            }
            if (checkWhiteQueenSideCastling(kingPosition)) {
                castling = new CastlingMove(kingPosition, kingPosition - 2);
                castling.rookInitialPosition = Castling.whiteQueenSideRookInitialPosition;
                castling.rookFinalPosition = Castling.whiteRookPositionAfterQueenSideCastling;
                castling.markAsCastling();
                legalMoves.add(castling);
            }
        }
        else {
            if (checkBlackKingSideCastling(kingPosition)) {
                castling = new CastlingMove(kingPosition, kingPosition + 2);
                castling.rookInitialPosition = Castling.blackKingSideRookInitialPosition;
                castling.rookFinalPosition = Castling.blackRookPositionAfterKingSideCastling;
                castling.markAsCastling();
                legalMoves.add(castling);
            }
            if (checkBlackQueenSideCastling(kingPosition)) {
                castling = new CastlingMove(kingPosition, kingPosition - 2);
                castling.rookInitialPosition = Castling.blackQueenSideRookInitialPosition;
                castling.rookFinalPosition = Castling.blackRookPositionAfterQueenSideCastling;
                castling.markAsCastling();
                legalMoves.add(castling);
            }
        }
    }

    public Move getWantedMove(Move wantedMove) {
        for (Move m : legalMoves) {
            if (m.equals(wantedMove)) {
                return m;
            }
        }
        return null;
    }

    /**
     * This method will rotate the board 180 degrees. The players color is kept. This allows
     * the player to play from any side(top or bottom). The state of the game is preserved.
     */
    public void changeSide() {
         /*
        When the board is rotated 180 degrees, the underlying representation of the game
        is not affected. The only things that change are the visuals. Since the views (the squares
        of the board) are never moved on the screen, 2 actions are required:
        1) Reverse the order in the list of views. The pieces are placed on the board from white's
        perspective. Since the view board does't actually move, the placement of the pieces should
        start from the other side of the board, thus the reverse of the order.
        2) Change the indices that identifies a view with it's place on the board. The views are
        numbered from white's perspective. Since the players changed sides, a new numbering is needed.
        This is acquired by revering the existing indices.
         */
        gameUI.reverseIndices();
        gameUI.reverseViewOrder();

        gameUI.undoHighlight(highlightedSquares);
        gameUI.drawPiecesOnBoard(game.getBoard());
        if (pieceToMove != null) {
            // Since the map was rotated, reselect the piece that was selected prior to
            // the rotation.
            gameUI.highlightSquares(highlightedSquares);
        }
    }

    public void StartGame() {
        game.loadGameStateFromFen();
        generateLegalMoves();
        gameUI.colorBoard();
        gameUI.drawPiecesOnBoard(game.getBoard());
    }
}
