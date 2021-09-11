package com.example.gallery;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.gallery.databinding.ChipLabelBinding;
import com.example.gallery.databinding.ColorChipBinding;
import com.example.gallery.databinding.DialogEditImageBinding;
import com.example.gallery.models.ItemModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.Set;

public class EditImageDialog {

    public Context context;

    public OnCompleteListener listener;
    public AlertDialog dialog;
    private LayoutInflater inflater;
    private Bitmap  bitmap;
    DialogEditImageBinding b;
    private boolean isCustomLabel;
    private String url1;

    EditImageDialog show(Context context, Bitmap bitmap, EditImageDialog.OnCompleteListener listener){
        this.context=context;
        this.listener = listener;
        this.bitmap=bitmap;

        //inflate dialogs layout
        if(context instanceof GalleryActivity){
            inflater=((GalleryActivity) context).getLayoutInflater();
           b= DialogEditImageBinding.inflate(inflater);

        }
        else{
            dialog.dismiss();
            listener.onError("Cast Exception");
            return this;

        }

        //Create & show dialog
        dialog= new MaterialAlertDialogBuilder(context).setView(b.getRoot()).show();
        return this;
    }


    public void editItem(){
        dialog.hide();
        new ItemHelper().editImage(bitmap, context, new ItemHelper.OnCompleteListener() {
            @Override
            public void onFetched(Bitmap image,String url, Set<Integer> colors, List<String> label) {
                dialog.show();
                url1=url;
                showData(bitmap,colors,label);
            }

            @Override
            public void onError(String error) {

            }
        });
    }
     // to show the data on dialog box-------------------
    private void showData(Bitmap image, Set<Integer> colors, List<String> label) {
       b.imageView.setImageBitmap(image);
        inflateColorChips(colors);
        inflateLabelChips(label);
        handleCustomLabelInput();
        handleAddImageEvent();
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
                listener.onImageAdded(new ItemModel(bitmap,url1,color,label));
                dialog.dismiss();
            }
        });
    }

     //to handle custom input------------------------------

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

        }
    }


    public interface OnCompleteListener {
        void onImageAdded(ItemModel item);
        void onError(String error);
    }
    }
