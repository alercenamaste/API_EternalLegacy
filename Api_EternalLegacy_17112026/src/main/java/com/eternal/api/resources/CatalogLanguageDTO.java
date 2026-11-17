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


public class CatalogLanguageDTO {

    private String code;

    private String languageName;

    private String nativeName;

    private Boolean rtl;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    public Boolean getRtl() {
        return rtl;
    }

    public void setRtl(Boolean rtl) {
        this.rtl = rtl;
    }

}