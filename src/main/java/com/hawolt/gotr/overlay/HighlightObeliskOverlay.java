package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.ObeliskType;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.ObeliskAnalysisEvent;
import com.hawolt.gotr.events.TickTimestampEvent;
import com.hawolt.gotr.events.minigame.impl.ObeliskTickRemainingEvent;
import com.hawolt.gotr.utility.ObeliskAnalysis;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class HighlightObeliskOverlay extends AbstractMinigameRenderer {

    @Inject
    private EventBus bus;
    @Inject
    private ItemManager itemManager;
    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Getter(AccessLevel.NONE)
    private final GuardianOfTheRiftOptimizerPlugin plugin;
    @Getter(AccessLevel.NONE)
    private final Map<ObeliskType, ObeliskAnalysis[]> map;
    @Getter(AccessLevel.NONE)
    private long lastTickTimestamp;
    @Getter(AccessLevel.NONE)
    private int ticksRemainingUntilUpdate, referenceClientTick;

    @Inject
    public HighlightObeliskOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.map = new HashMap<>();
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.setPosition(OverlayPosition.DYNAMIC);
    }

    @Subscribe
    public void onObeliskAnalysisEvent(ObeliskAnalysisEvent event) {
        this.map.put(event.getObeliskType(), event.getObeliskAnalysis());
    }

    @Subscribe
    public void onObeliskTickRemainingEvent(ObeliskTickRemainingEvent event) {
        this.ticksRemainingUntilUpdate = event.getRemainingTicksUntilUpdate();
        this.referenceClientTick = event.getClientTick();
    }

    @Subscribe
    public void onTickTimestampEvent(TickTimestampEvent event) {
        this.lastTickTimestamp = event.getTimestamp();
    }


    @Override
    public void renderWhenSecure(Graphics2D graphics2D) {
        if (!getRenderSafetyEvent().isWidgetAvailable()) return;
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        ObeliskAnalysis[] optimalObelisks = map.get(ObeliskType.OPTIMAL);
        ObeliskAnalysis[] secondaryObelisks = map.get(ObeliskType.SECONDARY);
        if (optimalObelisks == null || secondaryObelisks == null) return;
        for (ObeliskAnalysis optimal : optimalObelisks) {
            this.handleGuardianOutlineRender(
                    optimal,
                    config.isOptimalGuardianOutlineEnabled(),
                    config.optimalGuardianOutlineWidth(),
                    config.optimalGuardianOutlineColor(),
                    config.optimalGuardianOutlineFeatherDistance()
            );
            this.handleGuardianSpriteRender(
                    graphics2D,
                    optimal,
                    config.isOptimalGuardianSpriteEnabled()
            );
            this.handleGuardianTimerRender(
                    graphics2D,
                    optimal,
                    config.isOptimalGuardianTimerEnabled()
            );
            this.handleGuardianRunTimeRender(
                    graphics2D,
                    optimal,
                    config.isOptimalGuardianRunTime()
            );
            this.handleGuardianWeightRender(
                    graphics2D,
                    optimal,
                    config.enableWeightDebugging()
            );
        }
        for (ObeliskAnalysis secondary : secondaryObelisks) {
            this.handleGuardianOutlineRender(
                    secondary,
                    config.isSecondaryGuardianOutlineEnabled(),
                    config.secondaryGuardianOutlineWidth(),
                    config.secondaryGuardianOutlineColor(),
                    config.secondaryGuardianOutlineFeatherDistance()
            );
            this.handleGuardianSpriteRender(
                    graphics2D,
                    secondary,
                    config.isSecondaryGuardianSpriteEnabled()
            );
            this.handleGuardianTimerRender(
                    graphics2D,
                    secondary,
                    config.isSecondaryGuardianTimerEnabled()
            );
            this.handleGuardianRunTimeRender(
                    graphics2D,
                    secondary,
                    config.isSecondaryGuardianRunTime()
            );
            this.handleGuardianWeightRender(
                    graphics2D,
                    secondary,
                    config.enableWeightDebugging()
            );
        }
    }

    private void handleGuardianOutlineRender(
            ObeliskAnalysis analysis,
            boolean isEnabled,
            int outlineWidth,
            Color outlineColor,
            int feather
    ) {
        if (!isEnabled) return;
        if (analysis == null) return;
        GameObject object = analysis.getGameObject();
        if (object == null) return;
        try {
            Shape hull = object.getConvexHull();
            if (hull == null) return;
            this.modelOutlineRenderer.drawOutline(
                    object,
                    outlineWidth,
                    outlineColor,
                    feather
            );
        } catch (NullPointerException e) {
            // ignore
        }
    }

    private void handleGuardianTimerRender(
            Graphics2D graphics2D,
            ObeliskAnalysis analysis,
            boolean isEnabled
    ) {
        if (!isEnabled) return;
        if (analysis == null) return;
        GameObject object = analysis.getGameObject();
        if (object == null) return;
        if (analysis.isTalismanAvailable()) return;

        int ticksSinceEvent = plugin.getClient().getTickCount() - referenceClientTick;
        int ticksLeftToReach = (analysis.getNormalizedTileDistance() >> 1) + 1;
        int ticksLeftUntilUpdate = ticksRemainingUntilUpdate - ticksSinceEvent;
        int ticksAvailable = ticksLeftUntilUpdate - ticksLeftToReach;
        long elapsedSinceLastTick = System.currentTimeMillis() - lastTickTimestamp;
        long remaining = (ticksLeftUntilUpdate * StaticConstant.GAME_TICK_DURATION) - elapsedSinceLastTick;

        if (remaining < 0) return;

        String formatted = formatRemainingTime(remaining);
        Point canvasTextLocation = Perspective.getCanvasTextLocation(
                plugin.getClient(),
                graphics2D,
                object.getLocalLocation(),
                formatted,
                565
        );

        if (canvasTextLocation == null) return;

        Color color;
        if (ticksAvailable > 4) {
            color = Color.WHITE;
        } else if (ticksAvailable > 0) {
            color = Color.ORANGE;
        } else {
            color = Color.RED;
        }

        OverlayUtil.renderTextLocation(graphics2D, canvasTextLocation, formatted, color);
    }

    private void handleGuardianRunTimeRender(
            Graphics2D graphics2D,
            ObeliskAnalysis analysis,
            boolean isEnabled
    ) {
        if (!isEnabled) return;
        if (analysis == null) return;
        GameObject object = analysis.getGameObject();
        if (object == null) return;

        long timeToRun = (analysis.getNormalizedTileDistance() >> 1)
                * StaticConstant.GAME_TICK_DURATION
                + StaticConstant.GAME_TICK_DURATION;

        String formatted = formatRemainingTime(timeToRun);
        Point canvasTextLocation = Perspective.getCanvasTextLocation(
                plugin.getClient(),
                graphics2D,
                object.getLocalLocation(),
                formatted,
                0
        );

        if (canvasTextLocation == null) return;
        OverlayUtil.renderTextLocation(graphics2D, canvasTextLocation, formatted, Color.WHITE);
    }

    private void handleGuardianWeightRender(
            Graphics2D graphics2D,
            ObeliskAnalysis analysis,
            boolean isEnabled
    ) {
        if (!isEnabled) return;
        if (analysis == null) return;
        GameObject object = analysis.getGameObject();
        if (object == null) return;

        String efficiency = String.format("%.2f", analysis.getWeightedEfficiency());
        Point canvasTextLocation = Perspective.getCanvasTextLocation(
                plugin.getClient(),
                graphics2D,
                object.getLocalLocation(),
                efficiency,
                200
        );

        if (canvasTextLocation == null) return;
        OverlayUtil.renderTextLocation(graphics2D, canvasTextLocation, efficiency, Color.WHITE);
    }

    private void handleGuardianSpriteRender(
            Graphics2D graphics2D,
            ObeliskAnalysis analysis,
            boolean isEnabled
    ) {
        if (!isEnabled) return;
        if (analysis == null) return;
        GameObject object = analysis.getGameObject();
        if (object == null) return;
        BufferedImage sprite = !analysis.isDowngradeBetter() ?
                itemManager.getImage(analysis.getRuneCraftInfo().getSpriteId()) :
                itemManager.getImage(analysis.getRuneCraftInfo().getBaseRuneCraftInfo().getSpriteId());
        OverlayUtil.renderImageLocation(
                plugin.getClient(),
                graphics2D,
                object.getLocalLocation(),
                sprite,
                505
        );
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

}
