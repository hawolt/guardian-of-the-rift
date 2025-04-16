package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.ChargeableCellType;
import com.hawolt.gotr.data.StaticConstant;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GroundObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HighlightCellTileOverlay extends AbstractMinigameRenderer {

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Getter(AccessLevel.NONE)
    private GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private final Set<GroundObject> availableCellTypes = new HashSet<>();

    @Inject
    public HighlightCellTileOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        var groundObject = event.getGroundObject();
        if (!StaticConstant.MINIGAME_CELL_TILE_IDS.contains(groundObject.getId())) return;
        this.availableCellTypes.removeIf(
                cellTile -> cellTile.getWorldLocation().distanceTo(groundObject.getWorldLocation()) < 1
        );
        this.availableCellTypes.add(groundObject);
    }

    @Override
    public void renderWhenSecure(Graphics2D graphics2D) {
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        if (!config.isCellTilesOutlineEnabled()) return;
        ChargeableCellType type = this.plugin.getInventoryEssenceSlice().getAvailableChargeableCellType();
        if (type == null) return;
        WorldPoint currentPlayerLocation = plugin.getClient().getLocalPlayer().getWorldLocation();
        List<GroundObject> sortedNearestToFurthest = this.availableCellTypes.stream().sorted(
                Comparator.comparingInt(a -> a.getWorldLocation().distanceTo(currentPlayerLocation))
        ).collect(Collectors.toList());
        if (sortedNearestToFurthest.isEmpty()) return;
        GroundObject closest = sortedNearestToFurthest.get(0);
        if (closest == null) return;
        this.modelOutlineRenderer.drawOutline(
                closest,
                config.cellTilesOutlineWidth(),
                config.cellTilesOutlineColor(),
                config.cellTilesOutlineFeatherDistance()
        );
    }
}
