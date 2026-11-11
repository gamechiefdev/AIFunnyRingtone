package com.gamechief.aifunnyringtone.adsCondition;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static com.gamechief.aifunnyringtone.activities.FunnyJokes.REQUEST_IMAGE;
import static com.gamechief.aifunnyringtone.adsCondition.AdsController.adShowInterval;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gamechief.aifunnyringtone.activities.ImagePickerActivity;
import com.gamechief.aifunnyringtone.activities.ImagePreviewActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ParentActivity extends AppCompatActivity implements AdsController.IJCustomAdMobInterstitialAdListener {
    Context contexnailart;
    public Bundle extras;
    public Resources resourcesnailart;
    public static int countClicks = 0;
    public static AlertDialog builder;
    protected Context context;
    public Toast toast;
    protected DataASSavingPrefs dataDGSavingPrefs;
    public FragmentManager fragmentDGManager;
    public LinearLayoutManager linearDGLayoutManager;

    public boolean IS_REQUEST_IN_PROCESS = false;
    public Resources resourcesDG;

    protected FirebaseAnalytics firebaseAnalytics;
    public static boolean shouldSHowAppTourDialogOnPurchaseDialogDismiss = false;
    protected ImageView ivDGUpgrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        contexnailart = this;
        extras = getIntent ().getExtras ();
        AdsController.initialLayoutComplete = false;
        resourcesnailart = getResources ();
        /* UI only: floating motion for views tagged "float" / "float_late" (all activities) */
        getWindow().getDecorView().post(() -> {
            com.gamechief.aifunnyringtone.ui.UiMotion.floatTagged(getWindow().getDecorView());
            com.gamechief.aifunnyringtone.ui.UiMotion.wobbleTagged(getWindow().getDecorView());
        });
    }

    public void showToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        new Handler().postDelayed(() -> {
            if (toast != null)
                toast.cancel();

        }, 1500);
        if (toast != null)
            toast.show();
    }
    public void showToast(String msg, Context context) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        new Handler().postDelayed(() -> {
            if (toast != null)
                toast.cancel();

        }, 1500);
        if (toast != null)
            toast.show();
    }


    @Override
    public void onInterstitialAdClosed() {}

    public void showSeldomIntersAd(Activity activity) {
        countClicks++;
        if (countClicks % adShowInterval == 0) {
            if (!AdsController.isInAppPurchased) {
                AdsController.showInterstitialAdWithLoader ( activity,null );
                Log.d ( "kurti ", "designs" );

            }
        }
    }
    //....................................Display img dialog ..................................................
    public void showImagePickerOptions(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Dexter.withContext(activity)
                    .withPermissions(Manifest.permission.CAMERA)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                ImagePickerActivity.showImagePickerOptions(activity, new ImagePickerActivity.PickerOptionListener() {
                                    @Override
                                    public void onTakeCameraSelected() {
                                        AppOpenManager.isAppOpenAllowed = false;
                                        launchCameraIntent(activity);
                                    }

                                    @Override
                                    public void onChooseGallerySelected() {
                                        AppOpenManager.isAppOpenAllowed = false;
                                        launchGalleryIntent(activity);
                                    }
                                });
                            } else if (report.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog(activity);
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }else {
            Dexter.withContext(activity)
                    .withPermissions(Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                ImagePickerActivity.showImagePickerOptions(activity, new ImagePickerActivity.PickerOptionListener() {
                                    @Override
                                    public void onTakeCameraSelected() {
                                        launchCameraIntent(activity);
                                    }

                                    @Override
                                    public void onChooseGallerySelected() {
                                        launchGalleryIntent(activity);
                                    }
                                });
                            } else if (report.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog(activity);
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }
    }
    //.............................. choose from Gallery ...............................................
    public void launchGalleryIntent(Activity activity) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        activity.startActivityForResult(intent, REQUEST_IMAGE);
    }
    //............................... choose from Camera ...............................................
    public void launchCameraIntent(Activity activity) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        intent.putExtra("", "");
        activity.startActivityForResult(intent, REQUEST_IMAGE);
    }
    //................................. Get Image Uri .....................................................
    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void showSettingsDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Grant Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings(activity);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }
    // navigating user to app settings
    public void openSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }
    //..................................... Upload \ save image path ............................................
    public void saveSelectedImage(Activity activity, String dirName, Bitmap bitmap) {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String fileName = "" + timeStamp + ".JPG";

        String desFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File direct = new File(desFolder, "/"+dirName);


        if (!direct.exists()) {
            File wallpaperDirectory = new File(direct.toString());
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File(direct.toString()), fileName);
        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
//            Bitmap bitmap = BitmapFactory.decodeFile();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
            out.flush();
            out.close();
        } catch (Exception e) { e.printStackTrace(); }
        String pt = direct.toString()+"/"+fileName;
//        System.out.println("gggggggggggggggggggggttt8891--="+direct.toString()+fileName);
//        System.out.println("gggggggggggggggggggggttt8892--="+pt);
//        callBroadCast(pt);
//        view_waterMark.setVisibility(View.GONE);
        refreshGallery(activity,pt);
        startActivity(new Intent(activity, ImagePreviewActivity.class)
                .putExtra("imagePath",pt));

    }
    boolean value = false;
    public boolean isPermissionsGranted(Activity activity){
        Dexter.withContext(activity)
                .withPermissions(Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            value = true;
                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            value = false;
                            showSettingsDialog(activity);
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
        return value;
    }

    public void saveImageABOVE10(Activity activity,Bitmap bitmap,String path) {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String fileName = "lq" + timeStamp + ".JPG";
        OutputStream fos;
        String desFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver contentResolver = activity.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + path);

                Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (imageUri!=null) {
                    fos = contentResolver.openOutputStream(imageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                    showToast("Saved in Gallery...");
                    Objects.requireNonNull(fos);
                }
                String pp =  File.separator + path;
                String filePath = desFolder+pp+"/"+fileName;
//                System.out.println("gggggggggggggggggggggttt8893--="+filePath);
//                System.out.println("gggggggggggggggggggggttt8894--="+fileName);
                refreshGallery(activity,filePath);
                startActivity(new Intent(activity, ImagePreviewActivity.class)
                        .putExtra("imagePath",filePath)
                );

            }

        } catch (Exception e) { e.printStackTrace(); }

    }

    public void refreshGallery(Activity activity,String fileUri) {
        // Convert to file Object
        File file = new File(fileUri);
        // Write Kitkat version specific code for add entry to gallery database
        // Check for file existence
        if (file.exists()) {
            // Add / Move File
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(new File(fileUri));
            mediaScanIntent.setData(contentUri);
            activity.sendBroadcast(mediaScanIntent);
        } else {
            // Delete File
            try {
                activity.getContentResolver().delete(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.Media.DATA + "='"
                                + new File(fileUri).getPath() + "'", null);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }



    public boolean deleteAll(File file) {
        if (file == null || !file.exists()) return false;
        boolean success = true;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        success &= deleteAll(f);
                    }
                    if (!f.delete()) {
                        Log.w("deleteAll", "Failed to delete " + f);
                        success = false;
                    }
                }
            } else {
                if (!file.delete()) {
                    Log.w("deleteAll", "Failed to delete " + file);
                    success = false;
                }
            }
        } else {
            if (!file.delete()) {
                Log.w("deleteAll", "Failed to delete " + file);
                success = false;
            }
        }
        return success;
    }






}
