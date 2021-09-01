package com.example.gallery;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.palette.graphics.Palette;

import com.example.gallery.databinding.ChipLabelBinding;
import com.example.gallery.databinding.ColorChipBinding;
import com.example.gallery.databinding.DialogAddImageBinding;
import com.example.gallery.databinding.DialogEditImageBinding;
import com.example.gallery.models.ItemModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.ArrayList;
import java.util.HashSet;
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

    EditImageDialog show(Context context, Bitmap bitmap, EditImageDialog.OnCompleteListener listener){
        this.context=context;
        this.listener = listener;
        this.bitmap=bitmap;

        //inflate dialogs layout
        if(context instanceof  MainActivity){
            inflater=((MainActivity) context).getLayoutInflater();
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
            public void onFetched(Bitmap image, Set<Integer> colors, List<String> label) {
                dialog.show();
                showData(bitmap,colors,label);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

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
                listener.onImageAdded(new ItemModel(bitmap,color,label));
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

        }
    }


    public interface OnCompleteListener {
        void onImageAdded(ItemModel item);
        void onError(String error);
    }
    }
