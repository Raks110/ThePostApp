package com.thepost.app.utils;

import android.view.View;

import androidx.annotation.Keep;

@Keep
public interface ClickListener{
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}