package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.TickTimestampEvent;
import com.hawolt.gotr.events.minigame.impl.PortalSpawnEvent;
import com.hawolt.gotr.pathfinding.PathCreator;
import com.hawolt.gotr.pathfinding.Pathfinder;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class HighlightPortalOverlay extends AbstractMinigameRenderer {
    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Getter(AccessLevel.NONE)
    private GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private int portalSpawnedOnTick, portalTicksRemaining;

    @Getter(AccessLevel.NONE)
    private List<WorldPoint> pathToPortal;

    @Getter(AccessLevel.NONE)
    private int normalizedTileDistance;

    @Getter(AccessLevel.NONE)
    private long lastTickTimestamp;

    @Getter(AccessLevel.NONE)
    private WorldPoint worldPoint;

    @Getter(AccessLevel.NONE)
    private GameObject portal;

    @Inject
    public HighlightPortalOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public boolean isClientThreadRequiredOnShutDown() {
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        this.resetPortal();
    }

    private void resetPortal() {
        this.portal = null;
        this.plugin.getClient().clearHintArrow();
    }

    @Subscribe
    public void onGameState(GameState gameState) {
        if (gameState != GameState.LOGGING_IN && gameState != GameState.HOPPING) return;
        this.resetPortal();
    }

    @Subscribe
    public void onTickTimestampEvent(TickTimestampEvent event) {
        this.lastTickTimestamp = event.getTimestamp();
    }

    @Subscribe
    public void onChatMessage(ChatMessage message) {
        if (message.getType() != ChatMessageType.SPAM && message.getType() != ChatMessageType.GAMEMESSAGE) return;
        String content = message.getMessage();
        if (!content.contains("You step through the portal")) return;
        this.plugin.getClient().clearHintArrow();
    }

    private void updatePathToPortal() {
        Pathfinder pathfinder = new Pathfinder(plugin);
        Pair<List<WorldPoint>, Boolean> pathPair = PathCreator.pathTo(pathfinder, portal);
        this.pathToPortal = PathCreator.make(
                plugin.getClient(),
                pathPair.getLeft(),
                plugin.getPathfinderSlice().getBlockedWorldAreaByNPC(),
                true,
                new LinkedList<>(),
                new LinkedList<>(),
                pathPair.getRight()
        );
        this.normalizedTileDistance = normalizeTileCount(pathToPortal.size());
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        WorldPoint current = plugin.getClient().getLocalPlayer().getWorldLocation();
        RenderSafetyEvent renderSafetyEvent = getRenderSafetyEvent();
        if (renderSafetyEvent == null) return;
        if (!renderSafetyEvent.isWidgetAvailable()) return;
        if (worldPoint == null || !worldPoint.equals(current)) {
            this.updatePathToPortal();
        }
        this.worldPoint = current;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        if (gameObject.getId() != StaticConstant.MINIGAME_PORTAL_OBJECT_ID) return;
        this.plugin.getClient().setHintArrow(gameObject.getWorldLocation());
        this.portal = gameObject;
        this.updatePathToPortal();
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject gameObject = event.getGameObject();
        if (gameObject.getId() != StaticConstant.MINIGAME_PORTAL_OBJECT_ID) return;
        this.resetPortal();
    }

    @Subscribe
    public void onPortalSpawnEvent(PortalSpawnEvent event) {
        // for some reason the portal always lasts a bit longer than specified via client script
        // it also vanishes from HUD whilst still accessible so we add a 1 tick buffer time
        this.portalTicksRemaining = event.getTicksUntilDespawn() + 1;
        this.portalSpawnedOnTick = event.getClientTick();
    }

    @Override
    public void renderWhenSecure(Graphics2D graphics2D) {
        if (portal == null) return;
        if (!getRenderSafetyEvent().isWidgetAvailable()) return;
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        this.renderPortalOutline(config);

        int ticksSinceEvent = plugin.getClient().getTickCount() - portalSpawnedOnTick;
        int ticksLeftToReach = (normalizedTileDistance >> 1) + 1;
        int ticksLeftUntilUpdate = portalTicksRemaining - ticksSinceEvent;
        int ticksAvailable = ticksLeftUntilUpdate - ticksLeftToReach;
        long elapsedSinceLastTick = System.currentTimeMillis() - lastTickTimestamp;
        long remaining = (ticksLeftUntilUpdate * StaticConstant.GAME_TICK_DURATION) - elapsedSinceLastTick;

        this.renderPortalTime(config, graphics2D, ticksAvailable, remaining);
        this.renderPortalRunTime(config, graphics2D, remaining);
    }

    private void renderPortalOutline(GuardianOfTheRiftOptimizerConfig config) {
        if (!config.isPortalOutlineEnabled()) return;
        try {
            this.modelOutlineRenderer.drawOutline(
                    portal,
                    config.portalOutlineWidth(),
                    config.portalOutlineColor(),
                    config.portalOutlineFeatherDistance()
            );
        } catch (Exception e) {

        }
    }

    private void renderPortalTime(GuardianOfTheRiftOptimizerConfig config, Graphics2D graphics2D, int ticksAvailable, long remaining) {
        if (!config.isShowTimeSinceLastPortal()) return;

        if (remaining < 0) return;

        String formatted = formatRemainingTime(remaining);
        Point canvasTextLocation = Perspective.getCanvasTextLocation(
                plugin.getClient(),
                graphics2D,
                portal.getLocalLocation(),
                formatted,
                200
        );

        Color color;
        if (ticksAvailable > 3) {
            color = Color.WHITE;
        } else if (ticksAvailable >= 0) {
            color = Color.ORANGE;
        } else {
            color = Color.RED;
        }

        if (canvasTextLocation == null) return;
        OverlayUtil.renderTextLocation(graphics2D, canvasTextLocation, formatted, color);
    }

    private void renderPortalRunTime(GuardianOfTheRiftOptimizerConfig config, Graphics2D graphics2D, long remaining) {
        if (!config.isPortalRunTimeEnabled()) return;

        if (remaining < 0) return;

        long timeToRun = (normalizedTileDistance >> 1)
                * StaticConstant.GAME_TICK_DURATION
                + StaticConstant.GAME_TICK_DURATION;

        String formatted = formatRemainingTime(timeToRun);
        Point canvasTextLocation = Perspective.getCanvasTextLocation(
                plugin.getClient(),
                graphics2D,
                portal.getLocalLocation(),
                formatted,
                0
        );

        if (canvasTextLocation == null) return;
        OverlayUtil.renderTextLocation(graphics2D, canvasTextLocation, formatted, Color.WHITE);
    }

    private String formatRemainingTime(long remaining) {
        remaining = (long) (Math.ceil(remaining / 100.0) * 100);
        long seconds = remaining / 1000;
        long remainingMillis = remaining % 1000;
        long tenths = (long) Math.ceil(remainingMillis / 100.0);
        if (tenths == 10) {
            seconds++;
            tenths = 0;
        }
        return String.format("%d.%d", seconds, tenths);
    }

    private static int normalizeTileCount(int amount) {
        return (amount % 2 == 0) ? amount : amount + 1;
    }
}
