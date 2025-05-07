package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.events.RenderSafetyEvent;
import com.hawolt.gotr.events.minigame.impl.GuardianDespawnEvent;
import com.hawolt.gotr.events.minigame.impl.GuardianSpawnEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.PostMenuSort;
import net.runelite.client.eventbus.Subscribe;

import java.util.Arrays;

public class MenuOptionSlice extends AbstractPluginSlice {

    @Getter(AccessLevel.NONE)
    private int currentGuardianAmount;

    @Getter(AccessLevel.NONE)
    private RenderSafetyEvent renderSafetyEvent;

    @Override
    protected void startUp() {

    }

    @Override
    protected void shutDown() {

    }

    @Subscribe
    public void onRenderSafetyEvent(RenderSafetyEvent renderSafetyEvent) {
        this.renderSafetyEvent = renderSafetyEvent;
    }

    @Subscribe
    public void onGuardianDespawnEvent(GuardianDespawnEvent event) {
        this.currentGuardianAmount = event.getCurrentAmountOfGuardians();
    }

    @Subscribe
    public void onGuardianSpawnEvent(GuardianSpawnEvent event) {
        this.currentGuardianAmount = event.getCurrentAmountOfGuardians();
    }

    @Subscribe
    public void onPostMenuSort(PostMenuSort event) {
        if (renderSafetyEvent == null) return;
        if (!renderSafetyEvent.isWidgetAvailable()) return;
        if (client.isMenuOpen()) return;
        InventorySlice inventorySlice = plugin.getInventoryEssenceSlice();
        GuardianOfTheRiftOptimizerConfig config = plugin.getConfig();
        Menu menu = client.getMenu();
        MenuEntry[] entries = menu.getMenuEntries();
        entries = handleGuardianAssembleNoMaterial(config, inventorySlice, entries);
        entries = handleGroundItemPlaceCell(config, inventorySlice, entries);
        entries = handleGuardianPowerUp(config, inventorySlice, entries);
        entries = handleGuardianAssembleAllActive(config, entries);
        entries = handleDepositPoolDepositOption(config, entries);
        entries = handleUseOptionOnPlayer(config, entries);
        entries = handleApprenticeTalkTo(config, entries);
        entries = handleRuneUseOption(config, entries);
        menu.setMenuEntries(entries);
    }

    private MenuEntry[] handleRuneUseOption(GuardianOfTheRiftOptimizerConfig config, MenuEntry[] entries) {
        if (!config.isHideRuneUseInMinigame()) return entries;
        MenuEntry[] adjusted = Arrays.stream(entries)
                .filter(
                        entry ->
                                !entry.getTarget().contains(" rune") ||
                                        !entry.getOption().equals("Use")
                ).toArray(MenuEntry[]::new);
        Arrays.stream(adjusted).filter(entry -> entry.getOption().equals("Drop")).forEach(option -> option.setType(MenuAction.CC_OP));
        return adjusted;
    }

    private MenuEntry[] handleUseOptionOnPlayer(
            GuardianOfTheRiftOptimizerConfig config,
            MenuEntry[] entries
    ) {
        return config.isHideUseOptionOnPlayer() ?
                Arrays.stream(entries)
                        .filter(
                                entry ->
                                        !entry.getTarget().contains(" rune") ||
                                                entry.getPlayer() == null ||
                                                !entry.getOption().equals("Use")
                        ).toArray(MenuEntry[]::new) :
                entries;
    }

    private MenuEntry[] handleDepositPoolDepositOption(
            GuardianOfTheRiftOptimizerConfig config,
            MenuEntry[] entries
    ) {
        return config.isHideDepositPoolDepositOption() ?
                Arrays.stream(entries)
                        .filter(
                                entry ->
                                        entry.getTarget().contains(" rune") ||
                                                (!entry.getOption().contains("Deposit-items") &&
                                                        !entry.getOption().contains("Deposit-runes") &&
                                                        !entry.getTarget().contains("Deposit Pool"))
                        ).toArray(MenuEntry[]::new) :
                entries;
    }

    private MenuEntry[] handleGuardianAssembleNoMaterial(
            GuardianOfTheRiftOptimizerConfig config,
            InventorySlice inventorySlice,
            MenuEntry[] entries
    ) {
        return config.isHideGuardianAssembleNoMaterial() && !inventorySlice.isChiselAvailable() ?
                Arrays.stream(entries)
                        .filter(
                                entry ->
                                        !entry.getOption().contains("Assemble") &&
                                                !entry.getTarget().contains("Essence Pile")
                        ).toArray(MenuEntry[]::new) :
                entries;
    }

    private MenuEntry[] handleGuardianAssembleAllActive(
            GuardianOfTheRiftOptimizerConfig config,
            MenuEntry[] entries
    ) {
        return config.isHideGuardianAssembleAllActive() && currentGuardianAmount == 10 ?
                Arrays.stream(entries)
                        .filter(
                                entry ->
                                        !entry.getOption().contains("Assemble") &&
                                                !entry.getTarget().contains("Essence Pile")
                        ).toArray(MenuEntry[]::new) :
                entries;
    }

    private MenuEntry[] handleGroundItemPlaceCell(
            GuardianOfTheRiftOptimizerConfig config,
            InventorySlice inventorySlice,
            MenuEntry[] entries
    ) {
        return config.isHideGroundItemPlaceCell() && !inventorySlice.isChargedCellAvailable() ?
                Arrays.stream(entries)
                        .filter(
                                entry -> !entry.getOption().contains("Place-cell")
                        ).toArray(MenuEntry[]::new) :
                entries;
    }

    private MenuEntry[] handleGuardianPowerUp(
            GuardianOfTheRiftOptimizerConfig config,
            InventorySlice inventorySlice,
            MenuEntry[] entries
    ) {
        return config.isHideGuardianPowerUp() && !inventorySlice.isGuardianStoneAvailable() ?
                Arrays.stream(entries)
                        .filter(
                                entry -> !entry.getOption().contains("Power-up") ||
                                        !entry.getTarget().contains("Great Guardian")
                        ).toArray(MenuEntry[]::new) :
                entries;
    }

    private MenuEntry[] handleApprenticeTalkTo(GuardianOfTheRiftOptimizerConfig config, MenuEntry[] entries) {
        return config.isHideApprenticeTalkTo() ?
                Arrays.stream(entries)
                        .filter(
                                entry ->
                                        !entry.getOption().contains("Talk-to") &&
                                                (
                                                        !entry.getTarget().contains("Apprentice Cordelia") ||
                                                                !entry.getTarget().contains("Apprentice Tamara")
                                                )
                        ).toArray(MenuEntry[]::new) :
                entries;
    }
}
