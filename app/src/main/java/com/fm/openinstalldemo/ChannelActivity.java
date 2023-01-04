package com.fm.openinstalldemo;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fm.openinstall.OpenInstall;

import java.util.HashMap;
import java.util.Map;

public class ChannelActivity extends AppCompatActivity {

    Toolbar toolbar;
    public static final String POINT_TEST = "effect_test";
    public static final String POINT_DETAIL = "effect_detail";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("渠道统计");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.iv_expain).setBackgroundResource(R.drawable.expain_channel);

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 注册上报
                OpenInstall.reportRegister();
                showReportDialog();
            }
        });

        findViewById(R.id.effect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 效果点统计
                OpenInstall.reportEffectPoint(POINT_TEST, 1);
                showReportDialog();
            }
        });

        findViewById(R.id.detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 效果点自定义参数：需要控制台的效果点管理中启用“记录明细”并且添加“自定义参数”
                Map<String, String> extraMap = new HashMap<>();
                extraMap.put("brand", Build.BRAND);
                extraMap.put("model", Build.MODEL);
                // 效果点统计
                OpenInstall.reportEffectPoint(POINT_DETAIL, 1, extraMap);
                showReportDialog();
            }
        });

    }

    private void showReportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("数据已提交");
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
