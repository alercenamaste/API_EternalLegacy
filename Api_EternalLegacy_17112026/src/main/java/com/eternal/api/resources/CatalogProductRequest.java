/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eternal.api.resources;

public class CatalogProductRequest {

    private String language;
    private Integer categoryId;
    private String search;
    private String orderBy;
    private Boolean featuredOnly;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Boolean getFeaturedOnly() {
        return featuredOnly;
    }

    public void setFeaturedOnly(Boolean featuredOnly) {
        this.featuredOnly = featuredOnly;
    }

}