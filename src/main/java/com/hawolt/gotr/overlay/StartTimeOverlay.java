package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.Slice;
import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.minigame.impl.MinigameStateEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.events.ScriptPostFired;
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
import java.util.concurrent.TimeUnit;

public class StartTimeOverlay extends OverlayPanel implements Slice {

    @Inject
    protected EventBus bus;

    @Getter(AccessLevel.NONE)
    private MinigameState minigameState = MinigameState.UNKNOWN;

    @Getter(AccessLevel.NONE)
    private final GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private RenderSafetyEvent renderSafetyEvent;

    @Getter(AccessLevel.NONE)
    private boolean isFallBackRenderRequired;

    @Getter(AccessLevel.NONE)
    private long gameWillStartAtTimestamp;

    @Override
    public void startup() {
        this.bus.register(this);
    }

    @Override
    public void shutdown() {
        this.bus.unregister(this);
    }

    @Inject
    public StartTimeOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setPosition(OverlayPosition.TOP_LEFT);
        this.getMenuEntries().add(
                new OverlayMenuEntry(
                        MenuAction.RUNELITE_OVERLAY_CONFIG,
                        OverlayManager.OPTION_CONFIGURE,
                        "Guardians of the Rift"
                )
        );
    }

    @Subscribe
    public void onRenderSafetyEvent(RenderSafetyEvent event) {
        this.renderSafetyEvent = event;
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        if (renderSafetyEvent == null) return null;
        if (!renderSafetyEvent.isInGame() || renderSafetyEvent.isVolatileState()) return null;
        if (!isFallBackRenderRequired) return null;
        this.renderWhenSecure();
        return super.render(graphics2D);
    }

    public void renderWhenSecure() {
        long secondsUntilGameStart = TimeUnit.MILLISECONDS.toSeconds(
                gameWillStartAtTimestamp - System.currentTimeMillis() + StaticConstant.GAME_TICK_DURATION
        );
        this.panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("Game Starting in:")
                        .right(String.valueOf(secondsUntilGameStart))
                        .build()
        );
    }

    @Subscribe
    public void onMinigameStateEvent(MinigameStateEvent event) {
        this.minigameState = event.getCurrentMinigameState();
        if (minigameState == MinigameState.CLOSING) {
            this.gameWillStartAtTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60);
        } else if (minigameState == MinigameState.CLOSED) {
            this.gameWillStartAtTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
        }
    }

    @Subscribe
    public void onGameState(GameState event) {
        if (event != GameState.LOGGED_IN) return;
        this.gameWillStartAtTimestamp = 0;
    }

    @Subscribe
    public void onScriptPostFired(ScriptPostFired event) {
        if (minigameState != MinigameState.CLOSING && minigameState != MinigameState.CLOSED) return;
        int scriptId = event.getScriptId();
        if (scriptId != StaticConstant.MINIGAME_HUD_UPDATE_SCRIPT_ID) return;
        if (!plugin.getConfig().isShowGameStartTimer()) return;
        Widget baseWidget = plugin.getClient().getWidget(
                StaticConstant.MINIGAME_WIDGET_GROUP_ID,
                StaticConstant.MINIGAME_WIDGET_CHILD_GAME_ID
        );
        this.isFallBackRenderRequired = baseWidget == null;
        if (isFallBackRenderRequired) return;
        this.handleGameStartUpdate();
    }

    private void handleGameStartUpdate() {
        Widget target = plugin.getClient().getWidget(
                StaticConstant.MINIGAME_WIDGET_GROUP_ID,
                StaticConstant.MINIGAME_WIDGET_POWER_TEXT_WIDGET_ID
        );
        if (target == null) return;
        this.handleWidgetStartTimeUpdate(target);
    }

    private void handleWidgetStartTimeUpdate(Widget textWidget) {
        long secondsUntilGameStart = TimeUnit.MILLISECONDS.toSeconds(
                gameWillStartAtTimestamp - System.currentTimeMillis() + StaticConstant.GAME_TICK_DURATION
        );
        String textContent = textWidget.getText();
        if (!textContent.contains("Power")) return;
        String updated = String.format("Game Starting in: %s", secondsUntilGameStart);
        textWidget.setText(updated);
        textWidget.revalidate();
    }
}
