package com.hawolt.gotr.slices;

import com.hawolt.gotr.AbstractPluginSlice;
import com.hawolt.gotr.data.Pouch;
import com.hawolt.gotr.data.StaticConstant;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.Subscribe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PouchEssenceSlice extends AbstractPluginSlice {

    @Getter(AccessLevel.NONE)
    private final Object lock = new Object();

    @Getter(AccessLevel.NONE)
    private final Map<Pouch, Boolean> pouchInUseMapping = new HashMap<>();

    @Override
    public boolean isClientThreadRequiredOnStartup() {
        return true;
    }

    @Override
    protected void startUp() {
        if (client.getGameState() != GameState.LOGGED_IN) return;
        this.handle(client.getItemContainer(StaticConstant.INVENTORY_CONTAINER_ID));
    }

    @Override
    protected void shutDown() {

    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (client.getGameState() != GameState.LOGGED_IN) return;
        if (event.getContainerId() != StaticConstant.INVENTORY_CONTAINER_ID) return;
        this.handle(event.getItemContainer());
    }

    private void handle(ItemContainer container) {
        if (container == null) return;
        Item[] inventory = container.getItems();
        synchronized (lock) {
            for (Pouch pouch : Pouch.VALUES) {
                boolean isPouchInUse = Arrays.stream(inventory)
                        .anyMatch(item -> pouch.isOfType(item.getId()));
                this.pouchInUseMapping.put(pouch, isPouchInUse);
            }
        }
    }

    public int getAvailableEssenceInPouches() {
        synchronized (lock) {
            return pouchInUseMapping.entrySet()
                    .stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .mapToInt(pouch -> pouch.getStoredEssenceAmount(plugin.getClient()))
                    .sum();
        }
    }

}
