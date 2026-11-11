package com.gamechief.aifunnyringtone.DareGame;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.MemAdapter;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityAddNameListBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddNameList extends ParentActivity {
    private static final String KEY_NAME_LIST = "name_list";
    private static final String PREF_NAME = "dare_game_pref";
    MemAdapter adapter;
    ActivityAddNameListBinding binding;
    List<String> nameList = new ArrayList();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityAddNameListBinding.inflate(getLayoutInflater());
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
        loadNameList();
        this.binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        this.adapter = new MemAdapter(this.nameList, new MemAdapter.OnItemClickListener() {
            @Override
            public void onCloseClick(int i) {
                nameList.remove(i);
                adapter.notifyItemRemoved(i);
                saveNameList();
            }
        });
        this.binding.recyclerView2.setAdapter(this.adapter);
        this.binding.addName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNameDialog();
            }
        });
        this.binding.letBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameList.isEmpty()) {
                    Toast.makeText(AddNameList.this, "Add at least one name", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(AddNameList.this, SpinGame.class));
                }
            }
        });
    }







    private void showAddNameDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_name);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(true);
        EditText etName = (EditText) dialog.findViewById(R.id.etName);
        ((TextView) dialog.findViewById(R.id.btnAdd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                if (name.isEmpty()) {
                    etName.setError("Enter name");
                    return;
                }
                nameList.add(name);
                adapter.notifyItemInserted(nameList.size() - 1);
                saveNameList();
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(-1, -2);
        }
    }

    private void loadNameList() {
        String json = getSharedPreferences(PREF_NAME, 0).getString(KEY_NAME_LIST, (String) null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            this.nameList.clear();
            this.nameList.addAll((Collection) gson.fromJson(json, type));
        }
    }

    private void saveNameList() {
        SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, 0).edit();
        editor.putString(KEY_NAME_LIST, new Gson().toJson((Object) this.nameList));
        editor.apply();
    }
}
