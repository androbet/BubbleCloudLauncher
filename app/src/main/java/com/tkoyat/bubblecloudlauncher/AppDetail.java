package com.tkoyat.bubblecloudlauncher;

import android.graphics.drawable.Drawable;

public class AppDetail {
    Drawable icon;
    CharSequence label;
    int mViewType = 1;
    CharSequence name;

    public Drawable getIcon() {
        return this.icon;
    }

    public int getmViewType() {
        return this.mViewType;
    }
}