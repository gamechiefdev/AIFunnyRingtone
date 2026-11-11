package com.gamechief.aifunnyringtone.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.Interfaces.OnImageSelection;
import com.gamechief.aifunnyringtone.Interfaces.SelectedPosition;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.Adapter_JokesViewItem;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.adsCondition.SharedPreferenceManager;
import com.gamechief.aifunnyringtone.databinding.ActivityFunnyJokesBinding;
import com.gamechief.aifunnyringtone.models.JokesItemModel;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.lingala.zip4j.util.InternalZipConstants;
import org.json.JSONArray;
import org.json.JSONObject;

public class FunnyJokes extends ParentActivity implements SelectedPosition {
    public static final int REQUEST_IMAGE = 100;
    private static final String PREF_NAME = "AppPrefs";
    private static final String KEY_LANG = "selected_lang";
    
    int AD_COUNT = 4;
    Adapter_JokesViewItem adapter;
    private List<String> backgroundsList;
    ActivityFunnyJokesBinding binding;
    private String categoryName;
    int count = 0;
    private List<JokesItemModel> dataList;
    private TextView empty_tv;
    private FloatingActionButton fab;
    private String intentType;
    private List<String> likedList;
    OnImageSelection onImageSelection;
    private RecyclerView recyclerView;

    private String currentLang = "en";

    public void setListener(OnImageSelection onImageSelection2) {
        this.onImageSelection = onImageSelection2;
    }

    public void position(int po) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load saved language preference
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentLang = prefs.getString(KEY_LANG, "en");

        super.onCreate(savedInstanceState);
        this.binding = ActivityFunnyJokesBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            var systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.categoryName = getIntent().getStringExtra("catName");
        this.intentType = getIntent().getStringExtra("Liked");
        initViews();
    }

    private void initViews() {
        this.binding.imageView7.setOnClickListener(v -> onBackPressed());

        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);

        this.recyclerView = findViewById(R.id.rv_quotesItem);
        this.fab = findViewById(R.id.fab_top);
        this.empty_tv = findViewById(R.id.empty_ty);
        this.dataList = new ArrayList<>();
        this.likedList = loadLiked(this);

        this.fab.setOnClickListener(v -> recyclerView.smoothScrollToPosition(0));

        updateContent();

        if (Build.VERSION.SDK_INT >= 29) {
            loadBackgroundImagesFromIS();
        } else {
            loadSavedBackgroundsImages();
        }

        this.binding.changeLang.setOnClickListener(v -> showLanguageDialog());
    }

    private void updateContent() {
        if (this.intentType == null || !this.intentType.equals("Liked")) {
            loadingTextData(this.categoryName);
        } else {
            loadingLikedData();
        }
    }

    private void showLanguageDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_language_selection);
        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        setupLangOption(dialog, R.id.english, "en");
        setupLangOption(dialog, R.id.hindi, "hi");
        setupLangOption(dialog, R.id.urdu, "ur");
        setupLangOption(dialog, R.id.portuguese, "pt");
        setupLangOption(dialog, R.id.spanish, "es");
        setupLangOption(dialog, R.id.arabic, "ar");
        setupLangOption(dialog, R.id.indonesian, "in");
        setupLangOption(dialog, R.id.bengali, "bn");

        dialog.show();
    }

    private void setupLangOption(Dialog dialog, int viewId, String langCode) {
        TextView textView = dialog.findViewById(viewId);
        if (textView != null) {
            textView.setOnClickListener(v -> {
                currentLang = langCode;
                // Save selection
                getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                        .edit()
                        .putString(KEY_LANG, langCode)
                        .apply();
                
                updateContent();
                dialog.dismiss();
            });
        }
    }

    private void loadingTextData(String category) {
        if (this.dataList != null) this.dataList.clear();

        this.dataList = getQuotesData(this, category);
        loadBackgroundImage();

        if (category != null) {
            this.adapter = new Adapter_JokesViewItem(this, this, this.dataList, this.backgroundsList, this.intentType, this.likedList);
            this.adapter.setHasStableIds(true);
            this.recyclerView.setAdapter(this.adapter);
            setListener(this.adapter);
            this.fab.hide();
            this.adapter.setFab(this.fab);
            this.adapter.notifyDataSetChanged();
            loadAdsAndData();
        }
    }

    private void loadAdsAndData() {
        if (this.adapter != null) {
            List<JokesItemModel> freshData = getQuotesData(this, this.categoryName);
            this.adapter.setData(freshData);
            this.adapter.notifyDataSetChanged();
        }
    }

    public List<String> loadLiked(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("liked", 0);
        Set<String> set = prefs.getStringSet("like", null);
        if (set != null) {
            return new ArrayList<>(set);
        }
        return new ArrayList<>();
    }

    public void isListEmpty(Activity activity) {
        this.recyclerView = activity.findViewById(R.id.rv_quotesItem);
        this.empty_tv.setVisibility(View.VISIBLE);
        this.recyclerView.setVisibility(View.GONE);
    }

    private void loadingLikedData() {
        this.dataList = new ArrayList<>();
        loadBackgroundImage();
        List<String> currentLiked = loadLiked(this);

        this.dataList.clear();
        for (String text : currentLiked) {
            this.dataList.add(new JokesItemModel("", text, false, 0));
        }

        if (this.dataList.isEmpty()) {
            this.empty_tv.setVisibility(View.VISIBLE);
            this.recyclerView.setVisibility(View.GONE);
            return;
        }

        this.empty_tv.setVisibility(View.GONE);
        this.recyclerView.setVisibility(View.VISIBLE);
        this.adapter = new Adapter_JokesViewItem(this, this, this.dataList, this.backgroundsList, this.intentType, currentLiked);
        this.adapter.setHasStableIds(true);
        this.recyclerView.setAdapter(this.adapter);
        setListener(this.adapter);
        this.fab.hide();
        this.adapter.setData(this.dataList);
        this.adapter.setFab(this.fab);
        this.adapter.notifyDataSetChanged();
    }

    private void loadBackgroundImage() {
        this.backgroundsList = new ArrayList<>();
        try {
            String[] list = getResources().getAssets().list("data/backgrounds");
            if (list != null) {
                for (String s : list) {
                    this.backgroundsList.add("file:///android_asset/data/backgrounds/" + s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 29) {
            loadBackgroundImagesFromIS();
        } else {
            loadSavedBackgroundsImages();
        }
    }

    private void loadSavedBackgroundsImages() {
        File[] files;
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + InternalZipConstants.ZIP_FILE_SEPARATOR + getResources().getString(R.string.app_name) + "/uploads/.nomedia";
        File directory = new File(path);
        if (directory.exists() && (files = directory.listFiles()) != null) {
            for (File file : files) {
                if (!this.backgroundsList.contains(path + InternalZipConstants.ZIP_FILE_SEPARATOR + file.getName())) {
                    this.backgroundsList.add(path + InternalZipConstants.ZIP_FILE_SEPARATOR + file.getName());
                }
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), FunnyJokes.class).putExtra("Liked", "Liked"));
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == -1) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), (Uri) data.getParcelableExtra("path"));
                if (Build.VERSION.SDK_INT >= 29) {
                    saveImageIS(this, bitmap);
                } else {
                    String dirName = getResources().getString(R.string.app_name) + "/uploads/.nomedia";
                    saveSelectedImage(this, dirName, bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveSelectedImage(Activity activity, String dirName, Bitmap bitmap) {
        String fileName = "lq" + new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date()) + ".JPG";
        File direct = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString(), InternalZipConstants.ZIP_FILE_SEPARATOR + dirName);
        if (!direct.exists()) direct.mkdirs();
        File file = new File(direct, fileName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int position = new SharedPreferenceManager(activity, "position").getIntValue("cp", 0);
        if (this.onImageSelection != null) {
            this.onImageSelection.selected(file.getAbsolutePath(), position);
        }
    }

    private void saveImageIS(Activity activity, Bitmap bitmap) {
        String fileName = "lq" + new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.US).format(new Date()) + ".JPG";
        File path = new File(getExternalCacheDir(), "uploads");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        try (FileOutputStream out = new FileOutputStream(image)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int position = new SharedPreferenceManager(activity, "position").getIntValue("cp", 0);
        if (this.onImageSelection != null) {
            this.onImageSelection.selected(image.getAbsolutePath(), position);
        }
    }

    private void loadBackgroundImagesFromIS() {
        File[] listFiles;
        File path = new File(getExternalCacheDir(), "uploads");
        if (path.exists() && path.isDirectory() && (listFiles = path.listFiles()) != null) {
            for (File child : listFiles) {
                if (!this.backgroundsList.contains(child.getAbsolutePath())) {
                    this.backgroundsList.add(child.getAbsolutePath());
                }
            }
        }
    }

    public ArrayList<JokesItemModel> getQuotesData(Context context, String categoryName2) {
        ArrayList<JokesItemModel> quoteItemList = new ArrayList<>();

        String fileName = "all_jokes_data";
        if (currentLang != null && !currentLang.equals("en")) {
            fileName = "all_jokes_data_" + currentLang;
        }

        int resId = context.getResources().getIdentifier(fileName, "raw", context.getPackageName());
        if (resId == 0) {
            resId = R.raw.all_jokes_data;
        }

        try {
            InputStream is = context.getResources().openRawResource(resId);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            reader.close();

            JSONObject mainObj = new JSONObject(writer.toString());
            if (mainObj.has(categoryName2)) {
                JSONArray jArray = mainObj.getJSONArray(categoryName2);
                for (int j = jArray.length() - 1; j >= 0; j--) {
                    JokesItemModel model = new JokesItemModel();
                    JSONObject jsonObject = jArray.getJSONObject(j);
                    model.setId(jsonObject.optString("id", ""));
                    model.setText(jsonObject.optString("text", ""));
                    model.setBackground(jsonObject.optInt("background", 0));
                    
                    // Sync with saved likes by ID if IDs are used, otherwise fallback to text match
                    boolean isLiked = likedList.contains(model.getId()) || likedList.contains(model.getText());
                    model.setLiked(isLiked);
                    
                    model.setImgBackground(jsonObject.optString("imgBackground", ""));
                    model.setSelected(jsonObject.optBoolean("isSelected", false));
                    quoteItemList.add(model);
                }
            }
        } catch (Exception e) {
            Log.e("FunnyJokes", "Error loading JSON: " + e.getMessage());
        }
        return quoteItemList;
    }
}
