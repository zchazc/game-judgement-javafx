package com.example.gamejudgement;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.example.gamejudgement.ai.RandomService;
import com.example.gamejudgement.event.AIEvent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getDialogService;

public class GOBangApp extends GameApplication {

    public static final int ROW_SIZE = 9;
    public static final int COL_SIZE = 9;
    public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 600;
    public static final int EDGE_SIZE = Math.min(APP_HEIGHT,APP_WIDTH);
    public static final double CELL_SIZE = ((double) EDGE_SIZE )/Math.max(ROW_SIZE,COL_SIZE);
    public static final double BOARD_WIDTH = CELL_SIZE*COL_SIZE;
    public static final double BOARD_HEIGHT = CELL_SIZE*ROW_SIZE;
    public static final int WIN_COMBOS_SIZE = 4;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("GoBang");
        settings.setVersion("1.0");
        settings.setWidth(APP_WIDTH);
        settings.setHeight(APP_HEIGHT);
        settings.addEngineService(RandomService.class);
    }

    private Entity[][] board = new Entity[ROW_SIZE][COL_SIZE];
    private List<TileCombo> combos = new ArrayList<>();

    private boolean playerStarts = true;

    public Entity[][] getBoard() {
        return board;
    }

    public List<TileCombo> getCombos() {
        return combos;
    }

    @Override
    protected void onPreInit() {
        getEventBus().addEventHandler(AIEvent.MOVED, event -> checkGameFinished());
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GoBangFactory());
        for (int y = 0; y < ROW_SIZE; y++) {
            for (int x = 0; x < COL_SIZE; x++) {
                board[x][y] = spawn("tile", x * CELL_SIZE, y * CELL_SIZE);
            }
        }

        combos.clear();

        // horizontal
        for (int y = 0; y < ROW_SIZE; y++) {
            for (int x = 0; x + WIN_COMBOS_SIZE <= COL_SIZE; x++) {
                Entity[] combo = new Entity[WIN_COMBOS_SIZE];
                for (int i = 0; i < WIN_COMBOS_SIZE; i++) {
                    combo[i] = board[x+i][y];
                }

                combos.add(new TileCombo(combo));
            }
        }

        // vertical
        for (int x = 0; x < COL_SIZE; x++) {
            for (int y = 0; y + WIN_COMBOS_SIZE <= ROW_SIZE; y++) {
                Entity[] combo = new Entity[WIN_COMBOS_SIZE];
                for (int i = 0; i < WIN_COMBOS_SIZE; i++) {
                    combo[i] = board[x][y+i];
                }
                combos.add(new TileCombo(combo));
            }
        }

        // diagonals
        for (int x = 0; x < COL_SIZE; x++) {
            for (int y = 0; y  < ROW_SIZE; y++) {

                if(x + WIN_COMBOS_SIZE <= COL_SIZE && y + WIN_COMBOS_SIZE <= ROW_SIZE){
                    Entity[] combo = new Entity[WIN_COMBOS_SIZE];
                    for (int i = 0; i < WIN_COMBOS_SIZE; i++) {
                        combo[i] = board[x+i][y+i];
                    }
                    combos.add(new TileCombo(combo));
                }
                if(x - WIN_COMBOS_SIZE + 1 >= 0 && y - WIN_COMBOS_SIZE + 1>= 0){
                    Entity[] combo = new Entity[WIN_COMBOS_SIZE];
                    for (int i = 0; i < WIN_COMBOS_SIZE; i++) {
                        combo[i] = board[x-i][y-i];
                    }
                    combos.add(new TileCombo(combo));
                }
            }
        }

        if (playerStarts) {
            playerStarts = false;
        } else {
            aiMove();
            playerStarts = true;
        }
    }

    @Override
    protected void initUI() {

        getGameScene().addUINodes(
                IntStream.rangeClosed(0,ROW_SIZE)
                        .mapToObj(idx->new Line(0,CELL_SIZE * idx, BOARD_WIDTH, CELL_SIZE* idx))
                        .toArray(Line[]::new)
                );
        getGameScene().addUINodes(
                IntStream.rangeClosed(0,COL_SIZE)
                        .mapToObj(idx->new Line(CELL_SIZE * idx, 0, CELL_SIZE * idx, BOARD_HEIGHT))
                        .toArray(Line[]::new)
        );
//        Line line1 = new Line(getAppWidth() / 3, 0, getAppWidth() / 3, 0);
//        Line line2 = new Line(getAppWidth() / 3 * 2, 0, getAppWidth() / 3 * 2, 0);
//        Line line3 = new Line(0, getAppHeight() / 3, 0, getAppHeight() / 3);
//        Line line4 = new Line(0, getAppHeight() / 3 * 2, 0, getAppHeight() / 3 * 2);
//
//        getGameScene().addUINodes(line1, line2, line3, line4);
//
//        // animation
//        KeyFrame frame1 = new KeyFrame(Duration.seconds(0.5),
//                new KeyValue(line1.endYProperty(), getAppHeight()));
//
//        KeyFrame frame2 = new KeyFrame(Duration.seconds(1),
//                new KeyValue(line2.endYProperty(), getAppHeight()));
//
//        KeyFrame frame3 = new KeyFrame(Duration.seconds(0.5),
//                new KeyValue(line3.endXProperty(), getAppWidth()));
//
//        KeyFrame frame4 = new KeyFrame(Duration.seconds(1),
//                new KeyValue(line4.endXProperty(), getAppWidth()));
//
//        Timeline timeline = new Timeline(frame1, frame2, frame3, frame4);
//        timeline.play();
    }

    private boolean checkGameFinished() {
        for (TileCombo combo : combos) {
            if (combo.isComplete()) {
                playWinAnimation(combo);
                return true;
            }
        }

        for (int y = 0; y < ROW_SIZE; y++) {
            for (int x = 0; x < COL_SIZE; x++) {
                Entity tile = board[x][y];
                if (tile.getComponent(TileViewComponent.class).isEmpty()) {
                    // at least 1 tile is empty
                    return false;
                }
            }
        }

        gameOver("平局");
        return true;
    }

    private void playWinAnimation(TileCombo combo) {
        Line line = new Line();
        line.setStartX(combo.getFirstTile().getCenter().getX());
        line.setStartY(combo.getFirstTile().getCenter().getY());
        line.setEndX(combo.getFirstTile().getCenter().getX());
        line.setEndY(combo.getFirstTile().getCenter().getY());
        line.setStroke(Color.YELLOW);
        line.setStrokeWidth(3);

        getGameScene().addUINode(line);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
                new KeyValue(line.endXProperty(), combo.getLastTile().getCenter().getX()),
                new KeyValue(line.endYProperty(), combo.getLastTile().getCenter().getY())));
        timeline.setOnFinished(e -> gameOver(combo.getWinSymbol()));
        timeline.play();
    }

    private void gameOver(String winner) {
        getDialogService().showConfirmationBox("胜者: " + winner + "\n继续?", yes -> {
            if (yes)
                getGameController().startNewGame();
            else
                getGameController().exit();
        });
    }

    public void onUserMove(Entity tile) {
        boolean ok = tile.getComponent(TileViewComponent.class).mark(TileValue.X);

        if (ok) {
            boolean over = checkGameFinished();

            if (!over) {
                aiMove();
            }
        }
    }

    private void aiMove() {
        AIEvent aiEvent = new AIEvent(AIEvent.WAITING);
        getEventBus().fireEvent(aiEvent);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
