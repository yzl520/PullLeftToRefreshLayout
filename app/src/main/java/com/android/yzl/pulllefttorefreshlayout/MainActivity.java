package com.android.yzl.pulllefttorefreshlayout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.yzl.lib.PullLeftToRefreshLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void clickH(View v){
        startActivity(new Intent(this, HorizontalScrollViewActivity.class));
    }

    public void clickR(View v){
        startActivity(new Intent(this, RecyclerViewActivity.class));
    }

}
