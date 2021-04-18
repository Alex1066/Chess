package com.example.chess.game;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import com.example.chess.customviews.BoardSquareView;
import com.example.chess.customviews.PromotionOptionView;
import com.example.chess.design.ColorTheme;
import com.example.chess.entities.Piece;
import com.example.chess.entities.Square;
import com.example.chess.utility.SquareColor;

import java.util.List;
import java.util.Map;


public class GameUI {

    private BoardSquareView[] boardSquares;
    private List<PromotionOptionView> promotionOptions;
    private LinearLayout promotionLayout;
    private ColorTheme colorTheme;
    private Map<Integer, Drawable> pieces;

    /**
     * @param boardSquares the list of the views displayed on the screen
     * @param colorTheme   all the information about the color palette of the visuals on the board
     * @param pieces       the images of the chess pieces
     * @// TODO: 2/28/2021
     * On this matter a good thing to be implemented is to change the asset from background image
     * to foreground image. This way I can add as a background to the piece some sort of sign that
     * tells that this piece can be attacked by the selected piece.
     */
    public GameUI(BoardSquareView[] boardSquares, LinearLayout promotionLayout, List<PromotionOptionView> promotionOptions, ColorTheme colorTheme, Map<Integer, Drawable> pieces) {
        this.boardSquares = boardSquares;
        this.promotionOptions = promotionOptions;
        this.promotionLayout = promotionLayout;
        this.colorTheme = colorTheme;
        this.pieces = pieces;
    }

    public void displayPromotionOptions(Square selectedSquare) {
        int color = Piece.pieceColor(selectedSquare.getPiece());
        promotionLayout.setVisibility(View.VISIBLE);
        for (PromotionOptionView promotionOption : promotionOptions) {
            // The color bits start from bit 4, thus the 3 bit shift.
            promotionOption.setImageDrawable(pieces.get((color << 3) + promotionOption.getPieceType()));
        }
    }

    public void removePromotionOptions() {
        promotionLayout.setVisibility(View.GONE);
    }

    /**
     * This method sets the colors of the board to the current color theme.
     */
    public void colorBoard() {
        for (int i = 0; i < boardSquares.length / 2; i++) {
            if (SquareColor.findColorFromIndex(i) == SquareColor.lightSquare) {
                boardSquares[i].setBackgroundColor(colorTheme.getLightSquare());
                boardSquares[boardSquares.length - i - 1].setBackgroundColor(colorTheme.getLightSquare());
            } else {
                boardSquares[i].setBackgroundColor(colorTheme.getDarkSquare());
                boardSquares[boardSquares.length - i - 1].setBackgroundColor(colorTheme.getDarkSquare());
            }
        }
    }

    /**
     * This method will load the assets of the pieces on the squares, after the positions were set.
     *
     * @param boardData a list that contains the piece-codes for each square
     */
    public void drawPiecesOnBoard(Square[] boardData) {
        for (int index = 0; index < boardData.length; index++) {
            boardSquares[index].setImageDrawable(pieces.get(boardData[index].getPiece()));
        }
    }

    /**
     * Changes the color to the squares where the current piece can move.
     *
     * @param squaresToHighlight the list of squares that are to be highlighted
     */
    public void highlightSquares(List<Integer> squaresToHighlight) {
        for (Integer square : squaresToHighlight) {
            if (SquareColor.findColorFromIndex(boardSquares[square].getSquareIndex()) == SquareColor.lightSquare) {
                boardSquares[square].setBackgroundColor(colorTheme.getActiveLightSquare());
            } else {
                boardSquares[square].setBackgroundColor(colorTheme.getActiveDarkSquare());
            }
        }
    }

    /**
     * Changes the highlighted squares back to normal.
     *
     * @param highlightedSquares the list of squares to be reverted to the original color
     */
    public void undoHighlight(List<Integer> highlightedSquares) {
        for (Integer square : highlightedSquares) {
            if (SquareColor.findColorFromIndex(boardSquares[square].getSquareIndex()) == SquareColor.lightSquare) {
                boardSquares[square].setBackgroundColor(colorTheme.getLightSquare());
            } else {
                boardSquares[square].setBackgroundColor(colorTheme.getDarkSquare());
            }
        }
    }

    public void reverseIndices() {
        for (int i = 0; i < boardSquares.length / 2; i++) {
            int aux = boardSquares[i].getSquareIndex();
            boardSquares[i].setSquareIndex(boardSquares[boardSquares.length - i - 1].getSquareIndex());
            boardSquares[boardSquares.length - i - 1].setSquareIndex(aux);
        }
    }

    public void reverseViewOrder() {
        for (int i = 0; i < boardSquares.length / 2; i++) {
            BoardSquareView aux = boardSquares[i];
            boardSquares[i] = boardSquares[boardSquares.length - i - 1];
            boardSquares[boardSquares.length - i - 1] = aux;
        }
    }

    public BoardSquareView[] getBoardSquares() {
        return boardSquares;
    }

    public ColorTheme getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(ColorTheme colorTheme) {
        this.colorTheme = colorTheme;
    }

    public Map<Integer, Drawable> getPieces() {
        return pieces;
    }

    public void setPieces(Map<Integer, Drawable> pieces) {
        this.pieces = pieces;
    }

}
