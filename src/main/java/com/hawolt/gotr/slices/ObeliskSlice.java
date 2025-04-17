package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.data.*;
import com.hawolt.gotr.events.EssenceAmountUpdateEvent;
import com.hawolt.gotr.events.ObeliskAnalysisEvent;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.minigame.impl.MinigameStateEvent;
import com.hawolt.gotr.events.minigame.impl.ObeliskUpdateEvent;
import com.hawolt.gotr.events.minigame.impl.RegionUpdateEvent;
import com.hawolt.gotr.utility.ObeliskAnalysis;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObeliskSlice extends AbstractPluginSlice {

    @Getter(AccessLevel.NONE)
    private Obelisk elemental, catalytic;

    @Getter(AccessLevel.NONE)
    private WorldPoint worldPoint;

    @Getter(AccessLevel.NONE)
    private MinigameState minigameState;

    @Getter(AccessLevel.NONE)
    private RenderSafetyEvent renderSafetyEvent;

    @Getter(AccessLevel.NONE)
    private boolean isInMinigameRegion;

    @Getter(AccessLevel.NONE)
    private int currentRegionId, ticksRemainingUntilUpdate, currentClientTick;

    @Getter(AccessLevel.NONE)
    private final Map<Integer, GameObject> obeliskGameObjects = new HashMap<>();

    @Override
    protected void startUp() {

    }

    @Override
    protected void shutDown() {

    }

    @Subscribe
    public void onRegionUpdateEvent(RegionUpdateEvent event) {
        this.currentRegionId = event.getCurrentRegionId();
        this.isInMinigameRegion = (currentRegionId == StaticConstant.MINIGAME_REGION_ID);
        if (isInMinigameRegion) return;
        this.bus.post(new ObeliskAnalysisEvent(ObeliskType.OPTIMAL));
        this.bus.post(new ObeliskAnalysisEvent(ObeliskType.SECONDARY));
    }

    public void onMinigameStateEvent(MinigameStateEvent event) {
        this.minigameState = event.getCurrentMinigameState();
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        if (currentRegionId != StaticConstant.MINIGAME_REGION_ID) return;
        WorldPoint current = client.getLocalPlayer().getWorldLocation();
        if (minigameState == MinigameState.CLOSING || minigameState == MinigameState.CLOSED) return;
        if (renderSafetyEvent == null || !renderSafetyEvent.isWidgetVisible()) return;
        if (worldPoint == null || !worldPoint.equals(current)) {
            this.updateObeliskEfficiencyWeight();
        }
        this.worldPoint = current;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        if (!StaticConstant.MINIGAME_IDS_OBELISK_ID.contains(event.getGameObject().getId())) return;
        this.obeliskGameObjects.put(gameObject.getId(), gameObject);
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject gameObject = event.getGameObject();
        if (!StaticConstant.MINIGAME_IDS_OBELISK_ID.contains(event.getGameObject().getId())) return;
        this.obeliskGameObjects.remove(gameObject.getId());
    }

    @Subscribe
    public void onObeliskUpdateEvent(ObeliskUpdateEvent event) {
        this.catalytic = Obelisk.getObelisk(TypeAssociation.CATALYTIC, event.getCatalyticRuneEnumIndex());
        this.elemental = Obelisk.getObelisk(TypeAssociation.ELEMENTAL, event.getElementalRuneEnumIndex());
        this.ticksRemainingUntilUpdate = event.getRemainingTicksUntilUpdate();
        this.currentClientTick = event.getClientTick();
        this.updateObeliskEfficiencyWeight();
    }

    @Subscribe
    public void onEssenceAmountUpdateEvent(EssenceAmountUpdateEvent event) {
        if (currentRegionId != StaticConstant.MINIGAME_REGION_ID) return;
        this.updateObeliskEfficiencyWeight();
    }

    @Subscribe
    public void onRenderSafetyEvent(RenderSafetyEvent event) {
        this.renderSafetyEvent = event;
    }

    private void updateObeliskEfficiencyWeight() {
        if (catalytic == null || elemental == null) {
            this.bus.post(new ObeliskAnalysisEvent(ObeliskType.OPTIMAL));
            this.bus.post(new ObeliskAnalysisEvent(ObeliskType.SECONDARY));
        } else {
            if (!renderSafetyEvent.isInGame() || renderSafetyEvent.isVolatileState() || !isInMinigameRegion) return;
            Stream<Obelisk> available = plugin.getInventoryEssenceSlice().getAvailableTalismanList()
                    .stream()
                    .map(item -> Obelisk.getByTalismanItemId(item.getId()))
                    .filter(Objects::nonNull);
            List<ObeliskAnalysis> list = Stream.concat(
                            Stream.of(elemental, catalytic),
                            available
                    )
                    .distinct()
                    .map(obelisk ->
                            new ObeliskAnalysis(
                                    plugin,
                                    obelisk,
                                    currentClientTick,
                                    ticksRemainingUntilUpdate,
                                    obeliskGameObjects.get(obelisk.getGameObjectId())
                            )
                    )
                    .filter(analysis ->
                            client.getBoostedSkillLevel(Skill.RUNECRAFT) >= analysis.getRuneCraftInfo().getLevelRequired()
                    )

                    .sorted(Comparator.comparingDouble(ObeliskAnalysis::getWeightedEfficiency).reversed())
                    .collect(Collectors.toList());
            if (list.isEmpty()) {
                this.bus.post(new ObeliskAnalysisEvent(ObeliskType.OPTIMAL));
            } else {
                this.bus.post(new ObeliskAnalysisEvent(ObeliskType.OPTIMAL, list.get(0)));
            }
            if (list.size() > 1) {
                ObeliskAnalysis[] secondaries = list.subList(1, list.size()).toArray(ObeliskAnalysis[]::new);
                this.bus.post(new ObeliskAnalysisEvent(ObeliskType.SECONDARY, secondaries));
            } else {
                this.bus.post(new ObeliskAnalysisEvent(ObeliskType.SECONDARY));
            }
        }
    }
}
