package com.brioal.linecode.base;

import cn.bmob.v3.BmobObject;

/**
 * Created by Brioal on 2016/3/23.
 */
public class CodeItem extends BmobObject implements Comparable {
    private int mId;
    private String mTitle;
    private String mDesc;
    private String mCode;
    private String mAuthor;
    private String mTag;
    private String mTime;
    private int mRead;

    public CodeItem() {
    }

    public CodeItem(int mId, String mTitle, String mDesc, String mCode, String mAuthor, String mTag, String mTime, int mRead) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mDesc = mDesc;
        this.mCode = mCode;
        this.mAuthor = mAuthor;
        this.mTag = mTag;
        this.mTime = mTime;
        this.mRead = mRead;
    }

    public void setmCode(String mCode) {
        this.mCode = mCode;
    }

    public String getmCode() {
        return mCode;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public int getmRead() {
        return mRead;
    }

    public void setmRead(int mRead) {
        this.mRead = mRead;
    }

    @Override
    public int compareTo(Object another) { //按更新时间排序
        CodeItem rh = (CodeItem) another;
        return -(getUpdatedAt().compareTo(rh.getUpdatedAt()));
    }
}
