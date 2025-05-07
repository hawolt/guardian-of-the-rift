package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.data.ChargeableCellType;
import com.hawolt.gotr.data.StaticConstant;
import com.hawolt.gotr.events.EssenceAmountUpdateEvent;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.eventbus.Subscribe;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InventorySlice extends AbstractPluginSlice {

    @Getter(AccessLevel.PUBLIC)
    private int
            essenceInInventory,
            emptyInventorySlots,
            availableUnchargedCells;

    @Getter(AccessLevel.PUBLIC)
    private boolean
            isGuardianStoneAvailable,
            isChargedCellAvailable,
            isChiselAvailable,
            isUnchargedCellAvailable;

    @Getter(AccessLevel.PUBLIC)
    private ChargeableCellType availableChargeableCellType;


    @Getter(AccessLevel.PUBLIC)
    private List<Item> availableTalismanList = Collections.emptyList();


    @Override
    public boolean isClientThreadRequiredOnStartup() {
        return true;
    }

    @Override
    protected void startUp() {
        this.essenceInInventory = 0;
        this.emptyInventorySlots = 0;
        if (client.getGameState() != GameState.LOGGED_IN) return;
        this.handle(client.getItemContainer(InventoryID.INV));
    }

    @Override
    protected void shutDown() {

    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (client.getGameState() != GameState.LOGGED_IN) return;
        if (event.getContainerId() != InventoryID.INV) return;
        this.handle(event.getItemContainer());
    }

    private void handle(ItemContainer container) {
        if (container == null) return;
        Item[] inventory = container.getItems();
        int essenceInInventory = 0;
        int emptyInventorySlots = 0;
        for (Item item : inventory) {
            if (item.getId() == StaticConstant.MINIGAME_GUARDIAN_STONE_ID) essenceInInventory++;
            else if (item.getId() == -1) emptyInventorySlots++;
        }
        if (this.essenceInInventory != essenceInInventory) {
            this.bus.post(new EssenceAmountUpdateEvent(this.essenceInInventory, essenceInInventory));
        }
        this.essenceInInventory = essenceInInventory;
        this.emptyInventorySlots = emptyInventorySlots;
        this.isGuardianStoneAvailable = Arrays.stream(inventory)
                .anyMatch(item -> StaticConstant.MINIGAME_GUARDIAN_STONE_IDS.contains(item.getId()));
        this.isUnchargedCellAvailable = Arrays.stream(inventory)
                .anyMatch(item -> item.getId() == StaticConstant.MINIGAME_UNCHARGED_CELL_ID);
        this.isChargedCellAvailable = Arrays.stream(inventory)
                .anyMatch(item -> StaticConstant.MINIGAME_CHARGED_CELL_IDS.contains(item.getId()));
        this.isChiselAvailable = Arrays.stream(inventory)
                .anyMatch(item -> item.getId() == ItemID.CHISEL);
        this.availableUnchargedCells = Arrays.stream(inventory)
                .filter(item -> item.getId() == StaticConstant.MINIGAME_UNCHARGED_CELL_ID)
                .mapToInt(Item::getQuantity)
                .sum();
        List<ChargeableCellType> chargeableCellTypeList = Arrays.stream(inventory)
                .filter(item -> StaticConstant.MINIGAME_CHARGED_CELL_IDS.contains(item.getId()))
                .map(item -> ChargeableCellType.byItemId(item.getId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        this.availableChargeableCellType = chargeableCellTypeList.isEmpty() ? null : chargeableCellTypeList.get(0);
        this.availableTalismanList = Arrays.stream(inventory)
                .filter(item -> StaticConstant.MINIGAME_TALISMAN_IDS.contains(item.getId()))
                .collect(Collectors.toList());
    }
}
