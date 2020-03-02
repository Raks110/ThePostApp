package com.thepost.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.thepost.app.MainActivity;
import com.thepost.app.R;

import static com.thepost.app.utils.ContextUtils.getBottomPaddingForTheme;

@Keep
public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch(MainActivity.theme){

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

        setContentView(R.layout.activity_main_menu);

        setAllButtons();
    }

    private void setAllButtons(){

        findViewById(R.id.switch_theme).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainMenuActivity.this, ThemesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
            }
        });

        findViewById(R.id.website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.themitpost.com/"));
                startActivity(i);
            }
        });

        findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainMenuActivity.this, AboutActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
            }
        });

        findViewById(R.id.open_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.facebook.com/themitpost/"));
                startActivity(i);
            }
        });

        findViewById(R.id.open_instagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.instagram.com/themitpost/"));
                startActivity(i);
            }
        });

        findViewById(R.id.open_twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.twitter.com/themitpost/"));
                startActivity(i);
            }
        });

        findViewById(R.id.open_linkedin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.linkedin.com/company/themitpost/"));
                startActivity(i);
            }
        });

        findViewById(R.id.menuClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.recreate = false;
                finish();
                overridePendingTransition(R.anim.stay, R.anim.push_out_left);
            }
        });

        findViewById(R.id.privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainMenuActivity.this, PrivacyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
            }
        });

        findViewById(R.id.developer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createDeveloperDialog();
            }
        });
    }

    @Override
    public void onBackPressed(){
        MainActivity.recreate = false;
        finish();
        overridePendingTransition(R.anim.stay, R.anim.push_out_left);
    }

    private void createDeveloperDialog() {

        final DialogPlus dialogPlus = DialogPlus.newDialog(MainMenuActivity.this)
                .setContentHolder(new ViewHolder(R.layout.dialog_developer))
                .setCancelable(true)
                .setPadding(0, 0, 0, getBottomPaddingForTheme(MainActivity.theme, MainMenuActivity.this))
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
}
