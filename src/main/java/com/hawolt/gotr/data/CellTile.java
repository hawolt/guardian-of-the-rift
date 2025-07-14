package com.hawolt.gotr.data;

import lombok.Getter;

@Getter
public enum CellTile {
    INACTIVE(43739),
    WEAK(43740),
    MEDIUM(43741),
    STRONG(43742),
    OVERCHARGED(43743);
    private final int groundObjectId;

    CellTile(int groundObjectId) {
        this.groundObjectId = groundObjectId;
    }

    public static CellTile[] VALUES = CellTile.values();
}
