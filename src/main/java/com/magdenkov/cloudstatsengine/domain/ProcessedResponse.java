package com.magdenkov.cloudstatsengine.domain;

import java.util.ArrayList;
import java.util.List;

public class ProcessedResponse {

    private List<GeometricModel> originalInput = new ArrayList<>();

    private GeometricModel guavaCalculatedPercentile = new GeometricModel();

    private GeometricModel apacheCalculatedPercentile = new GeometricModel();

    public GeometricModel getApacheCalculatedPercentile() {
        return apacheCalculatedPercentile;
    }

    public void setApacheCalculatedPercentile(GeometricModel apacheCalculatedPercentile) {
        this.apacheCalculatedPercentile = apacheCalculatedPercentile;
    }

    public List<GeometricModel> getOriginalInput() {
        return originalInput;
    }

    public void setOriginalInput(List<GeometricModel> originalInput) {
        this.originalInput = originalInput;
    }

    public GeometricModel getGuavaCalculatedPercentile() {
        return guavaCalculatedPercentile;
    }

    public void setGuavaCalculatedPercentile(GeometricModel guavaCalculatedPercentile) {
        this.guavaCalculatedPercentile = guavaCalculatedPercentile;
    }
}
