package com.fm.openinstalldemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.fm.openinstall.Configuration;
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

            // 检查是否有权限，SDK不依赖此权限，仅在此示例中演示权限申请
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        forward();
                    }
                }, 1800);
            } else {
                // SDK不依赖此权限，仅在此示例中演示权限申请
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
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
                // SDK不依赖此权限，仅在此示例中演示权限申请
                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

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
        // 所有的设备标识符开发者都可控制，请参考集成文档
        Configuration configuration = new Configuration.Builder()
//                .serialNumber(null)  // 禁止 SDK 获取 serialNumber
//                // 只有在使用“移动广告效果监测”功能时才开启
//                .adEnabled(true)
//                .imeiDisabled()  // 禁止 SDK 获取 imei
//                .oaid("传入获取到的oaid")
                .build();

        OpenInstall.init(this, configuration);
        //获取唤醒参数
        OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
        //    1、如果在“Android集成” -> “Android下载配置” 中启用了 “集成应用宝” 开关并填入正确的应用宝地址
        // 请替换使用 getWakeUpYYB ，则可实现微信/QQ中打开应用，并还原参数
//        OpenInstall.getWakeUpYYB(SplashActivity.this, getIntent(), wakeUpAdapter);
        //    2、 如果想只要调用了接口，无论是否有数据时都走回调
        // 请替换使用 getWakeUpAlwaysCallback ，则可统一在回调中做业务跳转
//        OpenInstall.getWakeUpAlwaysCallback(getIntent(), wakeUpAdapter);
    }


    /**
     * 唤醒参数获取回调
     */
    AppWakeUpAdapter wakeUpAdapter = new AppWakeUpAdapter() {
        @Override
        public void onWakeUp(AppData appData) {
            // 获取渠道数据
            String channelCode = appData.getChannel();
            // 获取H5落地页传递的数据
            String bindData = appData.getData();

            // 根据获取到的数据处理业务
            showWakeUpDialog(appData);

        }
    };


    private void showWakeUpDialog(AppData appData) {
        if (appData == null) return;
        showWakeUp = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OpenInstall");
        builder.setMessage("这是App被拉起获取的数据\n"
                + "channelCode=" + appData.getChannel() + "\n"
                + "bindData=" + appData.getData());
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

