package com.hawolt.gotr.data;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

@Getter
public enum Pouch {
    SMALL(VarbitID.SMALL_ESSENCE_POUCH, ItemID.RCU_POUCH_SMALL),
    MEDIUM(VarbitID.MEDIUM_ESSENCE_POUCH, ItemID.RCU_POUCH_MEDIUM, ItemID.RCU_POUCH_MEDIUM_DEGRADE),
    LARGE(VarbitID.LARGE_ESSENCE_POUCH, ItemID.RCU_POUCH_LARGE, ItemID.RCU_POUCH_LARGE_DEGRADE),
    GIANT(VarbitID.GIANT_ESSENCE_POUCH, ItemID.RCU_POUCH_GIANT, ItemID.RCU_POUCH_GIANT_DEGRADE),
    COLOSSAL(VarbitID.COLOSSAL_ESSENCE_POUCH, ItemID.RCU_POUCH_COLOSSAL, ItemID.RCU_POUCH_COLOSSAL_DEGRADE);

    private final int varbitId;
    private final int[] itemIds;

    Pouch(int varbitId, int... itemIds) {
        this.varbitId = varbitId;
        this.itemIds = itemIds;
    }

    public boolean isOfType(int itemId) {
        for (int id : itemIds) {
            if (id != itemId) continue;
            return true;
        }
        return false;
    }

    public int getStoredEssenceAmount(Client client) {
        return client.getVarbitValue(varbitId);
    }

    public static final Pouch[] VALUES = Pouch.values();
}