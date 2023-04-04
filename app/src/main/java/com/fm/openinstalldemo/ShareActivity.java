package com.fm.openinstalldemo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.SharePlatform;
import com.fm.openinstall.listener.ResultCallback;
import com.fm.openinstall.model.Error;
import com.fm.openinstalldemo.utils.Tools;


public class ShareActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("裂变统计");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.selectPlatform).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectDialog();
            }
        });

        findViewById(R.id.shareReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String shareCode = ((EditText) findViewById(R.id.shareCode)).getText().toString();
                String sharePlatform = ((EditText) findViewById(R.id.sharePlatform)).getText().toString();
                OpenInstall.reportShare(shareCode, sharePlatform, new ResultCallback<Void>() {
                    @Override
                    public void onResult(Void aVoid, Error error) {
                        if (error != null) {
                            showReportDialog("分享上报失败");
                            Log.e("OpenInstallDemo", "分享上报失败：" + error.getErrorMsg());
                        } else {
                            showReportDialog("分享上报成功");

                            showGuide(shareCode);
                        }
                    }
                });
            }
        });

        ((EditText)findViewById(R.id.shareCode)).addTextChangedListener(textWatcher);
        ((EditText)findViewById(R.id.sharePlatform)).addTextChangedListener(textWatcher);
    }

    private void showSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择分享平台");
        final String[] items = {SharePlatform.QQ.name(), SharePlatform.Qzone.name(),
                SharePlatform.WechatSession.name(), SharePlatform.WechatTimeline.name(),
                SharePlatform.Sina.name(), SharePlatform.Sms.name()};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String platform = items[which];  // 分享平台
                ((EditText) findViewById(R.id.sharePlatform)).setText(platform);
            }
        });
        builder.setNegativeButton("取消", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showReportDialog(String data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(data);
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

    private void showGuide(String shareCode) {
        String appKey = Tools.getAppKey(this);
        StringBuilder stringBuilder = new StringBuilder();
        TextView descTv = findViewById(R.id.shareDesc);
        if (!TextUtils.isEmpty(appKey)) {
            final String shareLink = "https://app-" + appKey + ".n-9.me/page/" + appKey + "/js-test?shareCode=" + shareCode;
//            final String shareLink = "https://app-" + appKey + ".openinstall.io/page/" + appKey + "/js-test?shareCode=" + shareCode;
            stringBuilder.append("分享链接为：").append("\n").append(shareLink).append("\n");
            findViewById(R.id.copyLink).setVisibility(View.VISIBLE);
            findViewById(R.id.copyLink).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("COPY", shareLink);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(ShareActivity.this, "链接复制成功", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            stringBuilder.append("将shareCode的值拼接到分享H5页面的url后面，例如shareCode=1001，随后将拼接好的URL分享出去\n");
        }
        stringBuilder.append("\n当对方打开分享链接时，点击按钮安装或拉起App。如果是安装App，会自动完成分享归因，统计相关数据；如果是拉起App，则统计相关数据。");
        descTv.setText(stringBuilder.toString());
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            TextView descTv = findViewById(R.id.shareDesc);
            descTv.setText("");
            findViewById(R.id.copyLink).setVisibility(View.GONE);
        }
    };

}
