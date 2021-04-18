package com.example.chess.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.chess.R;

/**
 * The class extends the ImageView class, adding new attributes.The two additional attributes
 * of this class are squareIndex and piece.
 * -squareIndex tells which square on the board is represented by this view. As the views are
 * never moved, when the board is rotated this index will be the one that has to change. The
 * change must occurs as the board numbering must always be done from white's side.
 * -piece tells what piece, if any, is situated in this square
 */
public class BoardSquareView extends AppCompatImageView {
    private int squareIndex;

    /**
     * Class constructor.
     */
    public BoardSquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BoardSquareView, 0, 0);
        try {
            squareIndex = ta.getInteger(R.styleable.BoardSquareView_square_index, 0);
        } finally {
            ta.recycle();
        }
    }

    public int getSquareIndex() {
        return squareIndex;
    }

    public void setSquareIndex(int new_value) {
        squareIndex = new_value;
        invalidate();
        requestLayout();
    }

}
