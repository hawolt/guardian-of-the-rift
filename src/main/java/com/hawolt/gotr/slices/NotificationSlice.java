package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.data.*;
import com.hawolt.gotr.events.FragmentAmountUpdateEvent;
import com.hawolt.gotr.events.minigame.impl.GameStartingSoonEvent;
import com.hawolt.gotr.events.minigame.impl.MinigameStateEvent;
import com.hawolt.gotr.events.minigame.impl.ObeliskUpdateEvent;
import com.hawolt.gotr.events.minigame.impl.PortalSpawnEvent;
import net.runelite.client.Notifier;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;

import javax.inject.Inject;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NotificationSlice extends AbstractPluginSlice {
    @Inject
    private Notifier notifier;

    @Inject
    private ScheduledExecutorService executor;

    private final String base = "The %s Obelisk%s has become active";
    private int lastKnownFragmentAmount = 0;
    private ScheduledFuture<?> future;

    @Override
    protected void startUp() {

    }

    @Override
    protected void shutDown() {

    }

    private boolean isPlayerInPortalArea() {
        int playerX = client.getLocalPlayer().getWorldLocation().getX();
        int playerY = client.getLocalPlayer().getWorldLocation().getY();
        return playerX > StaticConstant.HUGE_GUARDRIAN_REMAINS_AREA_WEST_BOUNDARY &&
                playerX < StaticConstant.HUGE_GUARDRIAN_REMAINS_AREA_EAST_BOUNDARY &&
                playerY < StaticConstant.HUGE_GUARDRIAN_REMAINS_AREA_NORTH_BOUNDARY &&
                playerY > StaticConstant.HUGE_GUARDRIAN_REMAINS_AREA_SOUTH_BOUNDARY;
    }

    @Subscribe
    public void onPortalSpawnEvent(PortalSpawnEvent event) {
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        if (!config.notifyOnPortalSpawn().isEnabled() || isPlayerInPortalArea()) return;
        this.notifier.notify(config.notifyOnPortalSpawn(), "A portal has spawned");
    }

    @Subscribe
    public void onGameStartingSoonEvent(GameStartingSoonEvent event) {
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        GameStartIndicator indicator = config.notifyOnGameStart();
        if (indicator == GameStartIndicator.OFF) return;
        if (indicator != event.getGameStartIndicator()) return;
        this.notifier.notify("The game will start in " + indicator);
    }

    private String normalizeObeliskName(Obelisk obelisk) {
        if (obelisk == null) return "null";
        return obelisk.name().charAt(0) + obelisk.name().substring(1).toLowerCase();
    }

    @Subscribe
    public void onObeliskUpdateEvent(ObeliskUpdateEvent event) {
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        Set<Obelisk> set = config.notifyOnObeliskType();
        if (set.isEmpty()) return;
        Obelisk catalyticObelisk = Obelisk.getObeliskByRuneIndexId(TypeAssociation.CATALYTIC, event.getCatalyticRuneEnumIndex());
        Obelisk elementalObelisk = Obelisk.getObeliskByRuneIndexId(TypeAssociation.ELEMENTAL, event.getElementalRuneEnumIndex());
        if (catalyticObelisk == null || elementalObelisk == null) return;
        boolean bothObelisksActive = set.contains(catalyticObelisk) && set.contains(elementalObelisk);
        String insertion = null;
        if (bothObelisksActive) {
            insertion = normalizeObeliskName(catalyticObelisk) + " and " + normalizeObeliskName(elementalObelisk);
        } else if (set.contains(catalyticObelisk)) {
            insertion = normalizeObeliskName(catalyticObelisk);
        } else if (set.contains(elementalObelisk)) {
            insertion = normalizeObeliskName(elementalObelisk);
        }
        if (insertion == null) return;
        this.notifier.notify(String.format(base, insertion, bothObelisksActive ? "s" : ""));
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
