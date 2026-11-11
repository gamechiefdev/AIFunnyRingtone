package com.gamechief.aifunnyringtone.DareGame;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.ResultAdapter;
import com.gamechief.aifunnyringtone.adapters.StackAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivitySpinGameBinding;
import com.gamechief.aifunnyringtone.databinding.DialogListTruthdareBinding;
import com.gamechief.aifunnyringtone.databinding.DialogSelectTruthdareBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SpinGame extends ParentActivity {
    private static final String KEY_NAME_LIST = "name_list";
    private static final String PREF_NAME = "dare_game_pref";
    ActivitySpinGameBinding binding;
    private final String[] dareList = {"Do 10 jumping jacks", "Sing a song loudly", "Dance for 30 seconds", "Talk like a robot", "Act like a monkey", "Send a funny emoji", "Do 5 push-ups", "Tell a joke"};
    /* access modifiers changed from: private */
    public boolean isSpinning = false;
    /* access modifiers changed from: private */
    public float lastAngle = 0.0f;
    /* access modifiers changed from: private */
    public List<String> playerNames = new ArrayList();
    private final List<ResultAdapter.ResultItem> resultItems = new ArrayList();
    private final String[] truthList = {"What is your biggest fear?", "Who was your first crush?", "Have you ever lied to your best friend?", "What is your most embarrassing moment?", "Who do you like the most here?", "What secret are you hiding?", "Have you ever cheated in a game?", "What is your biggest regret?"};
    private SpinWheelView wheelView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivitySpinGameBinding.inflate(getLayoutInflater());
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
        loadPlayerNames();
        loadResults();
        this.wheelView = new SpinWheelView(this);
        this.wheelView.setNames(this.playerNames);
        this.binding.nameContainer.addView(this.wheelView, 0, new FrameLayout.LayoutParams(-1, -1));
        this.binding.spinImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinWheel();
            }
        });
        this.binding.spinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinWheel();
            }
        });
        this.binding.resultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResultDialog(resultItems);
            }
        });
    }





    private void loadPlayerNames() {
        String json = getSharedPreferences(PREF_NAME, 0).getString(KEY_NAME_LIST, (String) null);
        if (json != null) {
            this.playerNames = (List) new Gson().fromJson(json, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        if (this.playerNames == null) {
            this.playerNames = new ArrayList();
        }
        if (this.playerNames.isEmpty()) {
            this.playerNames.add("Player 1");
        }
    }

    private void spinWheel() {
        if (!this.isSpinning) {
            this.isSpinning = true;
            Random random = new Random();
            final float newAngle = this.lastAngle + ((float) ((random.nextInt(5) + 4) * 360)) + ((float) random.nextInt(360));
            RotateAnimation rotate = new RotateAnimation(this.lastAngle, newAngle, 1, 0.5f, 1, 0.5f);
            rotate.setDuration(3000);
            rotate.setFillAfter(true);
            rotate.setInterpolator(new DecelerateInterpolator());
            rotate.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    SpinGame.this.lastAngle = newAngle % 360.0f;
                    SpinGame.this.isSpinning = false;
                    int sliceCount = SpinGame.this.playerNames.size();
                    int index = (int) (((270.0f + (SpinGame.this.lastAngle % 360.0f)) % 360.0f) / (360.0f / ((float) sliceCount)));
                    if (index >= sliceCount) {
                        index = 0;
                    }
                    SpinGame.this.showTruthDareDialog((String) SpinGame.this.playerNames.get(index));
                }
            });
            this.binding.spinImg.startAnimation(rotate);
        }
    }

    /* access modifiers changed from: private */
    public void showTruthDareDialog(String player) {
        Dialog dialog = new Dialog(this);
        DialogSelectTruthdareBinding binding2 = DialogSelectTruthdareBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding2.getRoot());
        dialog.setCancelable(false);
        binding2.getName.setText(player);
        binding2.closeDialog.setOnClickListener(v -> dialog.dismiss());
        binding2.truthList.setOnClickListener(v ->  showCardDialog(player, "It's Truth", this.truthList));
        binding2.dareList.setOnClickListener(v ->  showCardDialog(player, "It's Dare", this.dareList));
        binding2.moveBack.setOnClickListener(v -> {
            dialog.dismiss();
            spinWheel();
        });
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }



    private void showCardDialog(String playerName, String title, String[] dataList) {
        Dialog dialog = new Dialog(this);
        DialogListTruthdareBinding binding2 = DialogListTruthdareBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding2.getRoot());
        dialog.setCancelable(false);
        binding2.getName.setText(title);
        binding2.closeDialog.setOnClickListener(v -> dialog.dismiss());
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
        binding2.linearLayout4.getChildAt(1).setOnClickListener(v -> {
            int topIndex = manager.getTopPosition();
            if (topIndex >= list.size()) {
                topIndex = list.size() - 1;
            }
            this.resultItems.add(new ResultAdapter.ResultItem(playerName, (String) list.get(topIndex)));
            dialog.dismiss();
            showResultDialog(this.resultItems);
        });
        binding2.linearLayout4.getChildAt(0).setOnClickListener(v -> dialog.dismiss());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }



    private void showResultDialog(List<ResultAdapter.ResultItem> results) {
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.result_dialog, (ViewGroup) null);
        dialog.setContentView(view);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.resultList);
        ResultAdapter adapter = new ResultAdapter(this, results, v -> {
            saveResults();

        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        ((ImageView) view.findViewById(R.id.closeDialog)).setOnClickListener(v -> dialog.dismiss());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(-1, -1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }



    private void addResult(String playerName, String selectedItem) {
        this.resultItems.add(new ResultAdapter.ResultItem(playerName, selectedItem));
        saveResults();
    }

    private void loadResults() {
        List<ResultAdapter.ResultItem> saved;
        String json = getSharedPreferences(PREF_NAME, 0).getString("result_list", (String) null);
        if (json != null && (saved = (List) new Gson().fromJson(json, new TypeToken<ArrayList<ResultAdapter.ResultItem>>() {
        }.getType())) != null) {
            this.resultItems.addAll(saved);
        }
    }

    private void saveResults() {
        SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, 0).edit();
        editor.putString("result_list", new Gson().toJson((Object) this.resultItems));
        editor.apply();
    }
}
