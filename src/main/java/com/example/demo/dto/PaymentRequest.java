package com.example.demo.dto;

public class PaymentRequest {

    private int customerID;
    private String date;
    private String time;
    private String timeZone;
    private double transactionAmount;
    private String currencyCode;

    // Optional testing field
    private Boolean forcePaymentStatusReturnType;

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Boolean getForcePaymentStatusReturnType() {
        return forcePaymentStatusReturnType;
    }

    public void setForcePaymentStatusReturnType(Boolean forcePaymentStatusReturnType) {
        this.forcePaymentStatusReturnType = forcePaymentStatusReturnType;
    }
}