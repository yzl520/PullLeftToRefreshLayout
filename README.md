# PullLeftToRefreshLayout
带有贝塞尔曲线效果的横向刷新布局，支持多种view，如RecyclerView，HorizontalScrollView等。
![image](https://github.com/yzl520/PullLeftToRefreshLayout/raw/master/image/UI.gif)
#XML布局
```
<com.android.yzl.lib.PullLeftToRefreshLayout
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/plrl"
    app:footerBgColor="@color/yellow"
    android:layout_width="match_parent"
    android:layout_height="100dp"
    android:background="@color/white">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/rv"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</com.android.yzl.lib.PullLeftToRefreshLayout>
```
#自定义属性
```
app:footerBgColor="@color/yellow"
```
    可以改变刷新区域的背景颜色
#监听事件
###刷新监听：当左拉距离达到刷新距离，且动画结束时调用。
```
PullLeftToRefreshLayout refreshLayout = (PullLeftToRefreshLayout) findViewById(R.id.plrl);
        refreshLayout.setOnRefreshListener(new PullLeftToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(RecyclerViewActivity.this, "刷新数据成功", Toast.LENGTH_SHORT).show();
            }
        });
```
###滑动监听：当需要与其他可滑动控件（如ListView）嵌套使用时调用。
```
refreshLayout.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollChange(boolean scroll) {
                mListView.requestDisallowInterceptTouchEvent(scroll);
            }
        });
```
