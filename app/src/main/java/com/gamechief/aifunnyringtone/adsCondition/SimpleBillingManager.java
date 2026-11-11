package com.gamechief.aifunnyringtone.adsCondition;


import android.app.Activity;
import android.app.Dialog;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.ProductDetails;


public class SimpleBillingManager {
    private final String SUBSCRIPTION_PRODUCT_ID = "my_plans";
    private final String TARGET_BASE_PLAN_ID = "weekly";
    private final String TARGET_OFFER_ID = "offer2-wk";

    private  Activity activity;
    private BillingClient billingClient;
    private ProductDetails currentProductDetails;
    private ProductDetails.SubscriptionOfferDetails targetOfferDetails;
    private ProductDetails.SubscriptionOfferDetails baseOfferDetails;

    private Dialog premiumDialog;

    private  OnBillingCallback billingCallback;
    private SharedPreferenceManager sharedPreferenceManager;

    public interface OnBillingCallback {
        void onPurchaseSuccess();
        void onDialogDismiss();
    }

//    public SimpleBillingManager(Activity activity, OnBillingCallback callback) {
//        sharedPreferenceManager = new SharedPreferenceManager(activity, IN_APP_VALUE);
//        this.activity = activity;
//        this.billingCallback = callback;
//        initBillingClient();
//        intPremiumDialog();
//    }

//    private void initBillingClient() {
//        billingClient = BillingClient.newBuilder(activity)
//                .enablePendingPurchases()
//                .setListener(this::onPurchaseUpdated)
//                .build();
//
//        billingClient.startConnection(new BillingClientStateListener() {
//            @Override
//            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
//                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    queryProductDetails();
//                }
//            }
//            @Override
//            public void onBillingServiceDisconnected() {
//                // Reconnect logic can be added here if needed
//            }
//        });
//    }
//
//    private void queryProductDetails() {
//        String tag = "queryProductDetails_01";
//        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
//                .setProductList(List.of(
//                        QueryProductDetailsParams.Product.newBuilder()
//                                .setProductId(SUBSCRIPTION_PRODUCT_ID)
//                                .setProductType(BillingClient.ProductType.SUBS)
//                                .build()
//                )).build();
//
//        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
//            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
//                ProductDetails productDetails = productDetailsList.get(0);
//                currentProductDetails = productDetails;
//                // Find offer with offerId "offer2-wk"
//                if (productDetails.getSubscriptionOfferDetails() != null) {
//                    for (ProductDetails.SubscriptionOfferDetails offer : productDetails.getSubscriptionOfferDetails()) {
//                        if (TARGET_OFFER_ID.equals(offer.getOfferId())) {
//                            targetOfferDetails = offer;
//                        } else if (offer.getOfferId() == null && TARGET_BASE_PLAN_ID.equals(offer.getBasePlanId())) {
//                            baseOfferDetails = offer;
//                        }
//                    }
//                }
//                Log.e(tag,"billing init - "+targetOfferDetails.getOfferId());
//                updatePriceInDialog();
//            }
//        });
//    }
//
//    private void onPurchaseUpdated(BillingResult billingResult, List<Purchase> purchases) {
//        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
//            for (Purchase purchase : purchases) {
//                handlePurchase(purchase);
//            }
//        }
//    }
//
//    private void handlePurchase(Purchase purchase) {
//        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
//            AcknowledgePurchaseParams acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
//                    .setPurchaseToken(purchase.getPurchaseToken())
//                    .build();
//            billingClient.acknowledgePurchase(acknowledgeParams, result -> {
//                if (result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    sharedPreferenceManager.setBooleanValue(IN_APP_PURCHASE_KEY, true);
//                    if (premiumDialog != null) premiumDialog.dismiss();
//                    if (billingCallback != null) billingCallback.onPurchaseSuccess();
//                    if (activity!=null) ((MainActivity) activity).triggerInAppEventForCategories(activity,"subscriptionPurchased");
//                }
//            });
//        }
//    }
//
//    public void intPremiumDialog() {
//        dialogBinding = LayoutPurchaseDialog2Binding.inflate(activity.getLayoutInflater());
//        premiumDialog = new Dialog(activity);
//        premiumDialog.setContentView(dialogBinding.getRoot());
//        premiumDialog.setCancelable(true);
//
//        if (premiumDialog.getWindow() != null) {
//            premiumDialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
//            premiumDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            premiumDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
//
//        dialogBinding.tvN12.setText(activity.getResources().getString(R.string.loading));
//        dialogBinding.tvPrice1.setText(activity.getResources().getString(R.string.loading));
//
//        dialogBinding.closeBtn.setOnClickListener(v -> {
//            premiumDialog.dismiss();
//            if (billingCallback != null) billingCallback.onDialogDismiss();
//        });
//
//
//        dialogBinding.continueBtn.setOnClickListener(v -> {
//            if (currentProductDetails == null || targetOfferDetails == null) {
//                initBillingClient();
//                Toast.makeText(activity, activity.getResources().getString(R.string.please_wait_loading), Toast.LENGTH_SHORT).show();
//            } else {
//                AppOpenManager.isAllowedAppOpen = false;
//                launchPurchaseFlow();
//            }
//        });
//
//        premiumDialog.setOnDismissListener(dialog -> {
//            if (billingCallback != null) billingCallback.onDialogDismiss();
//        });
//    }
//
//    public void showPremiumDialog() {
//        if (activity!=null && premiumDialog!=null) premiumDialog.show();
//    }
//
//    private void updatePriceInDialog() {
//        if (dialogBinding == null) return;
//        activity.runOnUiThread(() -> {
//            String discountedPriceStr = activity.getResources().getString(R.string.loading);
//            String originalPriceStr = "";
//            double discountedMicros = 0;
//            double originalMicros = 0;
//
//            // Get discounted price
//            if (targetOfferDetails != null) {
//                for (ProductDetails.PricingPhase phase : targetOfferDetails.getPricingPhases().getPricingPhaseList()) {
//                    if (phase.getPriceAmountMicros() > 0) {
//                        discountedPriceStr = phase.getFormattedPrice();
//                        discountedMicros = phase.getPriceAmountMicros();
//                        break;
//                    }
//                }
//            }
//            // Get original base price
//            if (baseOfferDetails != null) {
//                for (ProductDetails.PricingPhase phase : baseOfferDetails.getPricingPhases().getPricingPhaseList()) {
//                    if (phase.getPriceAmountMicros() > 0) {
//                        originalPriceStr = phase.getFormattedPrice();
//                        originalMicros = phase.getPriceAmountMicros();
//                        break;
//                    }
//                }
//            }
//
//            // Calculate discount percent
//            int discountPercent = 0;
//            if (originalMicros > 0 && discountedMicros > 0 && discountedMicros < originalMicros) {
//                discountPercent = (int) ((1 - (discountedMicros / originalMicros)) * 100);
//            }
//
//
////            activity.getResources().getString(R.string.)
//
//            // Show formatted text
//            String priceLine = activity.getResources().getString(R.string.just) + discountedPriceStr + activity.getResources().getString(R.string.per_week);
////            if (!originalPriceStr.isEmpty()) {
////                priceLine += " (Original: " + originalPriceStr + ")";
////            }
////            if (discountPercent > 0) {
////                priceLine += " - Save " + discountPercent + "%";
////            }
//            dialogBinding.tvDetails.setText(activity.getResources().getString(R.string.start_3_days_free_trial_flat) +discountPercent+
//                    activity.getResources().getString(R.string.discount_on_1st_order_and_after_that) +originalPriceStr+ activity.getResources().getString(R.string.per_week_will_be_charged));
//            dialogBinding.tvPrice1.setText(discountedPriceStr);
//            dialogBinding.tvPercentage.setText(activity.getResources().getString(R.string.flat) +discountPercent+ activity.getResources().getString(R.string.off));
//            dialogBinding.tvN12.setText(priceLine);
//
//            if (!originalPriceStr.isEmpty()) {
//                dialogBinding.tvOrgPrice.setText(originalPriceStr);
//                dialogBinding.tvOrgPrice.setPaintFlags(dialogBinding.tvOrgPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            }
//
//        });
//    }
//
//    private void launchPurchaseFlow() {
//        if (activity!=null)
//            ((MainActivity) activity).triggerInAppEventForCategories(activity,"purchaseFlowLaunched");
//        BillingFlowParams.ProductDetailsParams productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
//                .setProductDetails(currentProductDetails)
//                .setOfferToken(targetOfferDetails.getOfferToken())
//                .build();
//        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
//                .setProductDetailsParamsList(List.of(productDetailsParams))
//                .build();
//        if (activity != null) billingClient.launchBillingFlow(activity, billingFlowParams);
//    }
}
