package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.data.GameStartIndicator;
import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.events.FragmentAmountUpdateEvent;
import com.hawolt.gotr.events.minigame.impl.GameStartingSoonEvent;
import com.hawolt.gotr.events.minigame.impl.MinigameStateEvent;
import com.hawolt.gotr.events.minigame.impl.PortalSpawnEvent;
import net.runelite.client.Notifier;
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;

import javax.inject.Inject;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NotificationSlice extends AbstractPluginSlice {
    @Inject
    private Notifier notifier;

    @Inject
    private ScheduledExecutorService executor;

    private int lastKnownFragmentAmount = 0;
    private ScheduledFuture<?> future;

    @Override
    protected void startUp() {

    }

    @Override
    protected void shutDown() {

    }

    @Subscribe
    public void onPortalSpawnEvent(PortalSpawnEvent event) {
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        if (!config.notifyOnPortalSpawn().isEnabled()) return;
        this.notifier.notify("A portal has spawned");
    }

    @Subscribe
    public void onGameStartingSoonEvent(GameStartingSoonEvent event) {
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        GameStartIndicator indicator = config.notifyOnGameStart();
        if (indicator == GameStartIndicator.OFF) return;
        if (indicator != event.getGameStartIndicator()) return;
        this.notifier.notify("The game will start in " + indicator);
    }

    @Subscribe
    public void onFragmentAmountUpdateEvent(FragmentAmountUpdateEvent event) {
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        int notifyOnGuardianFragments = config.notifyOnGuardianFragments();
        if (notifyOnGuardianFragments == 0) return;
        int lastKnown = lastKnownFragmentAmount;
        this.lastKnownFragmentAmount = event.getCurrent();
        if (lastKnown >= notifyOnGuardianFragments) return;
        if (lastKnownFragmentAmount < notifyOnGuardianFragments) return;
        this.notifier.notify("You have reached " + notifyOnGuardianFragments + " fragments");
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged) {
        if (!"notifyOnGuardianOpen".equals(configChanged.getKey())) return;
        if (!"0".equals(configChanged.getNewValue())) return;
        if (future == null || future.isCancelled() || future.isDone()) return;
        this.future.cancel(true);
    }

    @Subscribe
    public void onMinigameStateEvent(MinigameStateEvent event) {
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        int notifyOnFirstObelisk = config.notifyOnFirstObelisk();
        if (event.getCurrentMinigameState() != MinigameState.START) return;
        if (notifyOnFirstObelisk == 0) return;
        long timeUntilOpen = event.getInternalGuardianTicksRemaining() * 600L;
        long delay = timeUntilOpen - TimeUnit.SECONDS.toMillis(notifyOnFirstObelisk);
        this.future = executor.schedule(() -> {
            notifier.notify("Obelisks will open in " + notifyOnFirstObelisk + " seconds");
        }, delay, TimeUnit.MILLISECONDS);
    }
}
