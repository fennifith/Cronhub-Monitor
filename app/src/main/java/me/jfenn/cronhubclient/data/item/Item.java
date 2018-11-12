package me.jfenn.cronhubclient.data.item;

import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

public abstract class Item<T extends RecyclerView.ViewHolder> {

    @LayoutRes
    private int layout;

    public Item(@LayoutRes int layout) {
        this.layout = layout;
    }

    @LayoutRes
    public final int getLayoutRes() {
        return layout;
    }

    public abstract T getViewHolder(View v);

    public abstract void bind(T holder);

}
