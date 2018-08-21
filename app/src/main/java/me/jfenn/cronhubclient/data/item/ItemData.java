package me.jfenn.cronhubclient.data.item;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;

public abstract class ItemData<T extends RecyclerView.ViewHolder> {

    @LayoutRes
    private int layout;

    public ItemData(@LayoutRes int layout) {
        this.layout = layout;
    }

    public final int getLayoutRes() {
        return layout;
    }

    public abstract T getViewHolder();

    public abstract void bindView(T holder);

}
