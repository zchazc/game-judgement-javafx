package com.example.gamejudgement.ai;

import javafx.util.Pair;

import java.util.Random;

public class RandomPlayer implements GoBangPlayer{
    public static final RandomPlayer DEFAULT = new RandomPlayer();
    private Random rd = new Random();
    @Override
    public Pair<Integer, Integer> makeMove(int[][] board, int col, int row, int playId) {
        int x,y;
        do{
            x = rd.nextInt(col);
            y = rd.nextInt(row);
        }
        while (board[x][y] != 0);
        return new Pair<>(x,y);
    }
}
