package com.gamechief.aifunnyringtone;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.webkit.internal.AssetHelper;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.gamechief.aifunnyringtone.activities.FavoritesScreen;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.AppOpenManager;
import com.gamechief.aifunnyringtone.adsCondition.SharedPreferenceManager;
import com.gamechief.aifunnyringtone.adsCondition.SimpleBillingManager;
import com.gamechief.aifunnyringtone.databinding.ActivityMainBinding;
import com.gamechief.aifunnyringtone.databinding.PremiumDialogBinding;
import com.gamechief.aifunnyringtone.fragments.Favourite;
import com.gamechief.aifunnyringtone.fragments.Histroy;
import com.gamechief.aifunnyringtone.fragments.Home;
import com.gamechief.aifunnyringtone.fragments.PrankCall;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private final String SUBSCRIPTION_PRODUCT_ID = "my_plan";
    private final String TARGET_BASE_PLAN_ID = "weekly";
    private final String TARGET_OFFER_ID = "offer1";
    private Activity activity;
    private ProductDetails.SubscriptionOfferDetails baseOfferDetails;
    private SimpleBillingManager.OnBillingCallback billingCallback;
    private BillingClient billingClient;
    ActivityMainBinding binding;
    /* access modifiers changed from: private */
    public BottomSheetDialog bottomSheetDialog;
    private ProductDetails currentProductDetails;
    private PremiumDialogBinding dialogBinding;
    private final Handler handler = new Handler();
    private boolean isPremiumDialogVisible = false;
    private Dialog premiumDialog;
    private SharedPreferenceManager sharedPreferenceManager;
    private ProductDetails.SubscriptionOfferDetails targetOfferDetails;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        /* UI only: floating motion for views tagged "float" / "float_late" */
        this.binding.getRoot().post(() -> {
            com.gamechief.aifunnyringtone.ui.UiMotion.floatTagged(this.binding.getRoot());
            com.gamechief.aifunnyringtone.ui.UiMotion.wobbleTagged(this.binding.getRoot());
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main_content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        this.binding.fav.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, FavoritesScreen.class));
            }
        });
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new Home(), "");
        pagerAdapter.addFragment(new PrankCall(), "");
        pagerAdapter.addFragment(new Favourite(), "");
        pagerAdapter.addFragment(new Histroy(), "");
        this.binding.viewPagerNailArt.setAdapter(pagerAdapter);
        
        handleIntent(getIntent());

        this.binding.viewPagerNailArt.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                MainActivity.this.resetIconColors();
                MainActivity.this.moveNavIndicator(position);
                switch (position) {
                    case 0:
                        MainActivity.this.binding.homeTxt.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.nav_selected));
                        MainActivity.this.binding.homTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.nav_selected));
                        return;
                    case 1:
                        MainActivity.this.binding.prank.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.nav_selected));
                        MainActivity.this.binding.prankTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.nav_selected));
                        return;
                    case 2:
                        MainActivity.this.binding.dualTxt.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.nav_selected));
                        MainActivity.this.binding.listTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.nav_selected));
                        return;
                    case 3:
                        MainActivity.this.binding.darkTxt.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.nav_selected));
                        MainActivity.this.binding.favTxt.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.nav_selected));
                        return;
                    default:
                        return;
                }
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        this.binding.bottom.post(() -> moveNavIndicator(binding.viewPagerNailArt.getCurrentItem()));
        this.binding.homeTxt.setOnClickListener(v -> binding.viewPagerNailArt.setCurrentItem(0));
        this.binding.prank.setOnClickListener(v -> binding.viewPagerNailArt.setCurrentItem(1));
        this.binding.dualTxt.setOnClickListener(v -> binding.viewPagerNailArt.setCurrentItem(2));
        this.binding.darkTxt.setOnClickListener(v -> binding.viewPagerNailArt.setCurrentItem(3));

        this.binding.homTxt.setOnClickListener(v -> binding.viewPagerNailArt.setCurrentItem(0));
        this.binding.prankTxt.setOnClickListener(v -> binding.viewPagerNailArt.setCurrentItem(1));
        this.binding.listTxt.setOnClickListener(v -> binding.viewPagerNailArt.setCurrentItem(2));
        this.binding.favTxt.setOnClickListener(v -> binding.viewPagerNailArt.setCurrentItem(3));
        setSupportActionBar(this.binding.menuOpen);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, this.binding.main, this.binding.menuOpen, R.string.navigation_drawer_close, R.string.navigation_drawer_close);
        drawerToggle.setDrawerIndicatorEnabled(false);
        this.binding.menuOpen.setNavigationIcon(R.drawable.menu_bar);
        this.binding.main.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // Android 13+
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    () -> handleBackPress()
            );
        } else {
            getOnBackPressedDispatcher().addCallback(this,
                    new OnBackPressedCallback(true) {
                        @Override
                        public void handleOnBackPressed() {
                            handleBackPress();
                        }
                    });
        }

        initBillingClient(this);
        intPremiumDialog(this);
        showPremiumDialog();

        CreateBottomSheetDialog();
        this.binding.menuOpen.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.binding.main.openDrawer((int) GravityCompat.START);
            }
        });
        this.binding.main.setScrimColor(0);
        this.binding.NavigationViewDpMaker.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.RateUsFunny) {
                    AppOpenManager.isAppOpenAllowed = false;
                    binding.main.closeDrawer((int) GravityCompat.START, false);
                    try {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://search?q=pub:" + "GameChief")));
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.gcl.funnyringtone.aiwallpapers.airingtones.superfunny")));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                if (item.getItemId() == R.id.ShareAppFunny) {
                    AppOpenManager.isAppOpenAllowed = false;
                    binding.main.closeDrawer((int) GravityCompat.START, false);
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType(AssetHelper.DEFAULT_MIME_TYPE);
                        intent.putExtra("android.intent.extra.SUBJECT", "Share demo");
                        intent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=com.gcl.funnyringtone.aiwallpapers.airingtones.superfunny");
                        startActivity(Intent.createChooser(intent, "Share by"));
                    } catch (Exception e2) {
                        Toast.makeText(MainActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }
                } else if (item.getItemId() == R.id.privacyPolicyFunny) {
                    AppOpenManager.isAppOpenAllowed = false;
                    binding.main.closeDrawer((int) GravityCompat.START, false);
                    try {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://sites.google.com/view/gamechieffz-llc/home")));
                    } catch (Exception e3) {
                        throw new RuntimeException(e3);
                    }
                } else if (item.getItemId() == R.id.MoreAppFunny) {
                    AppOpenManager.isAppOpenAllowed = false;
                    binding.main.closeDrawer((int) GravityCompat.START, false);
                    try {
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://search?q=pub:" + "GameChief")));
                        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/developer?id=GameChief+FZ-LLC")));
                    } catch (Exception e4) {
                        throw new RuntimeException(e4);
                    }
                } else if (item.getItemId() == R.id.RemoveAdsFunny) {
                    AppOpenManager.isAppOpenAllowed = false;
                    binding.main.closeDrawer((int) GravityCompat.START, false);
                    showPremiumDialog();
                }
                return false;
            }
            });

        this.binding.premiumOpen.setOnClickListener(v -> showPremiumDialog());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("open_history", false)) {
            this.binding.viewPagerNailArt.setCurrentItem(3, false);
            resetIconColors();
            this.binding.darkTxt.setColorFilter(ContextCompat.getColor(this, R.color.nav_selected));
            this.binding.favTxt.setTextColor(ContextCompat.getColor(this, R.color.nav_selected));
        }
    }

    /** UI only: slides the small indicator under the active bottom-nav tab. */
    private void moveNavIndicator(int position) {
        final View indicator = this.binding.navIndicator;
        final int slotWidth = this.binding.bottom.getWidth() / 4;
        if (slotWidth <= 0) {
            return;
        }
        int width = Math.round(22 * getResources().getDisplayMetrics().density);
        ViewGroup.LayoutParams params = indicator.getLayoutParams();
        if (params.width != width) {
            params.width = width;
            indicator.setLayoutParams(params);
        }
        float targetX = (position * slotWidth) + ((slotWidth - width) / 2f);
        indicator.animate().translationX(targetX).setDuration(200).start();
    }

    public void resetIconColors() {
        int unselectedColor = ContextCompat.getColor(this, R.color.nav_unselected);
        this.binding.homeTxt.setColorFilter(unselectedColor);
        this.binding.prank.setColorFilter(unselectedColor);
        this.binding.dualTxt.setColorFilter(unselectedColor);
        this.binding.darkTxt.setColorFilter(unselectedColor);
        this.binding.homTxt.setTextColor(unselectedColor);
        this.binding.prankTxt.setTextColor(unselectedColor);
        this.binding.listTxt.setTextColor(unselectedColor);
        this.binding.favTxt.setTextColor(unselectedColor);
    }

    private static class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList();
        private final List<String> fragmentTitleList = new ArrayList();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public void addFragment(Fragment fragment, String title) {
            this.fragmentList.add(fragment);
            this.fragmentTitleList.add(title);
        }

        public Fragment getItem(int position) {
            return this.fragmentList.get(position);
        }

        public int getCount() {
            return this.fragmentList.size();
        }

        public CharSequence getPageTitle(int position) {
            return this.fragmentTitleList.get(position);
        }
    }

    private void initBillingClient(Activity activity) {
        this.activity = activity;
        billingClient = BillingClient.newBuilder(activity)
                .enablePendingPurchases()
                .setListener(this::onPurchaseUpdated)
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Reconnect logic can be added here if needed
            }
        });
    }


    /* access modifiers changed from: private */
    public void queryProductDetails() {

        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("my_plan")
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
        );

        QueryProductDetailsParams params =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(productList)
                        .build();

        billingClient.queryProductDetailsAsync(params,
                (billingResult, productDetailsList) -> {

                    Log.e("BillingInit_Main_", "Billing response code: " + billingResult.getResponseCode());

                    if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK
                            || productDetailsList.isEmpty()) {

                        Log.e("BillingInit_Main_", "❌ queryProductDetailsAsync failed or empty");
                        return;
                    }

                    for (ProductDetails pd : productDetailsList) {

                        Log.d("BillingInit_Main_", "Product ID: " + pd.getProductId());

                        if (pd.getSubscriptionOfferDetails() != null) {

                            for (ProductDetails.SubscriptionOfferDetails offer : pd.getSubscriptionOfferDetails()) {

                                Log.d("BillingInit_Main_",
                                        "Offer ID: " + offer.getOfferId()
                                                + ", Base Plan ID: " + offer.getBasePlanId());

                                for (ProductDetails.PricingPhase phase :
                                        offer.getPricingPhases().getPricingPhaseList()) {

                                    Log.d("BillingInit_Main_",
                                            "Price: " + phase.getFormattedPrice()
                                                    + ", Duration: " + phase.getBillingPeriod());
                                }

                                // Match offer
                                if ("offer1".equals(offer.getOfferId())
                                        || "weekly".equals(offer.getBasePlanId())) {

                                    targetOfferDetails = offer;
                                    Log.d("BillingInit_Main_", "✅ Target offer selected");
                                }
                            }

                        } else {
                            Log.e("BillingInit_Main_",
                                    "❌ No offer details for product: " + pd.getProductId());
                        }
                    }

                    currentProductDetails = productDetailsList.get(0);
                    updatePriceInDialog();
                });
    }
    public void onPurchaseUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == 0 && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        }
    }

    private void handlePurchase(Purchase purchase) {

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED
                && !purchase.isAcknowledged()) {

            AcknowledgePurchaseParams params =
                    AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

            billingClient.acknowledgePurchase(params, billingResult -> {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                    AdsController.setIsInAppPurchased(true);

                    if (premiumDialog != null) {
                        premiumDialog.dismiss();
                    }

                    if (billingCallback != null) {
                        billingCallback.onPurchaseSuccess();
                    }
                }
            });
        }

    }

    public void intPremiumDialog(Activity activity2) {

        this.activity = activity2;
        this.dialogBinding = PremiumDialogBinding.inflate(activity2.getLayoutInflater());
        this.premiumDialog = new Dialog(activity2);

        this.premiumDialog.setContentView(this.dialogBinding.getRoot());
        this.premiumDialog.setCancelable(true);

        if (this.premiumDialog.getWindow() != null) {
            Window window = this.premiumDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                
                // Maintain light status bar icons if on Marshmallow or above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    View decorView = window.getDecorView();
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
            }
        }

        // Loading text
        this.dialogBinding.monthlyTxt.setText(activity2.getString(R.string.loading));
        this.dialogBinding.justPrice.setText(activity2.getString(R.string.loading));

        // ❌ FIXED Close Click
        this.dialogBinding.closeview.setOnClickListener(v -> {
            premiumDialog.dismiss();

            if (billingCallback != null) {
                billingCallback.onDialogDismiss();
            }
        });

        // ❌ FIXED Buy Click
        this.dialogBinding.buyPremium.setOnClickListener(v -> {

            Log.d("BillingClick", "currentProductDetails = " + currentProductDetails);
            Log.d("BillingClick", "targetOfferDetails = " + targetOfferDetails);
            Log.d("BillingClick", "billing ready = " + billingClient.isReady());

            if (currentProductDetails == null || targetOfferDetails == null) {
                Toast.makeText(activity2,
                        activity2.getString(R.string.please_wait_loading),
                        Toast.LENGTH_SHORT).show();
            } else {
                launchPurchaseFlow();
            }
        });

        // ❌ FIXED Dismiss Listener
        this.premiumDialog.setOnDismissListener(dialog -> {
            isPremiumDialogVisible = false;

            if (billingCallback != null) {
                billingCallback.onDialogDismiss();
            }
        });
    }

    public void showPremiumDialog() {
        if (this.premiumDialog != null && !this.premiumDialog.isShowing()) {
            this.isPremiumDialogVisible = true;
            this.premiumDialog.show();
        }
    }

    private void updatePriceInDialog() {

        if (dialogBinding == null) return;

        activity.runOnUiThread(() -> {

            String discountedPriceStr = activity.getString(R.string.loading);
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
                    activity.getString(R.string.start_3_days_free_trial_flat) + " "
                            + activity.getString(R.string.discount_on_1st_order_and_after_that) + " "
                            + discountedPriceStr + " "
                            + activity.getString(R.string.per_week_will_be_charged)
            );

            dialogBinding.justPrice.setText(
                    activity.getString(R.string.discount_on_1st_order_and_after_that) + " "
                            + activity.getString(R.string.just) + " "
                            + discountedPriceStr + " "
                            + activity.getString(R.string.per_month)
            );

        });
    }

    private void launchPurchaseFlow() {
        if (activity!=null)
            AdsController.getInstance().RegisterActivityEventInFb("purchaseFlowLaunched");
        BillingFlowParams.ProductDetailsParams productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(currentProductDetails)
                .setOfferToken(targetOfferDetails.getOfferToken())
                .build();
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(List.of(productDetailsParams))
                .build();
        if (activity != null) billingClient.launchBillingFlow(activity, billingFlowParams);
    }


    private void CreateBottomSheetDialog() {
        this.bottomSheetDialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.exit_dialog_tracker, (ViewGroup) null);
        this.bottomSheetDialog.setContentView(dialogView);
        AdsController.getInstance().loadBigBanner((FrameLayout) dialogView.findViewById(R.id.media_View));
        dialogView.findViewById(R.id.NO).setOnClickListener(v -> MainActivity.this.bottomSheetDialog.dismiss());
        ((TextView) dialogView.findViewById(R.id.YES)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.bottomSheetDialog.dismiss();
                MainActivity.this.finishAffinity();
            }
        });
        this.bottomSheetDialog.setCancelable(true);
    }

    private void showBottomSheetDialog() {
        if (this.bottomSheetDialog != null) {
            this.bottomSheetDialog.show();
        }
    }

    public void handleBackPress () {
        AppOpenManager.isAppOpenAllowed = false;
        if (this.binding.main.isDrawerOpen((int) GravityCompat.START)) {
            this.binding.main.closeDrawer((int) GravityCompat.START);
            return;
        }
        if (this.bottomSheetDialog == null) {
            CreateBottomSheetDialog();
        }
        showBottomSheetDialog();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0) {
            int i = grantResults[0];
        }
    }
}
