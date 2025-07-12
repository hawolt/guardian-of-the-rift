package com.hawolt.gotr.utility;

import lombok.Getter;

@Getter
public class PointStatus {
    private final int elementalPoints, catalyticPoints, elementalRewardPoints, catalyticRewardPoints;

    public PointStatus(
            int elementalPoints,
            int catalyticPoints,
            int elementalRewardPoints,
            int catalyticRewardPoints
    ) {
        this.elementalRewardPoints = elementalRewardPoints;
        this.catalyticRewardPoints = catalyticRewardPoints;
        this.elementalPoints = elementalPoints;
        this.catalyticPoints = catalyticPoints;
    }

    @Override
    public String toString() {
        return "PointStatus{" +
                "elementalPoints=" + elementalPoints +
                ", catalyticPoints=" + catalyticPoints +
                ", elementalRewardPoints=" + elementalRewardPoints +
                ", catalyticRewardPoints=" + catalyticRewardPoints +
                '}';
    }
}
