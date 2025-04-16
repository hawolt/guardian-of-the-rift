package com.hawolt.gotr.data;

import lombok.Getter;
import net.runelite.api.gameval.ItemID;

@Getter
public enum ChargeableCellType {
    WEAK(ItemID.GOTR_CELL_TIER1, 30),
    MEDIUM(ItemID.GOTR_CELL_TIER2, 100),
    STRONG(ItemID.GOTR_CELL_TIER3, 180),
    OVERCHARGED(ItemID.GOTR_CELL_TIER4, 250);

    private final int itemId, experienceReward;

    ChargeableCellType(int itemId, int experienceReward) {
        this.experienceReward = experienceReward;
        this.itemId = itemId;
    }

    public static ChargeableCellType[] VALUES = ChargeableCellType.values();

    public static ChargeableCellType byItemId(int itemId) {
        for (ChargeableCellType type : VALUES) {
            if (type.itemId == itemId) return type;
        }
        return null;
    }
}
