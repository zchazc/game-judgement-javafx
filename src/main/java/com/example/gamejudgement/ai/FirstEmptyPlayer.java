package com.example.gamejudgement.ai;

import javafx.util.Pair;

public class FirstEmptyPlayer implements GoBangPlayer{

    public static final FirstEmptyPlayer DEFAULT = new FirstEmptyPlayer();
    @Override
    public Pair<Integer, Integer> makeMove(int[][] board, int col, int row, int playId) {
        for (int x = 0; x < col; x++) {
            for (int y = 0; y < row; y++) {
                if(board[x][y] == 0){
                    return new Pair<>(x,y);
                }
            }
        }
        return null;
    }
}
