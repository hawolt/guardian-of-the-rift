package com.hawolt.gotr.data;

import lombok.Getter;

@Getter
public enum EssencePile {
    ELEMENTAL_ESSENCE_PILE(43722, TypeAssociation.ELEMENTAL),
    CATALYTIC_ESSENCE_PILE(43723, TypeAssociation.CATALYTIC);
    private final TypeAssociation typeAssociation;
    private final int gameObjectId;

    EssencePile(int gameObjectId, TypeAssociation typeAssociation) {
        this.typeAssociation = typeAssociation;
        this.gameObjectId = gameObjectId;
    }
}
