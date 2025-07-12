package com.hawolt.gotr.data;

import lombok.Getter;
import net.runelite.api.Quest;
import net.runelite.api.gameval.ItemID;

@Getter
public enum Obelisk {
    AIR(43701, 1, 4, ItemID.GOTR_PORTAL_TALISMAN_AIR, TypeAssociation.ELEMENTAL, ChargeableCellType.WEAK, null),
    MIND(43705, 1, 12, ItemID.GOTR_PORTAL_TALISMAN_MIND, TypeAssociation.CATALYTIC, ChargeableCellType.WEAK, null),
    WATER(43702, 2, 9, ItemID.GOTR_PORTAL_TALISMAN_WATER, TypeAssociation.ELEMENTAL, ChargeableCellType.MEDIUM, null),
    EARTH(43703, 3, 11, ItemID.GOTR_PORTAL_TALISMAN_EARTH, TypeAssociation.ELEMENTAL, ChargeableCellType.STRONG, null),
    FIRE(43704, 4, 11, ItemID.GOTR_PORTAL_TALISMAN_FIRE, TypeAssociation.ELEMENTAL, ChargeableCellType.OVERCHARGED, null),
    BODY(43709, 2, 5, ItemID.GOTR_PORTAL_TALISMAN_BODY, TypeAssociation.CATALYTIC, ChargeableCellType.WEAK, null),
    COSMIC(43710, 3, 19, ItemID.GOTR_PORTAL_TALISMAN_COSMIC, TypeAssociation.CATALYTIC, ChargeableCellType.MEDIUM, Quest.LOST_CITY),
    CHAOS(43706, 4, 10, ItemID.GOTR_PORTAL_TALISMAN_CHAOS, TypeAssociation.CATALYTIC, ChargeableCellType.MEDIUM, null),
    NATURE(43711, 5, 5, ItemID.GOTR_PORTAL_TALISMAN_NATURE, TypeAssociation.CATALYTIC, ChargeableCellType.STRONG, null),
    LAW(43712, 6, 13, ItemID.GOTR_PORTAL_TALISMAN_LAW, TypeAssociation.CATALYTIC, ChargeableCellType.STRONG, Quest.TROLL_STRONGHOLD),
    DEATH(43707, 7, 6, ItemID.GOTR_PORTAL_TALISMAN_DEATH, TypeAssociation.CATALYTIC, ChargeableCellType.OVERCHARGED, Quest.MOURNINGS_END_PART_II),
    BLOOD(43708, 8, 6, ItemID.GOTR_PORTAL_TALISMAN_BLOOD, TypeAssociation.CATALYTIC, ChargeableCellType.OVERCHARGED, Quest.SINS_OF_THE_FATHER);
    private final int gameObjectId, indexId, tileDistance, talismanItemId;
    private final TypeAssociation typeAssociation;
    private final ChargeableCellType cellType;
    private final Quest requiredQuest;

    Obelisk(
            int gameObjectId,
            int indexId,
            int tileDistance,
            int talismanItemId,
            TypeAssociation typeAssociation,
            ChargeableCellType cellType,
            Quest requiredQuest
    ) {
        this.typeAssociation = typeAssociation;
        this.talismanItemId = talismanItemId;
        this.requiredQuest = requiredQuest;
        this.tileDistance = tileDistance;
        this.gameObjectId = gameObjectId;
        this.cellType = cellType;
        this.indexId = indexId;
    }

    public static final Obelisk[] VALUES = Obelisk.values();

    public static Obelisk getByTalismanItemId(int talismanItemId) {
        for (Obelisk obelisk : VALUES) {
            if (obelisk.talismanItemId == talismanItemId) return obelisk;
        }
        return null;
    }

    public static Obelisk getObeliskByRuneIndexId(TypeAssociation association, int indexId) {
        for (Obelisk obelisk : VALUES) {
            if (obelisk.typeAssociation != association) continue;
            if (obelisk.indexId == indexId) return obelisk;
        }
        return null;
    }

    public static Obelisk getObeliskByGameObjectId(int gameObjectId) {
        for (Obelisk obelisk : VALUES) {
            if (obelisk.gameObjectId == gameObjectId) return obelisk;
        }
        return null;
    }
}
