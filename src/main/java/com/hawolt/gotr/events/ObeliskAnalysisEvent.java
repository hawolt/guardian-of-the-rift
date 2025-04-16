package com.hawolt.gotr.events;

import com.hawolt.gotr.data.ObeliskType;
import com.hawolt.gotr.utility.ObeliskAnalysis;
import lombok.Getter;

@Getter
public class ObeliskAnalysisEvent {
    private final ObeliskType obeliskType;
    private final ObeliskAnalysis[] obeliskAnalysis;

    public ObeliskAnalysisEvent(ObeliskType obeliskType, ObeliskAnalysis... obeliskAnalysis) {
        this.obeliskAnalysis = obeliskAnalysis;
        this.obeliskType = obeliskType;
    }
}
