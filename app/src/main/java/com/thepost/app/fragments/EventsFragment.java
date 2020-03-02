package com.thepost.app.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;
import com.thepost.app.MainActivity;
import com.thepost.app.R;
import com.thepost.app.activities.FullImageActivity;
import com.thepost.app.adapters.ArticleAdapter;
import com.thepost.app.adapters.EventsAdapter;
import com.thepost.app.models.EventsModel.DataModel;
import com.thepost.app.models.EventsModel.EventsModel;
import com.thepost.app.remotes.ApiUtils;
import com.thepost.app.remotes.EventsAPIService;
import com.thepost.app.utils.ClickListener;
import com.thepost.app.utils.ContextUtils;
import com.thepost.app.utils.RecyclerTouchListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsFragment extends Fragment {

    private EventsAPIService apiService;
    private View view;
    private RecyclerView recyclerView;
    private EventsModel model;
    private EventsAdapter adapter;
    private List<DataModel> list;
    private List<DataModel> LIST;

    static boolean loaded;


    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loaded = false;

        view = inflater.inflate(R.layout.fragment_events, container, false);
        apiService = ApiUtils.getEventsAPIService();

        LIST = new ArrayList<>();
        list = new ArrayList<>();

        view.findViewById(R.id.loadingCoverage).setVisibility(View.VISIBLE);

        new loadCoverage().execute();

        getAllEvents();
        return view;
    }

    /**
     * Use Retrofit to get all the events from the backend
     * Also set all onClickListeners for the Recycler elements
     */
    private void getAllEvents() {

        apiService.getEvents().enqueue(new Callback<EventsModel>() {

            @Override
            @EverythingIsNonNull
            public void onResponse(Call<EventsModel> call, final Response<EventsModel> response) {

                if (response.isSuccessful()) {

                    assert response.body() != null;

                    if (response.body().getStatus().equalsIgnoreCase("ok")) {

                        list = response.body().getData();
                        LIST.clear();
                        LIST.addAll(list);

                        List<DataModel> events = new ArrayList<>();

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                        for (DataModel dm : list) {

                            Date date;

                            try {
                                date = format.parse(dm.getDate());
                            } catch (ParseException e) {
                                date = new Date();
                            }

                            if (new Date().getTime() <= date.getTime() + (24 * 60 * 60 * 1000)) {

                                events.add(dm);
                            }
                        }

                        list.clear();
                        list.addAll(events);

                        if(list.size()>0) {

                            view.findViewById(R.id.emptyEvents).setVisibility(View.GONE);

                            RecyclerView recyclerView2 = view.findViewById(R.id.eventsRecycler);
                            LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
                            layoutManager2.setReverseLayout(true);
                            layoutManager2.setStackFromEnd(true);
                            recyclerView2.setLayoutManager(layoutManager2);

                            Collections.reverse(events);

                            EventsAdapter adapter2 = new EventsAdapter(list);
                            recyclerView2.setAdapter(adapter2);
                            recyclerView2.invalidate();

                            recyclerView2.setVisibility(View.VISIBLE);

                            recyclerView2.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView2, new ClickListener() {
                                @Override
                                public void onClick(final View view, final int position) {

                                    showFullEvent(position);
                                }

                                @Override
                                public void onLongClick(View view, int position) {
                                    //Long clicked. Do something here.
                                }
                            }));
                        }
                        else{
                            view.findViewById(R.id.eventsRecycler).setVisibility(View.GONE);
                            view.findViewById(R.id.emptyEvents).setVisibility(View.VISIBLE);
                        }
                    }
                } else {

                    //TODO: Show when error occurs
                }
            }

            @EverythingIsNonNull
            @Override
            public void onFailure(Call<EventsModel> call, Throwable t) {

                //TODO: Show when error occurs
            }
        });
    }

    /**
     *
     * @param position: The position of the event which was clicked (to display full event).
     */

    private void showFullEvent(final int position) {

        assert getContext() != null;

        final DialogPlus dialogPlus = DialogPlus.newDialog(getContext())
                .setContentHolder(new ViewHolder(R.layout.dialog_full))
                .setCancelable(true)
                .setExpanded(false)
                .create();

        final View viewDialog = dialogPlus.getHolderView();

        if (!list.get(position).getFormLink().equals("NA")) {
            viewDialog.findViewById(R.id.formLink).setVisibility(View.VISIBLE);
            viewDialog.findViewById(R.id.formLink).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        if (getActivity() != null) {
                            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                            i.setData(Uri.parse(list.get(position).getFormLink()));
                            startActivity(i);
                        }
                    } catch (Exception e) {

                        try {
                            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("Registration Link", list.get(position).getFormLink());

                            assert clipboardManager != null;

                            clipboardManager.setPrimaryClip(clipData);
                            ContextUtils.makeToast(getActivity(), "There was an error opening your browser. Link has been copied to clipboard.", 1);

                        } catch (Exception e1) {

                            ContextUtils.makeToast(getActivity(), "Error opening the registration link.", 1);
                        }
                    }
                }
            });
        }

        ((TextView) viewDialog.findViewById(R.id.locationFind)).setText(list.get(position).getLocation());
        viewDialog.findViewById(R.id.locationFind).setVisibility(View.VISIBLE);
        viewDialog.findViewById(R.id.locationFind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getActivity() != null) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("geo:0,0?q=" + list.get(position).getLocation().trim() + ", Manipal"));
                    getActivity().startActivity(i);
                }
            }
        });

        ((TextView) viewDialog.findViewById(R.id.notice_full_view_title)).setText(list.get(position).getTitle());
        ((TextView) viewDialog.findViewById(R.id.notice_full_view_content)).setText(list.get(position).getContent().concat("\n\n~").concat(list.get(position).getClubName()));

        String pattern = "yyyy-MM-dd";
        String outputPattern = "dd MMMM, yyyy";
        SimpleDateFormat format = new SimpleDateFormat(outputPattern, Locale.getDefault());

        Date date;

        try {
            date = new SimpleDateFormat(pattern, Locale.getDefault()).parse(list.get(position).getDate());
        } catch (Exception e) {
            date = new Date();
        }

        String finale = format.format(date);

        ((TextView) viewDialog.findViewById(R.id.notice_full_view_timestamp)).setText(finale);

        ImageView imageView = viewDialog.findViewById(R.id.notice_full_view_image);

        if (!list.get(position).getImageURL().equals("")) {


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

                    if (getActivity() != null) {
                        Intent intent = new Intent(getContext(), FullImageActivity.class);
                        intent.putExtra("imageLink", list.get(position).getImageURL());

                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                    }
                }
            });
        } else {

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
                ContextUtils.saveEvent(viewDialog.getContext(), list.get(position).getTitle(), list.get(position).getDate(), list.get(position).getTime());
            }
        });

        dialogPlus.show();
    }

    private class loadCoverage extends AsyncTask<Void, Void, Boolean> {
        protected Boolean doInBackground(Void... urls) {

            while(!loaded){
                //Empty loop
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {

            view.findViewById(R.id.loadingCoverage).setVisibility(View.GONE);

            RecyclerView recyclerView1 = view.findViewById(R.id.eventsRecyclerArticles);

            recyclerView1.setVisibility(View.VISIBLE);
            recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));

            ArticleAdapter adapter = new ArticleAdapter(HomeFragment.eventReports);
            recyclerView1.setAdapter(adapter);

            Log.e("Loaded","Async Ran");
        }
    }
}
