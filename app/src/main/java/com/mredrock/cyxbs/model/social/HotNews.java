package com.mredrock.cyxbs.model.social;

import android.os.Parcel;
import android.os.Parcelable;

import com.mredrock.cyxbs.model.RedrockApiWrapper;

import java.util.List;

/**
 * Created by mathiasluo on 16-4-5.
 */
public class HotNews extends RedrockApiWrapper<HotNewsContent> implements Parcelable {

    public String page;

    public HotNews(PersonLatest personLatest, String userId, String userName, String userHead) {
        this.data = new HotNewsContent(BBDDNews.BBDD + ""
                , personLatest.id
                , BBDDNews.BBDD
                , userId
                , userName
                , userHead
                , personLatest.createdTime
                , new OfficeNewsContent(personLatest.content)
                , new HotNewsContent.ImgBean(personLatest.thumbnailPhoto, personLatest.photo)
                , personLatest.likeNum
                , personLatest.remarkNum
                , true
                , personLatest.id);
    }

    public HotNews(OfficeNewsContent officeNewsContent) {
        this.data = new HotNewsContent(officeNewsContent);
    }

    public HotNews(BBDDNewsContent bbddNewsContent) {
        this.data = new HotNewsContent(bbddNewsContent.typeId
                , bbddNewsContent.id
                , 5
                , bbddNewsContent.stuNum
                , bbddNewsContent.nickName
                , bbddNewsContent.photoSrc
                , bbddNewsContent.createdTime
                , new OfficeNewsContent(bbddNewsContent.content)
                , new HotNewsContent.ImgBean(bbddNewsContent.articleThumbnailSrc, bbddNewsContent.articlePhotoSrc)
                , bbddNewsContent.likeNum
                , bbddNewsContent.remarkNum
                , bbddNewsContent.isMyLike
                , bbddNewsContent.id);
    }

    public HotNews(String content, List<Image> list) {
        list.remove(0);
        String a = "";
        String b = "";
        for (Image image : list) {
            a += image.url + ",";
            b += image.url + ",";

        }
        this.data = new HotNewsContent(new HotNewsContent.ImgBean(a, b), new OfficeNewsContent(content));
    }

    public HotNews(BBDDDetail bbddDetail) {
        this.data = new HotNewsContent("bbdd"
                , bbddDetail.id
                , BBDDNews.BBDD
                , ""
                , ""
                , bbddDetail.photoSrc
                , bbddDetail.createdTime
                , new OfficeNewsContent(bbddDetail.content)
                , new HotNewsContent.ImgBean(bbddDetail.thumbnailSrc, bbddDetail.photoSrc)
                , bbddDetail.likeNum
                , bbddDetail.remarkNum
                , false
                , bbddDetail.id);
    }

    protected HotNews(Parcel in) {
        page = in.readString();
    }

    public static final Creator<HotNews> CREATOR = new Creator<HotNews>() {
        @Override
        public HotNews createFromParcel(Parcel in) {
            return new HotNews(in);
        }

        @Override
        public HotNews[] newArray(int size) {
            return new HotNews[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(page);
    }
}
