package com.gamechief.aifunnyringtone.activities;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.HistoryAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.DataASSavingPrefs;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityAitextSoundBinding;
import com.gamechief.aifunnyringtone.databinding.PremiumDialogBinding;
import com.gamechief.aifunnyringtone.models.ApiClient;
import com.gamechief.aifunnyringtone.models.HistoryStorage;
import com.gamechief.aifunnyringtone.models.SoundModel;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
public class AITextSound extends ParentActivity {
    private static final int PERMISSION_REQ_CODE = 101;
    private static final String PREF_IS_FIRST_GENERATION = "is_first_ai_generation";
    HistoryAdapter adapter;
    private ApiClient apiClient;
    private ProductDetails.SubscriptionOfferDetails baseOfferDetails;

    private ActivityAitextSoundBinding binding;
    private List<SoundModel> historyList;
    private MediaPlayer mediaPlayer;
    private final List<String> suggestions = Arrays.asList(new String[]{"Bulldog barking", "Russian cat sound", "Monkey in danger", "Thunder rumbling", "Ocean waves crashing", "Birds chirping forest", "Rain drops on window", "Crackling fireplace", "City traffic ambience"});
    private DataASSavingPrefs dataPrefs;

    private BillingClient billingClient;
    private ProductDetails currentProductDetails;
    private ProductDetails.SubscriptionOfferDetails targetOfferDetails;
    private Dialog premiumDialog;
    private PremiumDialogBinding dialogBinding;
    private RewardedAd rewardedAd;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityAitextSoundBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.dataPrefs = new DataASSavingPrefs();
        this.historyList = new ArrayList<>();
        SoundModel recent = HistoryStorage.loadRecent(this);
        if (recent != null) {
            this.historyList.add(recent);
        }
        this.adapter = new HistoryAdapter(this, this.historyList, new HistoryAdapter.OnItemClickListener() {
            public void onItemClick(SoundModel item) {
                Intent intent = new Intent(AITextSound.this, PlayAiSound.class);
                intent.putExtra("soundUrl", item.getFilePath());
                intent.putExtra("prompt_text", item.getName());
                AITextSound.this.startActivity(intent);
            }
        });
        this.binding.recentlist.setAdapter(this.adapter);
        this.binding.imageView7.setOnClickListener(view -> AITextSound.this.getOnBackPressedDispatcher().onBackPressed());
        this.apiClient = new ApiClient(this);
        setupChips();
        setupButtons();
        checkPermissions();
        initBillingClient();
        initPremiumDialog();
    }



    private void setupChips() {
        int[] chipColors = {R.color.light, R.color.light, R.color.light};
        for (String text : this.suggestions) {
            Chip chip = new Chip(this);
            chip.setText(text);
            chip.setTextColor(ContextCompat.getColor(this, R.color.black));
            chip.setChipBackgroundColorResource(chipColors[new Random().nextInt(chipColors.length)]);
            chip.setRippleColorResource(R.color.light);
            chip.setTextSize(14.0f);
            chip.setChipStrokeWidth(0.0f);
            chip.setChipCornerRadius(35.0f);
            chip.setElevation(6.0f);
            chip.setClickable(true);
            chip.setCheckable(false);
            ChipGroup.LayoutParams params = new ChipGroup.LayoutParams(-2, (int) (getResources().getDisplayMetrics().density * 55.0f));
            params.setMargins(16, 10, 16, 10);
            chip.setLayoutParams(params);
            chip.setPadding(32, 0, 32, 0);
            chip.setOnClickListener(v -> binding.putText.setText(text));
            this.binding.chipGroup.addView(chip);
        }
    }



    private void setupButtons() {
        binding.btnSurprise.setOnClickListener(v -> {
            String randomPrompt = suggestions.get(new Random().nextInt(suggestions.size()));
            binding.putText.setText(randomPrompt);
        });

        binding.generateSound.setOnClickListener(v -> {
            String prompt = binding.putText.getText().toString().trim();
            if (!prompt.isEmpty()) {
                boolean isFirstTime = !dataPrefs.getBooleanPrefValue(this, PREF_IS_FIRST_GENERATION);
                if (isFirstTime || AdsController.isIsInAppPurchased()) {
                    if (isFirstTime) dataPrefs.setBooleanPrefValue(this, PREF_IS_FIRST_GENERATION, true);
                    navigateToGenerate(prompt);
                } else {
                    showLimitDialog(prompt);
                }
            } else {
                Toast.makeText(this, "Enter or pick a prompt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLimitDialog(String prompt) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_generation_limit);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialog.findViewById(R.id.btnWatchAd).setOnClickListener(v -> {
            dialog.dismiss();
            loadAndShowRewardedAd(prompt);
        });

        dialog.findViewById(R.id.btnGoPremium).setOnClickListener(v -> {
            dialog.dismiss();
            showPremiumDialog();
        });

        dialog.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void loadAndShowRewardedAd(String prompt) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ad...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        com.google.android.gms.ads.AdRequest adRequest = new com.google.android.gms.ads.AdRequest.Builder().build();
        RewardedAd.load(this, getString(R.string.rewardAd_high), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd ad) {
                progressDialog.dismiss();
                rewardedAd = ad;
                final boolean[] isRewarded = {false};

                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        if (isRewarded[0]) navigateToGenerate(prompt);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        navigateToGenerate(prompt);
                    }
                });

                rewardedAd.show(AITextSound.this, rewardItem -> {
                    isRewarded[0] = true;
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                RewardedAd.load(AITextSound.this, getString(R.string.rewardAd_medium), adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        progressDialog.dismiss();
                        rewardedAd = ad;
                        final boolean[] isRewarded = {false};

                        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                if (isRewarded[0]) {
                                    navigateToGenerate(prompt);
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                super.onAdFailedToShowFullScreenContent(adError);
                                navigateToGenerate(prompt);
                            }
                        });

                        rewardedAd.show(AITextSound.this, rewardItem -> {
                            isRewarded[0] = true;
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        progressDialog.dismiss();
                        Toast.makeText(AITextSound.this, "Failed to load ad. Please try again later.", Toast.LENGTH_SHORT).show();
                        // Optional: Navigate anyway for better UX
                        // navigateToGenerate(prompt);
                    }
                });
            }
        });
    }

    private void navigateToGenerate(String prompt) {
        Intent intent = new Intent(AITextSound.this, GenerateNow.class);
        intent.putExtra("prompt_text", prompt);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener((billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
                        for (Purchase purchase : purchases) {
                            handlePurchase(purchase);
                        }
                    }
                })
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {}
        });
    }

    private void queryProductDetails() {
        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId("my_plan")
                .setProductType(BillingClient.ProductType.SUBS)
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !productDetailsList.isEmpty()) {
                currentProductDetails = productDetailsList.get(0);
                if (currentProductDetails.getSubscriptionOfferDetails() != null) {
                    for (ProductDetails.SubscriptionOfferDetails offer : currentProductDetails.getSubscriptionOfferDetails()) {
                        if (offer.getOfferId() == null) {
                            baseOfferDetails = offer;
                        }
                        if ("offer1".equals(offer.getOfferId()) || "weekly".equals(offer.getBasePlanId())) {
                            targetOfferDetails = offer;
                        }
                    }
                }
                updatePriceInDialog();
            }
        });
    }

    private void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
            AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.acknowledgePurchase(params, billingResult -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    AdsController.setIsInAppPurchased(true);
                    if (premiumDialog != null) premiumDialog.dismiss();
                    Toast.makeText(this, "Premium Activated!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initPremiumDialog() {
        this.dialogBinding = PremiumDialogBinding.inflate(getLayoutInflater());
        this.premiumDialog = new Dialog(this);
        this.premiumDialog.setContentView(this.dialogBinding.getRoot());
        this.premiumDialog.setCancelable(true);

        if (this.premiumDialog.getWindow() != null) {
            this.premiumDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.premiumDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        this.dialogBinding.closeview.setOnClickListener(v -> premiumDialog.dismiss());
        this.dialogBinding.buyPremium.setOnClickListener(v -> {
            if (currentProductDetails != null && targetOfferDetails != null) {
                launchPurchaseFlow();
            } else {
                Toast.makeText(this, "Please wait, loading offer...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPremiumDialog() {
        if (premiumDialog != null && !premiumDialog.isShowing()) {
            premiumDialog.show();
        }
    }

    private void updatePriceInDialog() {

        if (dialogBinding == null) return;

       runOnUiThread(() -> {

            String discountedPriceStr = getString(R.string.loading);
            double discountedMicros = 0;
            double originalMicros = 0;

            // 🔹 Get discounted price
            if (targetOfferDetails != null) {
                for (ProductDetails.PricingPhase phase :
                        targetOfferDetails.getPricingPhases().getPricingPhaseList()) {

                    if (phase.getPriceAmountMicros() > 0) {
                        discountedPriceStr = phase.getFormattedPrice();
                        discountedMicros = phase.getPriceAmountMicros();
                        break;
                    }
                }
            }

            // 🔹 Get original price
            if (baseOfferDetails != null) {
                for (ProductDetails.PricingPhase phase :
                        baseOfferDetails.getPricingPhases().getPricingPhaseList()) {

                    if (phase.getPriceAmountMicros() > 0) {
                        originalMicros = phase.getPriceAmountMicros();
                        break;
                    }
                }
            }

            // 🔹 (Optional) Discount calculation
            if (originalMicros > 0 && discountedMicros > 0 && discountedMicros < originalMicros) {
                int discountPercent =
                        (int) ((1 - (discountedMicros / originalMicros)) * 100);

                Log.d("Billing", "Discount: " + discountPercent + "%");
            }

            // 🔹 Set UI text
            dialogBinding.freeTrial.setText(
                  getString(R.string.start_3_days_free_trial_flat) + " "
                            + getString(R.string.discount_on_1st_order_and_after_that) + " "
                            + discountedPriceStr + " "
                            + getString(R.string.per_week_will_be_charged)
            );

            dialogBinding.justPrice.setText(
                    getString(R.string.discount_on_1st_order_and_after_that) + " "
                            + getString(R.string.just) + " "
                            + discountedPriceStr + " "
                            + getString(R.string.per_month)
            );

        });
    }

    private void launchPurchaseFlow() {
        BillingFlowParams.ProductDetailsParams productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(currentProductDetails)
                .setOfferToken(targetOfferDetails.getOfferToken())
                .build();
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(List.of(productDetailsParams))
                .build();
        billingClient.launchBillingFlow(this, billingFlowParams);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0 || ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            for (int i : grantResults) {
            }
        }
    }

    public void onResume() {
        super.onResume();
        this.historyList.clear();
        SoundModel recent = HistoryStorage.loadRecent(this);
        if (recent != null) {
            this.historyList.add(new SoundModel(recent.getName(), recent.getFilePath()));
        }
        this.adapter.notifyDataSetChanged();
    }
}
