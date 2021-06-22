package com.example.chess.game;


import com.example.chess.entities.Move;

import java.util.List;
import java.util.Random;

public class AIPlayer {

    private final Game game;
    private final MovementHandler mh;

    public AIPlayer(Game game, MovementHandler mh) {
        this.game = game;
        this.mh = mh;
    }

    public Move doRandomMove() {
        mh.generateLegalMoves();
        List<Move> moveList = mh.legalMoves;
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(moveList.size());
        return moveList.get(index);
    }
    
}
