package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.data.PaintLocation;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.minigame.impl.MinigameStateEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PortalIndicatorMimicOverlay extends AbstractMinigameRenderer {

    @Getter(AccessLevel.NONE)
    private final int SPRITE_DIMENSION_HEIGHT = 32;

    @Getter(AccessLevel.NONE)
    private final int SPRITE_DIMENSION_WIDTH = 32;

    @Getter(AccessLevel.NONE)
    private GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private long lastTickTimestamp, lastPortalDespawnTimestamp, lastInternalUpdateTimestamp;

    @Getter(AccessLevel.NONE)
    private boolean isFirstPortalThisRound;

    @Getter(AccessLevel.NONE)
    private MinigameState minigameState = MinigameState.UNKNOWN;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    public PortalIndicatorMimicOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Subscribe
    public void onMinigameStateEvent(MinigameStateEvent event) {
        this.minigameState = event.getCurrentMinigameState();
    }

    @Subscribe
    public void onGameState(GameState event) {
        if (event != GameState.LOGGED_IN) return;
        this.lastPortalDespawnTimestamp = 0;
    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired event) {
        if (event.getScriptId() != StaticConstant.MINIGAME_HUD_UPDATE_SCRIPT_ID) return;
        Object[] arguments = event.getScriptEvent().getArguments();
        int portalTicksRemaining = Integer.parseInt(arguments[11].toString());
        int elementalRuneEnumIndex = Integer.parseInt(arguments[6].toString());
        int catalyticRuneEnumIndex = Integer.parseInt(arguments[7].toString());
        boolean isInStartup = elementalRuneEnumIndex == 0 && catalyticRuneEnumIndex == 0;
        boolean isUnknownPortal = portalTicksRemaining < 0xFFFFFF00;
        this.isFirstPortalThisRound = isUnknownPortal || isInStartup;
        int absoluteTickCount;
        if (isInStartup && portalTicksRemaining == -1) {
            int guardianTicksRemaining = Integer.parseInt(arguments[10].toString());
            absoluteTickCount = Math.abs(guardianTicksRemaining - 200);
            long elapsedSinceLastPortal = absoluteTickCount * StaticConstant.GAME_TICK_DURATION;
            this.lastPortalDespawnTimestamp = System.currentTimeMillis() - elapsedSinceLastPortal;
        } else if (!isUnknownPortal) {
            absoluteTickCount = Math.abs(portalTicksRemaining);
            long elapsedSinceLastPortal = absoluteTickCount * StaticConstant.GAME_TICK_DURATION;
            this.lastPortalDespawnTimestamp = System.currentTimeMillis() - elapsedSinceLastPortal;
        }
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        RenderSafetyEvent renderSafetyEvent = getRenderSafetyEvent();
        if (renderSafetyEvent == null) return null;
        if (!renderSafetyEvent.isWidgetAvailable() || renderSafetyEvent.isVolatileState()) return null;
        this.renderWhenSecure(graphics2D);
        return null;
    }

    @Override
    public void renderWhenSecure(Graphics2D graphics) {
        if (minigameState == MinigameState.COMPLETE ||
                minigameState == MinigameState.CLOSING ||
                minigameState == MinigameState.CLOSED ||
                minigameState == MinigameState.INITIALIZING
        ) {
            return;
        }

        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        if (!config.isShowTimeSinceLastPortal()) return;
        if (config.portalPaintLocation() == PaintLocation.INFOBOX) return;

        Widget parentWidget = plugin.getClient().getWidget(StaticConstant.MINIGAME_WIDGET_PARENT_ID);
        Widget portalWidget = plugin.getClient().getWidget(StaticConstant.MINIGAME_WIDGET_PORTAL_ID);

        if (parentWidget == null || portalWidget == null) return;
        if (parentWidget.isHidden() || !portalWidget.isHidden()) return;

        BufferedImage sprite = spriteManager.getSprite(StaticConstant.MINIGAME_PORTAL_SPRITE_ID, 0);
        if (sprite == null) return;

        BufferedImage spriteInGrayscale = ImageUtil.grayscaleImage(sprite);

        int spriteLocationX = parentWidget.getRelativeX() + portalWidget.getRelativeX() + 16;
        int spriteLocationY = parentWidget.getRelativeY() + portalWidget.getRelativeY() + 12;

        graphics.drawImage(
                spriteInGrayscale,
                spriteLocationX,
                spriteLocationY,
                SPRITE_DIMENSION_WIDTH,
                SPRITE_DIMENSION_HEIGHT,
                null
        );

        long elapsedSinceDespawnInMillis = System.currentTimeMillis() - lastPortalDespawnTimestamp;
        long elapsedSinceDespawnInSeconds = elapsedSinceDespawnInMillis / 1000;

        Color textColor = lastPortalDespawnTimestamp != 0 ?
                getPortalProbabilityColor(elapsedSinceDespawnInSeconds) :
                Color.WHITE;

        String text = lastPortalDespawnTimestamp != 0 ?
                formatRemainingTime(elapsedSinceDespawnInSeconds) :
                "?";

        Rectangle bounds = new Rectangle(
                spriteLocationX,
                spriteLocationY + SPRITE_DIMENSION_HEIGHT + 1,
                SPRITE_DIMENSION_WIDTH,
                24);

        this.drawStringCenteredToBoundingBox(
                graphics,
                bounds,
                text,
                textColor
        );
    }

    private Color getPortalProbabilityColor(long elapsedSinceDespawnInSeconds) {
        if (isFirstPortalThisRound) elapsedSinceDespawnInSeconds -= 40;
        if (elapsedSinceDespawnInSeconds >= 108) return Color.RED;
        else if (elapsedSinceDespawnInSeconds >= 85) return Color.YELLOW;
        return Color.WHITE;
    }

    private String formatRemainingTime(long elapsedSinceDespawnInSeconds) {
        int minutes = (int) (elapsedSinceDespawnInSeconds / 60D);
        int seconds = (int) (elapsedSinceDespawnInSeconds % 60);
        return String.format("%01d:%02d", minutes, seconds);
    }
}
