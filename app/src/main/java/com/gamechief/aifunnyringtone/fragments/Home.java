package com.gamechief.aifunnyringtone.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gamechief.aifunnyringtone.DareGame.SelectionActivity;
import com.gamechief.aifunnyringtone.PuzzleGame.PuzzleList;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.activities.AITextSound;
import com.gamechief.aifunnyringtone.activities.AiRecordSound;
import com.gamechief.aifunnyringtone.activities.DonkeyScreen;
import com.gamechief.aifunnyringtone.activities.FunnyAlarm;
import com.gamechief.aifunnyringtone.activities.FunnyJokes;
import com.gamechief.aifunnyringtone.activities.GifActivity;
import com.gamechief.aifunnyringtone.activities.SoundList;
import com.gamechief.aifunnyringtone.activities.StickersPackList;
import com.gamechief.aifunnyringtone.activities.StroyList;
import com.gamechief.aifunnyringtone.adapters.SoundCategoryAdapter;
import com.gamechief.aifunnyringtone.databinding.FragmentHomeBinding;
import com.gamechief.aifunnyringtone.models.SoundCategoryModel;
import com.gamechief.aifunnyringtone.myRingtone.CreateNameRingtone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class Home extends Fragment {
    SoundCategoryAdapter adapter;
    FragmentHomeBinding binding;
    ArrayList<SoundCategoryModel> categoryList = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.categoryList = new ArrayList<>();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = FragmentHomeBinding.inflate(inflater, container, false);
        setGreetingAndAnimate();
        this.binding.funnySticker.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), StickersPackList.class));
        });
        this.binding.funnyJokes.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), FunnyJokes.class).putExtra("catName", "Funny"));

        });
        this.binding.gifMemes.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), GifActivity.class));
        });
        this.binding.spinGame.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SelectionActivity.class));
        });
        this.binding.myRingName.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CreateNameRingtone.class));
        });
        this.binding.funnyStories.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), StroyList.class));
        });
        this.binding.puzzleGame.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), PuzzleList.class));
        });
        if (this.categoryList.isEmpty()) {
            setupCategories();
        }
        setupRecycler();
        this.binding.goAItextSound.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AITextSound.class));
        });
        this.binding.goRecord.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AiRecordSound.class));
        });

        this.binding.funnyDonkey.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), DonkeyScreen.class));
        });

        return this.binding.getRoot();
    }





    private void setupRecycler() {
        this.adapter = new SoundCategoryAdapter(getContext(), this.categoryList, v -> {
            Intent intent;
            if ("Funny Alarms".equals(v.title)) {
                intent = new Intent(getContext(), FunnyAlarm.class);
            } else {
                intent = new Intent(getContext(), SoundList.class);
            }
            intent.putIntegerArrayListExtra("sound_res", v.soundRes);
            intent.putIntegerArrayListExtra("image_res", v.imageRes);
            intent.putStringArrayListExtra("sound_names", v.soundNames);
            startActivity(intent);
        });
        this.binding.listiem.setLayoutManager(new GridLayoutManager(getContext(), 3));
        this.binding.listiem.setAdapter(this.adapter);
    }


    private void setupCategories() {
        this.categoryList.add(new SoundCategoryModel("Funny", R.drawable.three, new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.raw.funny1), Integer.valueOf(R.raw.funny2), Integer.valueOf(R.raw.funny3), Integer.valueOf(R.raw.funny4), Integer.valueOf(R.raw.funny5),
                Integer.valueOf(R.raw.funny6), Integer.valueOf(R.raw.funny7), Integer.valueOf(R.raw.funny8), Integer.valueOf(R.raw.funny9), Integer.valueOf(R.raw.funny10),
                Integer.valueOf(R.raw.funny11), Integer.valueOf(R.raw.funny12), Integer.valueOf(R.raw.funny13), Integer.valueOf(R.raw.funny14), Integer.valueOf(R.raw.funny15),
                Integer.valueOf(R.raw.funny16), Integer.valueOf(R.raw.funny17), Integer.valueOf(R.raw.funny18), Integer.valueOf(R.raw.funny19), Integer.valueOf(R.raw.funny20),
                Integer.valueOf(R.raw.funny21), Integer.valueOf(R.raw.funny22), Integer.valueOf(R.raw.funny23), Integer.valueOf(R.raw.funny24), Integer.valueOf(R.raw.funny25),
                Integer.valueOf(R.raw.funny26), Integer.valueOf(R.raw.funny27), Integer.valueOf(R.raw.funny28), Integer.valueOf(R.raw.funny29), Integer.valueOf(R.raw.funny30)})),
                new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.drawable.funny1)})),
                new ArrayList(Arrays.asList(new String[]{"Funny 1", "Funny 2", "Funny 3", "Funny 4", "Funny 5", "Funny 6", "Funny 7", "Funny 8", "Funny 9", "Funny 10",
                "Funny 11", "Funny 12", "Funny 13", "Funny 14", "Funny 15", "Funny 16", "Funny 17", "Funny 18", "Funny 19", "Funny 20",
                "Funny 21", "Funny 22", "Funny 23", "Funny 24", "Funny 25", "Funny 26", "Funny 27", "Funny 28", "Funny 29", "Funny 30"}))));

        this.categoryList.add(new SoundCategoryModel("Animal", R.drawable.four, new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.raw.animal1), Integer.valueOf(R.raw.animal2), Integer.valueOf(R.raw.animal3), Integer.valueOf(R.raw.animal4), Integer.valueOf(R.raw.animal5), Integer.valueOf(R.raw.animal6), Integer.valueOf(R.raw.animal7),
                Integer.valueOf(R.raw.animal8), Integer.valueOf(R.raw.animal9), Integer.valueOf(R.raw.animal10), Integer.valueOf(R.raw.animal11), Integer.valueOf(R.raw.animal12), Integer.valueOf(R.raw.animal13), Integer.valueOf(R.raw.animal14), Integer.valueOf(R.raw.animal15),
                Integer.valueOf(R.raw.animal16), Integer.valueOf(R.raw.animal17), Integer.valueOf(R.raw.animal18), Integer.valueOf(R.raw.animal19), Integer.valueOf(R.raw.animal20), Integer.valueOf(R.raw.animal21), Integer.valueOf(R.raw.animal22), Integer.valueOf(R.raw.animal23), Integer.valueOf(R.raw.animal24), Integer.valueOf(R.raw.animal25),
                Integer.valueOf(R.raw.animal26), Integer.valueOf(R.raw.animal27), Integer.valueOf(R.raw.animal28), Integer.valueOf(R.raw.animal29), Integer.valueOf(R.raw.animal30), Integer.valueOf(R.raw.animal31), Integer.valueOf(R.raw.animal32)})),
                new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.drawable.animal1)})),
                new ArrayList(Arrays.asList(new String[]{"Animal 1", "Animal 2", "Animal 3", "Animal 4", "Animal 5", "Animal 6", "Animal 7",
                "Animal 8", "Animal 9", "Animal 10", "Animal 11", "Animal 12", "Animal 13", "Animal 14", "Animal 15",
                "Animal 16", "Animal 17", "Animal 18", "Animal 19", "Animal 20", "Animal 21", "Animal 22", "Animal 23", "Animal 24", "Animal 25",
                "Animal 26", "Animal 27", "Animal 28", "Animal 29", "Animal 30", "Animal 31", "Animal 32"}))));

        this.categoryList.add(new SoundCategoryModel("Laugh", R.drawable.five, new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.raw.laugh1), Integer.valueOf(R.raw.laugh2), Integer.valueOf(R.raw.laugh3), Integer.valueOf(R.raw.laugh4), Integer.valueOf(R.raw.laugh5),
                Integer.valueOf(R.raw.laugh6), Integer.valueOf(R.raw.laugh7), Integer.valueOf(R.raw.laugh8), Integer.valueOf(R.raw.laugh9), Integer.valueOf(R.raw.laugh10), Integer.valueOf(R.raw.laugh11), Integer.valueOf(R.raw.laugh12), Integer.valueOf(R.raw.laugh13),
                Integer.valueOf(R.raw.laugh14), Integer.valueOf(R.raw.laugh15), Integer.valueOf(R.raw.laugh16), Integer.valueOf(R.raw.laugh17), Integer.valueOf(R.raw.laugh18), Integer.valueOf(R.raw.laugh19), Integer.valueOf(R.raw.laugh20),
                Integer.valueOf(R.raw.laugh21), Integer.valueOf(R.raw.laugh22), Integer.valueOf(R.raw.laugh23), Integer.valueOf(R.raw.laugh24), Integer.valueOf(R.raw.laugh25), Integer.valueOf(R.raw.laugh26), Integer.valueOf(R.raw.laugh27),
                Integer.valueOf(R.raw.laugh28), Integer.valueOf(R.raw.laugh29), Integer.valueOf(R.raw.laugh30)})),
                new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.drawable.laugh1)})),
                new ArrayList(Arrays.asList(new String[]{"Laugh 1", "Laugh 2", "Laugh 3", "Laugh 4", "Laugh 5", "Laugh 6", "Laugh 7",
                "Laugh 8", "Laugh 9", "Laugh 10", "Laugh 11", "Laugh 12", "Laugh 13", "Laugh 14", "Laugh 15",
                "Laugh 16", "Laugh 17", "Laugh 18", "Laugh 19", "Laugh 20", "Laugh 21", "Laugh 22", "Laugh 23", "Laugh 24", "Laugh 25",
                "Laugh 26", "Laugh 27", "Laugh 28", "Laugh 29", "Laugh 30"}))));

        this.categoryList.add(new SoundCategoryModel("Fart", R.drawable.six, new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.raw.fart1), Integer.valueOf(R.raw.fart2), Integer.valueOf(R.raw.fart3),
                Integer.valueOf(R.raw.fart4), Integer.valueOf(R.raw.fart5), Integer.valueOf(R.raw.fart6), Integer.valueOf(R.raw.fart7), Integer.valueOf(R.raw.fart8), Integer.valueOf(R.raw.fart9), Integer.valueOf(R.raw.fart10),
                Integer.valueOf(R.raw.fart11), Integer.valueOf(R.raw.fart12), Integer.valueOf(R.raw.fart13), Integer.valueOf(R.raw.fart14), Integer.valueOf(R.raw.fart15),
                Integer.valueOf(R.raw.fart16), Integer.valueOf(R.raw.fart17), Integer.valueOf(R.raw.fart18), Integer.valueOf(R.raw.fart19), Integer.valueOf(R.raw.fart20),
                Integer.valueOf(R.raw.fart21), Integer.valueOf(R.raw.fart22), Integer.valueOf(R.raw.fart23), Integer.valueOf(R.raw.fart24), Integer.valueOf(R.raw.fart25),
                Integer.valueOf(R.raw.fart26), Integer.valueOf(R.raw.fart27), Integer.valueOf(R.raw.fart28), Integer.valueOf(R.raw.fart29), Integer.valueOf(R.raw.fart30)})),
                new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.drawable.six)})),
                new ArrayList(Arrays.asList(new String[]{"Fart 1", "Fart 2", "Fart 3", "Fart 4", "Fart 5", "Fart 6", "Fart 7", "Fart 8", "Fart 9", "Fart 10",
                        "Fart 11", "Fart 12", "Fart 13", "Fart 14", "Fart 15", "Fart 16", "Fart 17", "Fart 18", "Fart 19", "Fart 20",
                        "Fart 21", "Fart 22", "Fart 23", "Fart 24", "Fart 25", "Fart 26", "Fart 27", "Fart 28", "Fart 29", "Fart 30"}))));

        this.categoryList.add(new SoundCategoryModel("Dialogue", R.drawable.seven, new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.raw.dial1), Integer.valueOf(R.raw.dial2), Integer.valueOf(R.raw.dial3), Integer.valueOf(R.raw.dial14), Integer.valueOf(R.raw.dial5), Integer.valueOf(R.raw.dial6), Integer.valueOf(R.raw.dial7),
                Integer.valueOf(R.raw.dial8), Integer.valueOf(R.raw.dial9), Integer.valueOf(R.raw.dial10), Integer.valueOf(R.raw.dial11), Integer.valueOf(R.raw.dial12), Integer.valueOf(R.raw.dial13), Integer.valueOf(R.raw.dial14), Integer.valueOf(R.raw.dial15),
                Integer.valueOf(R.raw.dial16), Integer.valueOf(R.raw.dial17), Integer.valueOf(R.raw.dial18), Integer.valueOf(R.raw.dial19), Integer.valueOf(R.raw.dial20),
                Integer.valueOf(R.raw.dial21), Integer.valueOf(R.raw.dial22), Integer.valueOf(R.raw.dial23), Integer.valueOf(R.raw.dial24)})),
                new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.drawable.dial1)})),
                new ArrayList(Arrays.asList(new String[]{"Dialogue 1", "Dialogue 2", "Dialogue 3", "Dialogue 4", "Dialogue 5",
                        "Dialogue 6", "Dialogue 7", "Dialogue 8", "Dialogue 9", "Dialogue 10",
                        "Dialogue 11", "Dialogue 12", "Dialogue 13", "Dialogue 14", "Dialogue 15",
                        "Dialogue 16", "Dialogue 17", "Dialogue 18", "Dialogue 19", "Dialogue 20",
                        "Dialogue 21", "Dialogue 22", "Dialogue 23", "Dialogue 24"}))));

        this.categoryList.add(new SoundCategoryModel("Baby", R.drawable.eight, new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.raw.baby1), Integer.valueOf(R.raw.baby2), Integer.valueOf(R.raw.baby3), Integer.valueOf(R.raw.baby4), Integer.valueOf(R.raw.baby5),
                Integer.valueOf(R.raw.baby6), Integer.valueOf(R.raw.baby7), Integer.valueOf(R.raw.baby8), Integer.valueOf(R.raw.baby9), Integer.valueOf(R.raw.baby10),
                Integer.valueOf(R.raw.baby11), Integer.valueOf(R.raw.baby12), Integer.valueOf(R.raw.baby13), Integer.valueOf(R.raw.baby14), Integer.valueOf(R.raw.baby15),
                Integer.valueOf(R.raw.baby16), Integer.valueOf(R.raw.baby17), Integer.valueOf(R.raw.baby18), Integer.valueOf(R.raw.baby19), Integer.valueOf(R.raw.baby20),
                Integer.valueOf(R.raw.baby21), Integer.valueOf(R.raw.baby22), Integer.valueOf(R.raw.baby23), Integer.valueOf(R.raw.baby24), Integer.valueOf(R.raw.baby25),
                Integer.valueOf(R.raw.baby26), Integer.valueOf(R.raw.baby27), Integer.valueOf(R.raw.baby28), Integer.valueOf(R.raw.baby29), Integer.valueOf(R.raw.baby30),
                Integer.valueOf(R.raw.baby31), Integer.valueOf(R.raw.baby32), Integer.valueOf(R.raw.baby33), Integer.valueOf(R.raw.baby34), Integer.valueOf(R.raw.baby35)})),
                new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.drawable.baby1)})),
                new ArrayList(Arrays.asList(new String[]{"Baby 1", "Baby 2", "Baby 3", "Baby 4", "Baby 5",
                "Baby 6", "Baby 7", "Baby 8", "Baby 9", "Baby 10", "Baby 11", "Baby 12", "Baby 13", "Baby 14", "Baby 15",
                "Baby 16", "Baby 17", "Baby 18", "Baby 19", "Baby 20", "Baby 21", "Baby 22", "Baby 23", "Baby 24", "Baby 25",
                "Baby 26", "Baby 27", "Baby 28", "Baby 29", "Baby 30", "Baby 31", "Baby 32", "Baby 33", "Baby 34", "Baby 35"}))));

        this.categoryList.add(new SoundCategoryModel("Funny Alarms", R.drawable.ic_alarm,
                new ArrayList<>(Arrays.asList(R.raw.alarm1, R.raw.alarm2, R.raw.alarm3, R.raw.alarm4, R.raw.alarm5,
                        R.raw.alarm6, R.raw.alarm7, R.raw.alarm8, R.raw.alarm9, R.raw.alarm10)),
                new ArrayList<>(Arrays.asList(R.drawable.funny3, R.drawable.funny3, R.drawable.funny3, R.drawable.funny3, R.drawable.funny3,
                        R.drawable.funny3, R.drawable.funny3, R.drawable.funny3, R.drawable.funny3, R.drawable.funny3)),
                new ArrayList<>(Arrays.asList("Alarm 1", "Alarm 2", "Alarm 3", "Alarm 4", "Alarm 5", "Alarm 6", "Alarm 7", "Alarm 8", "Alarm 9", "Alarm 10"))));
    }

    private void setGreetingAndAnimate() {
        int hour = Calendar.getInstance().get(11);
        if (hour >= 5 && hour < 12) {
            return;
        }
        if (hour >= 12 && hour < 17) {
            return;
        }
        if (hour >= 17 && hour < 21) {
        }
    }
}
