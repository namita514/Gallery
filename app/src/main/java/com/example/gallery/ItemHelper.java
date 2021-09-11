package com.example.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.gallery.helpers.RedirectedUrlHelper;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemHelper {
    private Context context;
    private OnCompleteListener listener;
    private String rectangularImageURL="https://picsum.photos/%d/%d"
            ,squareImageURL="https://picsum.photos/%d";
    private Bitmap bitmap;
    private Set<Integer> colors;
    private String url1;

    //Triggers---------------------------------------------------------------

// for Rectangular Image
    void fetchData(int x, int y, Context context, OnCompleteListener listener){
        this.context = context;
        this.listener = listener;
        fetchImage(String.format(rectangularImageURL,x,y));
    }
    // for Square image
    void fetchData(int x,Context context,OnCompleteListener listener){
        this.context = context;

        this.listener = listener;
        fetchImage(String.format(squareImageURL,x));
    }

    void fetchImageFromGallery(String url,Context context,OnCompleteListener listener){
        this.context = context;

        this.listener = listener;
        fetchImage(url);
    }

    //imageFetcher--------------------------------------------------------------
    void fetchImage(String Url){
         new RedirectedUrlHelper().fetchRedirectedUrl(new RedirectedUrlHelper.OnCompleteListener() {
             @Override
             public void onFetched(String redirectedUrl) {
                 url1=redirectedUrl;
                 Glide.with(context).asBitmap().load(url1).diskCacheStrategy(DiskCacheStrategy.NONE).into(new CustomTarget<Bitmap>() {
                     @Override
                     public void onResourceReady(@NonNull  Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                         extractPaletteColorFromBitmap(resource);
                     }

                     @Override
                     public void onLoadCleared(@Nullable  Drawable placeholder) {
                         super.onLoadFailed(placeholder);
//             listener.onError("image not found");
                     }
                 });
             }
         }).execute(Url);

    }
// paletteHelper---------------------------------------------------------
    private void extractPaletteColorFromBitmap(Bitmap bitmap) {
        this.bitmap=bitmap;
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // Use generated instance
           colors=getColorsFromPalette(p);
           labelImage();
            }
        });
    }
// labelHelper----------------------------------------------------------------------
    private void labelImage() {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        // Task completed successfully
                        // ...
                       List<String> strings=new ArrayList<>();
                       for(ImageLabel label:labels){
                          strings.add(label.getText());
                       }
                   listener.onFetched(bitmap,url1,colors,strings);}
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        // Task failed with an exception
                        // ...
listener.onError(e.toString());     }
                });
    }

    private Set<Integer> getColorsFromPalette(Palette p){
        Set<Integer> colors= new HashSet<>();
        colors.add(p.getVibrantColor(0));
        colors.add(p.getLightVibrantColor(0));
        colors.add(p.getDarkVibrantColor(0));

        colors.add(p.getMutedColor(0));
        colors.add(p.getLightMutedColor(0));
        colors.add(p.getDarkMutedColor(0));
        colors.remove(0);
        return colors;
    }
// listener--------------------------------------
    interface OnCompleteListener{
        void onFetched( Bitmap image,String url, Set<Integer> colors, List<String> label);
        void onError(String error);
    }

    // to get the color and label option for editable image
    public void editImage(Bitmap bitmap ,Context context,OnCompleteListener listener){
        this.bitmap=bitmap;
        this.context=context;
        this.listener=listener;
        extractPaletteColorFromBitmap(bitmap);
    }
}
