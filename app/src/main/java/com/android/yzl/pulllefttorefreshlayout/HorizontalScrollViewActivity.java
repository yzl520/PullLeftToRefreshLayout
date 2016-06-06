package com.android.yzl.pulllefttorefreshlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.yzl.lib.PullLeftToRefreshLayout;

/**
 * Created by yzl on 2016/6/6.
 */
public class HorizontalScrollViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horizontal);
        PullLeftToRefreshLayout plrl = (PullLeftToRefreshLayout) findViewById(R.id.plrl);
        plrl.setOnRefreshListener(new PullLeftToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(HorizontalScrollViewActivity.this, "刷新数据成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
