package com.thepost.app.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thepost.app.MainActivity;
import com.thepost.app.R;
import com.thepost.app.adapters.ThemesAdapter;
import com.thepost.app.utils.ContextUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.thepost.app.utils.ContextUtils.getColorForTheme;

@Keep
public class ThemesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (MainActivity.theme) {

            case 0:
                setTheme(R.style.AppThemeDark);
                break;
            case 1:
                setTheme(R.style.AppTheme);
                break;
            case 2:
                setTheme(R.style.AppThemeOrange);
                break;
            case 3:
                setTheme(R.style.AppThemeVibrant);
                break;
            case 4:
                setTheme(R.style.AppThemeTransDark);
                break;
        }

        setContentView(R.layout.activity_themes);

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.almost_dark);
        images.add(R.drawable.really_light);
        images.add(R.drawable.just_yellow);
        images.add(R.drawable.darkish_green);
        images.add(R.drawable.death_by_peach);

        List<String> names = new ArrayList<>();
        names.add("Almost Dark");
        names.add("Really Light");
        names.add("Just Yellow");
        names.add("Moss Green");
        names.add("Subtle Peach");

        int current = MainActivity.theme;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable drawable = toolbar.getNavigationIcon();
        assert drawable != null;

        drawable.setColorFilter(getColorForTheme(MainActivity.theme), PorterDuff.Mode.SRC_ATOP);

        RecyclerView recyclerView;
        ThemesAdapter adapter;

        recyclerView = findViewById(R.id.themesRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setPadding(0,0,0,(4* ContextUtils.getNavBarHeight(this)));

        adapter = new ThemesAdapter(images, names, current, ThemesActivity.this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.stay, R.anim.push_out_left);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.push_out_left);
    }

    boolean shouldFinish;

    @Override
    public void onPause() {

        shouldFinish = true;

        super.onPause();
        final Date date  = new Date();

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                Date d = new Date();
                if(d.getTime() - date.getTime() >= 15*60*1000){

                    if(shouldFinish)
                        finish();
                }

            }
        });
    }

    @Override
    public void onResume(){

        super.onResume();
        shouldFinish = false;
    }
}
