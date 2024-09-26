package com.example.gamejudgement;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.input.MouseEvent;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GoBangFactory implements EntityFactory {

    @Spawns("tile")
    public Entity newTile(SpawnData data) {
        var tile = entityBuilder(data)
                .bbox(new HitBox(BoundingShape.box(GOBangApp.CELL_SIZE, GOBangApp.CELL_SIZE)))
                .with(new TileViewComponent())
                .build();

        tile.getViewComponent().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> FXGL.<GOBangApp>getAppCast().onUserMove(tile));

        return tile;
    }
}
