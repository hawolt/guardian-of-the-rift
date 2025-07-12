package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.data.*;
import com.hawolt.gotr.events.EssenceAmountUpdateEvent;
import com.hawolt.gotr.events.ObeliskAnalysisEvent;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.RewardPointUpdateEvent;
import com.hawolt.gotr.events.minigame.impl.*;
import com.hawolt.gotr.utility.ObeliskAnalysis;
import com.hawolt.gotr.utility.PointStatus;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObeliskSlice extends AbstractPluginSlice {

    @Getter(AccessLevel.NONE)
    private final Object lock = new Object();

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
    private int currentRegionId, elementalPoints, catalyticPoints;

    @Getter(AccessLevel.NONE)
    private RewardPointUpdateEvent rewardPointUpdateEvent;

    @Getter(AccessLevel.NONE)
    private final Map<Integer, GameObject> obeliskGameObjects = new HashMap<>();

    @Getter(AccessLevel.NONE)
    private final List<GameObject> activeObeliskGameObjects = new ArrayList<>();

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

    @Subscribe
    public void onMinigameStateEvent(MinigameStateEvent event) {
        this.minigameState = event.getCurrentMinigameState();
        if (event.getCurrentMinigameState() != MinigameState.CLOSING) return;
        this.onPointResetEvent(null);
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        List<GameObject> snapshot = new ArrayList<>(activeObeliskGameObjects);
        this.activeObeliskGameObjects.clear();
        for (GameObject guardian : obeliskGameObjects.values()) {
            Animation animation = ((DynamicObject) guardian.getRenderable()).getAnimation();
            if (animation != null && animation.getId() == StaticConstant.MINIGAME_ACTIVE_GUARDIAN_ANIMATION_ID) {
                activeObeliskGameObjects.add(guardian);
            }
        }
        Set<Integer> snapshotIds = snapshot.stream()
                .map(GameObject::getId)
                .collect(Collectors.toSet());
        Set<Integer> activeGuardianIds = activeObeliskGameObjects.stream()
                .map(GameObject::getId)
                .collect(Collectors.toSet());
        if (!snapshotIds.equals(activeGuardianIds)) handleObeliskUpdate();
        if (currentRegionId != StaticConstant.MINIGAME_REGION_ID) return;
        WorldPoint current = client.getLocalPlayer().getWorldLocation();
        if (minigameState == MinigameState.CLOSING || minigameState == MinigameState.CLOSED) return;
        if (renderSafetyEvent == null || !renderSafetyEvent.isWidgetVisible()) return;
        if (worldPoint == null || !worldPoint.equals(current)) {
            this.updateObeliskEfficiencyWeight();
        }
        this.worldPoint = current;
    }

    private void handleObeliskUpdate() {
        if (activeObeliskGameObjects.size() != 2) {
            this.catalytic = null;
            this.elemental = null;
            this.updateObeliskEfficiencyWeight();
        } else {
            Obelisk first = Obelisk.getObeliskByGameObjectId(activeObeliskGameObjects.get(0).getId());
            Obelisk second = Obelisk.getObeliskByGameObjectId(activeObeliskGameObjects.get(1).getId());
            if (first == null || second == null) return;
            boolean isFirstCatalytic = first.getTypeAssociation() == TypeAssociation.CATALYTIC;
            this.bus.post(
                    new ObeliskUpdateEvent(
                            client.getTickCount(),
                            isFirstCatalytic ? second.getIndexId() : first.getIndexId(),
                            isFirstCatalytic ? first.getIndexId() : second.getIndexId()
                    )
            );
        }
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
        this.catalytic = Obelisk.getObeliskByRuneIndexId(TypeAssociation.CATALYTIC, event.getCatalyticRuneEnumIndex());
        this.elemental = Obelisk.getObeliskByRuneIndexId(TypeAssociation.ELEMENTAL, event.getElementalRuneEnumIndex());
        this.updateObeliskEfficiencyWeight();
    }

    @Subscribe
    public void onEssenceAmountUpdateEvent(EssenceAmountUpdateEvent event) {
        if (currentRegionId != StaticConstant.MINIGAME_REGION_ID) return;
        this.updateObeliskEfficiencyWeight();
    }

    @Subscribe
    public void onRewardPointUpdateEvent(RewardPointUpdateEvent rewardPointUpdateEvent) {
        this.rewardPointUpdateEvent = rewardPointUpdateEvent;
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
    }

    @Subscribe
    public void onPointResetEvent(PointResetEvent event) {
        this.elementalPoints = 0;
        this.catalyticPoints = 0;
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
            RewardPointUpdateEvent reference = rewardPointUpdateEvent == null ?
                    new RewardPointUpdateEvent(0, 0) :
                    rewardPointUpdateEvent;
            PointStatus pointStatus = new PointStatus(
                    elementalPoints,
                    catalyticPoints,
                    reference.getTotalElementalRewardPoints(),
                    reference.getTotalCatalyticRewardPoints()
            );
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
                                    pointStatus,
                                    obeliskGameObjects.get(obelisk.getGameObjectId())
                            )
                    )
                    .filter(analysis ->
                            client.getBoostedSkillLevel(Skill.RUNECRAFT) >= analysis.getRuneCraftInfo().getLevelRequired()
                    )
                    .filter(analysis ->
                            analysis.getObelisk().getRequiredQuest() == null ||
                                    analysis.getObelisk().getRequiredQuest().getState(client) == QuestState.FINISHED
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
