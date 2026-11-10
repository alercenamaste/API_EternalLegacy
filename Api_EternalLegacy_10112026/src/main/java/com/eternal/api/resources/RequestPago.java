/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eternal.api.resources;

import java.util.List;

/**
 *
 * @author mauricio.olguin
 */
public class RequestPago {

    private int id;
    private String email;
    private String transactionId;
    private String amount;
    private String fecha;
    private String currency;
    private String customerName;
    private String customerPhone;
    private RequestShipping shipping;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public RequestShipping getShipping() {
        return shipping;
    }

    public void setShipping(RequestShipping shipping) {
        this.shipping = shipping;
    }
    
    

    private List<PaymentItemRequest> items;

    public List<PaymentItemRequest> getItems() {
        return items;
    }

    public void setItems(List<PaymentItemRequest> items) {
        this.items = items;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    // private String url_qr;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}
