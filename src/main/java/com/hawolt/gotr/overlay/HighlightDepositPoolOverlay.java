package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.StaticConstant;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class HighlightDepositPoolOverlay extends AbstractMinigameRenderer {

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Getter(AccessLevel.NONE)
    private GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private GameObject depositPool;

    @Inject
    public HighlightDepositPoolOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        GameObject gameObject = event.getGameObject();
        if (gameObject.getId() != StaticConstant.MINIGAME_DEPOSIT_POOL_ID) return;
        this.depositPool = gameObject;
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        GameObject gameObject = event.getGameObject();
        if (gameObject.getId() != StaticConstant.MINIGAME_DEPOSIT_POOL_ID) return;
        this.depositPool = null;
    }

    @Override
    public void renderWhenSecure(Graphics2D graphics2D) {
        if (depositPool == null) return;
        if (!getRenderSafetyEvent().isMinigameWidgetVisible()) return;
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        if (!config.isDepositPoolOutlineEnabled()) return;
        try {
            Shape hull = depositPool.getConvexHull();
            if (hull == null) return;
            this.modelOutlineRenderer.drawOutline(
                    depositPool,
                    config.depositPoolOutlineWidth(),
                    config.depositPoolOutlineColor(),
                    config.depositPoolOutlineFeatherDistance()
            );
        } catch (NullPointerException e) {
            // ignore
        }
    }
}
