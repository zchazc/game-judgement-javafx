package com.example.gamejudgement;

import com.almasb.fxgl.entity.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileCombo {

    private List<Entity> tiles;


    public TileCombo(Entity ...tiles){
        this.tiles = Arrays.asList(tiles);
    }

    public Entity getFirstTile() {
        return tiles.get(0);
    }

    public Entity getLastTile() {
        return tiles.get(tiles.size()-1);
    }

    public boolean isComplete() {
        List<TileValue> distinctTiles = tiles.stream().map(this::getValueOf)
                .distinct()
                .collect(Collectors.toList());
        return distinctTiles.size() == 1 && distinctTiles.get(0) != TileValue.NONE;
    }

    /**
     * @return true if all tiles are empty
     */
    public boolean isOpen() {
        return tiles.stream()
                .allMatch(this::isEmpty);
    }

    /**
     * @param value tile value
     * @return true if this combo has 2 of value and an empty slot
     */
    public boolean isTwoThirds(TileValue value) {
        TileValue oppositeValue = value == TileValue.X ? TileValue.O : TileValue.X;

        if (tiles.stream().anyMatch(t -> getValueOf(t) == oppositeValue))
            return false;

        return tiles.stream()
                .filter(this::isEmpty)
                .count() == 1;
    }

    /**
     * @param value tile value
     * @return true if this combo has 1 of value and 2 empty slots
     */
    public boolean isOneThird(TileValue value) {
        TileValue oppositeValue = value == TileValue.X ? TileValue.O : TileValue.X;

        if (tiles.stream().anyMatch(t -> getValueOf(t) == oppositeValue))
            return false;

        return tiles.stream()
                .filter(this::isEmpty)
                .count() == 2;
    }

    /**
     * @return first empty tile or null if no empty tiles
     */
    public Entity getFirstEmpty() {
        return tiles.stream()
                .filter(this::isEmpty)
                .findAny()
                .orElse(null);
    }

    private TileValue getValueOf(Entity tile) {
        return tile.getComponent(TileViewComponent.class).getValue();
    }

    private boolean isEmpty(Entity tile) {
        return tile.getComponent(TileViewComponent.class).isEmpty();
    }

    public String getWinSymbol() {
        return getValueOf(tiles.get(0)).symbol;
    }
}
