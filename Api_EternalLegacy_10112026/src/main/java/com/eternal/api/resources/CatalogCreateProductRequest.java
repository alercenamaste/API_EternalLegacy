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


public class CatalogCreateProductRequest {

    private String code;

    private Integer categoryId;

    private String sku;

    private Double price;

    private String currency;

    private Integer stock;

    private Integer quality;

    private Boolean featured;

    private Boolean active;

    // Traducciones
    private String productNameEs;
    private String shortDescriptionEs;
    private String longDescriptionEs;

    private String productNameEn;
    private String shortDescriptionEn;
    private String longDescriptionEn;

    private String productNameRu;
    private String shortDescriptionRu;
    private String longDescriptionRu;

    private String productNameAr;
    private String shortDescriptionAr;
    private String longDescriptionAr;

    // ===== Getters y Setters =====

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getQuality() { return quality; }
    public void setQuality(Integer quality) { this.quality = quality; }

    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getProductNameEs() { return productNameEs; }
    public void setProductNameEs(String productNameEs) { this.productNameEs = productNameEs; }

    public String getShortDescriptionEs() { return shortDescriptionEs; }
    public void setShortDescriptionEs(String shortDescriptionEs) { this.shortDescriptionEs = shortDescriptionEs; }

    public String getLongDescriptionEs() { return longDescriptionEs; }
    public void setLongDescriptionEs(String longDescriptionEs) { this.longDescriptionEs = longDescriptionEs; }

    public String getProductNameEn() { return productNameEn; }
    public void setProductNameEn(String productNameEn) { this.productNameEn = productNameEn; }

    public String getShortDescriptionEn() { return shortDescriptionEn; }
    public void setShortDescriptionEn(String shortDescriptionEn) { this.shortDescriptionEn = shortDescriptionEn; }

    public String getLongDescriptionEn() { return longDescriptionEn; }
    public void setLongDescriptionEn(String longDescriptionEn) { this.longDescriptionEn = longDescriptionEn; }

    public String getProductNameRu() { return productNameRu; }
    public void setProductNameRu(String productNameRu) { this.productNameRu = productNameRu; }

    public String getShortDescriptionRu() { return shortDescriptionRu; }
    public void setShortDescriptionRu(String shortDescriptionRu) { this.shortDescriptionRu = shortDescriptionRu; }

    public String getLongDescriptionRu() { return longDescriptionRu; }
    public void setLongDescriptionRu(String longDescriptionRu) { this.longDescriptionRu = longDescriptionRu; }

    public String getProductNameAr() { return productNameAr; }
    public void setProductNameAr(String productNameAr) { this.productNameAr = productNameAr; }

    public String getShortDescriptionAr() { return shortDescriptionAr; }
    public void setShortDescriptionAr(String shortDescriptionAr) { this.shortDescriptionAr = shortDescriptionAr; }

    public String getLongDescriptionAr() { return longDescriptionAr; }
    public void setLongDescriptionAr(String longDescriptionAr) { this.longDescriptionAr = longDescriptionAr; }
}
