package com.magdenkov.cloudstatsengine.domain;

import com.opencsv.bean.CsvBindByName;

public class GeometricModel {

    @CsvBindByName(column = "DEPTH")
    private String depth;
    @CsvBindByName(column = "GR")
    private String gammaRay;
    @CsvBindByName(column = "RHOB")
    private String rhob;

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getGammaRay() {
        return gammaRay;
    }

    public void setGammaRay(String gammaRay) {
        this.gammaRay = gammaRay;
    }

    public String getRhob() {
        return rhob;
    }

    public void setRhob(String rhob) {
        this.rhob = rhob;
    }
}
