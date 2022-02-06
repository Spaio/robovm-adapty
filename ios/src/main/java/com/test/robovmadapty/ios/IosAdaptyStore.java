package com.test.robovmadapty.ios;


import com.test.robovmadapty.ios.proto.App;
import com.test.robovmadapty.ios.proto.ExternalStore;
import com.test.robovmadapty.ios.proto.ExternalStoreItemData;
import com.test.robovmadapty.ios.proto.StorePurchaseResultListener;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSString;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.objc.block.VoidBlock3;
import org.robovm.objc.block.VoidBlock5;
import org.robovm.pods.adapty.Adapty;
import org.robovm.pods.adapty.AdaptyError;
import org.robovm.pods.adapty.AdaptyLogLevel;
import org.robovm.pods.adapty.PaywallModel;
import org.robovm.pods.adapty.ProductModel;
import org.robovm.pods.adapty.PurchaserInfoModel;

import java.util.ArrayList;
import java.util.List;

class IosAdaptyStore implements ExternalStore {

    private List<ProductModel> lastRequestedProducts;
    private String userId;

    IosAdaptyStore() {
        Adapty.setLogLevel(AdaptyLogLevel.All);
        Adapty.activate("KEY_NEEDED");
    }

    @Override
    public void retrieveItems(ItemsListener listener) {
        Adapty.getPaywalls(false, new VoidBlock3<NSArray<PaywallModel>, NSArray<ProductModel>, AdaptyError>() {
            @Override
            public void invoke(NSArray<PaywallModel> paywallModels, NSArray<ProductModel> productModels, AdaptyError adaptyError) {

                lastRequestedProducts = new ArrayList<>();
                List<ExternalStoreItemData> items = new ArrayList<>();

                if (adaptyError == null && productModels != null) {
                    App.log("Amount of products: " + productModels.size());

                    // Падает здесь
                    for (ProductModel product : productModels) {
                        lastRequestedProducts.add(product);
                        items.add(new ExternalStoreItemData(
                                product.getVendorProductId(),
                                product.getCurrencyCode() + " " + product.getLocalizedPrice())
                        );
                    }

                } else {
                    App.log("Error appeared or products possibly null");
                    App.log(adaptyError);
                }

                listener.onReceive(items);
            }
        });
    }

    @Override
    public void purchase(String sku, StorePurchaseResultListener resultListener) {
        App.log("Attempting to make purchase for sku = " + sku);

        if (userId == null) {
            App.log("Error - user id has not been set");
            resultListener.onResult("");
            return;
        }
        Adapty.identify(userId, new VoidBlock1<AdaptyError>() {
            @Override
            public void invoke(AdaptyError adaptyError) {
                App.log(adaptyError);
            }
        });

        App.log("User id for purchase - " + userId);

        if (lastRequestedProducts == null) {
            App.log("Error - products list is not available");
            resultListener.onResult("");
            return;
        }

        ProductModel foundProduct = null;
        for (ProductModel productModel : lastRequestedProducts) {
            if (productModel.getVendorProductId().equals(sku)) {
                foundProduct = productModel;
                break;
            }
        }

        if (foundProduct == null) {
            App.log("Error - product unavailable");
            resultListener.onResult("");
            return;
        }

        Adapty.makePurchase(foundProduct, null, new VoidBlock5<PurchaserInfoModel, NSString, NSDictionary<NSString, ?>, ProductModel, AdaptyError>() {
            @Override
            public void invoke(PurchaserInfoModel purchaserInfoModel, NSString nsString, NSDictionary<NSString, ?> nsStringNSDictionary, ProductModel productModel, AdaptyError adaptyError) {
                App.log(purchaserInfoModel);
                if (adaptyError == null) {
                    App.log("Purchase succeed");
                    resultListener.onResult("Thank you for purchase!");
                } else {
                    App.log("Purchase failed");
                    resultListener.onResult("");
                }
            }
        });
    }

    @Override
    public void setUser(int userId) {
        App.log("Adapty#setUser(" + userId + ")");
        this.userId = userId + "";
        Adapty.identify(this.userId, new VoidBlock1<AdaptyError>() {
            @Override
            public void invoke(AdaptyError adaptyError) {
                App.log(adaptyError);
            }
        });
    }

    @Override
    public void removeUser() {
        App.log("Adapty#removeUser");
        this.userId = null;
        Adapty.logout(new VoidBlock1<AdaptyError>() {
            @Override
            public void invoke(AdaptyError adaptyError) {
                App.log(adaptyError);
            }
        });
    }
}