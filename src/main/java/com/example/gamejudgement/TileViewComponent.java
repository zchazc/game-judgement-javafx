package com.example.gamejudgement;

import com.almasb.fxgl.dsl.components.view.ChildViewComponent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileViewComponent extends ChildViewComponent {

    private TileValue value = TileValue.NONE;

    private Player player = null;

    private Arc arc = new Arc(34, 37, 34, 37, 0, 0);
    private Line line1 = new Line(0, 0, 0, 0);
    private Line line2 = new Line(75, 0, 75, 0);

    public TileViewComponent() {
        Rectangle bg = new Rectangle(GoBangApp.CELL_SIZE, GoBangApp.CELL_SIZE, Color.rgb(13, 222, 236));

        Rectangle bg2 = new Rectangle(GoBangApp.CELL_SIZE/3*2, GoBangApp.CELL_SIZE/3*2, Color.rgb(250, 250, 250, 0.25));
        bg2.setArcWidth(25);
        bg2.setArcHeight(25);

        arc.setFill(null);
        arc.setStroke(Color.BLACK);
        arc.setStrokeWidth(3);

        line1.setStrokeWidth(3);
        line2.setStrokeWidth(3);

        line1.setVisible(false);
        line2.setVisible(false);

        getViewRoot().getChildren().addAll(new StackPane(bg, bg2, arc, line1, line2));
    }

    public boolean isEmpty() {
        return player == null;
    }


    public TileValue getValue() {
        return value;
    }

    public Player getPlayer(){
        return player;
    }



    /**
     * @param value tile value
     * @return true if marking succeeded
     */
    public boolean mark(TileValue value) {
        if (this.value != TileValue.NONE)
            return false;

        this.value = value;

        animate(value);

        return true;
    }

    public boolean mark(Player player) {
        if (this.player != null)
            return false;

        this.player = player;

        animate(player.color);

        return true;
    }

    private void animate(Color color) {
        KeyFrame frame = new KeyFrame(Duration.seconds(0.5),
                new KeyValue(arc.lengthProperty(), 360));
        arc.setFill(Paint.valueOf(color.toString()));
        Timeline timeline = new Timeline(frame);
        timeline.play();
    }

    public void animate(TileValue value) {
        if (value == TileValue.O) {
            KeyFrame frame = new KeyFrame(Duration.seconds(0.5),
                    new KeyValue(arc.lengthProperty(), 360));

            Timeline timeline = new Timeline(frame);
            timeline.play();
        } else {

            line1.setVisible(true);
            line2.setVisible(true);

            KeyFrame frame1 = new KeyFrame(Duration.seconds(0.5),
                    new KeyValue(line1.endXProperty(), 75),
                    new KeyValue(line1.endYProperty(), 75));

            KeyFrame frame2 = new KeyFrame(Duration.seconds(0.5),
                    new KeyValue(line2.endXProperty(), 0),
                    new KeyValue(line2.endYProperty(), 75));

            Timeline timeline = new Timeline(frame1, frame2);
            timeline.play();
        }
    }
}
