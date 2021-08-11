package com.example.gallery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.databinding.ItemCardBinding;
import com.example.gallery.helpers.ItemTouchHelperAdapter;
import com.example.gallery.models.ItemModel;

import java.util.Collections;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private List<ItemModel> items;

    public ItemAdapter(Context context, List<ItemModel> items){

        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        //Inflate Layout and return viewholder
        ItemCardBinding cardBinding=ItemCardBinding.inflate(LayoutInflater.from(context),parent,false);

        // Create and return ViewHolder
        return new ViewHolder(cardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull  ItemAdapter.ViewHolder holder, int position) {
        ItemModel itemModel=items.get(position);
       holder.cardBinding.imageView.setImageBitmap(itemModel.image);
       holder.cardBinding.title.setText(itemModel.label);
       holder.cardBinding.title.setBackgroundColor(itemModel.color);
    }
    @Override
    public void onItemDismiss(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemCardBinding cardBinding;
        public ViewHolder( ItemCardBinding cardBinding) {
            super(cardBinding.getRoot());
            this.cardBinding = cardBinding;
        }
    }

}
