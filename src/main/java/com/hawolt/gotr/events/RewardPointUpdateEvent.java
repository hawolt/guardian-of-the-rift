package com.hawolt.gotr.events;

import lombok.Getter;

@Getter
public class RewardPointUpdateEvent {
    private final int totalElementalRewardPoints, totalCatalyticRewardPoints;

    public RewardPointUpdateEvent(int totalElementalRewardPoints, int totalCatalyticRewardPoints) {
        this.totalCatalyticRewardPoints = totalCatalyticRewardPoints;
        this.totalElementalRewardPoints = totalElementalRewardPoints;
    }

    @Override
    public String toString() {
        return "RewardPointUpdateEvent{" +
                "totalElementalRewardPoints=" + totalElementalRewardPoints +
                ", totalCatalyticRewardPoints=" + totalCatalyticRewardPoints +
                '}';
    }
}
