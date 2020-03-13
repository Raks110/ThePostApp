package com.thepost.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.thepost.app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Keep
public class ContextUtils {

    /**
     *
     * This function adds an event from the app, into the user's calender app (which the user can choose)
     *
     * @param context is the context to the calling activity to successfully open an Intent
     * @param date is the date of the event commencement (in String format)
     * @param time is the time of the event commencement (in String format)
     * @param title is the title of the event (This title will be added to the user's calender)
     */

    public static void saveEvent(Context context, String title, String date, String time){

        String clubbed = date.concat(" ").concat(time);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        Date dateObj;

        try{
            dateObj = format.parse(clubbed);
        }
        catch (ParseException e){

            dateObj = new Date();
        }

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", dateObj.getTime());
        intent.putExtra("endTime", dateObj.getTime() + (24*60*60*1000));
        intent.putExtra("allDay", true);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    /**
     *
     * This function adds events to the user's calender which don't have a specific begin time such as notice reminders.
     *
     * @param context is the context to the activity calling this function
     * @param title is the title of the event/notice
     */

    public static void saveEvent(Context context, String title){

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("allDay", true);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    /**
     *
     * Generates numbers in range [0,limit)
     *
     * @param limit is the upper bound of random number generator
     * @return is the positive (including 0) random number less than limit.
     */

    private static int randomGenerate(int limit){

        Random random = new Random();
        return random.nextInt(limit);
    }


    /**
     *
     * Hides the soft-keyboard whenever unnecessary.
     *
     * @param context is the context of the activity calling this
     * @param view is the view to which the keyboard is currently attached to.
     */

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboardFrom(Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    /**
     *
     * @param c takes context of the calling Activity
     * @return a boolean answering if the device is a tablet or not
     */

    private static boolean isTablet(Context c) {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     *
     * @param c is the context of the calling Activity
     * @return height of the navigation bar
     */

    public static int getNavBarHeight(Context c) {

        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if(!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = c.getResources();

            int orientation = resources.getConfiguration().orientation;
            int resourceId;
            if (isTablet(c)){
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
            }  else {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     *
     * @param context is the context of the calling activity
     * @return a boolean answering if there's internet connectivity
     */

    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connectivityManager != null;

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     *
     * @param activity is the activity object
     * @return height of the screen (while the activity object was visible)
     */

    public static int getScreenHeight(Activity activity){

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.y;
    }

    public static int getColorForTheme(int theme){

        switch(theme){
            case 0:
            case 3:
                return Color.WHITE;
            case 1:
            case 2:
            case 4:
                return Color.BLACK;

                default:
                    return Color.WHITE;
        }
    }

    public static int getBottomPaddingForTheme(int theme, Context context){

        int navigationBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        switch(theme){

            case 0:
            case 3:
                return 0;
            case 1:
            case 2:
            case 4:
                return navigationBarHeight + 32;

                default:
                    return 0;
        }
    }
}
