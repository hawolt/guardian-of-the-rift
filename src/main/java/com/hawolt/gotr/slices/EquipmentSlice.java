package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.data.StaticConstant;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.eventbus.Subscribe;

import java.util.Arrays;

public class EquipmentSlice extends AbstractPluginSlice {

    @Getter(AccessLevel.PUBLIC)
    private boolean isBindingNecklaceEquipped;

    protected void startUp() {
        if (client.getGameState() != GameState.LOGGED_IN) return;
        this.handle(client.getItemContainer(InventoryID.WORN));
    }

    @Override
    protected void shutDown() {

    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (client.getGameState() != GameState.LOGGED_IN) return;
        if (event.getContainerId() != InventoryID.WORN) return;
        this.handle(event.getItemContainer());
    }

    private void handle(ItemContainer container) {
        if (container == null) return;
        this.isBindingNecklaceEquipped = Arrays.stream(container.getItems())
                .anyMatch(item -> item.getId() == StaticConstant.BINDING_NECKLACE_ITEM_ID);
    }
}
