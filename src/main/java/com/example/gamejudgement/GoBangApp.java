package com.example.gamejudgement;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.example.gamejudgement.ai.old.DefaultService;
import com.example.gamejudgement.event.PlayerEvent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;

public class GoBangApp extends GameApplication {

    //设置行
    public static final int ROW_SIZE = 9;
    //设置列
    public static final int COL_SIZE = 9;
    public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 600;
    public static final int EDGE_SIZE = Math.min(APP_HEIGHT,APP_WIDTH);
    public static final double CELL_SIZE = ((double) EDGE_SIZE )/Math.max(ROW_SIZE,COL_SIZE);
    public static final double BOARD_WIDTH = CELL_SIZE*COL_SIZE;
    public static final double BOARD_HEIGHT = CELL_SIZE*ROW_SIZE;
    //设置多少连子算获胜
    public static final int WIN_COMBOS_SIZE = 4;

    //设置玩家数
    public static final int PLAYER_NUM = 2;

    private int currentPlayer;

    private int startPlayer = 0;
    private Player[] players = new Player[PLAYER_NUM];


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("GoBang");
        settings.setVersion("1.0");
        settings.setWidth(APP_WIDTH);
        settings.setHeight(APP_HEIGHT);
        settings.addEngineService(DefaultService.class);
    }

    private Entity[][] board = new Entity[ROW_SIZE][COL_SIZE];
    private List<TileCombo> combos = new ArrayList<>();

    private boolean isStart = false;

    private boolean canMove = true;

    public Entity[][] getBoard() {
        return board;
    }

    public List<TileCombo> getCombos() {
        return combos;
    }

    @Override
    protected void onPreInit() {
        getEventBus().addEventHandler(PlayerEvent.PLAYER_MOVED, event -> {
            boolean over = checkGameFinished();
            if(!over){
                canMove = true;
            }
        });
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GoBangFactory());
        Player.resetSeq();
        for (int y = 0; y < ROW_SIZE; y++) {
            for (int x = 0; x < COL_SIZE; x++) {
                board[x][y] = spawn("tile", x * CELL_SIZE, y * CELL_SIZE);
            }
        }

        for (int i = 0; i < PLAYER_NUM; i++) {
            //可以单独设置AI
            players[i] = new Player();
        }
        isStart = false;
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

        getInput().addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            if(!isStart){
                canMove = true;
                currentPlayer = startPlayer++ - 1;
                isStart = true;
            }
            if(canMove){
                nextPlayerMove();
            }
        });


    }


    public void nextPlayerMove(){
        playerMove(players[++currentPlayer % PLAYER_NUM]);
    }
    public void playerMove(Player player){
        canMove = false;
        getEventBus().fireEvent(new PlayerEvent(PlayerEvent.PLAYER_WAITING,player));
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

        draw();
        return true;
    }

    private void draw() {

        getDialogService().showConfirmationBox("平局\n继续?", yes -> {
            if (yes)
                getGameController().startNewGame();
            else
                getGameController().exit();
        });
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
        timeline.setOnFinished(e -> gameOver(combo.getWinner()));
        timeline.play();
    }

    private void gameOver(Player winner) {

        getDialogService().showConfirmationBox("胜者: " + winner.name + "\n继续?", yes -> {
            if (yes)
                getGameController().startNewGame();
            else
                getGameController().exit();
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
