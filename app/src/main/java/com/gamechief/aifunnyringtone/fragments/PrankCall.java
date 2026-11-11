package com.gamechief.aifunnyringtone.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.activities.AcceptVideoCall;
import com.gamechief.aifunnyringtone.activities.VoiceCallActivity;
import com.gamechief.aifunnyringtone.adapters.PrankAdapter;
import com.gamechief.aifunnyringtone.databinding.FragmentPrankCallBinding;
import com.gamechief.aifunnyringtone.models.PrankModel;

import java.util.ArrayList;

public class PrankCall extends Fragment {
    /* access modifiers changed from: private */
    public int AudioResourceId;
    /* access modifiers changed from: private */
    public int VideoResourceId;
    PrankAdapter adapter;
    /* access modifiers changed from: private */
    public int afterTime = 1000;
    /* access modifiers changed from: private */
    public AlertDialog alertDialog;
    FragmentPrankCallBinding binding;
    private int icon;
    ArrayList<PrankModel> list = new ArrayList<>();
    /* access modifiers changed from: private */
    public int selectedImage;
    /* access modifiers changed from: private */
    public int selectedPosition;
    /* access modifiers changed from: private */
    public String selectedText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.binding = FragmentPrankCallBinding.inflate(inflater, container, false);
        setupRecyclerView();
        if (this.list.isEmpty()) {
            loadData();
        }
        return this.binding.getRoot();
    }

    private void setupRecyclerView() {
        this.adapter = new PrankAdapter(requireContext(), this.list, new PrankAdapter.OnPrankClickListener() {
            public void onPrankClick(PrankModel model, int position) {
                PrankCall.this.selectedImage = model.getImage();
                PrankCall.this.selectedText = model.getName();
                PrankCall.this.selectedPosition = position;
                PrankCall.this.ShowAlertDialog();
            }
        });
        this.binding.prankList.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        this.binding.prankList.setAdapter(this.adapter);
    }

    private void loadData() {
        this.list.add(new PrankModel(R.drawable.agent, "Bank Agent", 0));
        this.list.add(new PrankModel(R.drawable.boss, "Boss", 1));
        this.list.add(new PrankModel(R.drawable.dilvery_boy, "Delivery Boy", 2));
        this.list.add(new PrankModel(R.drawable.girlfried, "Girl Friend", 3));
        this.list.add(new PrankModel(R.drawable.police, "Police", 4));
        this.list.add(new PrankModel(R.drawable.wife, "Wife", 5));
        this.adapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: private */
    public void ShowAlertDialog() {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog, (ViewGroup) null);
        ((ImageView) view.findViewById(R.id.profileImageCalling)).setImageResource(this.selectedImage);
        ((TextView) view.findViewById(R.id.textnameCalling)).setText(this.selectedText);
        ((ImageView) view.findViewById(R.id.closeImage)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PrankCall.this.alertDialog.dismiss();
            }
        });
        ((RelativeLayout) view.findViewById(R.id.VideoCall)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Handler handler = new Handler();
                ((ImageView) view.findViewById(R.id.profileImageCalling)).setImageResource(PrankCall.this.selectedImage);
                ((TextView) view.findViewById(R.id.textnameCalling)).setText(PrankCall.this.selectedText);
                final Intent intent = new Intent(PrankCall.this.getContext(), AcceptVideoCall.class);
                intent.putExtra("image", PrankCall.this.selectedImage);
                intent.putExtra("text", PrankCall.this.selectedText);
                intent.putExtra("position", PrankCall.this.selectedPosition);
                intent.putExtra("videoResourceId", PrankCall.this.VideoResourceId);
                intent.putExtra("icon", R.drawable.video_call);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        PrankCall.this.startActivity(intent);
                        PrankCall.this.alertDialog.dismiss();
                    }
                }, (long) PrankCall.this.afterTime);
            }
        });
        ((RelativeLayout) view.findViewById(R.id.VoiceCall)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Handler handler = new Handler();
                ((ImageView) view.findViewById(R.id.profileImageCalling)).setImageResource(PrankCall.this.selectedImage);
                ((TextView) view.findViewById(R.id.textnameCalling)).setText(PrankCall.this.selectedText);
                final Intent intent = new Intent(PrankCall.this.getContext(), VoiceCallActivity.class);
                intent.putExtra("image", PrankCall.this.selectedImage);
                intent.putExtra("text", PrankCall.this.selectedText);
                intent.putExtra("position", PrankCall.this.selectedPosition);
                intent.putExtra("AudioResourceId", PrankCall.this.AudioResourceId);
                intent.putExtra("icon", R.drawable.prank_call);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        PrankCall.this.startActivity(intent);
                        PrankCall.this.alertDialog.dismiss();
                    }
                }, (long) PrankCall.this.afterTime);
            }
        });
        this.alertDialog = new AlertDialog.Builder(getContext()).setView(view).create();
        this.alertDialog.show();
        this.alertDialog.setCancelable(true);
    }
}
