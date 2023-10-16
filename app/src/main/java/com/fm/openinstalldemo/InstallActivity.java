package com.fm.openinstalldemo;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallListener;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;
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
                OpenInstall.getInstall(new AppInstallListener() {
                    @Override
                    public void onInstallFinish(AppData appData, Error error) {
                        if (error != null && error.shouldRetry()) {
                            // 未获取到数据，可以重试
                            showRetryDialog();
                        } else {
                            // 获取渠道数据
                            String channelCode = appData.getChannel();
                            // 获取H5落地页传递的数据
                            String bindData = appData.getData();

                            // 根据获取到的数据处理业务
                            showInstallDialog(appData);
                            Store.sharedPreferencesPut(InstallActivity.this, Store.KET_GET, String.valueOf(true));
                        }
                    }
                }, 8);
            }
        });
    }

    private void showInstallDialog(AppData appData) {
        if (appData == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OpenInstall");
        builder.setMessage("这是App安装时获取的数据\n"
                + "channelCode=" + appData.getChannel() + "\n"
                + "bindData=" + appData.getData());
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

    private void showRetryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OpenInstall");
        builder.setMessage("未获取到数据，请稍后重试");
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
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
