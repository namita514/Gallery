package com.example.gallery.adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallery.databinding.ItemCardBinding;
import com.example.gallery.helpers.ItemTouchHelperAdapter;
import com.example.gallery.models.ItemModel;

import java.util.Collections;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private List<ItemModel> items;
    private ItemTouchHelper itemTouchHelper;

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
    public void onItemMove(int fromPosition, int toPosition) {
        ItemModel formItem = items.get(fromPosition);
        items.remove(formItem);
        items.add(toPosition, formItem);

        notifyItemMoved(fromPosition, toPosition);

    }
    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper){
        this.itemTouchHelper=itemTouchHelper;
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener,
            GestureDetector.OnGestureListener
    {
        GestureDetector gestureDetector;
        ItemCardBinding cardBinding;
        private ItemTouchHelper itemTouchHelper;

        public ViewHolder( ItemCardBinding cardBinding) {
            super(cardBinding.getRoot());
            this.cardBinding = cardBinding;
            gestureDetector=new GestureDetector(cardBinding.getRoot().getContext(),this);
            cardBinding.getRoot().setOnTouchListener(this);
        }

        @Override

        public boolean onTouch(View view, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            itemTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return true;
        }
    }

}
