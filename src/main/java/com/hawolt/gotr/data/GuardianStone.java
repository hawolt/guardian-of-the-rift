package com.hawolt.gotr.data;

import lombok.Getter;

@Getter
public enum GuardianStone {
    CATALYTIC(TypeAssociation.CATALYTIC, 2),
    ELEMENTAL(TypeAssociation.ELEMENTAL, 2),
    POLYELEMENTAL(TypeAssociation.ELEMENTAL, 3),
    POLYCATALYTIC(TypeAssociation.CATALYTIC, 3);
    private final TypeAssociation rewardPointType;
    private final int pointMultiplier;

    GuardianStone(TypeAssociation rewardPointType, int pointMultiplier) {
        this.pointMultiplier = pointMultiplier;
        this.rewardPointType = rewardPointType;
    }
}
