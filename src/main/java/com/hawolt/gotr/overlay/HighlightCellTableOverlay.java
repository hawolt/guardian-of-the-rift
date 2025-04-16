package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.StaticConstant;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class HighlightCellTableOverlay extends AbstractMinigameRenderer {

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Getter(AccessLevel.NONE)
    private GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private GameObject unchargedCellTable;

    @Inject
    public HighlightCellTableOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        if (gameObject.getId() != StaticConstant.MINIGAME_UNCHARGED_CELL_TABLE_OBJECT_ID) return;
        this.unchargedCellTable = gameObject;
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject gameObject = event.getGameObject();
        if (gameObject.getId() != StaticConstant.MINIGAME_UNCHARGED_CELL_TABLE_OBJECT_ID) return;
        this.unchargedCellTable = null;
    }

    @Override
    public void renderWhenSecure(Graphics2D graphics2D) {
        if (unchargedCellTable == null) return;
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        if (!config.isCellTableOutlineEnabled()) return;
        int availableUnchargedCells = this.plugin.getInventoryEssenceSlice().getAvailableUnchargedCells();
        if (availableUnchargedCells > 5) return;
        this.modelOutlineRenderer.drawOutline(
                unchargedCellTable,
                config.cellTableOutlineWidth(),
                config.cellTableOutlineColor(),
                config.cellTableOutlineFeatherDistance()
        );
    }
}
