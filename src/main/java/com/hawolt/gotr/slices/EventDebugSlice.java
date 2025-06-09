package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.*;
import com.hawolt.gotr.events.minigame.impl.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.client.eventbus.Subscribe;

import java.util.Arrays;

@Slf4j
public class EventDebugSlice extends AbstractPluginSlice {

    @Override
    protected void startUp() {

    }

    @Override
    protected void shutDown() {

    }

    @Subscribe
    public void onScriptPreFired(ScriptPreFired event) {
        if (event.getScriptId() != StaticConstant.MINIGAME_HUD_UPDATE_SCRIPT_ID) return;
        Object[] arguments = event.getScriptEvent().getArguments();
        log.info("onScriptPreFired{{}}", Arrays.asList(arguments));
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
    public void onFragmentAmountUpdateEvent(FragmentAmountUpdateEvent event) {
        log.info("{}", event);
    }

    @Subscribe
    public void onGameStartingSoonEvent(GameStartingSoonEvent event) {
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
