package com.example.gamejudgement.ai;

import com.almasb.fxgl.dsl.FXGL;
import com.example.gamejudgement.GOBangApp;
import com.example.gamejudgement.TileCombo;
import com.example.gamejudgement.TileValue;
import com.example.gamejudgement.TileViewComponent;

import java.util.List;

public class RandomService extends TicTacToeAIService {


    @Override
    public void makeMove() {
        List<TileCombo> combos = FXGL.<GOBangApp>getAppCast().getCombos();

        combos.stream().filter(t->t.getFirstEmpty()!=null).findAny().get().getFirstEmpty().getComponent(TileViewComponent.class).mark(TileValue.O);
    }
}
