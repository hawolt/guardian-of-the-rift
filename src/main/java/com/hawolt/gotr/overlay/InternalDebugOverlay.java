package com.hawolt.gotr.overlay;

import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerPlugin;
import com.hawolt.gotr.data.ObeliskType;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.ObeliskAnalysisEvent;
import com.hawolt.gotr.utility.ObeliskAnalysis;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalDebugOverlay extends AbstractMinigameRenderer {

    @Inject
    private EventBus bus;

    @Getter(AccessLevel.NONE)
    private final Map<ObeliskType, ObeliskAnalysis[]> map;

    private final GuardianOfTheRiftOptimizerPlugin plugin;

    @Override
    public void startup() {
        this.bus.register(this);
    }

    @Override
    public void shutdown() {
        this.bus.unregister(this);
    }

    @Inject
    public InternalDebugOverlay(GuardianOfTheRiftOptimizerPlugin plugin) {
        this.plugin = plugin;
        this.map = new HashMap<>();
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Subscribe
    public void onObeliskAnalysisEvent(ObeliskAnalysisEvent event) {
        this.map.put(event.getObeliskType(), event.getObeliskAnalysis());
    }


    @Override
    public void renderWhenSecure(Graphics2D graphics2D) {
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        ObeliskAnalysis[] optimalObelisks = map.get(ObeliskType.OPTIMAL);
        ObeliskAnalysis[] secondaryObelisks = map.get(ObeliskType.SECONDARY);
        for (ObeliskAnalysis optimal : optimalObelisks) {
            handleTilePath(
                    optimal,
                    graphics2D,
                    config.enableOptimalPathDebugging(),
                    config.tileOutlineColor()
            );
        }
        for (ObeliskAnalysis secondary : secondaryObelisks) {
            handleTilePath(
                    secondary,
                    graphics2D,
                    config.enableSecondaryPathDebugging(),
                    config.tileOutlineColor()
            );
        }
    }

    private void handleTilePath(ObeliskAnalysis analysis, Graphics2D graphics2D, boolean isEnabled, Color outline) {
        if (analysis == null || !isEnabled) return;
        Client client = plugin.getClient();
        int currentRegionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        if (currentRegionId != StaticConstant.MINIGAME_REGION_ID) return;
        List<WorldPoint> pathToGuardian = analysis.getPathToGuardian();
        for (WorldPoint worldPoint : pathToGuardian) {
            renderTile(
                    client,
                    graphics2D,
                    worldPoint,
                    outline
            );
        }
    }

    private void renderTile(
            Client client,
            Graphics2D graphics,
            WorldPoint worldPoint,
            Color outlineColor
    ) {
        LocalPoint localPoint = LocalPoint.fromWorld(client.getLocalPlayer().getWorldView(), worldPoint);
        if (localPoint == null) return;
        Polygon polygon = Perspective.getCanvasTilePoly(plugin.getClient(), localPoint);
        if (polygon == null) return;
        OverlayUtil.renderPolygon(graphics, polygon, outlineColor);
    }
}
