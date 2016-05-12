package com.brioal.linecode;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.brioal.linecode.base.PagerTag;
import com.brioal.linecode.database.CodeDataBaseHelper;
import com.brioal.linecode.fragment.CodeFragment;
import com.brioal.linecode.util.NetWorkUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import me.imid.swipebacklayout.lib.BaseActivity;

public class MainActivity extends BaseActivity {
    private static final int REFRESH_SUCCESS = 101;
    private static final String TAG = "MainInfo";

    @Bind(R.id.main_tablayout)
    TabLayout mainTablayout;
    @Bind(R.id.main_viewpager)
    ViewPager mainViewpager;

    private MyFragmentAdapter adapter;
    private CodeDataBaseHelper dataBaseHelper;
    private SQLiteDatabase database;
    private List<PagerTag> tags;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            initData();
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REFRESH_SUCCESS) {
                initView();
            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dataBaseHelper = new CodeDataBaseHelper(this, "LineCode.db");
        if (NetWorkUtil.isNetworkConnected(this)) { // 网络可用的时候更新数据
            new Thread(runnable).start();
        }
            getSavedData();

    }

    //从网络获取数据并更新UI
    public void initData() {
        BmobQuery<PagerTag> query = new BmobQuery<PagerTag>();
        query.findObjects(this, new FindListener<PagerTag>() {
            @Override
            public void onSuccess(List<PagerTag> object) {
                tags = object;
                handler.sendEmptyMessage(REFRESH_SUCCESS);
                Log.i(TAG, "onSuccess: 查询成功");
                Collections.sort(tags); // 按从大到小排序
                saveData(tags);
                for (PagerTag tag : object) {
                    Log.i(TAG, "查询到的Tag: " + tag.getmTag());
                }
            }

            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, "onError: 查询失败");
            }
        });


    }

    //更新UI
    public void initView() {
        if (adapter == null) {
            adapter = new MyFragmentAdapter(getSupportFragmentManager());
        } else {
            adapter.notifyDataSetChanged();
        }
        mainViewpager.setAdapter(adapter);
        mainTablayout.setupWithViewPager(mainViewpager);
    }

    //保存数据
    public void saveData(List<PagerTag> tags) {
        //TODo 保存数据到数据库
        database = dataBaseHelper.getReadableDatabase();
        database.execSQL("delete  from PagerTags");
        for (int i = 0; i < tags.size(); i++) {
            database.execSQL("insert into PagerTags values(null,?)", new String[]{tags.get(i).getmTag()});
        }

    }

    public void getSavedData() {
        //TODO 从数据库读取数据
        database = dataBaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from PagerTags ", null);
        PagerTag tag = null;
        if (tags == null) {
            tags = new ArrayList<>();
        } else {
            tags.clear();
        }
        while (cursor.moveToNext()) {
            tag = new PagerTag(cursor.getInt(0), cursor.getString(1));
            tags.add(tag);
        }
        if (tags.size() != 0) {
            handler.sendEmptyMessage(REFRESH_SUCCESS);
        }
    }

    class MyFragmentAdapter extends FragmentStatePagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            CodeFragment fragment = new CodeFragment();
            fragment.setTag(tags.get(position).getmTag());
            return fragment;
        }

        @Override
        public int getCount() {
            return tags.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tags.get(position).getmTag();
        }
    }


}
