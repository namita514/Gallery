package com.example.gallery.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gallery.R;
import com.example.gallery.databinding.DialogAddImageBinding;
import com.example.gallery.databinding.ItemCardBinding;
import com.example.gallery.helpers.ItemTouchHelperAdapter;
import com.example.gallery.models.ItemModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private Context context;
    private List<ItemModel> items;
    private List<ItemModel> visibleItems;
    public  ItemTouchHelper mItemTouchHelper;
    public int mode;
    public int index;
    public String url;
   public ItemCardBinding binding;
   public List<ViewHolder> holders=new ArrayList<>();

    public ItemAdapter(Context context, List<ItemModel> items){

        this.context = context;
        this.items = items;
        visibleItems=items;
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper){
       mItemTouchHelper=itemTouchHelper;
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
        holders.add(holder);
        ItemModel itemModel=visibleItems.get(position);
        Glide.with(context)
                .asBitmap()
                .load(itemModel.url)
                .into(holder.cardBinding.imageView);
       holder.cardBinding.title.setText(itemModel.label);
       holder.cardBinding.title.setBackgroundColor(itemModel.color);

    }
    @Override
    public void onItemDismiss(int position) {
        items.remove(position);
        if (items.isEmpty()) {
            Toast.makeText(context,"no item is there",Toast.LENGTH_SHORT).show();
        }
        visibleItems=items;
        notifyDataSetChanged();

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(items, items.indexOf(visibleItems.get(fromPosition)), items.indexOf(visibleItems.get(toPosition)));
        Collections.swap(visibleItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
return true;
    }

    @Override
    public int getItemCount() {
        return visibleItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnTouchListener,
            GestureDetector.OnGestureListener, View.OnCreateContextMenuListener
    {
        GestureDetector gestureDetector;
        ItemCardBinding cardBinding;


        public ViewHolder( ItemCardBinding cardBinding) {
            super(cardBinding.getRoot());
            this.cardBinding = cardBinding;
            gestureDetector=new GestureDetector(cardBinding.getRoot().getContext(),this);

            setUpListener();
        }
        public void setUpListener() {
            if(mode==1){
                cardBinding.imageView.setOnTouchListener(this);
                cardBinding.title.setOnTouchListener(this);
                cardBinding.imageView.setOnCreateContextMenuListener(null);
                cardBinding.title.setOnCreateContextMenuListener(null);
            }
            else
            {
                cardBinding.imageView.setOnTouchListener(null);
                cardBinding.title.setOnTouchListener(null);
                cardBinding.imageView.setOnCreateContextMenuListener(this);
                cardBinding.title.setOnCreateContextMenuListener(this);
            }
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
            if(mode==1)
            mItemTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle("select an action");
            menu.add(this.getAdapterPosition(), R.id.edit_picture,0,"Edit image");
            menu.add(this.getAdapterPosition(),R.id.share_image,1,"Share image");
            index=this.getAdapterPosition();
            binding=cardBinding;
            url=items.get(this.getAdapterPosition()).url;

        }

    }

    // to sort the data item of recycler view-----------------------------------------------------
    public void sortData(){
        Collections.sort(items, new Comparator<ItemModel>() {
            @Override
            public int compare(ItemModel lhs, ItemModel rhs) {
                return lhs.label.toLowerCase().compareTo(rhs.label.toLowerCase());
            }

        });
        visibleItems=items;
        notifyDataSetChanged();
    }

    //to search the item of recycler view
    public void searchData(String query){
        if(query==null){
            visibleItems=items;
            notifyDataSetChanged();
            return;
        }
        query = query.toLowerCase();
        List<ItemModel>filterData= new ArrayList<>();
        for(ItemModel item:items){
            if(item.label.toLowerCase().trim().contains(query)){
                filterData.add(item);
            }
        }
        visibleItems=filterData;
        notifyDataSetChanged();


    }

}
