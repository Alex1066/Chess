package com.example.chess.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.chess.R;

public class PromotionOptionView extends AppCompatImageView {
    private int pieceType;

    /**
     * Class constructor.
     */
    public PromotionOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PromotionOptionView, 0, 0);
        try {
            pieceType = ta.getInteger(R.styleable.PromotionOptionView_piece_type, 0);
        } finally {
            ta.recycle();
        }
    }

    public int getPieceType() {
        return pieceType;
    }

    public void setPieceType(int new_value) {
        pieceType = new_value;
        invalidate();
        requestLayout();
    }
}
