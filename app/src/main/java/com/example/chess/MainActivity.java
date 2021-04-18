package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.chess.design.ColorTheme;
import com.example.chess.entities.Piece;
import com.example.chess.customviews.BoardSquareView;
import com.example.chess.customviews.PromotionOptionView;
import com.example.chess.game.Fen;
import com.example.chess.game.Game;
import com.example.chess.game.GameController;
import com.example.chess.game.GameUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BoardSquareView[] squares = new BoardSquareView[64];
    private List<PromotionOptionView> promotionOptions = new ArrayList<>();
    private LinearLayout promotionLayout;
    private Map<String, Map<Integer, Drawable>> allSetsOfPieces = new HashMap<>();
    private Map<String, ColorTheme> allBoardThemes = new HashMap<>();
    private Game gc;
    private GameUI gd;
    private GameController gm;


    /**
     * @// TODO: 2/28/2021
     * Instead of passing the context it would be better to just pass a list of drawable
     * containing the chess pieces. This way GameControl will only handle the game logic and
     * nothing more.
     * @// TODO: 3/1/2021
     * The above TO DO was done. But it may be interesting to see if I could implement an GameMaster
     * that has as attributes an GameControl and GameDesign. Or create another class GameAbstract
     * or GameNumerical that does what GameControl does now, and the GameControl will have an
     * GameDesign and GameAbstract.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setClickListeners();
        loadAssets();
        loadColorThemes();

        promotionLayout = findViewById(getResources().getIdentifier("promotion_options", "id", getPackageName()));
        // @todo Perhaps read this from a file or let the user introduce its own fen. This one should
        // be the default value.
//        String startingFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        String startingFen = "r3k2r/8/n2bbq1n/8/8/B1NB1Q1N/8/R3K2R w KQkq - 0 1";
        Fen fen = new Fen(startingFen);

        gc = new Game(fen);
        gd = new GameUI(squares, promotionLayout, promotionOptions, allBoardThemes.get("red"), allSetsOfPieces.get("slim"));
        gm = new GameController(gc, gd);
        gm.StartGame();

    }
    public void setClickListenersForPromotionViews() {
        PromotionOptionView queen = findViewById(getResources().getIdentifier("queen", "id", getPackageName()));
        queen.setPieceType(Piece.queen);
        queen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                gm.doPromotion(Piece.queen);
            }
        });
        promotionOptions.add(queen);
        PromotionOptionView rook = findViewById(getResources().getIdentifier("rook", "id", getPackageName()));
        rook.setPieceType(Piece.rook);
        rook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                gm.doPromotion(Piece.rook);
            }
        });
        promotionOptions.add(rook);
        PromotionOptionView bishop = findViewById(getResources().getIdentifier("bishop", "id", getPackageName()));
        bishop.setPieceType(Piece.bishop);
        bishop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                gm.doPromotion(Piece.bishop);
            }
        });
        promotionOptions.add(bishop);
        PromotionOptionView knight = findViewById(getResources().getIdentifier("knight", "id", getPackageName()));
        knight.setPieceType(Piece.knight);
        knight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                gm.doPromotion(Piece.knight);
            }
        });
        promotionOptions.add(knight);
    }

    public void setClickListeners() {
        String viewID;
        int resID;

        for (int index = 0; index < 64; index++) {
            viewID = index < 10 ? "square_0" + index : "square_" + index;
            resID = getResources().getIdentifier(viewID, "id", getPackageName());
            squares[index] = findViewById(resID);
            squares[index].setOnClickListener(this);
        }
        Button bt = findViewById(getResources().getIdentifier("Reset", "id", getPackageName()));
        bt.setOnClickListener(this);

        setClickListenersForPromotionViews();

    }

    @Override
    public void onClick(View view) {
        if (promotionLayout.getVisibility() == View.VISIBLE && !(view instanceof PromotionOptionView)) {
            gm.undo();
            promotionLayout.setVisibility(View.GONE);
        }
        if (view instanceof BoardSquareView) {
            gm.actionHandler(((BoardSquareView) view).getSquareIndex());
        }
        if (view instanceof Button) {
            if (view.getId() == getResources().getIdentifier("Reset", "id", getPackageName())) {
//                gm.changeSide();
                gm.undo();
            }
        }
//        Toast.makeText(this, "Player won", Toast.LENGTH_SHORT).show();
    }

    public void loadColorThemes() {
        int lightSquare = ContextCompat.getColor(this, R.color.blue_theme_light_square);
        int darkSquare = ContextCompat.getColor(this, R.color.blue_theme_dark_square);
        int highlightedLightSquare = ContextCompat.getColor(this, R.color.blue_theme_highlighted_light_square);
        int highlightedDarkSquare = ContextCompat.getColor(this, R.color.blue_theme_highlighted_dark_square);
        allBoardThemes.put("blue", new ColorTheme(lightSquare, darkSquare, highlightedLightSquare, highlightedDarkSquare));

        lightSquare = ContextCompat.getColor(this, R.color.red_theme_light_square);
        darkSquare = ContextCompat.getColor(this, R.color.red_theme_dark_square);
        highlightedLightSquare = ContextCompat.getColor(this, R.color.red_theme_highlighted_light_square);
        highlightedDarkSquare = ContextCompat.getColor(this, R.color.red_theme_highlighted_dark_square);
        allBoardThemes.put("red", new ColorTheme(lightSquare, darkSquare, highlightedLightSquare, highlightedDarkSquare));
    }

    public void loadAssets() {
        Map<Integer, Drawable> pieceSet = new HashMap<>();

        // Classic set of pieces.
        pieceSet.put(Piece.black+ Piece.king, ContextCompat.getDrawable(this, R.drawable.classic_black_king));
        pieceSet.put(Piece.black+ Piece.queen, ContextCompat.getDrawable(this, R.drawable.classic_black_queen));
        pieceSet.put(Piece.black+ Piece.rook, ContextCompat.getDrawable(this, R.drawable.classic_black_rook));
        pieceSet.put(Piece.black+ Piece.bishop, ContextCompat.getDrawable(this, R.drawable.classic_black_bishop));
        pieceSet.put(Piece.black+ Piece.knight, ContextCompat.getDrawable(this, R.drawable.classic_black_knight));
        pieceSet.put(Piece.black+ Piece.pawn, ContextCompat.getDrawable(this, R.drawable.classic_black_pawn));
        pieceSet.put(Piece.white+ Piece.king, ContextCompat.getDrawable(this, R.drawable.classic_white_king));
        pieceSet.put(Piece.white+ Piece.queen, ContextCompat.getDrawable(this, R.drawable.classic_white_queen));
        pieceSet.put(Piece.white+ Piece.rook, ContextCompat.getDrawable(this, R.drawable.classic_white_rook));
        pieceSet.put(Piece.white+ Piece.bishop, ContextCompat.getDrawable(this, R.drawable.classic_white_bishop));
        pieceSet.put(Piece.white+ Piece.knight, ContextCompat.getDrawable(this, R.drawable.classic_white_knight));
        pieceSet.put(Piece.white+ Piece.pawn, ContextCompat.getDrawable(this, R.drawable.classic_white_pawn));
        allSetsOfPieces.put("classic", new HashMap<Integer, Drawable>(pieceSet));
        pieceSet.clear();

        // Slim set of pieces.
        pieceSet.put(Piece.black+ Piece.king, ContextCompat.getDrawable(this, R.drawable.slim_black_king));
        pieceSet.put(Piece.black+ Piece.queen, ContextCompat.getDrawable(this, R.drawable.slim_black_queen));
        pieceSet.put(Piece.black+ Piece.rook, ContextCompat.getDrawable(this, R.drawable.slim_black_rook));
        pieceSet.put(Piece.black+ Piece.bishop, ContextCompat.getDrawable(this, R.drawable.slim_black_bishop));
        pieceSet.put(Piece.black+ Piece.knight, ContextCompat.getDrawable(this, R.drawable.slim_black_knight));
        pieceSet.put(Piece.black+ Piece.pawn, ContextCompat.getDrawable(this, R.drawable.slim_black_pawn));
        pieceSet.put(Piece.white+ Piece.king, ContextCompat.getDrawable(this, R.drawable.slim_white_king));
        pieceSet.put(Piece.white+ Piece.queen, ContextCompat.getDrawable(this, R.drawable.slim_white_queen));
        pieceSet.put(Piece.white+ Piece.rook, ContextCompat.getDrawable(this, R.drawable.slim_white_rook));
        pieceSet.put(Piece.white+ Piece.bishop, ContextCompat.getDrawable(this, R.drawable.slim_white_bishop));
        pieceSet.put(Piece.white+ Piece.knight, ContextCompat.getDrawable(this, R.drawable.slim_white_knight));
        pieceSet.put(Piece.white+ Piece.pawn, ContextCompat.getDrawable(this, R.drawable.slim_white_pawn));
        allSetsOfPieces.put("slim", new HashMap<Integer, Drawable>(pieceSet));
        pieceSet.clear();
    }
}