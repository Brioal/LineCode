package com.brioal.linecode.base;

import cn.bmob.v3.BmobObject;

/**
 * Created by Brioal on 2016/3/24.
 */
public class PagerTag extends BmobObject implements Comparable{
    private int mId;
    private String mTag;

    public PagerTag(int mId, String mTag) {
        this.mId = mId;
        this.mTag = mTag;
    }

    public int getmId() {
        return mId;
    }

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }

    @Override
    public int compareTo(Object another) {
        return getmId()-((PagerTag) another).getmId();
    }
}
