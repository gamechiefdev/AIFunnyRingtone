package com.google.android.ads.nativetemplates;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
public class TemplateView extends FrameLayout {
  private int templateType;
  private NativeTemplateStyle styles;
  private NativeAd nativeAd;
  private NativeAdView nativeAdView;

  private TextView primaryView;
  private TextView secondaryView;
  private RatingBar ratingBar;
  private TextView tertiaryView,tv_rating;
  private ImageView iconView,iv_store;
  private MediaView mediaView;
  private Button callToActionView;
  private ConstraintLayout background;

  private static final String MEDIUM_TEMPLATE = "medium_template";
  private static final String SMALL_TEMPLATE = "small_template";
  private static final String LARGE_TEMPLATE = "large_template";

  public TemplateView(Context context) {
    super(context);
  }

  public TemplateView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initView(context, attrs);
  }

  public TemplateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView(context, attrs);
  }

  public TemplateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initView(context, attrs);
  }

  public void setStyles(NativeTemplateStyle styles) {
    this.styles = styles;
//    this.applyStyles();
  }

  public NativeAdView getNativeAdView() {
    return nativeAdView;
  }

  private boolean adHasOnlyStore(NativeAd nativeAd) {
    String store = nativeAd.getStore();
    String advertiser = nativeAd.getAdvertiser();
    return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser);
  }

  public void setNativeAd(@Nullable NativeAd nativeAd) {
    this.nativeAd = nativeAd;
    if (nativeAd!=null) {
      String store = nativeAd.getStore();
      String price = nativeAd.getPrice();
      String advertiser = nativeAd.getAdvertiser();
      String headline = nativeAd.getHeadline();
      String body = nativeAd.getBody();
      String cta = nativeAd.getCallToAction();
      Double starRating = nativeAd.getStarRating();
      NativeAd.Image icon = nativeAd.getIcon();

      String secondaryText;

      if (nativeAdView != null) {
        nativeAdView.setCallToActionView(callToActionView);
        nativeAdView.setHeadlineView(primaryView);
        nativeAdView.setMediaView(mediaView);
//      if (nativeAdView.getMediaView()!=null) nativeAdView.getMediaView().setImageScaleType(ImageView.ScaleType.FIT_XY);
        if (secondaryView != null) secondaryView.setVisibility(VISIBLE);
        if (adHasOnlyStore(nativeAd)) {
          nativeAdView.setStoreView(secondaryView);
          secondaryText = store;
        } else if (!TextUtils.isEmpty(advertiser)) {
          nativeAdView.setAdvertiserView(secondaryView);
          secondaryText = advertiser;
        } else {
          secondaryText = "";
        }

        if (primaryView != null && headline != null) primaryView.setText(headline);
        if (callToActionView != null && cta != null) callToActionView.setText(cta);

        //  Set the secondary view to be the star rating if available.
        if (starRating != null && starRating > 0) {
          if (secondaryView != null) secondaryView.setVisibility(GONE);
          if (ratingBar != null) {
            ratingBar.setVisibility(VISIBLE);
            ratingBar.setRating(starRating.floatValue());
          }
          if (tv_rating != null) tv_rating.setText(String.valueOf(starRating.floatValue()));
          if (ratingBar != null) nativeAdView.setStarRatingView(ratingBar);
        } else {
          if (secondaryText != null) secondaryView.setText(secondaryText);
          if (secondaryView != null) secondaryView.setVisibility(VISIBLE);
          if (ratingBar != null) ratingBar.setVisibility(GONE);
          if (tv_rating != null) tv_rating.setVisibility(GONE);
        }

        if (iconView != null && icon != null) {
          iconView.setVisibility(VISIBLE);
          iconView.setImageDrawable(icon.getDrawable());
        } else {
          if (iconView != null) iconView.setVisibility(GONE);
        }

        if (tertiaryView != null) {
          if (price == null) price = "";
          if (store == null) store = "";
          if (body == null) body = "";

          String styledText =
//                  price + "\t \t \t" + store + "<br>" + "<font color='black'>" + body + "</font>."+
                  "<font color='black'>" + body + "</font>."
//                          + " Ratio: "+nativeAd.getMediaContent().getAspectRatio()
//                          + " Size: "+nativeAd.getMediaContent().getMainImage().getIntrinsicWidth()+"/"
//                  +nativeAd.getMediaContent().getMainImage().getMinimumHeight()
                  ;
          tertiaryView.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
          nativeAdView.setBodyView(tertiaryView);

        }
        nativeAdView.setNativeAd(nativeAd);
      }

    }
  }

  /**
   * To prevent memory leaks, make sure to destroy your ad when you don't need it anymore. This
   * method does not destroy the template view.
   * https://developers.google.com/admob/android/native-unified#destroy_ad
   */
  public void destroyNativeAd() {
    nativeAd.destroy();
  }

  public String getTemplateTypeName() {
    if (templateType == R.layout.gnt_medium_template_view) {
      return MEDIUM_TEMPLATE;
    }
    else if (templateType == R.layout.gnt_small_template_view) {
      return SMALL_TEMPLATE;
    }
    else if (templateType == R.layout.gnt_large_template_view) {
      return LARGE_TEMPLATE;
    }
    return "";
  }

  public void setTemplateType(int templateType) {
    this.templateType = templateType;
  }

  private void initView(Context context, AttributeSet attributeSet) {
    TypedArray attributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.gnt_TemplateView, 0, 0);
    try {
      templateType = attributes.getResourceId(R.styleable.gnt_TemplateView_gnt_template_type, R.layout.gnt_medium_template_view);
    } finally {
      attributes.recycle();
    }
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(templateType, this);
  }

  @Override
  public void onFinishInflate() {
    super.onFinishInflate();
    nativeAdView = (NativeAdView) findViewById(R.id.native_ad_view);
    primaryView = (TextView) findViewById(R.id.primary);
    secondaryView = (TextView) findViewById(R.id.secondary);
    tertiaryView = (TextView) findViewById(R.id.body);

    iv_store = findViewById(R.id.iv_store);
    tv_rating = findViewById(R.id.tv_rating);

    ratingBar = (RatingBar) findViewById(R.id.rating_bar);
    ratingBar.setEnabled(false);

    callToActionView = (Button) findViewById(R.id.cta);
    iconView = (ImageView) findViewById(R.id.icon);
    mediaView = (MediaView) findViewById(R.id.media_view);
    background =  findViewById(R.id.background);
  }
}
