package com.tkoyat.bubblecloudlauncher;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.animation.AlphaAnimation;
import androidx.core.view.ViewCompat;
import java.util.Timer;
import java.util.TimerTask;


public class HomeKeyEventReceiver extends BroadcastReceiver {
    private static String TAG = "HomeKeyEventReceiver";
    boolean ishome;
    String SYSTEM_REASON = "reason";
    String SYSTEM_HOME_KEY = "homekey";

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, final Intent intent) {
        String stringExtra;
        if (!intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS") || (stringExtra = intent.getStringExtra(this.SYSTEM_REASON)) == null || !stringExtra.equals(this.SYSTEM_HOME_KEY)) {
            return;
        }
        if (this.ishome) {
            this.ishome = false;
            ViewCompat.animate(MainActivity.getInstance().recyclerView).setDuration(150L).scaleX(1.0f).scaleY(1.0f).start();
            new Timer().schedule(new TimerTask() {
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    MainActivity.getInstance().scale = 0;
                    MainActivity.getInstance().focused = 0;
                    MainActivity.getInstance().isOpened = false;
                    MainActivity.getInstance().bLayoutManager.isScale = true;
                    MainActivity.getInstance().bLayoutManager.scale = MainActivity.getInstance().scale;
                    MainActivity.getInstance().recyclerView.smoothScrollBy(0, 1);
                    intent.setClassName("com.google.android.wearable.app", "com.google.android.clockwork.home2.activity.HomeActivity2");
                    context.startActivity(intent);
                    MainActivity.getInstance().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }, 150L);
            return;
        }
        Intent intent2 = new Intent(context, MainActivity.class);
//        intent2.setFlags(268435456);
        try {
            PendingIntent.getActivity(context, 0, intent2, 0).send();
        } catch (PendingIntent.CanceledException e) {
            context.startActivity(intent);
            e.printStackTrace();
        }
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(1000L);
        MainActivity.getInstance().recyclerView.startAnimation(alphaAnimation);
        MainActivity.getInstance().recyclerView.setAlpha(1.0f);
        this.ishome = true;
    }

    public void setIshome(boolean z) {
        this.ishome = z;
    }
}
