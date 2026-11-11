package com.gamechief.aifunnyringtone.adapters;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import com.gamechief.aifunnyringtone.CustomUiViews.CustomPopupButton;
import com.gamechief.aifunnyringtone.Interfaces.OnImageSelection;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.activities.FunnyJokes;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.models.JokesItemModel;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import net.lingala.zip4j.util.InternalZipConstants;

public class Adapter_JokesViewItem extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnImageSelection {
    private static final int AD_INTERVAL = 5;
    static int OFFSET = 5;
    private static final int TYPE_AD = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_NORMAL = 1;
    /* access modifiers changed from: private */
    public final Activity activity;
    /* access modifiers changed from: private */
    public final List<String> backgroundsList;
    /* access modifiers changed from: private */
    public final Context context;
    private final List<JokesItemModel> dataList;
    private FloatingActionButton fab;

    boolean isLiked = false;
    private final List<String> likedList;
    private final List<Object> mixedData = new ArrayList();
    private final List<NativeAd> nativeAds = new ArrayList();
    int p = 0;
    String path;
    int position_ro = -1;
    boolean value = false;
    int viewAdsType = 0;

    public void selected(String img, int po) {
        this.path = img;
        this.backgroundsList.add(img);
        this.dataList.get(po).setImgBackground(img);
        this.dataList.get(po).setSelected(true);
        notifyItemChanged(po);
        System.out.println("yyyyyyyyyyyyyyyyyy1--=" + po);
    }

    public void setFab(FloatingActionButton fab2) {
        this.fab = fab2;
    }

    public String photoName() {
        return null;
    }

    // ✅ Interface for image picker
    public interface OnPickImageClick {
        void onPickImage(int position);
    }

    private OnPickImageClick pickImageListener;

    public void setOnPickImageClick(OnPickImageClick listener) {
        this.pickImageListener = listener;
    }

    public Adapter_JokesViewItem(Context context, Activity activity,
                                 List<JokesItemModel> dataList,
                                 List<String> backgroundsList,
                                 String intentType,
                                 List<String> likedList) {

        this.context = context;
        this.activity = activity;
        this.dataList = dataList;
        this.backgroundsList = backgroundsList;
        this.likedList = likedList;

        loadBackgroundsFromAssets(); // 🔥 IMPORTANT
    }
    // ✅ LOAD FROM ASSETS
    private void loadBackgroundsFromAssets() {
        try {
            String[] files = context.getAssets().list("backgrounds");

            if (files != null && files.length > 0) {
                backgroundsList.clear();

                for (String file : files) {
                    String path = "file:///android_asset/backgrounds/" + file;
                    backgroundsList.add(path);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        System.out.println("vvvvvvvvvvvvvvvvvvvvvvvt00=" + viewType);
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.jokes_itemlayout, parent, false));
    }

    public void setData(List<JokesItemModel> data) {
        this.dataList.clear();
        this.dataList.addAll(data);
        mixDataWithAds();
    }

    public void setAds(List<NativeAd> ads) {
        this.nativeAds.clear();
        this.nativeAds.addAll(ads);
        mixDataWithAds();
    }

    public void mixDataWithAds() {
        this.mixedData.clear();
        if (this.nativeAds.isEmpty()) {
            this.mixedData.addAll(dataList);
            notifyDataSetChanged();
            return;
        }
        int adIndex = 0;
        for (int i = 0; i < dataList.size(); i++) {
            this.mixedData.add(this.dataList.get(i));
            if ((i + 1) % 5 == 0 && i != this.dataList.size() - 1 && adIndex < this.nativeAds.size()) {
                this.mixedData.add(this.nativeAds.get(adIndex));
                adIndex = (adIndex + 1) % this.nativeAds.size();
            }
        }
        notifyDataSetChanged();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);
        if (viewHolder.getAdapterPosition() > 8) {
            this.fab.show();
        } else {
            this.fab.hide();
        }
        final ItemViewHolder holder = (ItemViewHolder) viewHolder;
        final JokesItemModel quoItemModel = (JokesItemModel) this.mixedData.get(position);
        if (quoItemModel != null) {
            holder.textView.setText(quoItemModel.getText());
            if (quoItemModel.isSelected()) {
                Glide.with(this.context).load(quoItemModel.getImgBackground()).into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.color.light);
            }
            SharedPreferences.Editor edit = this.activity.getSharedPreferences("liked", 0).edit();
            holder.btn2_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AdsController.isInAppPurchased) {
                        holder.view_waterMark.setVisibility(View.VISIBLE);
                    }
                    holder.relativeLayout.setDrawingCacheEnabled(true);
                    holder.relativeLayout.buildDrawingCache();
                    Bitmap bitmap = Bitmap.createBitmap(holder.relativeLayout.getDrawingCache());
                    String folderName = activity.getResources().getString(R.string.app_name);
                    if (Build.VERSION.SDK_INT >= 29) {
                        saveImageABOVE10(activity, bitmap, folderName);
                    } else if (isPermissionsGranted()) {
                        saveSelectedImage(activity, folderName, bitmap, holder.view_waterMark);
                        showToast("Saved in Gallery...");
                    }
                    if (!AdsController.isInAppPurchased) {
                        holder.view_waterMark.setVisibility(View.INVISIBLE);
                    }
                }
            });
            holder.btn3_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("AI Ringtone", quoItemModel.getText()));
                    showToast("\" " + quoItemModel.getText() + " \"\n  Copied");
                }
            });
            holder.btn4_share.setOnClickListener(v -> {

                if (!AdsController.isInAppPurchased) {
                    holder.view_waterMark.setVisibility(View.VISIBLE);
                }
                holder.relativeLayout.setDrawingCacheEnabled(true);
                holder.relativeLayout.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(holder.relativeLayout.getDrawingCache());
                if (Build.VERSION.SDK_INT >= 29) {
                    Uri bitmapUri = Uri.parse(MediaStore.Images.Media.insertImage(this.activity.getContentResolver(), bitmap, "AI Ringtone", (String) null));
                    Intent intent = new Intent("android.intent.action.SEND");
                    if (!AdsController.isInAppPurchased) {
                        intent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=com.gcl.funnyringtone.aiwallpapers.airingtones.superfunny");
                    }
                    intent.setType("image/*");
                    intent.putExtra("android.intent.extra.STREAM", bitmapUri);
                    this.activity.startActivity(Intent.createChooser(intent, "AI Ringtone"));
                    if (!AdsController.isInAppPurchased) {
                        holder.view_waterMark.setVisibility(View.INVISIBLE);
                    }
                } else if (isPermissionsGranted()) {
                    Uri bitmapUri2 = Uri.parse(MediaStore.Images.Media.insertImage(this.activity.getContentResolver(), bitmap, "AI Ringtone", (String) null));
                    Intent intent2 = new Intent("android.intent.action.SEND");
                    if (!AdsController.isInAppPurchased) {
                        intent2.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=com.gcl.funnyringtone.aiwallpapers.airingtones.superfunny");
                    }
                    intent2.setType("image/*");
                    intent2.putExtra("android.intent.extra.STREAM", bitmapUri2);
                    this.activity.startActivity(Intent.createChooser(intent2, "AIFunnyRingtone"));
                    if (!AdsController.isInAppPurchased) {
                        holder.view_waterMark.setVisibility(View.INVISIBLE);
                    }
                }

            });
            holder.btn5_next.setOnButtonClickListener(new CustomPopupButton.OnButtonClickListener() {

                @Override public void onLongHoldStart(View v) {}
                @Override public void onLongHoldEnd(View v) {}

                @Override
                public void onClick(View v) {

                    if (backgroundsList == null || backgroundsList.isEmpty()) {
                        Toast.makeText(context, "No backgrounds available", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (p >= backgroundsList.size()) {
                        p = 0;
                    }

                    quoItemModel.setSelected(true);
                    quoItemModel.setBackground(holder.getAdapterPosition());
                    position_ro = holder.getAdapterPosition();

                    String bg = backgroundsList.get(p);
                    quoItemModel.setImgBackground(bg);

                    Glide.with(context)
                            .asBitmap()
                            .load(bg)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource,
                                                            @Nullable Transition<? super Bitmap> transition) {
                                    holder.imageView.setImageBitmap(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {}
                            });

                    p = (p + 1) % backgroundsList.size(); // ✅ safe loop
                }
            });

//            SharedPreferenceManager pm = new SharedPreferenceManager(context, "position");
//            holder.btn7_load.setOnClickListener(v -> {
//                pm.setIntValue("cp", viewHolder.getAdapterPosition());
//                FunnyJokes itemsActivity = new FunnyJokes();
//                Toast.makeText(itemsActivity, "Clicked", Toast.LENGTH_SHORT).show();
//                itemsActivity.showImagePickerOptions(activity);
//            });
        }
    }



    public int getItemViewType(int position) {
        return (this.mixedData.get(position) instanceof NativeAd) ^ true ? 1 : 0;
    }

    private void ShareIt(View rootView) {
        if (rootView != null && this.context != null) {
            rootView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
            if (bitmap != null) {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/Image.png");
                try {
                    FileOutputStream outputStream = new FileOutputStream(String.valueOf(mediaStorageDir));
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri imageUri = FileProvider.getUriForFile(this.context, this.activity.getApplicationContext().getPackageName() + ".provider", mediaStorageDir);
                if (imageUri != null) {
                    Intent waIntent = new Intent("android.intent.action.SEND");
                    waIntent.setType("image/*");
                    waIntent.putExtra("android.intent.extra.STREAM", imageUri);
                    this.activity.startActivity(Intent.createChooser(waIntent, "Share with"));
                }
            }
        }
    }

    private Uri getLocalBitmapUri(Context context2, Bitmap bmp) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath(), "true_love_" + System.currentTimeMillis() + ".jpg");
        boolean isSDPresent = Environment.getExternalStorageState().equals("mounted");
        boolean isExternalStorageRemovable = Environment.isExternalStorageRemovable();
        if (isSDPresent) {
            System.out.println("sssssssssssssssssssssssssssdcrd01" + file.getAbsolutePath());
        } else {
            System.out.println("sssssssssssssssssssssssssssdcrd03" + file.getAbsolutePath());
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Uri.fromFile(file);
        } catch (FileNotFoundException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public void removeAt(int position) {
        if (this.dataList.size() > 0) {
            this.dataList.remove(position);
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, this.dataList.size());
    }

    public Context getContext() {
        return this.context;
    }

    public int getItemCount() {
        return this.mixedData.size();
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public void showToast(String msg){
        Toast toast =  Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        new Handler().postDelayed(() -> {
            if (toast!=null) toast.cancel();

        },1500);
        if (toast!=null) toast.show();
    }



    public void saveSelectedImage(Activity activity2, String dirName, Bitmap bitmap, RelativeLayout view_waterMark) {
        String fileName = "" + new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()) + ".JPG";
        File direct = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString(), InternalZipConstants.ZIP_FILE_SEPARATOR + dirName);
        if (!direct.exists()) {
            new File(direct.toString()).mkdirs();
        }
        File file = new File(new File(direct.toString()), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshGallery(direct.toString() + InternalZipConstants.ZIP_FILE_SEPARATOR + fileName);
    }

    private void saveImageABOVE10(Activity activity2, Bitmap bitmap, String path2) {
        String fileName = "lq" + new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()) + ".JPG";
        String desFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                ContentResolver contentResolver = activity2.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", fileName);
                contentValues.put("mime_type", "image/*");
                contentValues.put("relative_path", Environment.DIRECTORY_PICTURES + File.separator + path2);
                Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (imageUri != null) {
                    OutputStream fos = contentResolver.openOutputStream(imageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                    showToast("Saved in Gallery...");
                    Objects.requireNonNull(fos);
                }
                refreshGallery(desFolder + (File.separator + path2) + InternalZipConstants.ZIP_FILE_SEPARATOR + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshGallery(String fileUri) {
        if (new File(fileUri).exists()) {
            Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
            mediaScanIntent.setData(Uri.fromFile(new File(fileUri)));
            this.activity.sendBroadcast(mediaScanIntent);
            return;
        }
        try {
            this.activity.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "_data='" + new File(fileUri).getPath() + "'", (String[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPermissionsGranted() {
        Dexter.withContext(this.activity).withPermissions("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Adapter_JokesViewItem.this.value = true;
                } else if (report.isAnyPermissionPermanentlyDenied()) {
                    new FunnyJokes().showSettingsDialog(Adapter_JokesViewItem.this.activity);
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
        return this.value;
    }
}
