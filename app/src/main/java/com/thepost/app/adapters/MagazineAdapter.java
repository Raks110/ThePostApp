package com.thepost.app.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thepost.app.R;
import com.thepost.app.models.MagazineModel.MagazineModel;

import java.util.List;

public class MagazineAdapter extends RecyclerView.Adapter<MagazineAdapter.ViewHolder>{

    private List<MagazineModel> list;

    public MagazineAdapter(List<MagazineModel> list){

        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_magazine, parent, false);

        return new ViewHolder(ll);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Picasso.with(holder.itemView.getContext()).load(list.get(position).getImageURL()).into(holder.image, new Callback() {
            @Override
            public void onSuccess() {

                holder.progressBar.setVisibility(View.GONE);
                holder.error.setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {

                holder.progressBar.setVisibility(View.GONE);
                holder.error.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.INVISIBLE);
            }
        });

        holder.title.setText(list.get(position).getTitle());

        Log.e("Adapter", "Running");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        private TextView title;
        private ProgressBar progressBar;
        private ImageView error;

        ViewHolder(@NonNull LinearLayout ll){

            super(ll);
            image = ll.findViewById(R.id.magazine_cover);
            title = ll.findViewById(R.id.title);
            progressBar = ll.findViewById(R.id.progress);
            error = ll.findViewById(R.id.error);
        }
    }
}
