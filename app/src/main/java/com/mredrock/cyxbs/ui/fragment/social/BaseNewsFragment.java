package com.mredrock.cyxbs.ui.fragment.social;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.mredrock.cyxbs.APP;
import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.model.social.HotNews;
import com.mredrock.cyxbs.model.social.HotNewsContent;
import com.mredrock.cyxbs.subscriber.EndlessRecyclerOnScrollListener;
import com.mredrock.cyxbs.subscriber.SimpleSubscriber;
import com.mredrock.cyxbs.subscriber.SubscriberListener;
import com.mredrock.cyxbs.ui.adapter.HeaderViewRecyclerAdapter;
import com.mredrock.cyxbs.ui.adapter.NewsAdapter;
import com.mredrock.cyxbs.ui.fragment.BaseFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by mathiasluo on 16-4-26.
 */
public abstract class BaseNewsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final int PER_PAGE_NUM = 10;
    public static final String TAG = "BaseNewsFragment";
    public static final int FIRST_PAGE_INDEX = 0;

    @Bind(R.id.information_RecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.information_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int currentIndex = 0;
    private List<HotNews> mListHotNews = null;
    private FooterViewWrapper mFooterViewWrapper;

    protected NewsAdapter mNewsAdapter;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    abstract void provideData(Subscriber<List<HotNews>> subscriber, int size, int page);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    protected void init() {
        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(APP.getContext(), R.color.colorAccent),
                ContextCompat.getColor(APP.getContext(), R.color.colorPrimary)
        );
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mLinearLayoutManager = new LinearLayoutManager(getParentFragment().getActivity());

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        addOnScrollListener();
        initAdapter(null);
        getCurrentData(PER_PAGE_NUM, FIRST_PAGE_INDEX);
    }

    private void addOnScrollListener() {
        if (endlessRecyclerOnScrollListener != null)
            mRecyclerView.removeOnScrollListener(endlessRecyclerOnScrollListener);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                currentIndex++;
                getNextPageData(PER_PAGE_NUM, currentIndex);
            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    }

    @Override
    public void onRefresh() {
        getCurrentData(PER_PAGE_NUM, FIRST_PAGE_INDEX);
        currentIndex = 0;
        addOnScrollListener();
    }

    private void getDataFailed(String reason) {
        Toast.makeText(getActivity(), getString(R.string.erro), Toast.LENGTH_SHORT).show();
        Log.e(TAG, reason);

    }

    private void showLoadingProgress() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void closeLoadingProgress() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void getCurrentData(int size, int page) {
        mSwipeRefreshLayout.post(() -> showLoadingProgress());
        provideData(new SimpleSubscriber<>(getActivity(), new SubscriberListener<List<HotNews>>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mFooterViewWrapper.showLoadingFailed();
                closeLoadingProgress();
                getDataFailed(e.toString());
            }

            @Override
            public void onNext(List<HotNews> hotNewses) {
                super.onNext(hotNewses);
                if (mListHotNews == null) {
                    initAdapter(hotNewses);
                    if (hotNewses.size() == 0) mFooterViewWrapper.showLoadingNoData();
                } else mNewsAdapter.replaceDataList(hotNewses);
                Log.i("====>>>", "page===>>>" + page + "size==>>" + hotNewses.size());
                closeLoadingProgress();
            }
        }), size, page);
    }


    private void initAdapter(List<HotNews> listHotNews) {
        mListHotNews = listHotNews;
        mNewsAdapter = new NewsAdapter(mListHotNews) {
            @Override
            public void setDate(ViewHolder holder, HotNewsContent hotNewsContent) {
                BaseNewsFragment.this.setDate(holder, hotNewsContent);
            }
        };
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mNewsAdapter);
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        addFooterView(mHeaderViewRecyclerAdapter);
        mFooterViewWrapper.mCircleProgressBar.setVisibility(View.INVISIBLE);
    }

    protected void setDate(NewsAdapter.ViewHolder holder, HotNewsContent mDataBean) {
    }

    private void addFooterView(HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter) {
        mFooterViewWrapper = new FooterViewWrapper(getContext(), mRecyclerView);
        mHeaderViewRecyclerAdapter.addFooterView(mFooterViewWrapper.getFooterView());
        mFooterViewWrapper.onFailedClick(view -> {
            if (currentIndex == 0) getCurrentData(PER_PAGE_NUM, currentIndex);
            getNextPageData(PER_PAGE_NUM, currentIndex);
        });
    }

    private void getNextPageData(int size, int page) {
        mFooterViewWrapper.showLoading();
        provideData(new SimpleSubscriber<>(getContext(), new SubscriberListener<List<HotNews>>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mFooterViewWrapper.showLoadingFailed();
                getDataFailed(e.toString());
            }

            @Override
            public void onNext(List<HotNews> hotNewses) {
                super.onNext(hotNewses);
                if (hotNewses.size() == 0) {
                    mFooterViewWrapper.showLoadingNoMoreData();
                    return;
                }
                mNewsAdapter.addDataList(hotNewses);
                Log.i("====>>>", "page===>>>" + page + "size==>>" + hotNewses.size());
            }
        }), size, page);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public static class FooterViewWrapper {

        @Bind(R.id.progressBar)
        CircleProgressBar mCircleProgressBar;
        @Bind(R.id.textLoadingFailed)
        TextView mTextLoadingFailed;

        private View footerView;

        public FooterViewWrapper(Context context, ViewGroup parent) {
            footerView = LayoutInflater.from(context)
                    .inflate(R.layout.list_footer_item_news, parent, false);
            ButterKnife.bind(this, footerView);
        }

        public View getFooterView() {
            return footerView;
        }

        public void showLoading() {
            mCircleProgressBar.setVisibility(View.VISIBLE);
            mTextLoadingFailed.setVisibility(View.GONE);
        }

        public void showLoadingFailed() {
            mCircleProgressBar.setVisibility(View.INVISIBLE);
            mTextLoadingFailed.setVisibility(View.VISIBLE);
            mTextLoadingFailed.setText("加载失败，点击重新加载!");
        }

        public void showLoadingNoMoreData() {
            mCircleProgressBar.setVisibility(View.INVISIBLE);
            mTextLoadingFailed.setVisibility(View.VISIBLE);
            mTextLoadingFailed.setText("已经到底了,没有更多数据了哟!");
        }

        public void showLoadingNoData() {
            mCircleProgressBar.setVisibility(View.INVISIBLE);
            mTextLoadingFailed.setVisibility(View.VISIBLE);
            mTextLoadingFailed.setText("还没有数据哟,点击发送吧！");
        }

        public void onFailedClick(View.OnClickListener onClickListener) {
            mTextLoadingFailed.setOnClickListener(onClickListener::onClick);
        }

    }
}
