package com.example.smartlist;

import com.example.smartlist.Database.Product;

public class ProductData {

    /** Small class used to hold information for the adapters. */

    private Product product;
    private final SearchType searchType;
    private final String search;

    public ProductData(Product product, SearchType searchType, String search) {
        this.product = product;
        this.searchType = searchType;
        this.search = search;
    }

    public Product getProduct() {
        return product;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public String getSearch() {
        return search;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
