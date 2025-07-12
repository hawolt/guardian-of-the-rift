package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.RewardPointUpdateEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PointSlice extends AbstractPluginSlice {

    @Inject
    protected EventBus bus;

    @Getter(AccessLevel.NONE)
    private final GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private RenderSafetyEvent renderSafetyEvent;

    @Getter(AccessLevel.NONE)
    private Color potentialPointStatusColor = Color.RED;

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

    @Override
    protected void startUp() {

    }

    @Override
    protected void shutDown() {

    }

    @Inject
    public PointSlice(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
    }

    private void postRewardPointUpdate() {
        this.bus.post(
                new RewardPointUpdateEvent(
                        totalElementalRewardPoints,
                        totalCatalyticRewardPoints
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
        this.postRewardPointUpdate();
    }

    @Subscribe
    public void onChatMessage(ChatMessage message) {
        String content = message.getMessage();
        if (message.getType() == ChatMessageType.GAMEMESSAGE) {
            checkTotalPointStatus(content);
        } else if (message.getType() == ChatMessageType.SPAM) {
            checkPointsSpent(content);
        }
    }

    private void checkPointsSpent(String content) {
        if (!content.startsWith("You found some loot: ")) return;
        this.totalElementalRewardPoints -= 1;
        this.totalCatalyticRewardPoints -= 1;
        this.postRewardPointUpdate();
    }

    private void checkTotalPointStatus(String content) {
        Matcher matcher = TOTAL_POINT_PATTERN.matcher(content);
        if (!matcher.find()) return;
        this.totalElementalRewardPoints = Integer.parseInt(matcher.group(1));
        this.totalCatalyticRewardPoints = Integer.parseInt(matcher.group(2));
        this.postRewardPointUpdate();
    }
}
