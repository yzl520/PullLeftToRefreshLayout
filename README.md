# PullLeftToRefreshLayout
带有贝塞尔曲线效果的横向刷新布局，支持多种view，如RecyclerView，HorizontalScrollView等。
![image](https://github.com/yzl520/PullLeftToRefreshLayout/raw/master/image/UI.gif)
#XML布局
```
<com.android.yzl.lib.PullLeftToRefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
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
