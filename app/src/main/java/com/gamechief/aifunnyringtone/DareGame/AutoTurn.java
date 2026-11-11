package com.gamechief.aifunnyringtone.DareGame;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.StackAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityAutoTurnBinding;
import com.gamechief.aifunnyringtone.databinding.DialogListTruthdareBinding;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoTurn extends ParentActivity {
    ActivityAutoTurnBinding binding;
    private final String[] dareList = {"Do 10 jumping jacks", "Sing a song loudly", "Dance for 30 seconds", "Talk like a robot", "Act like a monkey", "Send a funny emoji", "Do 5 push-ups", "Tell a joke"};
    private final String[] truthList = {"What is your biggest fear?", "Who was your first crush?", "Have you ever lied to your best friend?", "What is your most embarrassing moment?", "Who do you like the most here?", "What secret are you hiding?", "Have you ever cheated in a game?", "What is your biggest regret?"};

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityAutoTurnBinding.inflate(getLayoutInflater());
        setContentView((View) this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        UltimateBarX.statusBarOnly((FragmentActivity) this).fitWindow(true).transparent().colorRes(R.color.white).light(true).apply();
        AdsController.getInstance().loadAdaptiveBannerAd(this, this.binding.adView);
        showSeldomIntersAd(this);
        this.binding.imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        this.binding.truthList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCardDialog("It's Truth", truthList);
            }
        });
        this.binding.dareList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCardDialog("It's Dare", dareList);

            }
        });
        this.binding.moveBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }







    private void showCardDialog(String title, String[] dataList) {
        Dialog dialog = new Dialog(this);
        DialogListTruthdareBinding binding2 = DialogListTruthdareBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding2.getRoot());
        dialog.setCancelable(false);
        binding2.getName.setText(title);
        binding2.closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        List<String> list = new ArrayList<>(Arrays.asList(dataList));
        StackAdapter adapter = new StackAdapter(list, R.layout.stack_itemlayout, this, (TextToSpeech) null);
        CardStackLayoutManager manager = new CardStackLayoutManager(this, new CardStackListener() {
            public void onCardDragging(Direction direction, float ratio) {
            }

            public void onCardSwiped(Direction direction) {
            }

            public void onCardRewound() {
            }

            public void onCardCanceled() {
            }

            public void onCardAppeared(View view, int position) {
            }

            public void onCardDisappeared(View view, int position) {
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setSwipeThreshold(0.3f);
        manager.setDirections(Direction.HORIZONTAL);
        binding2.stackView.setLayoutManager(manager);
        binding2.stackView.setAdapter(adapter);
        // Done button - add result and show result dialog
        binding2.linearLayout4.getChildAt(1).setOnClickListener(v -> {
            int topIndex = manager.getTopPosition();
            if (topIndex >= list.size()) topIndex = list.size() - 1;
            String selectedCard = list.get(topIndex);



            dialog.dismiss();

        });

        // Close button
        binding2.linearLayout4.getChildAt(0).setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }


}
