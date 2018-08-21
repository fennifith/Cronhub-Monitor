package me.jfenn.cronhubclient.data.item;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class Item<T extends RecyclerView.ViewHolder> {

    @LayoutRes
    private int layout;

    public Item(@LayoutRes int layout) {
        this.layout = layout;
    }

    public final int getLayoutRes() {
        return layout;
    }

    public abstract T getViewHolder(View v);

    public abstract void bind(T holder);

}
