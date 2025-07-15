package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.Slice;
import com.hawolt.gotr.data.GameStartIndicator;
import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.data.PaintLocation;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.minigame.impl.GameStartingSoonEvent;
import com.hawolt.gotr.events.minigame.impl.MinigameStateEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
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
    private long gameWillStartAtTimestamp;

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
    public StartTimeOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setPosition(OverlayPosition.TOP_RIGHT);
        this.getMenuEntries().add(
                new OverlayMenuEntry(
                        MenuAction.RUNELITE_OVERLAY_CONFIG,
                        OverlayManager.OPTION_CONFIGURE,
                        "Guardians of the Rift Timer"
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
        if (
                minigameState == MinigameState.UNKNOWN ||
                        minigameState == MinigameState.START ||
                        minigameState == MinigameState.ACTIVE ||
                        minigameState == MinigameState.COMPLETE
        ) {
            return null;
        }
        PaintLocation paintLocation = plugin.getConfig().gameStartLocation();
        if (paintLocation == PaintLocation.SCREEN) return null;
        long secondsUntilGameStart = TimeUnit.MILLISECONDS.toSeconds(
                gameWillStartAtTimestamp - System.currentTimeMillis() + StaticConstant.GAME_TICK_DURATION
        );
        String timeUntilGameStart = secondsUntilGameStart < 0 ? "?" : String.valueOf(secondsUntilGameStart);
        this.panelComponent.getChildren().add(
                LineComponent.builder()
                        .left("Game Starting in:")
                        .right(timeUntilGameStart)
                        .build()
        );
        return super.render(graphics2D);
    }

    @Subscribe
    public void onMinigameStateEvent(MinigameStateEvent event) {
        this.minigameState = event.getCurrentMinigameState();
        if (minigameState == MinigameState.CLOSING && event.getPreviousMinigameState() == MinigameState.COMPLETE) {
            this.gameWillStartAtTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60);
        } else if (minigameState == MinigameState.CLOSED && event.getPreviousMinigameState() == MinigameState.CLOSING) {
            this.gameWillStartAtTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage message) {
        if (!renderSafetyEvent.isInGame()) return;
        if (message.getType() != ChatMessageType.SPAM && message.getType() != ChatMessageType.GAMEMESSAGE) return;
        String content = message.getMessage();
        if (content.contains("The rift will become active in 30 seconds.")) {
            this.gameWillStartAtTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
            this.bus.post(new GameStartingSoonEvent(plugin.getClient().getTickCount(), GameStartIndicator.PHASE1));
        } else if (content.contains("The rift will become active in 10 seconds.")) {
            this.gameWillStartAtTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
            this.bus.post(new GameStartingSoonEvent(plugin.getClient().getTickCount(), GameStartIndicator.PHASE2));
        } else if (content.contains("The rift will become active in 5 seconds.")) {
            this.gameWillStartAtTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
            this.bus.post(new GameStartingSoonEvent(plugin.getClient().getTickCount(), GameStartIndicator.PHASE3));
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
        PaintLocation paintLocation = plugin.getConfig().gameStartLocation();
        if (paintLocation == PaintLocation.INFOBOX) return;
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
        String timeUntilGameStart = secondsUntilGameStart < 0 ? "?" : String.valueOf(secondsUntilGameStart);
        String textContent = textWidget.getText();
        if (!textContent.contains("Power")) return;
        String updated = String.format("Game Starting in: %s", timeUntilGameStart);
        textWidget.setText(updated);
        textWidget.revalidate();
    }
}
