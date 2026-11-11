package com.gamechief.aifunnyringtone.adsCondition;//package com.gamechief.funnyringtone.adsCondition;
//
//
//import android.app.Dialog;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.CheckBox;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.fragment.app.DialogFragment;
//
//import com.gamechief.plant.MainActivity;
//import com.gamechief.plant.R;
//
//
//public class PurchaseDialog extends DialogFragment {
//    private CheckBox cbDontShow;
//    private TextView tvHeader;
//    private CardView tvMonthly;
//    private CardView tvWeekly;
//    private CardView tvLifeTime;
//    public static Dialog dialog;
//
//    private TextView tvMonthlyTxt;
//    private TextView tvWeeklyTxt;
//    private TextView tvLifeTimeTxt;
//    private TextView tvSuccess;
//    public static String monthlyPriceStr;
//    public static String weeklyPriceStr;
//    public static String lifeTimePriceStr;
//    private int selectedPackage = -1;
//    private final DataASSavingPrefs dataSaving = new DataASSavingPrefs();
//
//    public static PurchaseDialog newInstance(String monthlyPriceStr0, String weeklyPriceStr0, String lifeTimePriceStr0) {
////        monthlyPriceStr = monthlyPriceStr0;
////        weeklyPriceStr = weeklyPriceStr0;
////        lifeTimePriceStr = lifeTimePriceStr0;
//        return new PurchaseDialog();
//    }
//
//        @Override
//        public void onStart() {
//            super.onStart();
//            Dialog dialog = getDialog();
//            if (dialog != null) {
//                int width = ViewGroup.LayoutParams.MATCH_PARENT;
//                int height = ViewGroup.LayoutParams.MATCH_PARENT;
//                dialog.getWindow().setLayout(width, height);
//            }
//        }
//
//        @NonNull
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//             dialog = getActivity() != null ? new Dialog(getActivity()) : null;
//            if (dialog != null) {
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//                Bundle args = getArguments();
//                if (args != null) {
//                    monthlyPriceStr = args.getString("monthlyPrice");
//                    weeklyPriceStr = args.getString("weeklyPrice");
//                    lifeTimePriceStr = args.getString("lifeTimePrice");
//                }
//
//                dialog.getWindow().setLayout(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT
//                );
//            }
//            return dialog;
//        }
//
//        @Override
//        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            if (getDialog() != null && getDialog().getWindow()!=null) {
//                getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//                getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            }
//            View v = inflater.inflate(R.layout.premium_dialog, container, false);
//
////            tvMonthly = v.findViewById(R.id.tvMonthly);
////            tvWeekly = v.findViewById(R.id.tvWeekly);
////            tvLifeTime = v.findViewById(R.id.tvLifeTime);
////
////            tvMonthlyTxt = v.findViewById(R.id.monthlyTxt);
////            tvWeeklyTxt = v.findViewById(R.id.weeklyTxt);
////            tvLifeTimeTxt = v.findViewById(R.id.lifetimeTxt);
//            CardView weekly, lifetime, monthly;
//
//
//            tvMonthlyTxt.setText("Monthly\n" + monthlyPriceStr);
//            tvWeeklyTxt.setText("Weekly\n" + weeklyPriceStr);
//            tvLifeTimeTxt.setText("Lifetime " + lifeTimePriceStr);
////
////            ImageView closeButton = v.findViewById(R.id.closeview);
////            ConstraintLayout purchaseButton = v.findViewById(R.id.buyPremium);
////            weekly = v.findViewById(R.id.tvWeekly);
////            lifetime = v.findViewById(R.id.tvLifeTime);
////            monthly = v.findViewById(R.id.tvMonthly);
////            closeButton.setOnClickListener(v2 -> dismiss());
////
////            purchaseButton.setOnClickListener(v1 -> {
////                    dismiss();
////                if (getActivity() instanceof MainActivity) {
//////                    ((ParentActivityBattery) getActivity()).showPurchaseDGDialog(selectedPackage);
////                }
////            });
////
////            tvMonthly.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    selectedPackage = 1;
////                    monthly.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
////                    weekly.setCardBackgroundColor(getResources().getColor(R.color.white));
////                    lifetime.setCardBackgroundColor(getResources().getColor(R.color.white));
////
////                    tvMonthlyTxt.setTextColor(getResources().getColor(R.color.white));
////                    tvWeeklyTxt.setTextColor(getResources().getColor(R.color.black));
////                    tvLifeTimeTxt.setTextColor(getResources().getColor(R.color.black));
////
////                }
////            });
//
////            tvWeekly.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    selectedPackage = 2;
////                    monthly.setCardBackgroundColor(getResources().getColor(R.color.white));
////                    weekly.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
////                    lifetime.setCardBackgroundColor(getResources().getColor(R.color.white));
////                    tvMonthlyTxt.setTextColor(getResources().getColor(R.color.black));
////                    tvWeeklyTxt.setTextColor(getResources().getColor(R.color.white));
////                    tvLifeTimeTxt.setTextColor(getResources().getColor(R.color.black));
////                }
////            });
////
////            tvLifeTime.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    selectedPackage = 3;
////                    monthly.setCardBackgroundColor(getResources().getColor(R.color.white));
////                    weekly.setCardBackgroundColor(getResources().getColor(R.color.white));
////                    lifetime.setCardBackgroundColor(getResources().getColor(R.color.purple_200));
////                    tvMonthlyTxt.setTextColor(getResources().getColor(R.color.black));
////                    tvWeeklyTxt.setTextColor(getResources().getColor(R.color.black));
////                    tvLifeTimeTxt.setTextColor(getResources().getColor(R.color.white));                }
////            });
//
////            selectedPackage = 3;
////
////
////            if (isIsInAppPurchased() && !dataSaving.getBooleanPrefValue(getContext(), IS_LIFE_TIME_MEMBERSHIP)) {
////                tvWeekly.setVisibility(View.GONE);
////                tvMonthly.setVisibility(View.GONE);
////                tvLifeTime.setVisibility(View.GONE);
////
////            } else {
////                tvWeekly.setVisibility(View.VISIBLE);
////                tvMonthly.setVisibility(View.VISIBLE);
////                lifetime.setVisibility(View.VISIBLE);
////
////
////            }
//
//            return v;
//        }
//
//
//
//        @Override
//        public void dismiss() {
//            super.dismiss();
//
//
//            if (getActivity() instanceof MainActivity && MainActivity.shouldSHowAppTourDialogOnPurchaseDialogDismiss &&
//                    !dataSaving.getBooleanPrefValue(getActivity(), AppASConfig.PREF_IS_APP_TOUR_SHOWN)) {
//                if (getActivity() != null && !getActivity().isDestroyed() && getActivity() instanceof MainActivity) {
////                    ((MainActivity) getActivity()).currentDemoScreen = 0;
////                    ((MainActivity) getActivity()).showAppTourDialog();
//                }
//            }
//        }
//    }
//
//
