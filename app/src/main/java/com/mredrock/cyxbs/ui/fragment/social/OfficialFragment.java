package com.mredrock.cyxbs.ui.fragment.social;


import com.mredrock.cyxbs.APP;
import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.model.social.HotNews;
import com.mredrock.cyxbs.model.social.HotNewsContent;
import com.mredrock.cyxbs.network.RequestManager;
import com.mredrock.cyxbs.ui.adapter.NewsAdapter;

import java.util.List;

import rx.Subscriber;

/**
 * Created by mathiasluo on 16-4-26.
 */
public class OfficialFragment extends BaseNewsFragment {

    @Override
    void provideData(Subscriber<List<HotNews>> subscriber, int size, int page) {
        RequestManager.getInstance().getListNews(subscriber, size, page, APP.getUser(getActivity()).stuNum, APP.getUser(getActivity()).idNum);
    }

    @Override
    protected void setDate(NewsAdapter.ViewHolder holder, HotNewsContent hotNewsContent) {
        super.setDate(holder, hotNewsContent);
        holder.mTextContent.setText(hotNewsContent.officeNewsContent.title);
        holder.mTextName.setText(hotNewsContent.officeNewsContent.getOfficeName());
        holder.enableAvatarClick = false;
        holder.mImgAvatar.setImageResource(R.drawable.ic_official_notification);
    }

}