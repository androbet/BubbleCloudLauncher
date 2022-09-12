package com.tkoyat.bubblecloudlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class AppReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        action.hashCode();
        char c = 65535;
        switch (action.hashCode()) {
            case -810471698:
                if (action.equals("android.intent.action.PACKAGE_REPLACED")) {
                    c = 0;
                    break;
                }
                break;
            case 525384130:
                if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                    c = 1;
                    break;
                }
                break;
            case 1544582882:
                if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                restartview(context, intent);
                return;
            case 1:
                Toast.makeText(context, "卸载成功", Toast.LENGTH_LONG).show();
                restartview(context, intent);
                return;
            case 2:
                restartview(context, intent);
                return;
            default:
                return;
        }
    }

    private void restartview(final Context context, Intent intent) {
    }
}