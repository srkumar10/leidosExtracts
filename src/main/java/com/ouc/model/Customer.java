package com.ouc.model;

public class Customer {

    public String accountID;
    public String servicePointID;
    public String meterID;
    public String meterBadge;
    public String meterConfigID;

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getServicePointID() {
        return servicePointID;
    }

    public void setServicePointID(String servicePointID) {
        this.servicePointID = servicePointID;
    }

    public String getMeterID() {
        return meterID;
    }

    public void setMeterID(String meterID) {
        this.meterID = meterID;
    }

    public String getMeterBadge() {
        return meterBadge;
    }

    public void setMeterBadge(String meterBadge) {
        this.meterBadge = meterBadge;
    }

    public String getMeterConfigID() {
        return meterConfigID;
    }

    public void setMeterConfigID(String meterConfigID) {
        this.meterConfigID = meterConfigID;
    }
}
