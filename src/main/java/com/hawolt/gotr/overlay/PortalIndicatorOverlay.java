package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.Slice;
import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.data.PaintLocation;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.RenderSafetyEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.MenuAction;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class PortalIndicatorOverlay extends OverlayPanel implements Slice {

    @Inject
    protected EventBus bus;

    @Getter(AccessLevel.NONE)
    private MinigameState minigameState = MinigameState.UNKNOWN;

    @Getter(AccessLevel.NONE)
    private final GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private RenderSafetyEvent renderSafetyEvent;

    @Getter(AccessLevel.NONE)
    private long lastPortalDespawnTimestamp;

    @Getter(AccessLevel.NONE)
    private boolean isFirstPortalThisRound;

    @Override
    public void startup() {
        this.bus.register(this);
    }

    @Override
    public void shutdown() {
        this.bus.unregister(this);
    }

    @Override
    public boolean isClientThreadRequiredOnStartup() {
        return false;
    }

    @Override
    public boolean isClientThreadRequiredOnShutDown() {
        return false;
    }

    @Inject
    public PortalIndicatorOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setPosition(OverlayPosition.TOP_RIGHT);
        this.getMenuEntries().add(
                new OverlayMenuEntry(
                        MenuAction.RUNELITE_OVERLAY_CONFIG,
                        OverlayManager.OPTION_CONFIGURE,
                        "Guardians of the Rift Portal Timer"
                )
        );
    }

    @Subscribe
    public void onRenderSafetyEvent(RenderSafetyEvent event) {
        this.renderSafetyEvent = event;
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
        if (renderSafetyEvent == null) return null;
        if (!renderSafetyEvent.isWidgetAvailable() || renderSafetyEvent.isVolatileState()) return null;
        if (minigameState == MinigameState.COMPLETE ||
                minigameState == MinigameState.CLOSING ||
                minigameState == MinigameState.CLOSED ||
                minigameState == MinigameState.INITIALIZING
        ) {
            return super.render(graphics2D);
        }

        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        if (!config.isShowTimeSinceLastPortal()) return super.render(graphics2D);
        if (config.portalPaintLocation() == PaintLocation.SCREEN) return super.render(graphics2D);

        Widget parentWidget = plugin.getClient().getWidget(StaticConstant.MINIGAME_WIDGET_PARENT_ID);
        Widget portalWidget = plugin.getClient().getWidget(StaticConstant.MINIGAME_WIDGET_PORTAL_ID);

        if (parentWidget == null || portalWidget == null) return super.render(graphics2D);
        if (parentWidget.isHidden() || !portalWidget.isHidden()) return super.render(graphics2D);

        long elapsedSinceDespawnInMillis = System.currentTimeMillis() - lastPortalDespawnTimestamp;
        long elapsedSinceDespawnInSeconds = elapsedSinceDespawnInMillis / 1000;

        Color textColor = lastPortalDespawnTimestamp != 0 ?
                getPortalProbabilityColor(elapsedSinceDespawnInSeconds) :
                Color.WHITE;

        String text = lastPortalDespawnTimestamp != 0 ?
                formatRemainingTime(elapsedSinceDespawnInSeconds) :
                "?";

        this.panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("Last Portal:")
                        .right(text)
                        .rightColor(textColor)
                        .build()
        );

        return super.render(graphics2D);
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