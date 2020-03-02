package com.thepost.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import com.thepost.app.R;
import com.thepost.app.activities.ArticleView;
import com.thepost.app.models.ArticleModel.ArticleModel;
import com.thepost.app.models.ArticleModel.Date;
import com.thepost.app.utils.ClickListener;
import com.thepost.app.utils.ContextUtils;
import com.thepost.app.utils.RecyclerTouchListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.internal.EverythingIsNonNull;

/*public class ArticlesPagerAdapter extends PagerAdapter {

    private Context mContext;
    private Activity activity;
    private List<List<ArticleModel>> articles;
    private List<String> titles;

    public ArticlesPagerAdapter(Context context, Activity activity, List<List<ArticleModel>> articles, List<String> titles) {
        mContext = context;
        this.articles = articles;
        this.titles = titles;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, final int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.pager_articles, collection, false);

        layout.findViewById(R.id.categorizedArticles).setVisibility(View.GONE);
        layout.findViewById(R.id.empty).setVisibility(View.VISIBLE);

        if(articles.get(position).size() < 0) {

            RecyclerView recyclerView = layout.findViewById(R.id.categorizedArticles);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

            ArticleAdapter adapter = new ArticleAdapter(articles.get(position));
            recyclerView.setAdapter(adapter);

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(mContext, recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, final int internalPosition) {
                    //Values are passing to activity & to fragment as well

                    if(mContext != null) {

                        Intent intent = new Intent(mContext, ArticleView.class);
                        intent.putExtra("_id", articles.get(position).get(internalPosition).getId());
                        intent.putExtra("author", articles.get(position).get(internalPosition).getAuthor().getName());
                        intent.putExtra("title", articles.get(position).get(internalPosition).getTitle());
                        intent.putExtra("link", articles.get(position).get(internalPosition).getLink());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                    }
                }

                @Override
                public void onLongClick(View view, int position) {
                    //Long clicked. Do something here.
                }
            }));
        }
        else{

            layout.findViewById(R.id.categorizedArticles).setVisibility(View.GONE);
            layout.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        }

        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @EverythingIsNonNull
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return titles.get(position);
    }

}*/

public class ArticlesPagerAdapter extends RecyclerView.Adapter<ArticlesPagerAdapter.ViewHolder>{

    private Context mContext;
    private Activity activity;
    private List<List<ArticleModel>> articles;
    private List<String> titles;

    public ArticlesPagerAdapter(Context context, Activity activity, List<List<ArticleModel>> articles, List<String> titles) {
        mContext = context;
        this.articles = articles;
        this.titles = titles;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ScrollView cl = (ScrollView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pager_articles, parent, false);

        return new ViewHolder(cl);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if(articles.get(position).size() > 0) {

            for(int i=0;i<articles.get(position).size();i++) {

                final int pos = i;

                MaterialCardView cl = (MaterialCardView) LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.rv_articles, (ViewGroup) holder.itemView, false);

                ArticleModel am = articles.get(position).get(i);

                TextView articlesView = cl.findViewById(R.id.articlesView);
                TextView timestampView = cl.findViewById(R.id.timestampView);
                TextView authorView = cl.findViewById(R.id.authorView);
                ImageView articleImage = cl.findViewById(R.id.articleImage);
                TextView articleSummary = cl.findViewById(R.id.articleSummary);

                articlesView.setText(Html.fromHtml(am.getTitle()));

                Date amd = am.getDate();
                String datetime = amd.getMonth().substring(0, 3) + " " + amd.getDay() + ", " + amd.getYear();

                java.util.Date date;

                try {
                    date = new SimpleDateFormat("MMM dd, yyyy").parse(datetime);
                } catch (ParseException e) {

                    date = new java.util.Date();
                }
                PrettyTime prettyTime = new PrettyTime();
                timestampView.setText(prettyTime.format(date));

                articleSummary.setText(Html.fromHtml(am.getMessage()));

                authorView.setText(am.getAuthor().getName());
                Picasso.with(holder.itemView.getContext()).load(am.getFeaturedMedia()).into(articleImage);

                articleImage.setContentDescription("Featured image for this article.");

                cl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(activity != null) {

                            Intent intent = new Intent(activity, ArticleView.class);
                            intent.putExtra("_id", articles.get(position).get(pos).getId());
                            intent.putExtra("author", articles.get(position).get(pos).getAuthor().getName());
                            intent.putExtra("title", articles.get(position).get(pos).getTitle());
                            intent.putExtra("link", articles.get(position).get(pos).getLink());
                            activity.startActivity(intent);

                            activity.overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                        }
                    }
                });

                holder.linearLayout.addView(cl);
            }
        }
        else{

            holder.constraintLayout.setVisibility(View.VISIBLE);
            holder.constraintLayout.findViewById(R.id.refresh).setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout constraintLayout;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull ScrollView itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.empty);
            linearLayout = itemView.findViewById(R.id.allArticles);
        }
    }
}
