package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.CellTilePriority;
import com.hawolt.gotr.data.ChargeableCellType;
import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.minigame.impl.MinigameStateEvent;
import com.hawolt.gotr.pathfinding.PathCreator;
import com.hawolt.gotr.pathfinding.Pathfinder;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class HighlightCellTileOverlay extends AbstractMinigameRenderer {

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Getter(AccessLevel.NONE)
    private GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private final Set<GroundObject> availableCellTypes = new HashSet<>();

    @Getter(AccessLevel.NONE)
    private final Map<LocalPoint, Integer> distanceMapping = new HashMap<>();

    @Getter(AccessLevel.NONE)
    private final Object lock = new Object();

    @Getter(AccessLevel.NONE)
    private MinigameState minigameState;

    @Getter(AccessLevel.NONE)
    private WorldPoint worldPoint;

    @Inject
    public HighlightCellTileOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() != GameState.LOADING) return;
        this.availableCellTypes.clear();
        synchronized (lock) {
            this.distanceMapping.clear();
        }
    }

    @Subscribe
    public void onGroundObjectSpawned(GroundObjectSpawned event) {
        GroundObject groundObject = event.getGroundObject();
        if (!StaticConstant.MINIGAME_CELL_TILE_IDS.contains(groundObject.getId())) return;
        synchronized (lock) {
            this.availableCellTypes.removeIf(
                    cellTile -> cellTile.getWorldLocation().distanceTo(groundObject.getWorldLocation()) < 1
            );
            this.availableCellTypes.add(groundObject);
        }
    }

    @Subscribe
    public void onMinigameStateEvent(MinigameStateEvent event) {
        this.minigameState = event.getCurrentMinigameState();
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        WorldPoint current = plugin.getClient().getLocalPlayer().getWorldLocation();
        RenderSafetyEvent renderSafetyEvent = getRenderSafetyEvent();
        if (renderSafetyEvent == null || !renderSafetyEvent.isWidgetVisible()) return;
        if (worldPoint == null || !worldPoint.equals(current)) {
            this.updateDistanceMapping();
        }
        this.worldPoint = current;
    }

    private void updateDistanceMapping() {
        List<GroundObject> copy;
        synchronized (lock) {
            copy = new ArrayList<>(availableCellTypes);
        }
        for (GroundObject cell : copy) {
            LocalPoint localPoint = cell.getLocalLocation();
            Pathfinder pathfinder = new Pathfinder(plugin);
            Pair<List<WorldPoint>, Boolean> pair = PathCreator.pathTo(pathfinder, localPoint);
            List<WorldPoint> path = PathCreator.make(
                    plugin.getClient(),
                    pair.getLeft(),
                    plugin.getPathfinderSlice().getBlockedWorldAreaByNPC(),
                    true,
                    new LinkedList<>(),
                    new LinkedList<>(),
                    pair.getRight()
            );
            synchronized (lock) {
                this.distanceMapping.put(localPoint, path.size());
            }
        }
    }

    @Override
    public void renderWhenSecure(Graphics2D graphics2D) {
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        if (!config.isCellTilesOutlineEnabled()) return;
        ChargeableCellType type = this.plugin.getInventoryEssenceSlice().getAvailableChargeableCellType();
        if (type == null) return;
        CellTilePriority priority = this.plugin.getConfig().cellTilePriority();
        List<GroundObject> sorted = this.availableCellTypes.stream().sorted(
                (a, b) -> {
                    int distanceToA = distanceMapping.getOrDefault(a.getLocalLocation(), Integer.MAX_VALUE);
                    int distanceToB = distanceMapping.getOrDefault(b.getLocalLocation(), Integer.MAX_VALUE);
                    int distanceComparison = Integer.compare(distanceToA, distanceToB);
                    if (priority == CellTilePriority.CLOSEST) {
                        return distanceComparison;
                    } else {
                        int typeOfA = a.getId();
                        int typeOfB = b.getId();

                        int typeComparison = Integer.compare(typeOfA, typeOfB);
                        if (typeComparison != 0) return typeComparison;
                        return distanceComparison;
                    }
                }
        ).collect(Collectors.toList());
        if (sorted.isEmpty()) return;
        GroundObject closest = sorted.get(0);
        this.modelOutlineRenderer.drawOutline(
                closest,
                config.cellTilesOutlineWidth(),
                config.cellTilesOutlineColor(),
                config.cellTilesOutlineFeatherDistance()
        );
    }
}
