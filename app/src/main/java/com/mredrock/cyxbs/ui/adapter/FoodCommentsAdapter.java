package com.mredrock.cyxbs.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.model.FoodComment;
import com.mredrock.cyxbs.util.GlideHelper;
import com.mredrock.cyxbs.util.SchoolCalendar;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Stormouble on 16/4/28.
 */
public class FoodCommentsAdapter extends BaseRecyclerViewAdapter<FoodComment, FoodCommentsAdapter.RestaurantCommentsViewHolder>{

    private GlideHelper mGlideHelper;

    public FoodCommentsAdapter(List<FoodComment> mDatas, Context context) {
        super(mDatas, context);
        mGlideHelper = new GlideHelper(context);
    }

    @Override
    protected void bindData(RestaurantCommentsViewHolder holder, FoodComment data, int position) {
        holder.mCommentContent.setText(data.comment_content);
        holder.mCommentAuthor.setText(data.comment_author_name);
        holder.mCommentDate.setText(
                new SchoolCalendar(Long.parseLong(data.comment_date)).getString("yyyy年MM月dd日HH:mm"));
    }

    @Override
    public RestaurantCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_surrounding_food_detail, parent, false);
        return new RestaurantCommentsViewHolder(view);
    }

    public static class RestaurantCommentsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.food_detail_restaurant_comment_content)
        TextView mCommentContent;
        @Bind(R.id.food_detail_restaurant_comment_user_img)
        CircularImageView mCommentUserIcon;
        @Bind(R.id.food_detail_restaurant_comment_author)
        TextView mCommentAuthor;
        @Bind(R.id.food_detail_restaurant_comment_date)
        TextView mCommentDate;

        public RestaurantCommentsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
