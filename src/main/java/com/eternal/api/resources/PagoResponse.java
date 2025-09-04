/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eternal.api.resources;

/**
 *
 * @author mauricio.olguin
 */
public class PagoResponse {
    private int pagoId;
    private String qrUrl;
    private String detalle;

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    
    
    public PagoResponse(int pagoId, String qrUrl, String detalle) {
        this.pagoId = pagoId;
        this.qrUrl = qrUrl;
        this.detalle=detalle;
    }

    // Getters
    public int getPagoId() { return pagoId; }
    public String getQrUrl() { return qrUrl; }
}
