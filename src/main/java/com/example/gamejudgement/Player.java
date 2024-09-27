package com.example.gamejudgement;

import com.example.gamejudgement.ai.FirstEmptyPlayer;
import com.example.gamejudgement.ai.GoBangPlayer;
import com.example.gamejudgement.ai.RandomPlayer;
import javafx.scene.paint.Color;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Player {
    private final static AtomicInteger SEQ_GENERATOR = new AtomicInteger(1);

    private final static Map<String,Color> NAMED_COLORS = Arrays.stream(Color.class.getDeclaredFields())
            .filter(field -> field.getType().equals(Color.class))
            .collect(Collectors.toMap(Field::getName,field -> {
                try {
                    return (Color) field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }));

    private final static List<String> ORDERED_COLOR = new ArrayList<>(NAMED_COLORS.keySet());



    public final GoBangPlayer operator;

    public final int id;

    final String name;
    final Color color;

    public Player(Color color) {
        this.color = color;
        this.id = SEQ_GENERATOR.getAndIncrement();
        this.name = "PLAYER-"+id;
        this.operator = id % 2 == 0 ? RandomPlayer.DEFAULT: FirstEmptyPlayer.DEFAULT;
    }

    public Player() {
        this.id = SEQ_GENERATOR.getAndIncrement();
        this.color = NAMED_COLORS.get(ORDERED_COLOR.get(id % ORDERED_COLOR.size()));
        this.name = "PLAYER-"+id;
        this.operator = id % 2 == 0 ? RandomPlayer.DEFAULT: FirstEmptyPlayer.DEFAULT;
    }

    public Player(GoBangPlayer operator) {
        this.id = SEQ_GENERATOR.getAndIncrement();
        this.color = NAMED_COLORS.get(ORDERED_COLOR.get(id % ORDERED_COLOR.size()));
        this.name = "PLAYER-"+id;
        this.operator = operator;
    }

    public static void resetSeq(){
        SEQ_GENERATOR.set(1);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                '}';
    }

    public static void main(String[] args) {


    }

}
