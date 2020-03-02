package com.thepost.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.thepost.app.R;
import com.thepost.app.models.EventsModel.DataModel;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder>{

    private List<DataModel> list;

    public EventsAdapter(List<DataModel> list){

        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_events, parent, false);

        return new ViewHolder(ll);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        Picasso.with(holder.itemView.getContext()).load(list.get(position).getImageURL()).into(holder.image, new Callback() {
            @Override
            public void onSuccess() {

                holder.itemView.findViewById(R.id.progress).setVisibility(View.GONE);
                holder.itemView.findViewById(R.id.error).setVisibility(View.GONE);
                holder.image.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {

                holder.itemView.findViewById(R.id.progress).setVisibility(View.GONE);
                holder.itemView.findViewById(R.id.error).setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView image;

        ViewHolder(@NonNull LinearLayout ll){

            super(ll);
            image = ll.findViewById(R.id.notice_past_image);
        }
    }
}
