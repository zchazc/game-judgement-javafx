package com.example.gamejudgement.ai;

import com.almasb.fxgl.core.EngineService;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.event.Subscriber;
import com.example.gamejudgement.GoBangApp;
import com.example.gamejudgement.Player;
import com.example.gamejudgement.TileViewComponent;
import com.example.gamejudgement.event.PlayerEvent;
import javafx.util.Pair;

import static com.example.gamejudgement.GoBangApp.COL_SIZE;
import static com.example.gamejudgement.GoBangApp.ROW_SIZE;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class GoBangAIService extends EngineService {

    private Subscriber eventListener;



    @Override
    public void onInit() {

        eventListener = FXGL.getEventBus().addEventHandler(PlayerEvent.PLAYER_WAITING, event -> {
            Entity[][] board = FXGL.<GoBangApp>getAppCast().getBoard();
            int[][] intBoard = new int[ROW_SIZE][COL_SIZE];

            for (int x = 0; x < COL_SIZE; x++) {
                for (int y = 0; y  < ROW_SIZE; y++) {
                    Player thatPlayer = board[x][y].getComponent(TileViewComponent.class).getPlayer();
                    intBoard[x][y] = thatPlayer == null ? 0 : thatPlayer.id;
                }
            }
            Pair<Integer, Integer> step = event.player.operator.makeMove(intBoard, COL_SIZE, ROW_SIZE, event.player.id);
            if(step != null
                   && 0 <= step.getKey() && step.getKey() < COL_SIZE
            && 0 <= step.getValue() && step.getValue() < ROW_SIZE){
                board[step.getKey()][step.getValue()].getComponent(TileViewComponent.class).mark(event.player);
            }
            System.out.println(event.player + " MOVING " + step);
            FXGL.getEventBus().fireEvent(new PlayerEvent(PlayerEvent.PLAYER_MOVED,event.player));
        });
    }

    @Override
    public void onExit() {
        eventListener.unsubscribe();
    }

    public abstract void makeMove(Player player);
}
