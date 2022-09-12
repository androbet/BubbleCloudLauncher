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
    HomeKeyEventReceiver homeKeyEventReceiver;
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

    // gyroscope sensor object
    private static final float M_COMPARE_VALUE = 0.05f;

    private SensorManager gyroscopeSensormanager = null;
    private Sensor gyroscopeSensor = null;
    private SensorEventListener gyroscopeSensorListener = null;

    private final float[] data_last = {0, 0, 0};
    private final float[] data_current = {0, 0, 0};

    private boolean onetime = true;

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
    }

    private void initSensor() {
        gyroscopeSensormanager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = gyroscopeSensormanager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        gyroscopeSensorListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor s, int accuracy) {
            }

            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                data_current[0] = x;
                data_current[1] = y;
                data_current[2] = z;
                if (onetime && (x != 0) && (y != 0) && (z != 0)) {
                    onetime = false;
                    data_last[0] = x;
                    data_last[1] = y;
                    data_last[2] = z;
                }
                if (gSensorCheck(data_current[0]) || gSensorCheck(data_current[1]) || gSensorCheck(data_current[1])) {
                    if ((data_current[0] != data_last[0]) || (data_current[1] != data_last[1]) || (data_current[2] != data_last[2])) {
                        recyclerView.smoothScrollBy((int) -y * 400, (int) -x * 200);
                        Log.d(TAG, "mmi test values: x " + x + "," + y + "," + z);
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
        this.homeKeyEventReceiver = new HomeKeyEventReceiver();
        registerReceiver(this.homeKeyEventReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        this.homeKeyEventReceiver.setIshome(true);
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
                View view = recyclerView;

                if (e1.getX() - e2.getX() > 130 && Math.abs(e1.getX() - e2.getX()) / Math.abs(e1.getY() - e2.getY()) > 2) {
                    Log.i(TAG, "向左滑动");
                    scaledScrollFactor = -1.0f; // 缩小

                }
                if (e2.getX() - e1.getX() > 130 && Math.abs(e1.getX() - e2.getX()) / Math.abs(e1.getY() - e2.getY()) > 2) {
                    Log.i(TAG, "向右滑动");
                    scaledScrollFactor = 1.0f; // 放大
                }

                if (scaledScrollFactor != 0.0f) {
                    int i = (scaledScrollFactor > 0.0f ? 1 : (scaledScrollFactor == 0.0f ? 0 : -1));
                    if (i != 0) {
                        focused = 2;
                    }
                    if (focused == 2) {
                        if (i > 0 && scale < 20) {
                            scale++;
                            time = 0L;
                        } else if (scaledScrollFactor < 0.0f && scale > -20) {
                            scale--;
                        }
                        ViewCompat.animate(view).setDuration(50L).scaleX((scale * 0.03f) + 1.0f).scaleY((scale * 0.03f) + 1.0f).start();
                        int i2 = scale;
                        if (i2 == -20) {
                            focused = 1;
                        } else if (i2 == 0) {
                            focused = 0;
                        } else if (i2 == 20) {
                            focused = 3;
                        }
                        int i3 = focused;
                        if (i3 == 0) {
                            ViewCompat.animate(view).setDuration(200L).scaleX(1.0f).scaleY(1.0f).start();
                            isOpened = false;
                        } else if (i3 != 1) {
                            if (i3 == 3 && !isOpened && recyclerView.getScaleX() >= 1.55f) {
                                isOpened = true;
                                ViewCompat.animate(view).setDuration(100L).scaleX(1.0f).scaleY(1.0f).start();
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
                                ViewCompat.animate(view).setDuration(500L).scaleX(1.0f).scaleY(1.0f).start();
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
}