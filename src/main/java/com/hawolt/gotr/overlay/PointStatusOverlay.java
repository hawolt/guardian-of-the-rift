package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.Slice;
import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.data.TypeAssociation;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.minigame.impl.MinigameStateEvent;
import com.hawolt.gotr.events.minigame.impl.PointGainedEvent;
import com.hawolt.gotr.events.minigame.impl.PointResetEvent;
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
import net.runelite.client.ui.overlay.components.TextComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PointStatusOverlay extends OverlayPanel implements Slice {

    @Inject
    protected EventBus bus;

    @Getter(AccessLevel.NONE)
    private final Object lock = new Object();

    @Getter(AccessLevel.NONE)
    private final GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private RenderSafetyEvent renderSafetyEvent;

    @Getter(AccessLevel.NONE)
    private Color potentialPointStatusColor = Color.RED;

    @Getter(AccessLevel.NONE)
    private int elementalPoints, catalyticPoints;

    @Getter(AccessLevel.NONE)
    private int elementalRewardPoints, catalyticRewardPoints;

    @Getter(AccessLevel.NONE)
    private int totalElementalRewardPoints = -1, totalCatalyticRewardPoints = -1;

    @Getter(AccessLevel.NONE)
    private final Pattern CHECKUP_POINT_PATTERN = Pattern.compile(StaticConstant.MINIGAME_POINT_STATUS_CHECKUP);

    @Getter(AccessLevel.NONE)
    private final Pattern GAINED_POINT_PATTERN = Pattern.compile(StaticConstant.MINIGAME_POINT_STATUS_GAINED);

    @Getter(AccessLevel.NONE)
    private final Pattern TOTAL_POINT_PATTERN = Pattern.compile(StaticConstant.MINIGAME_POINT_STATUS_TOTAL);

    @Override
    public void startup() {
        this.bus.register(this);
    }

    @Override
    public void shutdown() {
        this.bus.unregister(this);
    }

    @Inject
    public PointStatusOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setPosition(OverlayPosition.TOP_CENTER);
        this.getMenuEntries().add(
                new OverlayMenuEntry(
                        MenuAction.RUNELITE_OVERLAY_CONFIG,
                        OverlayManager.OPTION_CONFIGURE,
                        "Guardians of the Rift Point Analysis"
                )
        );
    }

    @Subscribe
    public void onRenderSafetyEvent(RenderSafetyEvent event) {
        this.renderSafetyEvent = event;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!renderSafetyEvent.isInGame()) return;
        Widget dialog = plugin.getClient().getWidget(
                StaticConstant.GAME_DIALOG_WIDGET_GROUP,
                StaticConstant.GAME_DIALOG_WIDGET_CONTENT
        );
        if (dialog == null) return;
        String content = dialog.getText();
        Matcher matcher = CHECKUP_POINT_PATTERN.matcher(content);
        if (!matcher.find()) return;
        this.totalElementalRewardPoints = Integer.parseInt(matcher.group(2));
        this.totalCatalyticRewardPoints = Integer.parseInt(matcher.group(1));
    }


    @Subscribe
    public void onPointGainedEvent(PointGainedEvent event) {
        synchronized (lock) {
            TypeAssociation typeAssociation = event.getRuneType();
            switch (typeAssociation) {
                case ELEMENTAL:
                    this.elementalPoints += event.getGained();
                    break;
                case CATALYTIC:
                    this.catalyticPoints += event.getGained();
                    break;
            }
        }
        this.potentialPointStatusColor = (elementalPoints + catalyticPoints) >= 300 ?
                Color.GREEN :
                Color.RED;
    }

    @Subscribe
    public void onMinigameStateEvent(MinigameStateEvent event) {
        if (event.getCurrentMinigameState() != MinigameState.CLOSING) return;
        this.onPointResetEvent(null);
    }

    @Subscribe
    public void onPointResetEvent(PointResetEvent event) {
        this.potentialPointStatusColor = Color.RED;
        this.elementalPoints = 0;
        this.catalyticPoints = 0;
    }

    @Subscribe
    public void onChatMessage(ChatMessage message) {
        if (message.getType() != ChatMessageType.GAMEMESSAGE) return;
        String content = message.getMessage();
        checkGainedPointStatus(content);
        checkTotalPointStatus(content);
    }

    private void checkTotalPointStatus(String content) {
        Matcher matcher = TOTAL_POINT_PATTERN.matcher(content);
        if (!matcher.find()) return;
        this.totalElementalRewardPoints = Integer.parseInt(matcher.group(1));
        this.totalCatalyticRewardPoints = Integer.parseInt(matcher.group(2));
    }

    private void checkGainedPointStatus(String content) {
        Matcher matcher = GAINED_POINT_PATTERN.matcher(content);
        if (!matcher.find()) return;
        this.elementalRewardPoints += Integer.parseInt(matcher.group(1));
        this.catalyticRewardPoints += Integer.parseInt(matcher.group(2));
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        if (renderSafetyEvent == null) return null;
        if (!plugin.getConfig().isShowPointStatusInfobox()) return null;
        if (renderSafetyEvent.isInGame() || renderSafetyEvent.isWidgetVisible()) {
            this.panelComponent.getChildren().add(
                    LineComponent.builder()
                            .left("Reward Points:")
                            .right(
                                    String.format(
                                            "%s/%s",
                                            totalElementalRewardPoints != -1 ? totalElementalRewardPoints : "?",
                                            totalCatalyticRewardPoints != -1 ? totalCatalyticRewardPoints : "?"
                                    )
                            )
                            .build()
            );
        }
        if (renderSafetyEvent.isWidgetVisible()) {
            this.panelComponent.getChildren().add(
                    LineComponent.builder()
                            .left("Potential:")
                            .rightColor(potentialPointStatusColor)
                            .right(
                                    String.format(
                                            "%s/%s",
                                            String.format("%.2f", elementalPoints / 100D),
                                            String.format("%.2f", catalyticPoints / 100D)
                                    )
                            )
                            .build()
            );
        }
        return super.render(graphics2D);
    }
}
