package com.brioal.linecode.base;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Brioal on 2016/3/25.
 */
public class User extends BmobUser {
    private BmobFile mHead ;

    public User(BmobFile mHead) {
        this.mHead = mHead;
    }

    public BmobFile getmHead() {
        return mHead;
    }

    public void setmHead(BmobFile mHead) {
        this.mHead = mHead;
    }
}
