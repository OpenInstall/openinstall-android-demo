package com.fm.openinstalldemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;
import com.fm.openinstalldemo.utils.PrivacyDialog;
import com.fm.openinstalldemo.utils.Store;


public class SplashActivity extends AppCompatActivity {


    private PrivacyDialog privacyDialog;
    private boolean showWakeUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        boolean agree = Boolean.parseBoolean(Store.sharedPreferencesGet(this, Store.KEY_PRIVACY));
        // 检查是否有同意《服务协议》和《隐私政策》
        if (agree) {

            // 初始化需要在权限申请（requestPermissions）之前，或者权限处理（onRequestPermissionsResult）之后
            openinstall();

            // 检查是否有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        forward();
                    }
                }, 1800);
            } else {
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
            }
        } else {
            // 展示《服务协议》和《隐私政策》
            showPrivacyDialog();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //此处要调用，否则App在后台运行时，会无法截获
        OpenInstall.getWakeUp(intent, wakeUpAdapter);
    }

    private void showPrivacyDialog() {
        PrivacyDialog.Builder builder = new PrivacyDialog.Builder(this);
        builder.setNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 拒绝接受《服务协议》和《隐私政策》，不进行SDK的初始化
                SplashActivity.this.finish();
            }
        });
        builder.setPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (privacyDialog != null && privacyDialog.isShowing()) {
                    privacyDialog.dismiss();
                }

                Store.sharedPreferencesPut(SplashActivity.this, Store.KEY_PRIVACY, String.valueOf(true));

                // 初始化需要在权限申请（requestPermissions）之前，或者权限处理（onRequestPermissionsResult）之后
                openinstall();

                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, 100);

            }
        });
        privacyDialog = builder.create();
        privacyDialog.setCancelable(false);
        privacyDialog.setCanceledOnTouchOutside(false);
        privacyDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        forward();
    }

    private void forward() {

        if (showWakeUp) return;

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (privacyDialog != null && privacyDialog.isShowing()) {
            privacyDialog.dismiss();
        }
        wakeUpAdapter = null;
    }

    private void openinstall() {
        OpenInstall.init(this);
        //获取唤醒参数
        OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
    }


    /**
     * 唤醒参数获取回调
     * 如果在没有数据时有特殊的需求，可将AppWakeUpAdapter替换成AppWakeUpListener
     *
     * @param appData
     */
    AppWakeUpAdapter wakeUpAdapter = new AppWakeUpAdapter() {
        @Override
        public void onWakeUp(AppData appData) {
            //获取渠道数据
            String channelCode = appData.getChannel();
            //获取自定义数据
            String bindData = appData.getData();

            // 根据获取到的参数处理业务
            showWakeUpDialog(appData.toString());

        }
    };


    private void showWakeUpDialog(String data) {
        if (TextUtils.isEmpty(data)) return;
        showWakeUp = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OpenInstall");
        builder.setMessage("这是App被拉起获取的数据\n" + data);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showWakeUp = false;
                forward();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

}

