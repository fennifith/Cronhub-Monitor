package me.jfenn.cronhubclient.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import me.jfenn.cronhubclient.data.item.Item;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Item> infos;

    public ItemAdapter(List<Item> infos) {
        this.infos = infos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Item info = infos.get(viewType);
        return info.getViewHolder(LayoutInflater.from(parent.getContext()).inflate(info.getLayoutRes(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        infos.get(position).bind(holder);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

}
