package com.gamechief.aifunnyringtone.PuzzleGame;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gamechief.aifunnyringtone.PuzzleGame.adapter.PuzzleAdapter;
import com.gamechief.aifunnyringtone.PuzzleGame.models.Pieces;
import com.gamechief.aifunnyringtone.PuzzleGame.models.Puzzle;
import com.gamechief.aifunnyringtone.PuzzleGame.models.PuzzlePiece;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;
import com.gamechief.aifunnyringtone.databinding.ActivityPuzzlegameBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Puzzlegame extends ParentActivity {

    ActivityPuzzlegameBinding binding;
    Context context;

    int countGrid = 0;
    HashMap<String, Pieces> piecesModelHashMap = new HashMap<>();
    List<Pieces> piecesModelListMain = new ArrayList<>();

    Puzzle puzzle;
    PuzzleAdapter puzzleListAdapter;
    ArrayList<PuzzlePiece> puzzlePiecesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPuzzlegameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UltimateBarX.statusBarOnly((FragmentActivity) this)
                .fitWindow(true)
                .transparent()
                .colorRes(R.color.white)
                .light(true)
                .apply();

        AdsController.getInstance().loadAdaptiveBannerAd(this, binding.adView);

        binding.imageView7.setOnClickListener(v -> onBackPressed());

        context = this;
        puzzle = new Puzzle();

        int selectedImage = getIntent().getIntExtra("selectedPuzzleImage", 0);
        Log.d("PUZZLE", "Image ID = " + selectedImage);

        // 👉 Slightly dim background guide
        binding.frameImage.setAlpha(0.3f);

        binding.frameImage.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        binding.frameImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        int width = binding.frameImage.getWidth();
                        int height = binding.frameImage.getHeight();

                        new Thread(() -> {
                            ArrayList<PuzzlePiece> pieces = puzzle.createPuzzlePieces(
                                    Puzzlegame.this,
                                    selectedImage,
                                    width,
                                    height,
                                    binding.frameImage,
                                    "/puzzles/",
                                    3,
                                    3
                            );

                            runOnUiThread(() -> {
                                puzzlePiecesList.clear();
                                puzzlePiecesList.addAll(pieces);

                                getAdapter();
                                setPuzzleListAdapter();
                            });

                        }).start();
                    }
                });

        binding.listView2.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
    }

    // =========================
    // CREATE BOARD
    // =========================
    public void getAdapter() {
        countGrid = 0;
        binding.relativeLayout.removeAllViews();
        piecesModelListMain.clear();
        piecesModelHashMap.clear();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                if (countGrid >= puzzlePiecesList.size()) break;
                PuzzlePiece piece = puzzlePiecesList.get(countGrid);

                RelativeLayout.LayoutParams params =
                        new RelativeLayout.LayoutParams(piece.getWidth(), piece.getHeight());

                ImageView targetSlot = new ImageView(this);
                targetSlot.setLayoutParams(params);
                // ✅ Ensure the piece fills the slot exactly to avoid edge spots
                targetSlot.setScaleType(ImageView.ScaleType.FIT_XY);

                // Position correctly
                targetSlot.setX(piece.getAnchorPoint().x - piece.getCenterPoint().x);
                targetSlot.setY(piece.getAnchorPoint().y - piece.getCenterPoint().y);

                targetSlot.setTag(i + "," + j);

                // Transparent slots initially
                targetSlot.setImageDrawable(null);
                targetSlot.setBackgroundColor(Color.TRANSPARENT);

                targetSlot.setOnDragListener(new MyDragListener(targetSlot));
                binding.relativeLayout.addView(targetSlot);

                // Model
                Pieces model = new Pieces();
                model.setpX(i);
                model.setpY(j);
                model.setPosition(countGrid);
                model.setOriginalResource(piece.getImage());

                piecesModelListMain.add(model);
                piecesModelHashMap.put(i + "," + j, model);

                countGrid++;
            }
        }

        Collections.shuffle(piecesModelListMain);
    }

    // =========================
    // ADAPTER
    // =========================
    public void setPuzzleListAdapter() {
        if (puzzleListAdapter == null) {
            puzzleListAdapter = new PuzzleAdapter(this, piecesModelListMain);
            binding.listView2.setAdapter(puzzleListAdapter);
        } else {
            puzzleListAdapter.updateList(piecesModelListMain);
        }
    }

    // =========================
    // DRAG START
    // =========================
    public static class MyClickListener implements View.OnLongClickListener {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onLongClick(View view) {
            ClipData data = ClipData.newPlainText("", view.getTag().toString());
            view.startDragAndDrop(data, new View.DragShadowBuilder(view), view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    // =========================
    // DRAG DROP
    // =========================
    public class MyDragListener implements View.OnDragListener {

        ImageView target;

        public MyDragListener(ImageView target) {
            this.target = target;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {

            View dragged = (View) event.getLocalState();

            switch (event.getAction()) {

                case DragEvent.ACTION_DROP:

                    if (dragged == null) break;

                    String pieceTag = dragged.getTag().toString();
                    String targetTag = v.getTag().toString();

                    if (pieceTag.equals(targetTag)) {

                        Pieces model = piecesModelHashMap.get(targetTag);

                        // 🔥 Smooth snap and fade-in animation
                        target.setImageBitmap(model.getOriginalResource());
                        target.setAlpha(0f);
                        target.setScaleX(0.7f);
                        target.setScaleY(0.7f);

                        target.animate()
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(300)
                                .withEndAction(() -> {
                                    // Ensure full opacity to cover edge guide marks
                                    target.setAlpha(1.0f);
                                })
                                .start();

                        v.setOnDragListener(null);

                        int index = piecesModelListMain.indexOf(model);
                        if (index != -1) {
                            piecesModelListMain.remove(index);
                            puzzleListAdapter.notifyItemRemoved(index);
                        }

                        if (piecesModelListMain.isEmpty()) {
                            binding.frameImage.setAlpha(1f);
                            binding.relativeLayout.setVisibility(View.GONE);
                            Toast.makeText(context, "Puzzle Completed!", Toast.LENGTH_LONG).show();
                        }

                    } else {
                        dragged.setVisibility(View.VISIBLE);
                    }
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    if (dragged != null && !event.getResult()) {
                        dragged.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            return true;
        }
    }
}
