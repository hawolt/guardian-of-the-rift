package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.MinigameState;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.minigame.impl.MinigameStateEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.NPC;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

public class HighlightGreatGuardianOverlay extends AbstractMinigameRenderer {

    @Inject
    private ModelOutlineRenderer modelOutlineRenderer;

    @Getter(AccessLevel.NONE)
    private GuardianOfTheRiftOptimizerPlugin plugin;

    @Getter(AccessLevel.NONE)
    private MinigameState minigameState;

    @Getter(AccessLevel.NONE)
    private NPC greatGuardian;

    @Inject
    public HighlightGreatGuardianOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        this.greatGuardian = null;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (npc.getId() != StaticConstant.MINIGAME_GREAT_GUARDIAN_NPC_ID) return;
        this.greatGuardian = npc;
    }

    @Subscribe
    public void onMinigameStateEvent(MinigameStateEvent event) {
        this.minigameState = event.getCurrentMinigameState();
    }

    @Override
    public void renderWhenSecure(Graphics2D graphics2D) {
        if (greatGuardian == null) return;
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        if (!config.isTheGreatGuardianOutlineEnabled()) return;
        if (!plugin.getInventoryEssenceSlice().isGuardianStoneAvailable()) return;
        if (minigameState == MinigameState.CLOSING) return;
        this.modelOutlineRenderer.drawOutline(
                greatGuardian,
                config.theGreatGuardianOutlineWidth(),
                config.theGreatGuardianOutlineColor(),
                config.theGreatGuardianOutlineFeatherDistance()
        );
    }
}
