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
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
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
    public void shutdown() {
        super.shutdown();
        this.portal = null;
        this.plugin.getClient().clearHintArrow();
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
        this.plugin.getClient().clearHintArrow();
        this.portal = null;
    }

    @Subscribe
    public void onPortalSpawnEvent(PortalSpawnEvent event) {
        this.portalTicksRemaining = event.getTicksUntilDespawn();
        this.portalSpawnedOnTick = event.getClientTick();
    }

    @Override
    public void renderWhenSecure(Graphics2D graphics2D) {
        if (portal == null) return;
        if (!getRenderSafetyEvent().isWidgetAvailable()) return;
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        this.renderPortalOutline(config);
        this.renderPortalTime(config, graphics2D);
        this.renderPortalRunTime(config, graphics2D);
    }

    private void renderPortalOutline(GuardianOfTheRiftOptimizerConfig config) {
        if (!config.isPortalOutlineEnabled()) return;
        this.modelOutlineRenderer.drawOutline(
                portal,
                config.portalOutlineWidth(),
                config.portalOutlineColor(),
                config.portalOutlineFeatherDistance()
        );
    }

    private void renderPortalTime(GuardianOfTheRiftOptimizerConfig config, Graphics2D graphics2D) {
        if (!config.isShowTimeSinceLastPortal()) return;

        int ticksSinceEvent = plugin.getClient().getTickCount() - portalSpawnedOnTick;
        int ticksLeftUntilUpdate = portalTicksRemaining - ticksSinceEvent;
        long elapsedSinceLastTick = System.currentTimeMillis() - lastTickTimestamp;
        long remaining = (ticksLeftUntilUpdate * StaticConstant.GAME_TICK_DURATION) - elapsedSinceLastTick;

        if (remaining < 0) return;

        String formatted = formatRemainingTime(remaining);
        Point canvasTextLocation = Perspective.getCanvasTextLocation(
                plugin.getClient(),
                graphics2D,
                portal.getLocalLocation(),
                formatted,
                200
        );

        if (canvasTextLocation == null) return;
        OverlayUtil.renderTextLocation(graphics2D, canvasTextLocation, formatted, Color.WHITE);
    }

    private void renderPortalRunTime(GuardianOfTheRiftOptimizerConfig config, Graphics2D graphics2D) {
        if (!config.isPortalRunTimeEnabled()) return;

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
