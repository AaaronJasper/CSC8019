package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class HorsePayRequest {

    private String storeID;
    private String customerID;
    private String date;
    private String time;
    private String timeZone;
    private BigDecimal transactionAmount;
    private String currencyCode;

    @JsonProperty("forcePaymentSatusReturnType")
    private Boolean forcePaymentSatusReturnType;

    public String getStoreID() { return storeID; }
    public void setStoreID(String storeID) { this.storeID = storeID; }

    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    public BigDecimal getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public Boolean getForcePaymentSatusReturnType() { return forcePaymentSatusReturnType; }
    public void setForcePaymentSatusReturnType(Boolean forcePaymentSatusReturnType) { this.forcePaymentSatusReturnType = forcePaymentSatusReturnType; }
}
