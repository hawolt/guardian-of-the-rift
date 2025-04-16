package com.hawolt.gotr.data;

import lombok.Getter;
import net.runelite.api.gameval.ItemID;

@Getter
public enum Obelisk {
    AIR(43701, 1, 4, ItemID.GOTR_PORTAL_TALISMAN_AIR, TypeAssociation.ELEMENTAL, ChargeableCellType.WEAK),
    MIND(43705, 1, 12, ItemID.GOTR_PORTAL_TALISMAN_MIND, TypeAssociation.CATALYTIC, ChargeableCellType.WEAK),
    WATER(43702, 2, 9, ItemID.GOTR_PORTAL_TALISMAN_WATER, TypeAssociation.ELEMENTAL, ChargeableCellType.MEDIUM),
    EARTH(43703, 3, 11, ItemID.GOTR_PORTAL_TALISMAN_EARTH, TypeAssociation.ELEMENTAL, ChargeableCellType.STRONG),
    FIRE(43704, 4, 11, ItemID.GOTR_PORTAL_TALISMAN_FIRE, TypeAssociation.ELEMENTAL, ChargeableCellType.OVERCHARGED),
    BODY(43709, 2, 5, ItemID.GOTR_PORTAL_TALISMAN_BODY, TypeAssociation.CATALYTIC, ChargeableCellType.WEAK),
    COSMIC(43710, 3, 19, ItemID.GOTR_PORTAL_TALISMAN_COSMIC, TypeAssociation.CATALYTIC, ChargeableCellType.MEDIUM),
    CHAOS(43706, 4, 10, ItemID.GOTR_PORTAL_TALISMAN_CHAOS, TypeAssociation.CATALYTIC, ChargeableCellType.MEDIUM),
    NATURE(43711, 5, 5, ItemID.GOTR_PORTAL_TALISMAN_NATURE, TypeAssociation.CATALYTIC, ChargeableCellType.STRONG),
    LAW(43712, 6, 13, ItemID.GOTR_PORTAL_TALISMAN_LAW, TypeAssociation.CATALYTIC, ChargeableCellType.STRONG),
    DEATH(43707, 7, 6, ItemID.GOTR_PORTAL_TALISMAN_DEATH, TypeAssociation.CATALYTIC, ChargeableCellType.OVERCHARGED),
    BLOOD(43708, 8, 6, ItemID.GOTR_PORTAL_TALISMAN_BLOOD, TypeAssociation.CATALYTIC, ChargeableCellType.OVERCHARGED);
    private final int gameObjectId, indexId, tileDistance, talismanItemId;
    private final TypeAssociation typeAssociation;
    private final ChargeableCellType cellType;

    Obelisk(int gameObjectId, int indexId, int tileDistance, int talismanItemId, TypeAssociation typeAssociation, ChargeableCellType cellType) {
        this.typeAssociation = typeAssociation;
        this.talismanItemId = talismanItemId;
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

    public static Obelisk getObelisk(TypeAssociation association, int indexId) {
        for (Obelisk obelisk : VALUES) {
            if (obelisk.typeAssociation != association) continue;
            if (obelisk.indexId == indexId) return obelisk;
        }
        return null;
    }
}
