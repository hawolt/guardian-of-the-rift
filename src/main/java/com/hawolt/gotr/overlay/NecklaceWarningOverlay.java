package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.RuneCraftInfo;
import com.hawolt.gotr.events.RenderSafetyEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class NecklaceWarningOverlay extends AbstractMinigameRenderer {

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Getter(AccessLevel.NONE)
    private GameObject lastSeenAltar;

    @Getter(AccessLevel.NONE)
    private GuardianOfTheRiftOptimizerPlugin plugin;

    @Inject
    public NecklaceWarningOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        RenderSafetyEvent renderSafetyEvent = getRenderSafetyEvent();
        if (renderSafetyEvent == null) return null;
        if (!renderSafetyEvent.isWidgetAvailable() || renderSafetyEvent.isVolatileState()) return null;
        this.renderWhenSecure(graphics2D);
        return null;
    }

    private ObjectComposition getObjectComposition(int id) {
        ObjectComposition objectComposition = plugin.getClient().getObjectDefinition(id);
        return objectComposition.getImpostorIds() == null ? objectComposition : objectComposition.getImpostor();
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        if (gameObject == null) return;
        ObjectComposition composition = getObjectComposition(gameObject.getId());
        if (composition == null) return;
        if (!"Altar".equals(composition.getName())) return;
        RuneCraftInfo runeCraftInfo = RuneCraftInfo.find(
                plugin.getConfig(),
                gameObject.getId()
        );
        if (runeCraftInfo == null || !runeCraftInfo.isCombinationRune()) return;
        this.lastSeenAltar = gameObject;
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject gameObject = event.getGameObject();
        if (gameObject == null || lastSeenAltar == null) return;
        if (gameObject.getId() != lastSeenAltar.getId()) return;
        this.lastSeenAltar = null;
    }

    @Override
    public void renderWhenSecure(Graphics2D graphics2D) {
        if (lastSeenAltar == null) return;
        if (plugin.getEquipmentSlice().isBindingNecklaceEquipped()) return;
        try {
            Shape hull = lastSeenAltar.getConvexHull();
            if (hull == null) return;
            this.modelOutlineRenderer.drawOutline(
                    lastSeenAltar,
                    5,
                    Color.RED,
                    5
            );
        } catch (NullPointerException e) {
            // ignore
        }
    }

}
