package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.events.EssenceAmountUpdateEvent;
import com.hawolt.gotr.events.ObeliskAnalysisEvent;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.TickTimestampEvent;
import com.hawolt.gotr.events.minigame.impl.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
public class EventDebugSlice extends AbstractPluginSlice {

    @Override
    protected void startUp() {

    }

    @Override
    protected void shutDown() {

    }

    @Subscribe
    public void onTickTimestampEvent(TickTimestampEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onRenderSafetyEvent(RenderSafetyEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onObeliskAnalysisEvent(ObeliskAnalysisEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onEssenceAmountUpdateEvent(EssenceAmountUpdateEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onRewardEligibleEvent(RewardEligibleEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onRegionUpdateEvent(RegionUpdateEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onPortalSpawnEvent(PortalSpawnEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onPortalDespawnEvent(PortalDespawnEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onPointGainedEvent(PointGainedEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onObeliskUpdateEvent(ObeliskUpdateEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onMinigameStateEvent(MinigameStateEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onMilestoneEvent(MilestoneEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onGuardianSpawnEvent(GuardianSpawnEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onGuardianDespawnEvent(GuardianDespawnEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onGameStatusEvent(GameStatusEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onPointResetEvent(PointResetEvent event) {
        log.info("{}", event);
    }
}
