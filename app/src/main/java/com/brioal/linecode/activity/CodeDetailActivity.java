package com.brioal.linecode.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brioal.linecode.R;
import com.brioal.linecode.base.CodeItem;
import com.brioal.linecode.view.MyWebView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class CodeDetailActivity extends SwipeBackActivity {

    private static final String TAG = "DetailInfo";
    @Bind(R.id.detail_toolbar)
    Toolbar toolbar;
    @Bind(R.id.detail_head)
    LinearLayout head;
    @Bind(R.id.detail_web)
    MyWebView detailWeb;
    @Bind(R.id.detail_tv_author)
    TextView tv_author;
    @Bind(R.id.detail_tv_read)
    TextView tv_rean;
    @Bind(R.id.detail_tv_time)
    TextView tv_time;

    private String mAuthor;
    private String mTitle;
    private int mRead;
    private String mCode;
    private Bitmap mHead;
    private String mTime;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_detail);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    public void initData() {
        mAuthor = getIntent().getStringExtra("mAuthor");
        mTitle = getIntent().getStringExtra("mTitle");
        mCode = getIntent().getStringExtra("mCode");
        mRead = getIntent().getIntExtra("mRead", 1);
        mHead = getIntent().getParcelableExtra("mHead");
        mTime = getIntent().getStringExtra("mTime");
        mId = getIntent().getStringExtra("mId");
        BmobQuery<CodeItem> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", mId);
        query.findObjects(CodeDetailActivity.this, new FindListener<CodeItem>() {
            @Override
            public void onSuccess(List<CodeItem> list) {
                final CodeItem item = list.get(0);
                item.increment("mRead"); // 分数递增1
                item.update(CodeDetailActivity.this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "onSuccess: 更新成功");
                        tv_rean.setText("" + item.getmRead());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.i(TAG, "onFailure: 更新失败" + s);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public void initView() {
        toolbar.setTitle(mTitle);

        toolbar.setLogo(new BitmapDrawable(mHead));//TODO 设置头像
        setSupportActionBar(toolbar);

        tv_author.setText(mAuthor);
        tv_rean.setText(mRead + "");
        tv_time.setText(mTime);

        WebSettings mWebSettings = detailWeb.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        detailWeb.loadDataWithBaseURL("file:///android_asset/", getFileContent("index.html").replace("Brioal_TItle", mTitle).replace("Brioal_is_hardingworking", mCode), "text/html", null, null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CodeDetailActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getFileContent(String file) {
        String content = "";
        try {
            // 把数据从文件读入内存
            InputStream is = getResources().getAssets().open(file);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int i = is.read(buffer, 0, buffer.length);
            while (i > 0) {
                bs.write(buffer, 0, i);
                i = is.read(buffer, 0, buffer.length);
            }

            content = new String(bs.toByteArray(), Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return content;
    }

}
