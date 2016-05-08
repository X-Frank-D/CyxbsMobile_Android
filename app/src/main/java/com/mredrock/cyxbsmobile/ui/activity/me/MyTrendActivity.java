package com.mredrock.cyxbsmobile.ui.activity.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.mredrock.cyxbsmobile.R;
import com.mredrock.cyxbsmobile.config.Const;
import com.mredrock.cyxbsmobile.model.User;
import com.mredrock.cyxbsmobile.model.social.HotNews;
import com.mredrock.cyxbsmobile.network.RequestManager;
import com.mredrock.cyxbsmobile.ui.activity.BaseActivity;
import com.mredrock.cyxbsmobile.ui.activity.social.SpecificNewsActivity;
import com.mredrock.cyxbsmobile.ui.adapter.NewsAdapter;
import com.mredrock.cyxbsmobile.util.ImageLoader;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MyTrendActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {


    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.my_trend_recycler_view)
    RecyclerView myTrendRecyclerView;
    @Bind(R.id.my_trend_refresh_layout)
    SwipeRefreshLayout myTrendRefreshLayout;

    private List<HotNews> mNewsList;
    private NewsAdapter mNewsAdapter;
    private User mUser;

    public static void startActivityWithUser(Context context,User user){
        Intent intent = new Intent(context, SpecificNewsActivity.class);
        intent.putExtra(Const.Extras.EDIT_USER, user);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trend);
        ButterKnife.bind(this);
        initToolbar();

        mUser = getIntent().getParcelableExtra(Const.Extras.EDIT_USER);
        init();
        showProgress();
    }


    @Override
    public void onRefresh() {
        getMyTrendData();
    }

/*
    @Override
    public void onItemClick(View itemView, int position, HotNewsContent dataBean) {
        Intent intent = new Intent(this, SpecificNewsActivity.class);
        intent.putExtra(SpecificNewsActivity.START_DATA, dataBean);
        startActivity(intent);
    }
*/

    private void init() {
        myTrendRefreshLayout.setOnRefreshListener(this);

        myTrendRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimary)
        );

        mNewsList = new ArrayList<>();
        myTrendRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNewsAdapter = new NewsAdapter(mNewsList) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                ImageLoader.getInstance().loadAvatar(mUser
                        .photo_thumbnail_src, holder.mImgAvatar);
                holder.mTextName.setText(mUser.nickname.equals("") ? mUser
                        .stuNum : mUser.nickname);
            }
        };
        // mNewsAdapter.setOnItemOnClickListener(this);
        myTrendRecyclerView.setAdapter(mNewsAdapter);
    }


    private void initToolbar() {
        if (toolbar != null) {
            toolbar.setTitle("");
            toolbarTitle.setText("我的动态");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(
                    v -> MyTrendActivity.this.finish());
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }


    private void getMyTrendData() {

        if (mUser != null) {
            Logger.d(mUser.toString());
            RequestManager.getInstance()
                          .getMyTrend(mUser.stuNum, mUser.idNum)
                          .subscribe(newses -> {
                              dismissProgress();
                              mNewsList.clear();
                              mNewsList.addAll(newses);
                              mNewsAdapter.notifyDataSetChanged();
                          }, throwable -> {
                              dismissProgress();
                              getDataFailed(throwable.getMessage());
                          });
            dismissProgress();
        }
    }


    private void showProgress() {
        if(mUser != null) {
            myTrendRefreshLayout.getViewTreeObserver()

                                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override public void onGlobalLayout() {
                                        myTrendRefreshLayout.getViewTreeObserver()
                                                            .removeGlobalOnLayoutListener(this);
                                        myTrendRefreshLayout.setRefreshing(true);
                                        getMyTrendData();
                                    }
                                });
        }
    }


    private void dismissProgress() {
        if(myTrendRefreshLayout != null && myTrendRefreshLayout.isRefreshing()) {
            myTrendRefreshLayout.setRefreshing(false);
        }
    }


    private void getDataFailed(String reason) {
        Toast.makeText(MyTrendActivity.this, "获取数据失败，原因:" + reason, Toast.LENGTH_SHORT).show();
    }
}