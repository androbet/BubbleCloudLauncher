package com.tkoyat.bubblecloudlauncher.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tkoyat.bubblecloudlauncher.R;

public class ToastUtils {

    public static void showToastMessage(Context context, String content) {
        Toast toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        View view = View.inflate(context, R.layout.toast, null);
        toast.setView(view);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 50);
        TextView tv = view.findViewById(R.id.tv);
        tv.setText(content);
        toast.show();
    }
}
