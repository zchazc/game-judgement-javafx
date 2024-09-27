package com.example.gamejudgement.ai;

import javafx.util.Pair;

public interface GoBangPlayer {
    Pair<Integer,Integer> makeMove(int[][] board, int col, int row, int playId);
}
