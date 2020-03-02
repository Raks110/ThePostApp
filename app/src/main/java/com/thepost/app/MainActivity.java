package com.thepost.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;
import com.thepost.app.activities.AboutActivity;
import com.thepost.app.activities.FullImageActivity;
import com.thepost.app.activities.MagazineActivity;
import com.thepost.app.activities.MagazineListActivity;
import com.thepost.app.activities.PrivacyActivity;
import com.thepost.app.activities.SettingsActivity;
import com.thepost.app.adapters.OptionsAdapter;
import com.thepost.app.fragments.EventsFragment;
import com.thepost.app.fragments.HomeFragment;
import com.thepost.app.fragments.LoggedInSlcmFragment;
import com.thepost.app.fragments.NoticesFragment;
import com.thepost.app.fragments.SlcmFragment;
import com.thepost.app.models.NoticeModel.NoticeDataModel;
import com.thepost.app.models.SlcmModel.BasicModel.SlcmBasicModel;
import com.thepost.app.remotes.ApiUtils;
import com.thepost.app.remotes.TokenAPIService;
import com.thepost.app.utils.ClickListener;
import com.thepost.app.utils.ContextUtils;
import com.thepost.app.utils.RecyclerTouchListener;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.thepost.app.utils.ContextUtils.getBottomPaddingForTheme;

public class MainActivity extends AppCompatActivity {

    public static boolean logged_static;
    // All fragment transactions used to simultaneously handle each fragment set in its given frame.
    // (DO NOT make this local. Future-proofing by keeping it outside).
    private FragmentTransaction fragmentTransaction, fragmentTransaction1, fragmentTransaction2, fragmentTransaction3;
    private FragmentTransaction fragmentTransaction4;

    private List<Integer> drawables;
    private List<String> titles;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private boolean clicked;

    /**
     * Initializes the actions to be performed whenever a given item is clicked from the bottom navigation bar.
     */

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.navigation_events:
                    initializeFragment(R.id.eventsContainer);
                    if(findViewById(R.id.emptyEventsAnimation) != null && findViewById(R.id.emptyEventsAnimation).getVisibility() == View.VISIBLE) {
                        ((LottieAnimationView) findViewById(R.id.emptyEventsAnimation)).setProgress(0.0f);
                        ((LottieAnimationView) findViewById(R.id.emptyEventsAnimation)).playAnimation();
                    }
                    return true;

                case R.id.navigation_home:
                    initializeFragment(R.id.articlesContainer);
                    return true;

                case R.id.navigation_more:
                    showOptions();
                    return false;

                case R.id.navigation_slcm:
                    if(logged_static) {
                        checkCredentials();
                    }
                    else{
                        initializeFragment(R.id.slcmContainer);
                        Log.e("Trying", "Visi");
                    }
                    return true;

                case R.id.navigation_notices:
                    initializeFragment(R.id.noticesContainer);
                    return true;
            }
            return false;
        }
    };

    /**
     *
     * @param frameToBeVisible Makes this frame visible in the Main Activity.
     *                         This frame holds the particular fragment that we want to show the user.
     */

    private void initializeFragment(int frameToBeVisible) {

        findViewById(R.id.articlesContainer).setVisibility(View.GONE);
        findViewById(R.id.slcmContainer).setVisibility(View.GONE);
        findViewById(R.id.noticesContainer).setVisibility(View.GONE);
        findViewById(R.id.eventsContainer).setVisibility(View.GONE);
        findViewById(R.id.loggedSlcmContainer).setVisibility(View.GONE);

        findViewById(frameToBeVisible).setVisibility(View.VISIBLE);

    }

    /**
     * Initializes the frame set-up. It adds the respective fragments to their respective frames.
     */

    private void setFragmentsInFrames() {

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.articlesContainer, HomeFragment.newInstance()).commit();
        fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.replace(R.id.slcmContainer, SlcmFragment.newInstance()).commit();
        fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.replace(R.id.noticesContainer, NoticesFragment.newInstance()).commit();
        fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction3.replace(R.id.eventsContainer, EventsFragment.newInstance()).commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFragmentsInFrames();

        initFirebase();
        createOnboardDialog();

        initializeFragment(R.id.articlesContainer);

        initOptions();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * Populate the Pop-Up Menu
     */

    private void initOptions(){
        drawables = new ArrayList<>();
        titles = new ArrayList<>();

        drawables.add(R.drawable.ic_privacy);
        titles.add("Privacy");

        drawables.add(R.drawable.ic_website);
        titles.add("Our Website");

        drawables.add(R.drawable.ic_about);
        titles.add("About Us");

        drawables.add(R.drawable.ic_magazine);
        titles.add("Magazines");

        drawables.add(R.drawable.ic_source_code);
        titles.add("Source Code");

        drawables.add(R.drawable.ic_developer);
        titles.add("Developers");

        drawables.add(R.drawable.ic_calendar);
        titles.add("Academic Calendar");
    }

    /**
     * Dialog Initialization for the Pop-Up Menu
     */

    private void showOptions(){

        clicked = false;

        final DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.dialog_options))
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                        if(clicked){
                            createDeveloperDialog();
                        }
                    }
                })
                .create();

        View view = dialogPlus.getHolderView();

        RecyclerView recyclerView = view.findViewById(R.id.optionsRecycler);
        OptionsAdapter adapter = new OptionsAdapter(drawables, titles);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well

                Intent intent;

                switch(position){

                    case 0:
                        intent = new Intent(MainActivity.this, PrivacyActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                        break;

                    case 1:
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.themitpost.com/"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                        break;

                    case 2:
                        intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                        break;

                    case 3:
                        intent = new Intent(MainActivity.this, MagazineListActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                        break;

                    case 4:
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.github.com/Raks110/ThePostApp"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                        break;

                    case 5:
                        dialogPlus.dismiss();
                        clicked = true;
                        break;

                    case 6:

                        intent = new Intent(getApplicationContext(), FullImageActivity.class);
                        intent.putExtra("imageLink", "https://app.themitpost.com/static/images/academic_calendar");
                        startActivity(intent);
                        overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                        break;
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                //Long clicked. Do something here.
            }
        }));

        dialogPlus.show();
    }

    private void checkCredentials(){


        executor = ContextCompat.getMainExecutor(getApplicationContext());
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                Toast.makeText(getApplicationContext(), "You will need to verify yourself before accessing SLCM content.", Toast.LENGTH_LONG).show();
                ((BottomNavigationView) findViewById(R.id.nav_view)).setSelectedItemId(R.id.navigation_home);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {

                super.onAuthenticationSucceeded(result);
                fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                fragmentTransaction4.replace(R.id.loggedSlcmContainer, LoggedInSlcmFragment.newInstance()).commitAllowingStateLoss();
                initializeFragment(R.id.loggedSlcmContainer);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

                Toast.makeText(getApplicationContext(), "You will need to verify yourself before accessing SLCM content.", Toast.LENGTH_LONG).show();
                ((BottomNavigationView) findViewById(R.id.nav_view)).setSelectedItemId(R.id.navigation_home);
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Please Confirm your Identity to access SLCM")
                .setDeviceCredentialAllowed(true)
                .build();

        biometricPrompt.authenticate(promptInfo);

    }

    private void createOnboardDialog() {

        if (!getSharedPreferences("thepostapp-first-use", Context.MODE_PRIVATE).getBoolean("done_first_use", false)) {

            final DialogPlus dialogPlus = DialogPlus.newDialog(MainActivity.this)
                    .setContentHolder(new ViewHolder(R.layout.dialog_onboard))
                    .setCancelable(false)
                    .setExpanded(false)
                    .create();

            View v = dialogPlus.getHolderView();

            v.findViewById(R.id.close_onboard).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogPlus.dismiss();
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    dialogPlus.show();
                    getSharedPreferences("thepostapp-first-use", Context.MODE_PRIVATE).edit().putBoolean("done_first_use", true).apply();
                }
            }, 1000);

        }
    }

    private void createDeveloperDialog() {

        clicked = false;

        final DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.dialog_developer))
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                        showOptions();
                    }
                })
                .setCancelable(true)
                .setExpanded(false)
                .create();

        View v = dialogPlus.getHolderView();

        v.findViewById(R.id.close_dev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPlus.dismiss();
            }
        });

        v.findViewById(R.id.open_github_rakshit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.github.com/raks110"));
                startActivity(i);
            }
        });

        v.findViewById(R.id.open_linkedin_rakshit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.linkedin.com/in/rakshitgl"));
                startActivity(i);
            }
        });

        v.findViewById(R.id.open_instagram_rakshit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.instagram.com/rakshit110"));
                startActivity(i);
            }
        });

        dialogPlus.show();
    }

    private void initFirebase() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);

            assert notificationManager != null;

            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }

        try {
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nMgr.cancelAll();
        } catch (Exception e) {
            NotificationManagerCompat.from(this).cancelAll();
        }

        if (getIntent().getExtras() != null) {

            //Debug: Type of notification received
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.e("MainActivity", "Key: " + key + " Value: " + value);
            }

            //Handle SLCM Notification
            if (Boolean.parseBoolean((String) getIntent().getExtras().get("isSLCM"))) {

                ((BottomNavigationView) findViewById(R.id.nav_view)).setSelectedItemId(R.id.navigation_slcm);
            }

            //Handle New Notice Notification
            if (Boolean.parseBoolean((String) getIntent().getExtras().get("isNotice"))) {

                ((BottomNavigationView) findViewById(R.id.nav_view)).setSelectedItemId(R.id.navigation_notices);

                //Display the notice in the full view
                Gson gson = new Gson();
                final NoticeDataModel model = gson.fromJson((String) getIntent().getExtras().get("internalData"), NoticeDataModel.class);

                String dateParser = model.getDate();
                Date date;

                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(dateParser);
                } catch (Exception e) {
                    date = new Date();
                }

                final DialogPlus dialogPlus = DialogPlus.newDialog(this)
                        .setContentHolder(new ViewHolder(R.layout.dialog_full))
                        .setCancelable(true)
                        .setExpanded(false)
                        .create();

                final View viewDialog = dialogPlus.getHolderView();

                ((TextView) viewDialog.findViewById(R.id.notice_full_view_title)).setText(model.getTitle());
                if (!model.getPdfLink().equals(""))
                    ((TextView) viewDialog.findViewById(R.id.notice_full_view_content)).setText(model.getContent() + "\n\nHere's an additional resource:\n\n" + model.getPdfLink());
                else
                    ((TextView) viewDialog.findViewById(R.id.notice_full_view_content)).setText(model.getContent());

                viewDialog.findViewById(R.id.reminder).setVisibility(View.GONE);

                String timestamp = "Posted on " + new SimpleDateFormat("dd MMMM, yy").format(date);
                ((TextView) viewDialog.findViewById(R.id.notice_full_view_timestamp)).setText(timestamp);

                ImageView imageView = viewDialog.findViewById(R.id.notice_full_view_image);

                if (!model.getImageURL().equals("")) {

                    Picasso.with(this).load(model.getImageURL()).into(imageView, new com.squareup.picasso.Callback() {
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

                            Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
                            intent.putExtra("imageLink", model.getImageURL());

                            startActivity(intent);
                            overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
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
                        ContextUtils.saveEvent(viewDialog.getContext(), model.getTitle());
                    }
                });

                dialogPlus.show();
            }
        }

        //Whenever the user's device's Firebase ID changes, resend it to the server to replace the old ID with the new one
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e("MainActivity", "getInstanceId failed", task.getException());
                            return;
                        }

                        String token = task.getResult().getToken();

                        Log.e("New Token", token);

                        getSharedPreferences("thepostapp", Context.MODE_PRIVATE).edit().putString("fcm_token", token).apply();

                        TokenAPIService mAPIService = ApiUtils.getTokenAPIService();

                        String regNo = getSharedPreferences("thepostapp", Context.MODE_PRIVATE).getString("regNo", "");
                        String pass = getSharedPreferences("thepostapp", Context.MODE_PRIVATE).getString("pass", "");

                        if (regNo.trim().equals("") || pass.trim().equals("")) {
                            return;
                        }

                        HashMap<String, String> params = new HashMap<>();
                        params.put("regNumber", regNo);
                        params.put("pass", pass);
                        params.put("fcm_token", token);
                        params.put("action", "update");
                        String strRequestBody = new Gson().toJson(params);

                        final RequestBody requestBody = RequestBody.create(MediaType.
                                parse("application/json"), strRequestBody);

                        mAPIService.sendToken(requestBody).enqueue(new Callback<Object>() {
                            @Override
                            @EverythingIsNonNull
                            public void onResponse(Call<Object> call, Response<Object> response) {

                                if (response.isSuccessful()) {

                                    Log.e("MainActivity", "Successfully updated token");
                                }

                                if (!response.isSuccessful()) {

                                    Log.e("MainActivity", "Failed to update token.");
                                }

                            }

                            @Override
                            @EverythingIsNonNull
                            public void onFailure(Call<Object> call, Throwable t) {

                                Log.e("MainActivity", "Failed to update token.");
                            }
                        });

                        Log.e("TOKEN", token);
                    }
                });

        //Subscribe the user to general notifications
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successfully subscribed.";
                        if (!task.isSuccessful()) {
                            msg = "Error while subscribing.";
                        }
                        Log.d("Firebase", msg);
                    }
                });

        //Subscribe the user to receive new notices
        FirebaseMessaging.getInstance().subscribeToTopic("notice")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successfully subscribed.";
                        if (!task.isSuccessful()) {
                            msg = "Error while subscribing.";
                        }
                        Log.d("Firebase", msg);
                    }
                });

    }
}
