package com.gamechief.aifunnyringtone.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.Story_Adapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityStroyListBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.util.ArrayList;

public class StroyList extends ParentActivity {
    ArrayList<Integer> arrayList = new ArrayList<>();
    ActivityStroyListBinding binding;
    Story_Adapter story_adapter;
    ArrayList<String> textList = new ArrayList<>();
    ArrayList<String> timeList = new ArrayList<>();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityStroyListBinding.inflate(getLayoutInflater());
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
        this.arrayList.add(Integer.valueOf(R.drawable.s_1));
        this.arrayList.add(Integer.valueOf(R.drawable.s_2));
        this.arrayList.add(Integer.valueOf(R.drawable.s_3));
        this.arrayList.add(Integer.valueOf(R.drawable.s_4));
        this.arrayList.add(Integer.valueOf(R.drawable.s_5));
        this.arrayList.add(Integer.valueOf(R.drawable.s_6));
        this.arrayList.add(Integer.valueOf(R.drawable.s_7));
        this.arrayList.add(Integer.valueOf(R.drawable.s_8));
        this.arrayList.add(Integer.valueOf(R.drawable.s_9));
        this.arrayList.add(Integer.valueOf(R.drawable.s_10));

        this.textList.add("The Elf's Ant Story");
        this.textList.add("The Coconut Vendor");
        this.textList.add("The Lazy Donkey and the Clever Merchant");
        this.textList.add("The Forgetful Fish and the Magic Bubble");
        this.textList.add("Use your brain, not just strength");
        this.textList.add("The Clever Mouse and the Lazy Cat");
        this.textList.add("The Boy Who Cried Homework");
        this.textList.add("Have fun, but always be careful!");
        this.textList.add("The Funny Hen and the Duck");
        this.textList.add("The Funny Barber");



        this.story_adapter = new Story_Adapter(this, this.arrayList, this.textList, new Story_Adapter.onCardClickListener() {
            public void onCardClick(int position) {
                Intent intent = new Intent(StroyList.this, ReadStory.class);
                intent.putExtra("image", StroyList.this.arrayList.get(position));
                intent.putExtra("text", StroyList.this.textList.get(position));
                intent.putExtra("position", position);
                StroyList.this.startActivity(intent);
            }
        });
        this.binding.itemList.setLayoutManager(new GridLayoutManager((Context) this, 2, RecyclerView.VERTICAL, false));
        this.binding.itemList.setAdapter(this.story_adapter);
    }

}
