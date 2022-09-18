package com.tkoyat.bubblecloudlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tkoyat.bubblecloudlauncher.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    private static final String TAG = "BubbleCloudLauncher";

    private GestureDetector mDetector;
    private static MainActivity ins;
    BubbleRecyclerViewAdapter appListAdapter;
    List<AppDetail> apps;
    SharedPreferences.Editor editor;
    boolean isOpened;
    DrawerLayout layout;
    private PackageManager manager;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    BubbleLayoutManager bLayoutManager;
    long time;
    Timer timer;
    int scale = 0;
    int focused = 0;

    // 缩放
    private static final int MAX_SCALE = 150;
    private static final int MIN_SCALE = -15;

    // gyroscope sensor object
    private static final float M_COMPARE_VALUE = 2.0f;
    private static final float MIN_ANGLE = 2.0f;
    private static final float MAX_ANGLE = 30.0f;

    private SensorManager gyroscopeSensormanager = null;
    private Sensor gyroscopeSensor = null;
    private SensorEventListener gyroscopeSensorListener = null;

    private final float[] data_last = {0, 0, 0};
    private final float[] data_current = {0, 0, 0};  // 记录偏移值xyz

    private int h_offset = 0;
    private int v_offset = 0;

    private boolean onetime = true;

    // Inmo Ring
    private static final int KEY_SHORTCLICK1 = 290;
    private static final int KEY_SHORTCLICK2 = 291;
    private static final int KEY_LONGPRESS1 = 292;
    private static final int KEY_LONGPRESS2 = 293;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(Integer.MIN_VALUE, Integer.MIN_VALUE);
        setContentView(R.layout.activity_main);
        init();
        initListenner();
        loadapps();     // 加载应用
        initGesture();  // 触摸板手势
        initSensor();   // 陀螺仪传感器
    }

    private boolean gSensorCheck(float data) {
        return Math.abs(data) > M_COMPARE_VALUE;
//        return MIN_ANGLE < Math.abs(data) && Math.abs(data) < MAX_ANGLE;
    }

    private void initSensor() {
        gyroscopeSensormanager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = gyroscopeSensormanager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        gyroscopeSensorListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor s, int accuracy) {
            }

            public void onSensorChanged(SensorEvent event) {
                data_current[2] = event.values[2];  // x
                data_current[1] = event.values[1];  // y

                if (Math.abs(data_current[2]) < 30 && Math.abs(data_current[1]) < 30) {
                    if (gSensorCheck(Math.abs(data_current[2] - data_last[2])) || gSensorCheck(Math.abs(data_current[1] - data_last[1]))) {
                        float x_move = (data_current[2] - data_last[2]) * 10;
                        float y_move = (data_current[1] - data_last[1]) * 10;
                        h_offset += x_move;
                        v_offset += y_move;
                        recyclerView.smoothScrollBy((int) x_move, (int) y_move);
                        data_last[2] = data_current[2];
                        data_last[1] = data_current[1];
//                        Log.d(TAG, "smoothScrollBy current angle: " + data_current[2] + "," + data_current[1]);
//                        Log.d(TAG, "smoothScrollBy last angle: " + data_last[2] + "," + data_last[1]);
//                        Log.d(TAG, "smoothScrollBy current offset: " + x_move + "," + y_move);
//                        Log.d(TAG, "smoothScrollBy all offset: " + h_offset + "," + v_offset);
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        gyroscopeSensormanager.registerListener(gyroscopeSensorListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        if (gyroscopeSensormanager != null) {
            gyroscopeSensormanager.unregisterListener(gyroscopeSensorListener);
        }
        super.onPause();
    }

    private void initListenner() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        registerReceiver(new AppReceiver(), intentFilter);
    }

    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences("config", 0);
        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
        this.bLayoutManager = new BubbleLayoutManager();
        this.layout = (DrawerLayout) findViewById(R.id.menu);
        ins = this;
        this.timer = new Timer();
    }

    public void loadapps() {
        this.manager = getPackageManager();
        this.apps = new ArrayList();
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        for (ResolveInfo resolveInfo : this.manager.queryIntentActivities(intent, 0)) {
            AppDetail appDetail = new AppDetail();
            appDetail.icon = resolveInfo.activityInfo.loadIcon(this.manager);
            appDetail.name = resolveInfo.activityInfo.packageName;
            appDetail.label = resolveInfo.activityInfo.name;
            if (!appDetail.name.equals(BuildConfig.APPLICATION_ID)) {
                this.apps.add(appDetail);
            }
        }

        this.recyclerView = (RecyclerView) findViewById(R.id.main_rv);
        this.appListAdapter = new BubbleRecyclerViewAdapter(this, this.apps);
        this.recyclerView.setLayoutManager(this.bLayoutManager);
        this.recyclerView.setAdapter(this.appListAdapter);

        this.bLayoutManager.scale = 1.0f;
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.recycler_anim));
        layoutAnimationController.setOrder(0);
        layoutAnimationController.setDelay(0.2f);
        this.recyclerView.setLayoutAnimation(layoutAnimationController);
    }

    public static MainActivity getInstance() {
        return ins;
    }

    private void scroll_recycleview(float scaledScrollFactor) {
        View view = recyclerView;

        int i = (scaledScrollFactor > 0.0f ? 1 : (scaledScrollFactor == 0.0f ? 0 : -1));  // 单次缩放大小
        if (i != 0) {
            focused = 2;
        }
        if (focused == 2) {
            if (i > 0 && scale < MAX_SCALE) {
                scale+=scaledScrollFactor;
                time = 0L;
            } else if (scaledScrollFactor < 0.0f && scale > MIN_SCALE) {
                scale+=scaledScrollFactor;
            }
            ViewCompat.animate(view).setDuration(50L).scaleX((scale * 0.03f) + 1.0f).scaleY((scale * 0.03f) + 1.0f).start();
            // 缩放倍数控制
            int i2 = scale;
            if (i2 <= MIN_SCALE) {
                focused = 1;  // 缩小最小值
            } else if (i2 == 0) {
                focused = 0;  // 缩放过程
            } else if (i2 >= MAX_SCALE) {
                focused = 3;  // 放大最大值，打开app
            }

            int i3 = focused;
            if (i3 == 0) {
                // 执行缩放过程
                ViewCompat.animate(view).setDuration(200L).scaleX(scaledScrollFactor).scaleY(scaledScrollFactor).start();
                isOpened = false;
            } else if (i3 != 1) {
                if (i3 == 3 && !isOpened && recyclerView.getScaleX() >= 1.55f) {
                    isOpened = true;
                    ViewCompat.animate(view).setDuration(50L).scaleX(scaledScrollFactor).scaleY(scaledScrollFactor).start();
                    scale = 0;
                    Intent intent = new Intent();
                    intent.setClassName(apps.get(bLayoutManager.getPos()).name.toString(), apps.get(bLayoutManager.pos).label.toString());
                    startActivity(intent);
                    focused = 0;
                    isOpened = false;
                }
            } else if (scaledScrollFactor < 0.0f) {
                if (time == 0) {
                    time = System.currentTimeMillis();
                } else if (time <= System.currentTimeMillis() - 1000) {
                    ViewCompat.animate(view).setDuration(500L).scaleX(scaledScrollFactor).scaleY(scaledScrollFactor).start();
                    bLayoutManager.isScale = true;
                    bLayoutManager.scale = scale;
                    recyclerView.smoothScrollBy(0, 1);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            scale = 0;
                            time = 0L;
                            bLayoutManager.isScale = true;
                            bLayoutManager.scale = scale;
                            recyclerView.smoothScrollBy(0, 1);
                        }
                    }, 500L);
                } else {
                    time = System.currentTimeMillis();
                }
            } else {
                time = 0L;
            }
        }
        bLayoutManager.isScale = true;
        bLayoutManager.scale = scale;
        recyclerView.smoothScrollBy(0, 1);
    }

    private void initGesture() {
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            //单击
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return super.onSingleTapConfirmed(e);
            }

            // 按下时调用
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                // TODO Auto-generated method stub
                System.out.println("onSingleTapUp==" + e.getAction());// 1
                return super.onSingleTapUp(e);
            };


            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);

                ToastUtils.showToastMessage(MainActivity.this, apps.get(0).label.toString());
            }

            // 滑动一段距离，up时触发
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {//双击事件
                return super.onDoubleTap(e);
            }


            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float scaledScrollFactor = 0.0f; // 缩放参数


                if (e1.getX() - e2.getX() > 130 && Math.abs(e1.getX() - e2.getX()) / Math.abs(e1.getY() - e2.getY()) > 2) {
                    Log.i(TAG, "向后滑动");
                    scaledScrollFactor = -2.0f; // 缩小

                }
                if (e2.getX() - e1.getX() > 130 && Math.abs(e1.getX() - e2.getX()) / Math.abs(e1.getY() - e2.getY()) > 2) {
                    Log.i(TAG, "向前滑动");
                    scaledScrollFactor = 4.0f; // 放大
                }

                if (scaledScrollFactor != 0.0f) {
                    scroll_recycleview(scaledScrollFactor);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(@NotNull MotionEvent ev) {
        mDetector.onTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode) {
            case KEY_SHORTCLICK1:
                Log.d(TAG, "morse: " + "KEY_SHORTCLICK1");
                scroll_recycleview(40.0f);
                break;
            case KEY_SHORTCLICK2:
                Log.d(TAG, "morse: " + "KEY_SHORTCLICK2");
                scroll_recycleview(-20.0f);
                break;
            case KEY_LONGPRESS1:
                Log.d(TAG, "morse: " + "KEY_LONGPRESS1");
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}