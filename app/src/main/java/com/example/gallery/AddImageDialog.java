package com.example.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.gallery.databinding.ChipLabelBinding;
import com.example.gallery.databinding.ColorChipBinding;
import com.example.gallery.databinding.DialogAddImageBinding;
import com.example.gallery.models.ItemModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.net.URL;
import java.util.List;
import java.util.Set;

public class AddImageDialog {
    private Context context;
    private OnCompleteListener listener;
    private LayoutInflater inflater;
    DialogAddImageBinding b;
    private boolean isCustomLabel;
    private Bitmap image;
   private AlertDialog dialog;
   private ItemModel item;
   private String Url;

    AddImageDialog show(Context context, OnCompleteListener listener){
        this.context=context;
        this.listener = listener;

        //inflate dialogs layout
        if(context instanceof GalleryActivity){
            inflater=((GalleryActivity) context).getLayoutInflater();
       b=DialogAddImageBinding.inflate(inflater);

        }
        else{
            dialog.dismiss();
            listener.onError("Cast Exception");
            return this;
        }

        //Create & show dialog
         dialog= new MaterialAlertDialogBuilder(context).setView(b.getRoot()).show();
        //handle events
        handleDimensionsInput();
        hideErrorsForEditText();

    return this;
    }
    //utils

    private void hideErrorsForEditText() {
        b.width.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
             b.width.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    //Step1: Input Dimensions

    private void handleDimensionsInput() {
        b.fetchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String widthStr,HeightStr;

                // get String from editTexts
                widthStr =b.width.getEditText().getText().toString().trim();
                HeightStr =b.height.getEditText().getText().toString().trim();

                // guard code

                if(widthStr.isEmpty()&& HeightStr.isEmpty()){
                    b.width.setError("please enter at least one dimension!");
                    return;
                }

                //update Ui

                // square image
                if(widthStr.isEmpty()){
                    int Height=Integer.parseInt(HeightStr);
                    fetchRandomImage(Height);
                }

                else if(HeightStr.isEmpty()){
                    int Width=Integer.parseInt(widthStr);
                    fetchRandomImage(Width);
                }
                // rectangular image
                else
                {
                    int height=Integer.parseInt(HeightStr);
                    int width=Integer.parseInt(widthStr);
                    fetchRandomImage(width,height);
                }
                b.inputDimensionsRoot.setVisibility(View.GONE);
                b.progressIndicatorRoot.setVisibility(View.VISIBLE);

                // hide keyboard
               hideKeyboard();
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.

        imm.hideSoftInputFromWindow(b.width.getWindowToken(), 0);
    }

    // step 2: Fetch Random Image

    // for rectangle image
    private void fetchRandomImage(int width, int height) {
   new ItemHelper().fetchData(width, height, context, new ItemHelper.OnCompleteListener() {
       @Override
       public void onFetched(Bitmap image,String url, Set<Integer> colors, List<String> label)
       {b.progressIndicatorRoot.setVisibility(View.GONE);
       Url=url;
       showData(image ,colors,label);


       }
       @Override
       public void onError(String error) {
           dialog.dismiss();
       listener.onError("error");
       }
   });
    }



    //for square image
    private void fetchRandomImage(int x) {
        new ItemHelper().fetchData(x, context, new ItemHelper.OnCompleteListener() {
            @Override
            public void onFetched(Bitmap image,String url, Set<Integer> colors, List<String> label) {
                b.progressIndicatorRoot.setVisibility(View.GONE);
                Url=url;
                showData(image ,colors,label);
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
               listener.onError(error);
            }
        });

    }

    // for image from gallery
    public void addImageFromGallery(String url){
        dialog.hide();
        new ItemHelper().fetchImageFromGallery(url, context, new ItemHelper.OnCompleteListener() {
            @Override
            public void onFetched(Bitmap image,String url, Set<Integer> colors, List<String> label) {
                b.inputDimensionsRoot.setVisibility(View.GONE);
                dialog.show();
                    Url=url;
                b.mainRoot.setVisibility(View.VISIBLE);
                showData(image,colors,label);
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                listener.onError(error);
            }
        });
    }
    // step 3:show data

    private void showData(Bitmap image, Set<Integer> colors, List<String> label) {
        this.image = image;
        b.imageView.setImageBitmap(image);
        inflateColorChips(colors);
        inflateLabelChips(label);
        handleCustomLabelInput();
        handleAddImageEvent();

        b.progressIndicatorRoot.setVisibility(View.GONE);
       b.mainRoot.setVisibility(View.VISIBLE);
       b.customLabelInput.setVisibility(View.GONE);
    }

    private void handleAddImageEvent() {
        b.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               int colorChipId= b.colorChips.getCheckedChipId()
               ,labelChipId=b.labelChips.getCheckedChipId();

               //guard code
                if(colorChipId==-1||labelChipId==-1){
                    Toast.makeText(context,"please select color and label",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isCustomLabel){
                    String customLabel=b.customLabelEditText.getText().toString().trim();
                    if(customLabel.isEmpty()){
                    Toast.makeText(context,"please select color a label",Toast.LENGTH_SHORT).show();
                    return;}
                }

                // get color and label

                String label=((Chip)b.labelChips.findViewById(labelChipId)).getText().toString();
                int color=((Chip)b.colorChips.findViewById(colorChipId)).getChipBackgroundColor().getDefaultColor();

                //send callbacks
                listener.onImageAdded(new ItemModel(image,Url,color,label));
                dialog.dismiss();
            }
        });
    }

    private void handleCustomLabelInput() {
        ChipLabelBinding binding=ChipLabelBinding.inflate(inflater);
        binding.getRoot().setText("custom");
        b.labelChips.addView(binding.getRoot());

       binding.getRoot().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean bo) {
               if(compoundButton.isChecked()){
                   b.customLabelInput.setVisibility(View.VISIBLE);
                   isCustomLabel=true;
               }
           }
       });
    }

    // label chips
    private void inflateLabelChips(List<String> labels) {
        for(String label:labels){
            ChipLabelBinding binding=ChipLabelBinding.inflate(inflater);
            binding.getRoot().setText(label);
            b.labelChips.addView(binding.getRoot());
        }}




    // color chips
    private void inflateColorChips(Set<Integer> colors) {
        for(int Color:colors){
            ColorChipBinding binding=ColorChipBinding.inflate(inflater);
            binding.getRoot().setChipBackgroundColor(ColorStateList.valueOf(Color));
            b.colorChips.addView(binding.getRoot());
            if (item != null && item.color == Color) {
                binding.getRoot().setChecked(true);
            }
        }
    }

    interface OnCompleteListener{
        void onImageAdded(ItemModel item);
        void onError(String error);
    }
}
