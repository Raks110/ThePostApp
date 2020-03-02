package com.thepost.app.adapters;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import com.thepost.app.R;
import com.thepost.app.models.ArticleModel.ArticleModel;
import com.thepost.app.models.ArticleModel.Date;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Keep
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    static private List<ArticleModel> articles;
    private long DURATION = 500;
    private boolean on_attach = true;
    private int lastPosition;

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView articlesView;
        private TextView timestampView;
        private TextView authorView;
        private ImageView articleImage;
        private TextView articleSummary;

        private ViewHolder(MaterialCardView cl) {
            super(cl);

            articlesView = cl.findViewById(R.id.articlesView);
            timestampView = cl.findViewById(R.id.timestampView);
            authorView = cl.findViewById(R.id.authorView);
            articleImage = cl.findViewById(R.id.articleImage);
            articleSummary = cl.findViewById(R.id.articleSummary);
        }

    }

    public ArticleAdapter(List<ArticleModel> inArts) {
        articles = inArts;
        lastPosition = -1;
    }

    @Override
    @NonNull
    public ArticleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        MaterialCardView cl = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_articles, parent, false);

        return new ViewHolder(cl);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        ArticleModel am = articles.get(position);

        holder.articlesView.setText(Html.fromHtml(am.getTitle()));

        Date amd = am.getDate();
        String datetime = amd.getMonth().substring(0,3) + " " + amd.getDay() + ", " + amd.getYear();

        java.util.Date date;

        try {
            date = new SimpleDateFormat("MMM dd, yyyy").parse(datetime);
        }
        catch(ParseException e){

            date = new java.util.Date();
        }
        PrettyTime prettyTime = new PrettyTime();
        holder.timestampView.setText(prettyTime.format(date));

        holder.articleSummary.setText(Html.fromHtml(am.getMessage()));

        holder.authorView.setText(am.getAuthor().getName());
        Picasso.with(holder.itemView.getContext()).load(am.getFeaturedMedia()).into(holder.articleImage);

        holder.articleImage.setContentDescription("Featured image for this article.");

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
        return articles.size();
    }
}
