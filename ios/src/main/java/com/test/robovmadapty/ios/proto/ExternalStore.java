package com.test.robovmadapty.ios.proto;

import java.util.List;

public interface ExternalStore {

    void retrieveItems(ItemsListener listener);

    void purchase(String sku, StorePurchaseResultListener resultListener);

    void setUser(int userId);
    void removeUser();

    interface ItemsListener {
        void onReceive(List<ExternalStoreItemData> items);
    }

}
