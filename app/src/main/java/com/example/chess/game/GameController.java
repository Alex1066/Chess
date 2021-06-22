package com.example.chess.game;

import com.example.chess.entities.Move;
import com.example.chess.entities.Piece;
import com.example.chess.entities.PlayMode;
import com.example.chess.entities.Square;
import com.example.chess.utility.Color;

import java.util.ArrayList;
import java.util.List;


public class GameController {
    private final Game game;
    private final GameUI gameUI;
    private final MovementHandler mh;

    private Square pieceToMove = null;
    private int promotedPawnIndex;
    private int promotedTo;
    private final List<Integer> highlightedSquares = new ArrayList<>();
    private List<Move> legalMoves = new ArrayList<>();
    private AIPlayer AI;
    private int playMode;

    public GameController(Game game, GameUI gameUI, int playMode) {
        this.game = game;
        this.gameUI = gameUI;
        this.playMode = playMode;
        mh = new MovementHandler(game);
        AI = new AIPlayer(game, mh);
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
            if (pieceColor == game.sideToMove) {
                selectPiece(targetedSquare);
            }
        } else {
            if (pieceColor == game.sideToMove) {
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
                if (game.sideToMove == Color.white) {
                    System.out.println("BLACK HAS WOOOON");
                } else {
                    System.out.println("WHITE HAS WOOOON");
                }
            }
            else if(playMode == PlayMode.vsComputer) {
                Move AIMove = AI.doRandomMove();
                game.executeMove(AIMove);
                generateLegalMoves();
                if (isCheckMate()) {
                    if (game.sideToMove == Color.white) {
                        System.out.println("BLACK HAS WOOOON");
                    } else {
                        System.out.println("WHITE HAS WOOOON");
                    }
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

    public void undo(int undoDepth) {
        endAction();
        game.undo(undoDepth);
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
        else if(playMode == PlayMode.vsComputer){
            Move AIMove = AI.doRandomMove();
            game.executeMove(AIMove);
            generateLegalMoves();
            if (isCheckMate()) {
                if (game.sideToMove == Color.white) {
                    System.out.println("BLACK HAS WOOOON");
                } else {
                    System.out.println("WHITE HAS WOOOON");
                }
            }
        }
        gameUI.removePromotionOptions();
        gameUI.drawPiecesOnBoard(game.getBoard());
    }

    /**
     * @// TODO: 3/23/2021 When it is check mate display a message for the player. also add a reset button
     */
    public boolean isCheckMate() {
        return legalMoves.size() == 0;
    }

    public void generateLegalMoves() {
        mh.generateLegalMoves();
        legalMoves = mh.legalMoves;
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
