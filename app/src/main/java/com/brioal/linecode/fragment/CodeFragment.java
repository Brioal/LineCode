package com.brioal.linecode.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brioal.linecode.R;
import com.brioal.linecode.activity.CodeDetailActivity;
import com.brioal.linecode.base.CodeItem;
import com.brioal.linecode.database.CodeDataBaseHelper;
import com.brioal.linecode.util.NetWorkUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Brioal on 2016/3/24.
 */
public class CodeFragment extends Fragment {
    private static final int INITDATA_FINISH = 111;
    private static final int STOP_REFRESH = 11;
    private static final int START_REFRESH = 1111;
    private static final String TAG = "FragmentInfo";
    RecyclerView recycle;
    SwipeRefreshLayout refreshLayout;
    private String mTag = "首页";
    private CodeItemAdapter adapter;
    private List<CodeItem> lists;
    private SQLiteDatabase database;
    private CodeDataBaseHelper helper;
    private View rootView;
    private int dataCount = 10;

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
            if (msg.what == INITDATA_FINISH) {
                Log.i(TAG, "handleMessage: 刷新完毕");
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                });
                setView();
            } else if (msg.what == START_REFRESH) {
                if (refreshLayout != null) {
                    Log.i(TAG, "handleMessage: 开始刷新");
                    refreshLayout.setRefreshing(true);
                }
            } else if (msg.what == STOP_REFRESH) {
                if (refreshLayout != null) {
                    Log.i(TAG, "handleMessage: 停止刷新");

                }
            }
        }
    };

    public static CodeFragment getInstance(String mTag) {
        CodeFragment fragment = new CodeFragment();
        fragment.setTag(mTag);
        return fragment;
    }

    public void setTag(String mTag) {
        this.mTag = mTag;

    }

    public CodeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        rootView = inflater.inflate(R.layout.fragment_code, container, false);
        recycle = (RecyclerView) rootView.findViewById(R.id.fragment_recyclerView);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_refresh);
        refreshLayout.setColorSchemeResources(R.color.color_refresh1, R.color.color_refresh2, R.color.color_refresh3);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "onRefresh: 调用数据更新");
                if (NetWorkUtil.isNetworkConnected(getActivity())) {
                    new Thread(runnable).start();
                } else {
                    getSavedData();
                }
            }
        });
        getSavedData();
        if (NetWorkUtil.isNetworkConnected(getActivity()) && mTag.equals("首页")) {
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    new Thread(runnable).start();
                }
            });

        }
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new CodeDataBaseHelper(getActivity(), "LineCode.db");
    }

    //获取本地信息
    public void getSavedData() {
        if (lists == null) {
            lists = new ArrayList<>();
        } else {
            lists.clear();
        }
        database = helper.getReadableDatabase();
        Cursor cursor = null;
        if (mTag.equals("首页")) {
            cursor = database.rawQuery("select * from CodeItems where mId > 0", null);
        } else {
            cursor = database.rawQuery("select * from CodeItems where mTitle like '%" + mTag + "%' or mDesc like '%" + mTag + "%' or mTag like '%" + mTag + "%'", null);
        }
        CodeItem item = null;
        while (cursor.moveToNext()) {
            item = new CodeItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getInt(7));
            lists.add(item);
        }
        if (refreshLayout.isRefreshing()) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(false);
                }
            });
        }
        handler.sendEmptyMessage(INITDATA_FINISH);
    }

    //保存数据到本地
    public void saveData() {
        database = helper.getReadableDatabase();
        database.execSQL("delete from CodeItems");
        for (int i = 0; i < lists.size(); i++) {
            CodeItem item = lists.get(i);
            database.execSQL("insert into CodeItems values(null,?,?,?,?,?,?,?)", new Object[]{item.getmTitle(), item.getmDesc(), item.getmCode(), item.getmAuthor(), item.getmTag(), item.getmTime(), item.getmRead()});
        }
    }

    public void initData() {
        BmobQuery<CodeItem> query = new BmobQuery<>();
        if (!mTag.equals("首页")) {
            query.addWhereContains("mTag", mTag);
        }
        query.setLimit(dataCount);
        query.findObjects(getActivity(), new FindListener<CodeItem>() {
            @Override
            public void onSuccess(List<CodeItem> list) {
                lists = list;
                Log.i(TAG, "onSuccess: 查询成功" + lists.size());
                if (mTag.equals("首页")) {
                    saveData();
                }
                handler.sendEmptyMessage(INITDATA_FINISH);
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: 加载失败");
            }
        });

    }

    public void setView() {
        if (adapter == null) {
            adapter = new CodeItemAdapter();
            recycle.setAdapter(adapter);
            recycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        } else {
            Log.i(TAG, "setView: 数据更新");
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class CodeItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new CodeItemHolder(inflater.inflate(R.layout.item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CodeItemHolder) {
                final CodeItem item = lists.get(position);
                final CodeItemHolder codeItemHolder = (CodeItemHolder) holder;
                codeItemHolder.itemTitle.setText(item.getmTitle());
                codeItemHolder.itemDesc.setText(item.getmDesc());
                codeItemHolder.itemTags.setText(item.getmTag());
                codeItemHolder.itemTime.setText(item.getUpdatedAt());
                //设置Item点击事件
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CodeDetailActivity.class);
                        intent.putExtra("mTitle", item.getmTitle());
                        intent.putExtra("mAuthor", item.getmAuthor());
                        intent.putExtra("mCode", item.getmCode());
                        intent.putExtra("mRead", item.getmRead());
                        intent.putExtra("mTime", item.getUpdatedAt());
                        intent.putExtra("mId", item.getObjectId());
                        getActivity().startActivity(intent);

                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            if (lists != null) {
                return lists.size();
            }
            return 0;
        }
    }


    class CodeItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_title)
        TextView itemTitle;
        @Bind(R.id.item_desc)
        TextView itemDesc;
        @Bind(R.id.item_tags)
        TextView itemTags;
        @Bind(R.id.item_time)
        TextView itemTime;
        View itemView;

        public CodeItemHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemDesc = (TextView) itemView.findViewById(R.id.item_desc);
            itemTags = (TextView) itemView.findViewById(R.id.item_tags);
            itemTime = (TextView) itemView.findViewById(R.id.item_time);
        }
    }

}
