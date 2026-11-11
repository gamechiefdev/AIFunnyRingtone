package com.gamechief.aifunnyringtone.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import com.bumptech.glide.Glide;

import com.gamechief.aifunnyringtone.Interfaces.OnClick;
import com.gamechief.aifunnyringtone.R;
import com.gamechief.aifunnyringtone.adapters.HorizonRVAdapterGif;
import com.gamechief.aifunnyringtone.adsCondition.AdsController;
import com.gamechief.aifunnyringtone.adsCondition.ParentActivity;

import com.gamechief.aifunnyringtone.databinding.ActivityShowGifBinding;
import com.zackratos.ultimatebarx.ultimatebarx.java.UltimateBarX;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import net.lingala.zip4j.util.InternalZipConstants;
import pl.droidsonroids.gif.GifDrawable;

public class ShowGif extends ParentActivity implements OnClick {
    private static final int REQUEST_STORAGE = 1;
    ActivityShowGifBinding binding;
    /* access modifiers changed from: private */
    public boolean checkfromwhatsapp = false;
    private List<GifDrawable> gifList;
    private HorizonRVAdapterGif horizonRVAdapterGif;
    String myFileName = "";
    private int position;
    private List<Integer> rawresourceslist;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.binding = ActivityShowGifBinding.inflate(getLayoutInflater());
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
        this.gifList = new ArrayList();
        this.rawresourceslist = new ArrayList();
        Resources resources = getResources();
        try {
            this.gifList.add(new GifDrawable(resources, R.drawable.gif25));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif26));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif27));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif29));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif31));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif32));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif33));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif35));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif36));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif38));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif39));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif1));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif2));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif3));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif4));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif5));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif6));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif7));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif8));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif9));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif10));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif11));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif12));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif13));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif14));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif15));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif16));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif17));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif18));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif19));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif20));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif21));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif22));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif23));
            this.gifList.add(new GifDrawable(resources, R.drawable.gif24));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif25));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif26));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif27));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif29));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif31));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif32));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif33));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif35));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif36));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif38));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif39));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif1));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif2));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif3));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif4));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif5));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif6));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif7));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif8));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif9));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif10));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif11));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif12));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif13));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif14));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif15));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif16));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif17));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif18));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif19));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif20));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif21));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif22));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif23));
        this.rawresourceslist.add(Integer.valueOf(R.raw.gif24));
        this.position = getIntent().getIntExtra("selectedAnimation", 0);
        if (this.position < 0) {
            this.position = 0;
        }
        if (this.position >= this.gifList.size()) {
            this.position = this.gifList.size() - 1;
        }
        Log.d("gifposition", "from intent: " + this.position);
        updateGif(this.position);
        String[] filenameParts = getApplication().getResources().getString(this.rawresourceslist.get(this.position).intValue()).split(InternalZipConstants.ZIP_FILE_SEPARATOR);
        this.myFileName = filenameParts[filenameParts.length - 1];
        Log.d("myFileName", "Extracted filename: " + this.myFileName);
        this.binding.imageView7.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ShowGif.this.onBackPressed();
            }
        });
        this.binding.share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ShowGif.this.checkfromwhatsapp = false;
                if (Build.VERSION.SDK_INT >= 33) {
                    ShowGif.this.saveImage();
                } else if (ShowGif.this.hasPermission()) {
                    ShowGif.this.saveImage();
                } else {
                    ShowGif.this.requestPermission();
                }
            }
        });
        this.binding.whatsappshare.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ShowGif.this.checkfromwhatsapp = true;
                if (Build.VERSION.SDK_INT >= 33) {
                    ShowGif.this.saveImage();
                } else if (ShowGif.this.hasPermission()) {
                    ShowGif.this.saveImage();
                } else {
                    ShowGif.this.requestPermission();
                }
            }
        });
        this.horizonRVAdapterGif = new HorizonRVAdapterGif(this.gifList, this);
        this.binding.gifPreRv.setAdapter(this.horizonRVAdapterGif);
    }

    /* access modifiers changed from: private */
    public void saveImage() {
        if (this.checkfromwhatsapp) {
            saveGifFromRaw(this, this.rawresourceslist.get(this.position).intValue(), this.myFileName);
        } else {
            saveGifFromRawSimeple(this, this.rawresourceslist.get(this.position).intValue(), this.myFileName);
        }
    }

    /* access modifiers changed from: private */
    public boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0;
    }

    /* access modifiers changed from: private */
    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            Toast.makeText(this, "Permission denied. Cannot save image.", Toast.LENGTH_LONG).show();
        } else if (grantResults.length <= 0 || grantResults[0] != 0) {
            saveImage();
        } else {
            saveImage();
        }
    }

    private void updateGif(int positi) {
        Log.d("gifposition", "in inupdategif: " + positi);
        if (this.gifList != null && !this.gifList.isEmpty() && positi < this.gifList.size()) {
            Glide.with(getApplicationContext()).load((Drawable) this.gifList.get(positi)).into(this.binding.imageView9);
        }
    }

    public void saveGifFromRaw(Context context, int rawId, String fileName) {
        OutputStream outputStream;
        InputStream inputStream = context.getResources().openRawResource(rawId);
        File gifFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        try {
            outputStream = new FileOutputStream(gifFile);
            byte[] buffer = new byte[1024];
            while (true) {
                int read = inputStream.read(buffer);
                int read2 = read;
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read2);
            }
            outputStream.flush();
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("image/gif");
            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getPackageName() + ".provider", gifFile));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setPackage("com.whatsapp");
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(intent, "Share GIF"));
            } else {
                Toast.makeText(context, "WhatsApp not installed!", Toast.LENGTH_LONG).show();
            }
            outputStream.close();
            try {
                inputStream.close();
                return;
            } catch (IOException e) {
                return;
            }
        } catch (IOException e2) {
            try {
                e2.printStackTrace();
                Toast.makeText(context, "Error Sharing GIF!", Toast.LENGTH_LONG).show();
                return;
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    public void saveGifFromRawSimeple(Context context, int rawId, String fileName) {
        OutputStream outputStream;
        InputStream inputStream = context.getResources().openRawResource(rawId);
        File gifFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        try {
            outputStream = new FileOutputStream(gifFile);
            byte[] buffer = new byte[1024];
            while (true) {
                int read = inputStream.read(buffer);
                int read2 = read;
                if (read == -1) {
                    break;
                }
                outputStream.write(buffer, 0, read2);
            }
            outputStream.flush();
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("image/gif");
            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getPackageName() + ".provider", gifFile));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(intent, "Share GIF"));
            } else {
                Toast.makeText(context, "WhatsApp not installed!", Toast.LENGTH_LONG).show();
            }
            outputStream.close();
            try {
                inputStream.close();
                return;
            } catch (IOException e) {
                return;
            }
        } catch (IOException e2) {
            try {
                e2.printStackTrace();
                Toast.makeText(context, "Error Sharing GIF!", Toast.LENGTH_LONG).show();
                return;
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    public void itemPosition(int pos) {
        this.position = pos;
    }
}
