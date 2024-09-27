package com.example.gamejudgement.ai.old;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.example.gamejudgement.*;
import com.example.gamejudgement.ai.GoBangAIService;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * A decent AI but can be easily defeated by analyzing the pattern.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RuleBasedService extends GoBangAIService {

    private List<Predicate<TileCombo> > aiPredicates = Arrays.asList(
            c -> c.isTwoThirds(TileValue.O),
            c -> c.isTwoThirds(TileValue.X),
            c -> c.isOneThird(TileValue.O),
            c -> c.isOpen(),
            c -> c.getFirstEmpty() != null
    );

    @Override
    public void makeMove(Player player) {
        List<TileCombo> combos = FXGL.<GoBangApp>getAppCast().getCombos();

        Entity tile = aiPredicates.stream()
                .map(predicate -> {
                    return combos.stream()
                            .filter(predicate)
                            .findAny()
                            .map(TileCombo::getFirstEmpty)
                            .orElse(null);
                })
                .filter(t -> t != null)
                .findFirst()
                // should not happen
                .orElseThrow(() -> new IllegalStateException("No empty tiles"));

        tile.getComponent(TileViewComponent.class).mark(TileValue.O);
    }
}
