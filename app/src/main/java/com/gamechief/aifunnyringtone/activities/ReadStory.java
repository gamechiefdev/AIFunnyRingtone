package com.gamechief.aifunnyringtone.activities;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityReadStoryBinding;
import com.gamechief.aifunnyringtone.models.StoriesData;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.util.Map;

public class ReadStory extends ParentActivity {
    ActivityReadStoryBinding binding;
    private String currentLang = "en";
    private int currentPosition = 0;
    private String currentTitle = "";

    private Map<String, String[]> languageStories = StoriesData.getLanguageStories();
    private Map<String, String[]> languageTitles = StoriesData.getLanguageTitles();

    /* access modifiers changed from: protected */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityReadStoryBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.binding.imageView7.setOnClickListener(onClickListener -> onBackPressed());



        currentPosition = getIntent().getIntExtra("position", 0);
        currentTitle = getIntent().getStringExtra("text");

        updateContent();

        this.binding.changeLang.setOnClickListener(v -> showLanguageDialog());

        this.binding.btn3CopyRowVi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullStory = binding.text.getText().toString() + "\n\n" + binding.getStory.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("story", fullStory);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ReadStory.this, "Story Copied!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.binding.btn4ShareRowVi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullStory = binding.text.getText().toString() + "\n\n" + binding.getStory.getText().toString();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, fullStory);
                startActivity(Intent.createChooser(shareIntent, "Share Story Via"));
            }
        });
    }


    private void updateContent() {
        String[] stories = languageStories.get(currentLang);
        String[] titles = languageTitles.get(currentLang);

        if (stories != null && currentPosition < stories.length) {
            binding.getStory.setText(stories[currentPosition]);
        }

        if (titles != null && currentPosition < titles.length) {
            binding.text.setText(titles[currentPosition]);
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
                updateContent();
                dialog.dismiss();
            });
        }
    }
}
