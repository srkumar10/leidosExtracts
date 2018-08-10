package com.ouc.model;

public class Meter {

    private String meterBadge;
    private String manufactureCode;
    private String modelCode;
    private String meterType;
    private String servicePointID;
    private String servicePointType;
    private String premiseID;
    private String premiseType;


    public String getMeterBadge() {
        return meterBadge;
    }

    public void setMeterBadge(String meterBadge) {
        this.meterBadge = meterBadge;
    }

    public String getManufactureCode() {
        return manufactureCode;
    }

    public void setManufactureCode(String manufactureCode) {
        this.manufactureCode = manufactureCode;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

    public String getServicePointID() {
        return servicePointID;
    }

    public void setServicePointID(String servicePointID) {
        this.servicePointID = servicePointID;
    }

    public String getServicePointType() {
        return servicePointType;
    }

    public void setServicePointType(String servicePointType) {
        this.servicePointType = servicePointType;
    }

    public String getPremiseID() {
        return premiseID;
    }

    public void setPremiseID(String premiseID) {
        this.premiseID = premiseID;
    }

    public String getPremiseType() {
        return premiseType;
    }

    public void setPremiseType(String premiseType) {
        this.premiseType = premiseType;
    }
}
