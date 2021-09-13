package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.gallery.adapters.ItemAdapter;
import com.example.gallery.databinding.ActivityGalleryBinding;
import com.example.gallery.helpers.SimpleItemTouchHelperCallback;
import com.example.gallery.models.ItemModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
ActivityGalleryBinding b;
private List<ItemModel> cardItem=new ArrayList<>();
    SharedPreferences preferences;
    ItemAdapter adapter;

      int mode=0;
    ItemTouchHelper.Callback callback2;
    ItemTouchHelper itemTouchHelper1;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting up view binding
        b = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        preferences=getSharedPreferences("shared preferences",MODE_PRIVATE);



      loadSharedPreferences();


    }
    //binding adapter to recycler view
    private void setUpRecyclerView() {
         adapter=new ItemAdapter(this,cardItem);
         // setting up layout manager
        b.cardItem.setLayoutManager(new LinearLayoutManager(this));
         // initialising itemTouchHelper callback
        callback2 = new SimpleItemTouchHelperCallback(adapter);
        // initialising itemTouchHelper
        itemTouchHelper1 = new ItemTouchHelper(callback2);
        adapter.setItemTouchHelper(itemTouchHelper1);
        itemTouchHelper1.attachToRecyclerView(b.cardItem);
        restoreEnableDragAndDrop();
        b.cardItem.setAdapter(adapter);

    }

    // to create option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.add_image,menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView =  (SearchView) MenuItemCompat.getActionView(search);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.searchData(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.searchData(s);
                return false;
            }
        });
       return true;
    }
     // to add functionality to the selected items of option menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.add_image_from_internet:
                showAddImageDialog();
            case R.id.add_from_gallery:
                addFromGallery();
                return true;
            case R.id.drag_and_drop:
                enableDragAndDrop();
                return true;
            case R.id.sort:
                adapter.sortData();
            default:
                return super.onOptionsItemSelected(item);
        }


    }
      // enable or disable drag and drop
    private void enableDragAndDrop() {

        if (mode == 0) {
            mode = 1;
            adapter.mode = 1;
            Toast.makeText(GalleryActivity.this, "Drag and Drop enable", Toast.LENGTH_SHORT).show();
            List<ItemAdapter.ViewHolder> holders = adapter.holders;
            for (int i = 0; i < holders.size(); i++) {
                holders.get(i).setUpListener();
            }

        } else {
            mode = 0;
            adapter.mode = 0;
            List<ItemAdapter.ViewHolder> holders = adapter.holders;
            for (int j = 0; j < holders.size(); j++) {
                holders.get(j).setUpListener();
            }
        }
    }
    private void restoreEnableDragAndDrop() {

        if (mode == 1) {

            adapter.mode = 1;
            Toast.makeText(GalleryActivity.this, "Drag and Drop enable", Toast.LENGTH_SHORT).show();
            List<ItemAdapter.ViewHolder> holders = adapter.holders;
            for (int i = 0; i < holders.size(); i++) {
                holders.get(i).setUpListener();
            }

        } else {
            mode = 0;
            adapter.mode = 0;
            List<ItemAdapter.ViewHolder> holders = adapter.holders;
            for (int j = 0; j < holders.size(); j++) {
                holders.get(j).setUpListener();
            }
        }
    }
    // to add functionality to the selected items of contextual menu
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.edit_picture:
                bitmap=adapter.images;
                int index=adapter.index;
                  editImage(bitmap,index);
                Toast.makeText(GalleryActivity.this,"edit picture",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.share_image:
                shareImage();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }
//to edit the image--------------------
    private void editImage(Bitmap bitmap,int index) {

        new EditImageDialog().show(this, bitmap, new EditImageDialog.OnCompleteListener() {
            @Override
            public void onImageAdded(ItemModel item) {
              cardItem.set(index,item);
              adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {

            }
        }).editItem();
    }

    // to share the recycler view items to other apps


    private void shareImage() {
        Bitmap icon = screenShot(adapter.binding.getRoot());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);


        OutputStream outstream;
        try {
            outstream = getContentResolver().openOutputStream(uri);
            icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, "Share Image"));
    }

    // taking screenshot of the selected cardview


    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // to add image in recycler view through gallery with implicit intents

    private void addFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);

    }

    // setting up on activity result callback
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode==0){
            Uri targetUri = data.getData();
             String uri=targetUri.toString();
          new AddImageDialog().show(this, new AddImageDialog.OnCompleteListener() {
              @Override
              public void onImageAdded(ItemModel item) {
                  cardItem.add(item) ;

                  setUpRecyclerView();
//                  b.zeroItem.setVisibility(View.GONE);

              }

              @Override
              public void onError(String error) {
                  Toast.makeText(GalleryActivity.this, error, Toast.LENGTH_SHORT).show();

              }
          }).addImageFromGallery(uri);

        }
    }
     // dialog to add image

    private void showAddImageDialog() {
        new AddImageDialog().show(this, new AddImageDialog.OnCompleteListener() {
            @Override
            public void onImageAdded(ItemModel item) {

               cardItem.add(item) ;
                setUpRecyclerView();
            }

            @Override
            public void onError(String error) {
                 new MaterialAlertDialogBuilder(GalleryActivity.this)
                         .setTitle("Error")
                         .setMessage(error)
                         .show();
            }
        });
    }
// //shared preferences--------------------------------------------------
//
//    //To get json from itemModel
//    public String listToJson(List<ItemModel> items){
//        Gson json=new Gson();
//        return json.toJson(items);
//    }
//    public List<ItemModel> jsonToList(String string){
//        Gson json2=new Gson();
//
//        return json2.fromJson(string,new TypeToken<ArrayList<ItemModel>>(){}.getType());
//    }
//
    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor=preferences.edit();
        Gson gson=new Gson();
        String json=gson.toJson(cardItem);
        editor.putString("item list",json);
        editor.apply();
            }



    private void loadSharedPreferences() {
        Gson gson=new Gson();
        String json=preferences.getString("item list",null);
        Type type=new TypeToken<ArrayList<ItemModel>>(){
        }.getType();
        cardItem=gson.fromJson(json,type);

        if(cardItem==null){
            cardItem=new ArrayList<>();
        }

        setUpRecyclerView();





        }

    }




