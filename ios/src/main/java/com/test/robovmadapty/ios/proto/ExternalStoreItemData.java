package com.test.robovmadapty.ios.proto;

public class ExternalStoreItemData {

    private final String sku, price;

    public ExternalStoreItemData(String sku, String price) {
        this.sku = sku;
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public String getPrice() {
        return price;
    }

}
