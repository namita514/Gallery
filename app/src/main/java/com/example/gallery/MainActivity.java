package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gallery.adapters.ItemAdapter;
import com.example.gallery.databinding.ActivityMainBinding;
import com.example.gallery.databinding.ChipLabelBinding;
import com.example.gallery.databinding.ColorChipBinding;
import com.example.gallery.databinding.DialogAddImageBinding;
import com.example.gallery.databinding.ItemCardBinding;
import com.example.gallery.helpers.SimpleItemTouchHelperCallback;
import com.example.gallery.models.ItemModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags;

public class MainActivity extends AppCompatActivity {
ActivityMainBinding b;
private final List<ItemModel> cardItem=new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

    }
    //binding adapter to
    private void setUpRecyclerView( List<ItemModel> cardItem) {
        ItemAdapter adapter=new ItemAdapter(this,cardItem);
        b.cardItem.setLayoutManager(new LinearLayoutManager(this));
          b.cardItem.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);

        mItemTouchHelper = new ItemTouchHelper(callback);
        adapter.setItemTouchHelper(mItemTouchHelper);
        mItemTouchHelper.attachToRecyclerView(b.cardItem);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.add_image,menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId()==R.id.add_image){
//           showAddImageDialog();
//            return true;
//        }
        switch(item.getItemId()){
            case R.id.add_image:
                showAddImageDialog();
            case R.id.add_from_gallery:
                addFromGallery();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }



    private void addFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode==0){
            Uri targetUri = data.getData();
             String uri=targetUri.toString();
          new addImageDialog().show(this, new addImageDialog.OnCompleteListener() {
              @Override
              public void onImageAdded(Bitmap image, int color, String label) {
                  cardItem.add(new ItemModel(image,color,label)) ;
                  setUpRecyclerView(cardItem);
//                  b.zeroItem.setVisibility(View.GONE);

              }

              @Override
              public void onError(String error) {
                  Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();

              }
          }).addImageFromGallery(uri);

        }
    }

    private void showAddImageDialog() {
        new addImageDialog().show(this, new addImageDialog.OnCompleteListener() {
            @Override
            public void onImageAdded(Bitmap image, int color, String label) {
//                inflateViewForItem(image,color,label);
//                b.zeroItem.setVisibility(View.GONE);
               cardItem.add(new ItemModel(image,color,label)) ;
                setUpRecyclerView(cardItem);
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

//    private void inflateViewForItem(Bitmap image, int color, String label) {
//
//        //inflate layout
//        ItemCardBinding binding=ItemCardBinding.inflate(getLayoutInflater());
//        //bind data
//        binding.imageView.setImageBitmap(image);
//        binding.title.setText(label);
//        binding.title.setBackgroundColor(color);
//
//        //add it to the list
//        b.list.addView(binding.getRoot());
//
//    }


}