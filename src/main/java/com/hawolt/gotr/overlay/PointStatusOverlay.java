package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.Slice;
import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.data.TypeAssociation;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.RewardPointUpdateEvent;
import com.hawolt.gotr.events.minigame.impl.MinigameStateEvent;
import com.hawolt.gotr.events.minigame.impl.PointGainedEvent;
import com.hawolt.gotr.events.minigame.impl.PointResetEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.MenuAction;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;
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
    private int totalElementalRewardPoints = -1, totalCatalyticRewardPoints = -1;

    @Getter(AccessLevel.NONE)
    private int elementalPoints, catalyticPoints;

    @Getter(AccessLevel.NONE)
    private final Pattern GAINED_POINT_PATTERN = Pattern.compile(StaticConstant.MINIGAME_POINT_STATUS_GAINED);

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
    public PointStatusOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setPosition(plugin.getConfig().pointStatusOverlayPosition());
        this.getMenuEntries().add(
                new OverlayMenuEntry(
                        MenuAction.RUNELITE_OVERLAY_CONFIG,
                        OverlayManager.OPTION_CONFIGURE,
                        "Guardians of the Rift Point Analysis"
                )
        );
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (!"pointStatusInfoboxPosition".equals(configChanged.getKey())) return;
        this.setPosition(plugin.getConfig().pointStatusOverlayPosition());
    }

    @Subscribe
    public void onRenderSafetyEvent(RenderSafetyEvent event) {
        this.renderSafetyEvent = event;
    }

    @Subscribe
    public void onRewardPointUpdateEvent(RewardPointUpdateEvent rewardPointUpdateEvent) {
        this.totalElementalRewardPoints = rewardPointUpdateEvent.getTotalElementalRewardPoints();
        this.totalCatalyticRewardPoints = rewardPointUpdateEvent.getTotalCatalyticRewardPoints();
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
