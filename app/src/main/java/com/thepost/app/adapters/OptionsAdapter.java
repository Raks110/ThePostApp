package com.thepost.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thepost.app.R;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder>{

    List<Integer> drawables;
    List<String> titles;

    public OptionsAdapter(List<Integer> drawables, List<String> titles){

        this.drawables = drawables;
        this.titles = titles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout cardView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_options, parent, false);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.imageView.setImageResource(drawables.get(position));
        holder.title.setText(titles.get(position));
    }

    @Override
    public int getItemCount() {
        return drawables.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView title;

        public ViewHolder(@NonNull LinearLayout itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
        }
    }
}
