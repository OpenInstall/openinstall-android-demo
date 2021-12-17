package com.fm.openinstalldemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.model.AppData;
import com.fm.openinstalldemo.utils.Store;

public class InstallActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expain);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("个性化安装");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.iv_expain).setBackgroundResource(R.drawable.expain_install);

        // 业务需要时调用
        findViewById(R.id.install).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取参数，处理业务
                OpenInstall.getInstall(new AppInstallAdapter() {
                    @Override
                    public void onInstall(AppData appData) {
                        // 获取渠道数据
                        String channelCode = appData.getChannel();
                        // 获取个性化安装数据
                        String bindData = appData.getData();

                        // 根据获取到的参数处理业务
                        showInstallDialog(appData.toString());

                        Store.sharedPreferencesPut(InstallActivity.this, Store.KET_GET, String.valueOf(true));
                    }
                }, 5);
            }
        });
    }

    private void showInstallDialog(String data) {
        if (TextUtils.isEmpty(data)) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OpenInstall");
        builder.setMessage("这是App安装时获取的数据\n" + data);
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
