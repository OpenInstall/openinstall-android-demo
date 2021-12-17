package com.fm.openinstalldemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fm.openinstall.OpenInstall;

public class ChannelActivity extends AppCompatActivity {

    Toolbar toolbar;
    public static final String POINT_ID = "effect_test";

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
                //注册上报
                OpenInstall.reportRegister();
                showReportDialog();
            }
        });

        findViewById(R.id.effect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //效果点统计
                OpenInstall.reportEffectPoint(POINT_ID, 1);
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
