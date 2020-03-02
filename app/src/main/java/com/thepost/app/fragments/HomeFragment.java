package com.thepost.app.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.thepost.app.R;
import com.thepost.app.adapters.ArticleAdapter;
import com.thepost.app.adapters.ArticlesPagerAdapter;
import com.thepost.app.models.ArticleModel.ArticleModel;
import com.thepost.app.remotes.ApiUtils;
import com.thepost.app.remotes.ArticleAPIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class HomeFragment extends Fragment {

    public List<ArticleModel> list;
    public static List<ArticleModel> LIST;

    static List<ArticleModel> eventReports;

    private List<List<ArticleModel>> categorized;

    private View view;
    private ArticleAPIService mAPIService;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private List<String> titles;
    private boolean searching;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        list = new ArrayList<>();
        LIST = new ArrayList<>();
        eventReports = new ArrayList<>();

        showLoading();
        searchArticles();

        mAPIService = ApiUtils.getArticleAPIService();
        getAllPosts();

        view.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                getAllPosts();
            }
        });

        return view;
    }

    /**
     * Gets all articles as a List of ArticleModel using Retrofit.
     */

    private void getAllPosts() {

        mAPIService.getPosts().enqueue(new Callback<List<ArticleModel>>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<ArticleModel>> call, Response<List<ArticleModel>> response) {

                if (response.isSuccessful()) {

                    list = response.body();

                    if(list == null){

                        showError("Check your internet connection and try again.");
                        return;
                    }

                    LIST.clear();
                    LIST.addAll(list);

                    //new loadArticles().execute();

                    viewPager = view.findViewById(R.id.viewPager);
                    tabLayout = view.findViewById(R.id.articleTabs);

                    makeCategories();

                    viewPager.setOffscreenPageLimit(titles.size());
                    viewPager.setAdapter(new ArticlesPagerAdapter(getContext(), getActivity(), categorized, titles));

                    //tabLayout.setupWithViewPager(viewPager);
                    new TabLayoutMediator(tabLayout, viewPager,
                            new TabLayoutMediator.TabConfigurationStrategy() {
                                @Override
                                public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                    tab.setText(titles.get(position));
                                }
                            }).attach();


                    showContent();
                } else {

                    showError("Check your internet connection and try again.");
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<ArticleModel>> call, Throwable t) {

                showError("Check your internet connection and try again.");
            }
        });
    }

    /**
     * Show loading screen and hide everything else.
     */

    private void showLoading() {

        view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
        view.findViewById(R.id.search).setVisibility(View.GONE);
        view.findViewById(R.id.empty).setVisibility(View.GONE);
    }

    /**
     * Show the articles (called after the list of articles is ready)
     */

    private void showContent() {

        view.findViewById(R.id.loading).setVisibility(View.GONE);
        view.findViewById(R.id.search).setVisibility(View.VISIBLE);
        view.findViewById(R.id.empty).setVisibility(View.GONE);
    }

    /**
     * @param message This will be printed as the Error Message for the user to read.
     */

    private void showError(String message) {

        view.findViewById(R.id.loading).setVisibility(View.GONE);
        view.findViewById(R.id.search).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.emptyMsg)).setText(message);
        view.findViewById(R.id.empty).setVisibility(View.VISIBLE);
    }

    /**
     * Initialize all the categories to show
     */
    private void makeCategories() {

        titles = new ArrayList<>();
        titles.add("FAQ");
        titles.add("World");
        titles.add("Interviews");
        titles.add("General");
        titles.add("News");
        titles.add("TechTatva");
        titles.add("Reports");
        titles.add("Cultural");
        titles.add("Science");
        titles.add("Others");

        categorized = categorize();
    }

    /**
     * @return Returns a 2D List of articles, each list containing articles belonging to the given category
     */
    private List<List<ArticleModel>> categorize() {

        List<List<ArticleModel>> cats = new ArrayList<>();

        for (int i = 0; i < titles.size(); i++) {
            cats.add(new ArrayList<ArticleModel>());
        }

        for (ArticleModel article : LIST) {

            boolean flag = false;

            int i;
            for (i = 0; i < titles.size() - 1; i++) {

                String toSearch = titles.get(i);
                switch (toSearch) {

                    case "Cultural":
                        toSearch = "culture";
                        break;

                    case "Science & Tech":
                        toSearch = "science";
                        break;

                    case "Interviews":
                        toSearch = "sitting";
                        break;
                }

                if (article.getCategory().toLowerCase().contains(toSearch.toLowerCase())) {

                    flag = true;
                    cats.get(i).add(article);
                    break;
                }
            }

            if (!flag) {

                cats.get(i).add(article);
            }
        }

        eventReports.addAll(cats.get(6));
        EventsFragment.loaded = true;

        return cats;
    }


    private void searchArticles() {

        searching = false;

        view.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!searching) {

                    searching = true;

                    ((ImageView) view.findViewById(R.id.search)).setImageResource(R.drawable.ic_cancel);
                    view.findViewById(R.id.searchWrap).setVisibility(View.VISIBLE);

                    final EditText editText = view.findViewById(R.id.searchText);
                    editText.setVisibility(View.VISIBLE);

                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                            if (s.length() <= 0) {

                                view.findViewById(R.id.articleTabs).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.viewPager).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.articleRecyclerSearch).setVisibility(View.GONE);
                            } else {

                                //Hide Respective Views and show the ones needed after search is started

                                view.findViewById(R.id.articleTabs).setVisibility(View.GONE);
                                view.findViewById(R.id.viewPager).setVisibility(View.GONE);
                                view.findViewById(R.id.articleRecyclerSearch).setVisibility(View.VISIBLE);

                                RecyclerView rv = view.findViewById(R.id.articleRecyclerSearch);
                                rv.setLayoutManager(new LinearLayoutManager(getContext()));

                                rv.setAdapter(new ArticleAdapter(searchResults(s.toString())));
                            }
                        }
                    });
                }
                else{

                    searching = false;

                    //Hide Respective Views and show the ones needed after search is stopped

                    ((TextInputEditText)view.findViewById(R.id.searchText)).getText().clear();

                    ((ImageView) view.findViewById(R.id.search)).setImageResource(R.drawable.ic_search);
                    view.findViewById(R.id.searchWrap).setVisibility(View.GONE);

                    view.findViewById(R.id.articleRecyclerSearch).setVisibility(View.GONE);
                    view.findViewById(R.id.articleTabs).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.viewPager).setVisibility(View.VISIBLE);

                    //ViewPager tends to act unexpectedly after a keyboard is made to hide
                    //Thus the following code initializes the ViewPager's Constraints once again.

                    ConstraintSet constraintSet = new ConstraintSet();
                    ConstraintLayout constraintLayout = view.findViewById(R.id.articleParent);

                    view.findViewById(R.id.viewPager).setLayoutParams(new ConstraintLayout.LayoutParams(
                            ConstraintLayout.LayoutParams.MATCH_PARENT,
                            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
                    ));

                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.viewPager,ConstraintSet.TOP,R.id.articleTabs,ConstraintSet.BOTTOM,0);
                    constraintSet.connect(R.id.viewPager,ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM,0);
                    constraintSet.applyTo(constraintLayout);

                    //Used to explicitly hide the keyboard

                    InputMethodManager inputManager = (InputMethodManager) view
                            .getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);

                    if(inputManager != null) {

                        IBinder binder = view.getWindowToken();
                        inputManager.hideSoftInputFromWindow(binder,
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }

            }
        });
    }

    private List<ArticleModel> searchResults(String search){

        List<ArticleModel> l = new ArrayList<>();

        if(search.length() > 0 && LIST != null){

            for(ArticleModel articleModel : LIST){

                if(articleModel.getTitle().toLowerCase().contains(search.trim()) || articleModel.getAuthor().getName().toLowerCase().contains(search.trim())
                        || articleModel.getCategory().toLowerCase().contains(search.trim())){

                    l.add(articleModel);
                }
            }
        }

        return l;
    }
}
