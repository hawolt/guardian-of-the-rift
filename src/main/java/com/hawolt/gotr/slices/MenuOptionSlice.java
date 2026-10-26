package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.GuardianOfTheRiftOptimizerConfig;
import com.hawolt.gotr.data.Obelisk;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        entries = handleInactiveGuardianOptions(config, entries);
        menu.setMenuEntries(entries);
    }

    // menu entries are ordered by priority: the last entry is the left click option
    private MenuEntry[] handleInactiveGuardianOptions(GuardianOfTheRiftOptimizerConfig config, MenuEntry[] entries) {
        if (!config.isDeprioritizeInactiveGuardians()) return entries;
        int walkHereIndex = findWalkHereIndex(entries);
        boolean isWalkHereAvailable = walkHereIndex != -1;
        if (!isWalkHereAvailable) return entries;
        List<MenuEntry> entriesToDemote = findGuardianEntriesToDemote(entries, walkHereIndex);
        if (entriesToDemote.isEmpty()) return entries;
        return demoteBelowWalkHere(entries, entriesToDemote, walkHereIndex);
    }

    private int findWalkHereIndex(MenuEntry[] entries) {
        return IntStream.range(0, entries.length)
                .filter(index -> entries[index].getType() == MenuAction.WALK)
                .findFirst()
                .orElse(-1);
    }

    // demote every option of the guardian, not just Enter: Toggle-talisman also
    // sorts above Walk here and would otherwise become the new left click
    private List<MenuEntry> findGuardianEntriesToDemote(MenuEntry[] entries, int walkHereIndex) {
        return IntStream.range(walkHereIndex + 1, entries.length)
                .mapToObj(index -> entries[index])
                .filter(this::isOptionOnUnusableGuardian)
                .collect(Collectors.toList());
    }

    private MenuEntry[] demoteBelowWalkHere(MenuEntry[] entries, List<MenuEntry> entriesToDemote, int walkHereIndex) {
        List<MenuEntry> reordered = new ArrayList<>(Arrays.asList(entries));
        reordered.removeAll(entriesToDemote);
        reordered.addAll(walkHereIndex, entriesToDemote);
        return reordered.toArray(MenuEntry[]::new);
    }

    private boolean isOptionOnUnusableGuardian(MenuEntry entry) {
        if (!isGameObjectAction(entry.getType())) return false;
        // for game object actions the identifier is the game object id
        Obelisk guardian = Obelisk.getObeliskByGameObjectId(entry.getIdentifier());
        boolean isGuardian = guardian != null;
        if (!isGuardian) return false;
        boolean isGuardianActive = plugin.getObeliskSlice().isObeliskActive(guardian);
        if (isGuardianActive) return false;
        return !isMatchingTalismanAvailable(guardian);
    }

    private boolean isMatchingTalismanAvailable(Obelisk guardian) {
        return plugin.getInventoryEssenceSlice().getAvailableTalismanList()
                .stream()
                .anyMatch(talisman -> talisman.getId() == guardian.getTalismanItemId());
    }

    private boolean isGameObjectAction(MenuAction type) {
        switch (type) {
            case GAME_OBJECT_FIRST_OPTION:
            case GAME_OBJECT_SECOND_OPTION:
            case GAME_OBJECT_THIRD_OPTION:
            case GAME_OBJECT_FOURTH_OPTION:
            case GAME_OBJECT_FIFTH_OPTION:
                return true;
            default:
                return false;
        }
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
                                        !entry.getOption().contains("Talk-to") ||
                                                (
                                                        !entry.getTarget().contains("Apprentice Cordelia") &&
                                                                !entry.getTarget().contains("Apprentice Tamara")
                                                )
                        ).toArray(MenuEntry[]::new) :
                entries;
    }
}
