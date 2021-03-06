package com.mredrock.cyxbs.ui.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.ui.activity.BaseActivity;
import com.mredrock.cyxbs.ui.activity.LoginActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.setting_remind_layout)
    RelativeLayout settingRemindLayout;
    @Bind(R.id.setting_feedback_layout)
    RelativeLayout settingFeedbackLayout;
    @Bind(R.id.setting_about_layout)
    RelativeLayout settingAboutLayout;
    @Bind(R.id.setting_exit_layout)
    RelativeLayout settingExitLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolbar();
    }

    @OnClick(R.id.setting_remind_layout)
    void clickToRemind() {
        startActivity(new Intent(this, NewsRemindActivity.class));
    }

    @OnClick(R.id.setting_feedback_layout)
    void clickToFeedback() {

    }

    @OnClick(R.id.setting_about_layout)
    void clickToAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    @OnClick(R.id.setting_exit_layout)
    void clickToExit() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void initToolbar() {
        if (toolbar != null) {
            toolbar.setTitle("");
            toolbarTitle.setText("设置");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> SettingActivity.this.finish());
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }
}