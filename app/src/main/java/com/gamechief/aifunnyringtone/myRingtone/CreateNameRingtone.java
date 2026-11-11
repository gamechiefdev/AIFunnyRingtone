package com.gamechief.aifunnyringtone.myRingtone;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.PromptAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityCreateNameRingtoneBinding;
import com.gamechief.aifunnyringtone.models.PromptItem;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateNameRingtone extends ParentActivity {
    ActivityCreateNameRingtoneBinding binding;
    private ActivityResultLauncher<Intent> speechLauncher;
    private boolean shouldShowDialog = false;

    /* access modifiers changed from: protected */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityCreateNameRingtoneBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.binding.imageView7.setOnClickListener(v -> onBackPressed());
        speechLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> matches = result.getData()
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        if (matches != null && !matches.isEmpty()) {
                            binding.getName.setText(matches.get(0));   // 🎯 Set voice text to EditText
                        }
                    }
                }
        );
        this.binding.getRingtonePrompt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (CreateNameRingtone.this.binding.getName.getText().toString().trim().isEmpty()) {
                    CreateNameRingtone.this.binding.getName.setError("Please enter your name first.");
                    CreateNameRingtone.this.binding.getName.requestFocus();
                    return;
                }
                CreateNameRingtone.this.showPromptDialog();
            }
        });
        binding.openMic.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your name");

            try {
                speechLauncher.launch(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Speech input not supported on this device", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldShowDialog) {
            shouldShowDialog = false;
            if (binding.getName.getText() != null && !binding.getName.getText().toString().trim().isEmpty()) {
                showPromptDialog();
            }
        }
    }

    /* access modifiers changed from: private */
    public void showPromptDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_ring_prompt);
        dialog.setCancelable(true);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView3);
        String name = this.binding.getName.getText().toString().trim();
        List<PromptItem> list = new ArrayList<>();
        list.add(new PromptItem("Dear", "Why are you ignoring your phone?"));
        list.add(new PromptItem("Miss", "Your phone is lonely!"));
        list.add(new PromptItem("Mr", "Time to answer before it rings off!"));
        list.add(new PromptItem("Mrs", "Someone is calling you right now!"));
        list.add(new PromptItem("Dear", "Your phone is bored, pick it up!"));
        list.add(new PromptItem("Miss", "Stop scrolling, answer the call!"));
        list.add(new PromptItem("Mr", "Incoming call alert!"));
        list.add(new PromptItem("Mrs", "Your ringtone wants attention!"));
        list.add(new PromptItem("Dear", "Phone wants your love!"));
        list.add(new PromptItem("Miss", "Call waiting, don’t ignore it!"));
        list.add(new PromptItem("Mr", "Ring ring! Don’t delay!"));
        list.add(new PromptItem("Mrs", "Pick up before it cries!"));
        list.add(new PromptItem("Dear", "Your call adventure awaits!"));
        list.add(new PromptItem("Miss", "The call fairy is watching!"));
        list.add(new PromptItem("Mr", "Incoming mischief, answer quick!"));
        list.add(new PromptItem("Mrs", "Don’t let the phone get sad!"));
        list.add(new PromptItem("Dear", "Someone funny wants to talk!"));
        list.add(new PromptItem("Miss", "Ring ring! Attention needed!"));
        list.add(new PromptItem("Mr", "Phone says hello!"));
        list.add(new PromptItem("Mrs", "Urgent LOL call incoming!"));
        list.add(new PromptItem("Dear", "Pick up for fun surprises!"));
        list.add(new PromptItem("Miss", "Your phone is throwing a tantrum!"));
        list.add(new PromptItem("Mr", "Don’t leave the call hanging!"));
        list.add(new PromptItem("Mrs", "Funny call incoming!"));
        list.add(new PromptItem("Dear", "Alert! Someone wants to prank you!"));
        list.add(new PromptItem("Miss", "Ring ring! Emergency laughter!"));
        list.add(new PromptItem("Mr", "Pick up before the ringtone dances!"));
        list.add(new PromptItem("Mrs", "Your phone says hi!"));
        list.add(new PromptItem("Dear", "Laugh incoming! Answer now!"));
        list.add(new PromptItem("Miss", "Ring ring! The fun waits!"));
        list.add(new PromptItem("Mr", "Time for a silly conversation!"));
        list.add(new PromptItem("Mrs", "Pick up! Your ringtone is funny!"));
        list.add(new PromptItem("Dear", "Incoming giggle alert!"));
        list.add(new PromptItem("Miss", "Call wants attention!"));
        list.add(new PromptItem("Mr", "The phone is impatient!"));
        list.add(new PromptItem("Mrs", "Someone is being silly on the call!"));
        list.add(new PromptItem("Dear", "Pick up! Fun is waiting!"));
        list.add(new PromptItem("Miss", "Ring ring! Don’t make it wait!"));
        list.add(new PromptItem("Mr", "Funny business incoming!"));
        list.add(new PromptItem("Mrs", "Your ringtone is playful!"));
        list.add(new PromptItem("Dear", "Urgent LOL call!"));
        list.add(new PromptItem("Miss", "Pick up the giggle line!"));
        list.add(new PromptItem("Mr", "Phone mischief alert!"));
        list.add(new PromptItem("Mrs", "Call wants your attention!"));
        list.add(new PromptItem("Dear", "Ring ring! Silly times ahead!"));
        list.add(new PromptItem("Miss", "Someone wants to prank call you!"));
        list.add(new PromptItem("Mr", "Answer before it laughs!"));
        list.add(new PromptItem("Mrs", "Incoming fun call!"));
        list.add(new PromptItem("Dear", "Don’t miss the LOL moment!"));
        list.add(new PromptItem("Miss", "Your ringtone is alive!"));
        list.add(new PromptItem("Mr", "Time to answer a silly call!"));
        list.add(new PromptItem("Mrs", "Ring ring! Attention required!"));
        PromptAdapter adapter = new PromptAdapter(this, list, name, v -> {
            String finalText = v.getFullText(name);
            Intent intent = new Intent(this, PlayMyNameRingtone.class);
            intent.putExtra("selectedPrompt", finalText);
            shouldShowDialog = true;
            startActivity(intent);
            dialog.dismiss();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        ((ImageView) dialog.findViewById(R.id.closeDialog)).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
