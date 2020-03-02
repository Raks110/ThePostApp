package com.thepost.app.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thepost.app.R;
import com.thepost.app.models.NoticeModel.NoticeDataModel;

import java.util.List;

@Keep
public class NoticesAdapter extends RecyclerView.Adapter<NoticesAdapter.ViewHolder>{

    private List<NoticeDataModel> list;
    private long DURATION = 100;
    private boolean on_attach = true;
    private int lastPosition;

    public NoticesAdapter(List<NoticeDataModel> list){

        this.list = list;
        lastPosition = -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout cl = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_notices, parent, false);

        return new NoticesAdapter.ViewHolder(cl);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NoticeDataModel noticeDataModel = list.get(position);

        holder.noticesTitle.setText(noticeDataModel.getTitle());

        if(lastPosition < position){
            lastPosition = position;
            setAnimation(holder.itemView,position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View itemView, int i) {
        if (!on_attach) {
            i = -1;
        }
        boolean isNotFirstItem = i == -1;
        i++;
        itemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, "alpha", 0.f, 0.5f, 1.0f);
        ObjectAnimator.ofFloat(itemView, "alpha", 0.f).start();
        animator.setStartDelay(isNotFirstItem ? DURATION / 2 : (i * DURATION / 3));
        animator.setDuration(500);
        animatorSet.play(animator);
        animator.start();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView noticesTitle;

        private ViewHolder(LinearLayout cl) {
            super(cl);

            noticesTitle = cl.findViewById(R.id.notices_title);
        }

    }
}
