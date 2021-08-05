package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.example.gallery.databinding.ActivityMainBinding;
import com.example.gallery.databinding.ChipLabelBinding;
import com.example.gallery.databinding.ColorChipBinding;
import com.example.gallery.databinding.DialogAddImageBinding;
import com.example.gallery.databinding.ItemCardBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.add_image,menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.add_image){
           showAddImageDialog();
            return true;
        }

        return false;
    }

    private void showAddImageDialog() {
        new addImageDialog().show(this, new addImageDialog.OnCompleteListener() {
            @Override
            public void onImageAdded(Bitmap image, int color, String label) {
                inflateViewForItem(image,color,label);
            }

            @Override
            public void onError(String error) {
                 new MaterialAlertDialogBuilder(MainActivity.this)
                         .setTitle("Error")
                         .setMessage(error)
                         .show();
            }
        });
    }

    private void inflateViewForItem(Bitmap image, int color, String label) {

        //inflate layout
        ItemCardBinding binding=ItemCardBinding.inflate(getLayoutInflater());
        //bind data
        binding.imageView.setImageBitmap(image);
        binding.title.setText(label);
        binding.title.setBackgroundColor(color);

        //add it to the list
        b.list.addView(binding.getRoot());

    }


}