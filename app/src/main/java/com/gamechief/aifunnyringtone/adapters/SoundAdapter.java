package com.gamechief.aifunnyringtone.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.models.HistoryStorage;
import com.gamechief.aifunnyringtone.models.SoundModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_AD = 2;
    private final Context context;
    private final OnItemClickListener listener;
    private final List<Object> mixedList = new ArrayList<>();
    private final List<SoundModel> originalList = new ArrayList<>();

    public interface OnItemClickListener {
        void onFavoriteClick(SoundModel soundModel, int i);
        void onItemClick(SoundModel soundModel, int i);
    }

    public SoundAdapter(Context context, List<SoundModel> soundList, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
        updateData(soundList);
    }

    public void updateData(List<SoundModel> newList) {
        this.originalList.clear();
        if (newList != null) {
            this.originalList.addAll(newList);
        }
        mixDataWithAds(this.originalList);
        notifyDataSetChanged();
    }

    private void mixDataWithAds(List<SoundModel> soundList) {
        mixedList.clear();
        if (soundList == null || soundList.isEmpty()) return;

        for (int i = 0; i < soundList.size(); i++) {
            mixedList.add(soundList.get(i));
            if (!AdsController.isInAppPurchased && (i + 1) % 5 == 0) {
                mixedList.add("NATIVE_AD");
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mixedList.get(position) instanceof SoundModel) {
            return TYPE_ITEM;
        } else {
            return TYPE_AD;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new SoundViewHolder(LayoutInflater.from(this.context).inflate(R.layout.sound_itemlayout, parent, false));
        } else {
            return new AdViewHolder(LayoutInflater.from(this.context).inflate(R.layout.ad_native_layout_medium_new, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            SoundViewHolder itemHolder = (SoundViewHolder) holder;
            SoundModel model = (SoundModel) this.mixedList.get(position);
            
            itemHolder.numbers.setText(model.getNumber());
            itemHolder.namesR.setText(model.getName());
            itemHolder.durationR.setText(model.getDuration());
            itemHolder.wallImage.setImageResource(model.getImageResId());
            itemHolder.favorities.setImageResource(model.isFavorite() ? R.drawable.fill_heart : R.drawable.unfill_heart);
            
            itemHolder.favorities.setOnClickListener(v -> {
                boolean fav = !model.isFavorite();
                model.setFavorite(fav);
                itemHolder.favorities.setImageResource(fav ? R.drawable.fill_heart : R.drawable.unfill_heart);
                if (fav) {
                    HistoryStorage.saveFavorite(context, model);
                } else {
                    HistoryStorage.removeFavorite(context, model);
                }
                if (listener != null) {
                    listener.onFavoriteClick(model, originalList.indexOf(model));
                }
            });

            itemHolder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(model, originalList.indexOf(model));
                }
            });
        } else {
            AdViewHolder adHolder = (AdViewHolder) holder;
            loadNativeAd(adHolder);
        }
    }

    private void loadNativeAd(AdViewHolder holder) {
        // Reset visibility and size immediately to handle recycled views
        holder.itemView.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        if (params instanceof RecyclerView.LayoutParams) {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            holder.itemView.setLayoutParams(params);
        }

        AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.navtive_ad_id))
                .forNativeAd(nativeAd -> {
                    // Populate the view on success
                    populateNativeAdView(nativeAd, holder.adContainer);
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        Log.e("SoundAdapter", "Ad failed: " + adError.getMessage());
                        // Hide only on failure
                        holder.itemView.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                        if (params instanceof RecyclerView.LayoutParams) {
                            ((RecyclerView.LayoutParams) params).height = 0;
                            ((RecyclerView.LayoutParams) params).width = 0;
                            holder.itemView.setLayoutParams(params);
                        }
                    }
                })
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        if (adView.getHeadlineView() != null) {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        }

        if (adView.getBodyView() != null) {
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (adView.getCallToActionView() != null) {
            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        }

        if (adView.getIconView() != null) {
            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }

        if (adView.getAdvertiserView() != null) {
            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        }

        if (adView.getMediaView() != null && nativeAd.getMediaContent() != null) {
            adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        }

        adView.setNativeAd(nativeAd);
    }

    public int getItemCount() {
        return this.mixedList.size();
    }

    public static class SoundViewHolder extends RecyclerView.ViewHolder {
        TextView durationR, namesR, numbers;
        ImageView favorities, wallImage;

        public SoundViewHolder(View itemView) {
            super(itemView);
            this.numbers = itemView.findViewById(R.id.numbers);
            this.namesR = itemView.findViewById(R.id.namesR);
            this.durationR = itemView.findViewById(R.id.durationR);
            this.wallImage = itemView.findViewById(R.id.wallImage);
            this.favorities = itemView.findViewById(R.id.favorities);
        }
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {
        NativeAdView adContainer;
        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            adContainer = (NativeAdView) itemView;
        }
    }
}
