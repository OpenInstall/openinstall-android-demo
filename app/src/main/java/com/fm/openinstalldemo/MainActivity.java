package com.fm.openinstalldemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallRetryAdapter;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;
import com.fm.openinstalldemo.utils.Store;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        findViewById(R.id.install).setOnClickListener(this);
        findViewById(R.id.channel).setOnClickListener(this);
        findViewById(R.id.wakeup).setOnClickListener(this);

        // 首次获取
        boolean hasGet = Boolean.parseBoolean(Store.sharedPreferencesGet(this, Store.KET_GET));
        if (!hasGet) {
            // 首次提前获取 OpenInstall 数据，使用可重试api，尽量保证匹配数据的获取。
            // 如果未获取到，则在业务需要时（InstallActivity）再次重试获取
            OpenInstall.getInstallCanRetry(new AppInstallRetryAdapter() {
                @Override
                public void onInstall(AppData appData, boolean retry) {
                    if (!retry) {
                        //获取渠道数据
                        String channelCode = appData.getChannel();
                        //获取个性化安装数据
                        String bindData = appData.getData();

                        if (!appData.isEmpty()) {
                            showInstallDialog(appData.toString());
                        }

                        Store.sharedPreferencesPut(MainActivity.this, Store.KET_GET, String.valueOf(true));
                    }
                }
            }, 5);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.install:
                intent.setClass(this, InstallActivity.class);
                break;
            case R.id.channel:
                intent.setClass(this, ChannelActivity.class);
                break;
            case R.id.wakeup:
                intent.setClass(this, WakeupActivity.class);
                break;
        }
        startActivity(intent);
    }

    private void showInstallDialog(String data) {
        if (TextUtils.isEmpty(data)) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OpenInstall");
        builder.setMessage("我是来自那个集成了 openinstall JS SDK 页面的安装，请根据你的需求" +
                "将我计入统计数据或是根据贵公司App的业务流程处理（如免填邀请码建立邀请关系、自动" +
                "加好友、自动进入某个群组或房间等）\n" + data);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
