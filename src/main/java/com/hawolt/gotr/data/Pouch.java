package com.hawolt.gotr.data;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

@Getter
public enum Pouch {
    SMALL(ItemID.RCU_POUCH_SMALL, VarbitID.SMALL_ESSENCE_POUCH),
    MEDIUM(ItemID.RCU_POUCH_SMALL, VarbitID.MEDIUM_ESSENCE_POUCH),
    LARGE(ItemID.RCU_POUCH_SMALL, VarbitID.LARGE_ESSENCE_POUCH),
    GIANT(ItemID.RCU_POUCH_SMALL, VarbitID.GIANT_ESSENCE_POUCH),
    COLOSSAL(ItemID.RCU_POUCH_SMALL, VarbitID.COLOSSAL_ESSENCE_POUCH);

    private final int itemId, varbitId;

    Pouch(int itemId, int varbitId) {
        this.varbitId = varbitId;
        this.itemId = itemId;
    }

    public int getStoredEssenceAmount(Client client) {
        return client.getVarbitValue(varbitId);
    }

    public static final Pouch[] VALUES = Pouch.values();
}