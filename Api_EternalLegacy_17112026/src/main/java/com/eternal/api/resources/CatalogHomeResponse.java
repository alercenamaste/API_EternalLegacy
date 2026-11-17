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

import java.util.ArrayList;
import java.util.List;

public class CatalogHomeResponse {

    private List<CatalogProductDTO> featuredProducts = new ArrayList<>();

    private List<CatalogProductDTO> bestProducts = new ArrayList<>();

    private List<CatalogCategoryDTO> categories = new ArrayList<>();

    private List<CatalogLanguageDTO> languages = new ArrayList<>();

    public List<CatalogProductDTO> getFeaturedProducts() {
        return featuredProducts;
    }

    public void setFeaturedProducts(List<CatalogProductDTO> featuredProducts) {
        this.featuredProducts = featuredProducts;
    }

    public List<CatalogProductDTO> getBestProducts() {
        return bestProducts;
    }

    public void setBestProducts(List<CatalogProductDTO> bestProducts) {
        this.bestProducts = bestProducts;
    }

    public List<CatalogCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CatalogCategoryDTO> categories) {
        this.categories = categories;
    }

    public List<CatalogLanguageDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<CatalogLanguageDTO> languages) {
        this.languages = languages;
    }

}
