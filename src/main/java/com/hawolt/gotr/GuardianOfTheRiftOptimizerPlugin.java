package com.hawolt.gotr;

import com.google.inject.Provides;
import com.hawolt.gotr.overlay.*;
import com.hawolt.gotr.slices.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayPanel;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@PluginDescriptor(
        name = "Guardians of the Rift Optimizer",
        description = "Various utilities to improve and optimize the Guardians of the Rift minigame",
        tags = {"minigame", "overlay", "guardians of the rift", "gotr", "minmax", "ehp"}
)
public class GuardianOfTheRiftOptimizerPlugin extends Plugin {

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private GuardianOfTheRiftOptimizerConfig config;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private EventBus bus;

    // PLUGIN SLICES

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private InventorySlice inventoryEssenceSlice;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private BindingNecklaceSlice bindingNecklaceSlice;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private PouchEssenceSlice pouchEssenceSlice;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private PathfinderSlice pathfinderSlice;

    @Inject
    @Getter(AccessLevel.PUBLIC)
    private EquipmentSlice equipmentSlice;

    @Inject
    private BindingNecklaceIndicatorOverlay bindingNecklaceIndicatorOverlay;

    @Inject
    private HighlightGreatGuardianOverlay highlightGreatGuardianOverlay;

    @Inject
    private HighlightCellTableOverlay highlightCellTableOverlay;

    @Inject
    private HighlightCellTileOverlay highlightCellTileOverlay;

    @Inject
    private HighlightDepositPoolOverlay depositPoolOverlay;

    @Inject
    private HighlightPortalOverlay highlightPortalOverlay;

    @Inject
    private TickTimestampSlice tickTimestampSlice;

    @Inject
    private RenderSafetySlice renderSafetySlice;

    @Inject
    private StartTimeOverlay startTimeOverlay;

    @Inject
    private MenuOptionSlice menuOptionSlice;

    @Inject
    private MinigameSlice minigameSlice;

    @Inject
    private ObeliskSlice obeliskSlice;

    @Inject
    private MuteSlice muteSlice;

    // OVERLAY

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PortalIndicatorOverlay portalIndicatorOverlay;

    @Inject
    private HighlightObeliskOverlay highlightGuardianOverlay;

    @Inject
    private InternalDebugOverlay debugOverlay;

    // PLUGIN LOGIC

    @Getter(AccessLevel.NONE)
    private final Set<Slice> slices = new HashSet<>();

    public void add(Slice... slices) {
        this.slices.addAll(Arrays.asList(slices));
    }

    @Override
    protected void startUp() throws Exception {
        this.add(
                bindingNecklaceIndicatorOverlay,
                highlightGreatGuardianOverlay,
                highlightCellTableOverlay,
                highlightGuardianOverlay,
                highlightCellTileOverlay,
                highlightPortalOverlay,
                portalIndicatorOverlay,
                inventoryEssenceSlice,
                bindingNecklaceSlice,
                depositPoolOverlay,
                tickTimestampSlice,
                renderSafetySlice,
                pouchEssenceSlice,
                startTimeOverlay,
                pathfinderSlice,
                menuOptionSlice,
                equipmentSlice,
                minigameSlice,
                obeliskSlice,
                debugOverlay,
                muteSlice
        );
        for (Slice slice : slices) {
            this.clientThread.invoke(slice::startup);
            if (slice instanceof AbstractMinigameRenderer) {
                AbstractMinigameRenderer renderer = (AbstractMinigameRenderer) slice;
                this.overlayManager.add(renderer);
            } else if (slice instanceof OverlayPanel) {
                OverlayPanel panel = (OverlayPanel) slice;
                this.overlayManager.add(panel);
            }
        }
    }

    @Override
    protected void shutDown() throws Exception {
        for (Slice slice : slices) {
            this.clientThread.invoke(slice::shutdown);
            if (slice instanceof AbstractMinigameRenderer) {
                AbstractMinigameRenderer renderer = (AbstractMinigameRenderer) slice;
                this.overlayManager.remove(renderer);
            } else if (slice instanceof OverlayPanel) {
                OverlayPanel panel = (OverlayPanel) slice;
                this.overlayManager.remove(panel);
            }
        }
    }

    @Provides
    GuardianOfTheRiftOptimizerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GuardianOfTheRiftOptimizerConfig.class);
    }
}
