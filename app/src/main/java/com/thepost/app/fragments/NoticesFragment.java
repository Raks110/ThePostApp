package com.thepost.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;
import com.thepost.app.MainActivity;
import com.thepost.app.R;
import com.thepost.app.activities.FullImageActivity;
import com.thepost.app.adapters.NoticesAdapter;
import com.thepost.app.adapters.RecentNoticesAdapter;
import com.thepost.app.models.NoticeModel.NoticeDataModel;
import com.thepost.app.models.NoticeModel.NoticeModel;
import com.thepost.app.remotes.ApiUtils;
import com.thepost.app.remotes.NoticesAPIService;
import com.thepost.app.utils.ClickListener;
import com.thepost.app.utils.ContextUtils;
import com.thepost.app.utils.RecyclerTouchListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class NoticesFragment extends Fragment {


    private NoticesAPIService noticesAPIService;
    private View view;
    private NoticesAdapter adapter;
    private RecentNoticesAdapter recentNoticesAdapter;
    private List<NoticeDataModel> listOld;
    private List<NoticeDataModel> listNew;
    private List<NoticeDataModel> LIST;
    private boolean searching;

    public NoticesFragment() {
        // Required empty public constructor
    }

    public static NoticesFragment newInstance() {
        return new NoticesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notices, container, false);

        listNew = new ArrayList<>();
        listOld = new ArrayList<>();
        LIST = new ArrayList<>();

        noticesAPIService = ApiUtils.getNoticesAPIService();

        getAllNotices();
        searchNotices();

        return view;
    }

    private void getAllNotices(){

        noticesAPIService.getNotices().enqueue(new Callback<NoticeModel>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<NoticeModel> call, final Response<NoticeModel> response) {

                //Log.e("TAG", "response 33: "+new Gson().toJson(response.body()) );

                if(response.isSuccessful()){

                    assert response.body() != null;

                    if(response.body().getStatus().equals("OK")){

                        LIST = response.body().getData();
                        Collections.reverse(LIST);

                        if(LIST.size() >= 2){

                            listNew.add(LIST.get(0));
                            listNew.add(LIST.get(1));

                            listOld.addAll(LIST);

                            listOld.remove(0);
                            listOld.remove(0);
                        }
                        else{

                            listNew.addAll(LIST);
                        }

                        if(listNew.isEmpty()){

                            view.findViewById(R.id.loading).setVisibility(View.GONE);

                            view.findViewById(R.id.recentNoticesRecycler).setVisibility(View.GONE);
                            view.findViewById(R.id.olderNoticesRecycler).setVisibility(View.GONE);
                            view.findViewById(R.id.emptyNotices).setVisibility(View.VISIBLE);


                            String noticesMsg = "There are no notices to show.\nWe'll notify you when one appears!";
                            view.findViewById(R.id.refresh_notices_no_internet).setVisibility(View.GONE);
                            ((TextView) view.findViewById(R.id.emptyNoticesMsg)).setText(noticesMsg);

                            return;
                        }

                        final RecyclerView recyclerView = view.findViewById(R.id.recentNoticesRecycler);
                        final RecyclerView recyclerViewOld = view.findViewById(R.id.olderNoticesRecycler);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerViewOld.setLayoutManager(layoutManager);

                        if(getActivity()!=null) {

                            //Set divider for old notices recycler-view

                            DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerViewOld.getContext(), layoutManager.getOrientation());

                            TypedValue value = new TypedValue();
                            getActivity().getTheme().resolveAttribute(R.attr.secondaryTextColor, value, true);
                            @ColorInt int color = value.data;

                            itemDecoration.setDrawable(new ColorDrawable(color));

                            recyclerViewOld.addItemDecoration(itemDecoration);
                        }

                        recyclerView.setVisibility(View.VISIBLE);

                        if(listOld.size() > 0) {
                            recyclerViewOld.setVisibility(View.VISIBLE);
                        }
                        else{
                            recyclerViewOld.setVisibility(View.GONE);
                        }

                        view.findViewById(R.id.emptyNotices).setVisibility(View.GONE);

                        assert getActivity() != null;

                        view.findViewById(R.id.loading).setVisibility(View.GONE);

                        adapter = new NoticesAdapter(listOld);
                        recentNoticesAdapter = new RecentNoticesAdapter(listNew);

                        recyclerView.setAdapter(recentNoticesAdapter);
                        recyclerViewOld.setAdapter(adapter);

                        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
                            @Override
                            public void onClick(final View view, final int position) {

                                showFullNotice(view, position, false);
                            }

                            @Override
                            public void onLongClick(View view, int position) {
                                //Long clicked. Do something here.
                            }
                        }));

                        recyclerViewOld.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerViewOld, new ClickListener() {
                            @Override
                            public void onClick(final View view, final int position) {

                                showFullNotice(view, position, true);
                            }

                            @Override
                            public void onLongClick(View view, int position) {
                                //Long clicked. Do something here.
                            }
                        }));

                    }
                    else{

                        view.findViewById(R.id.loading).setVisibility(View.GONE);


                        String noticesMsg = "Your phone and our servers are struggling to communicate. Please try again.";
                        ((TextView) view.findViewById(R.id.emptyNoticesMsg)).setText(noticesMsg);

                        view.findViewById(R.id.recentNoticesRecycler).setVisibility(View.GONE);
                        view.findViewById(R.id.olderNoticesRecycler).setVisibility(View.GONE);

                        view.findViewById(R.id.emptyNotices).setVisibility(View.VISIBLE);
                    }
                }
                else{

                    view.findViewById(R.id.loading).setVisibility(View.GONE);

                    String noticesMsg = "Your phone and our servers are struggling to communicate. Please try again.";
                    ((TextView) view.findViewById(R.id.emptyNoticesMsg)).setText(noticesMsg);

                    view.findViewById(R.id.recentNoticesRecycler).setVisibility(View.GONE);
                    view.findViewById(R.id.olderNoticesRecycler).setVisibility(View.GONE);

                    view.findViewById(R.id.emptyNotices).setVisibility(View.VISIBLE);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<NoticeModel> call, Throwable t) {

                view.findViewById(R.id.loading).setVisibility(View.GONE);

                String noticesMsg = "Your phone and our servers are struggling to communicate. Please try again.";
                ((TextView) view.findViewById(R.id.emptyNoticesMsg)).setText(noticesMsg);

                view.findViewById(R.id.recentNoticesRecycler).setVisibility(View.GONE);
                view.findViewById(R.id.olderNoticesRecycler).setVisibility(View.GONE);

                view.findViewById(R.id.emptyNotices).setVisibility(View.VISIBLE);
            }
        });
    }

    private void showFullNotice(final View view, final int position, boolean isOld){

        final List<NoticeDataModel> list = new ArrayList<>();

        if(isOld){
            list.addAll(listOld);
        }
        else{
            list.addAll(listNew);
        }

        String dateParser = list.get(position).getDate();
        Date date;

        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateParser);
        }
        catch(ParseException e){
            date = new Date();
        }

        assert getContext() != null;

        final DialogPlus dialogPlus = DialogPlus.newDialog(getContext())
                .setContentHolder(new ViewHolder(R.layout.dialog_full))
                .setCancelable(true)
                .setExpanded(false)
                .create();

        final View viewDialog = dialogPlus.getHolderView();

        ((TextView) viewDialog.findViewById(R.id.notice_full_view_title)).setText(list.get(position).getTitle());
        if(!list.get(position).getPdfLink().equals(""))
            ((TextView) viewDialog.findViewById(R.id.notice_full_view_content)).setText(list.get(position).getContent() + "\n\nHere's an additional resource:\n\n" + list.get(position).getPdfLink());
        else
            ((TextView) viewDialog.findViewById(R.id.notice_full_view_content)).setText(list.get(position).getContent());

        viewDialog.findViewById(R.id.reminder).setVisibility(View.GONE);

        String timestamp = "Posted on " + new SimpleDateFormat("dd MMMM, yy").format(date);
        ((TextView) viewDialog.findViewById(R.id.notice_full_view_timestamp)).setText(timestamp);

        ImageView imageView = viewDialog.findViewById(R.id.notice_full_view_image);

        if(!list.get(position).getImageURL().equals("")){

            Picasso.with(getContext()).load(list.get(position).getImageURL()).into(imageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                    viewDialog.findViewById(R.id.error).setVisibility(View.GONE);
                    viewDialog.findViewById(R.id.progress).setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                    viewDialog.findViewById(R.id.error).setVisibility(View.VISIBLE);
                    viewDialog.findViewById(R.id.progress).setVisibility(View.GONE);
                }
            });

            viewDialog.findViewById(R.id.notice_full_view_image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(getActivity() != null) {
                        Intent intent = new Intent(getContext(), FullImageActivity.class);
                        intent.putExtra("imageLink", list.get(position).getImageURL());

                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                    }
                }
            });
        }

        else{

            viewDialog.findViewById(R.id.progress).setVisibility(View.GONE);
        }

        viewDialog.findViewById(R.id.notice_full_view_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPlus.dismiss();
            }
        });

        viewDialog.findViewById(R.id.reminder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextUtils.saveEvent(viewDialog.getContext(), list.get(position).getTitle());
            }
        });

        dialogPlus.show();
    }

    private void searchNotices() {

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

                                view.findViewById(R.id.recentNoticesRecycler).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.header).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.divider).setVisibility(View.VISIBLE);
                                view.findViewById(R.id.olderNoticesRecycler).setVisibility(View.VISIBLE);

                                view.findViewById(R.id.olderNoticesRecyclerSearch).setVisibility(View.GONE);
                            } else {

                                //Hide Respective Views and show the ones needed after search is started

                                view.findViewById(R.id.recentNoticesRecycler).setVisibility(View.GONE);
                                view.findViewById(R.id.header).setVisibility(View.GONE);
                                view.findViewById(R.id.divider).setVisibility(View.GONE);
                                view.findViewById(R.id.olderNoticesRecycler).setVisibility(View.GONE);

                                view.findViewById(R.id.olderNoticesRecyclerSearch).setVisibility(View.VISIBLE);

                                RecyclerView rv = view.findViewById(R.id.olderNoticesRecyclerSearch);
                                rv.setLayoutManager(new LinearLayoutManager(getContext()));

                                rv.setAdapter(new NoticesAdapter(searchResults(s.toString())));
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

                    view.findViewById(R.id.recentNoticesRecycler).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.header).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.divider).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.olderNoticesRecycler).setVisibility(View.VISIBLE);

                    view.findViewById(R.id.olderNoticesRecyclerSearch).setVisibility(View.GONE);

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

    /**
     *
     * @param search Used as a key to return relevant Notices
     * @return a list of notices that matches the search
     */

    private List<NoticeDataModel> searchResults(String search){

        List<NoticeDataModel> l = new ArrayList<>();

        if(search.length() > 0 && LIST != null){

            for(NoticeDataModel notice : LIST){

                if(notice.getTitle().toLowerCase().contains(search.trim()) || notice.getContent().toLowerCase().contains(search.trim())){

                    l.add(notice);
                }
            }
        }

        return l;
    }
}
