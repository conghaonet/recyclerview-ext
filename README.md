# RecyclerViewExt
实现RecyclerView上拉刷新，添加footerView。RecyclerViewExt与其它上拉刷新控件比较，最大的不同是：当初次加载的数据不满一屏（RecyclerView无法滚动时），可以正常上拉刷新，且配合SwipeRefreshLayout时无冲突。
支持LinearLayoutManager、GridLayoutManager、StaggeredGridLayoutManager的垂直布局。
目前工作中不需要在列表中显示headerView，所以目前仅支持添加footerView。

[![](https://jitpack.io/v/conghaonet/RecyclerViewExt.svg)](https://jitpack.io/#conghaonet/RecyclerViewExt)

## Dependency
Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Then, add the library to your module `build.gradle`
```gradle
dependencies {
    compile 'com.github.conghaonet:RecyclerViewExt:0.1'
}
```

## Usage
layout xml:
```xml
<com.app2m.widget.recyclerview.RecyclerViewExt
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

java code: 
```java
recyclerView.setOnExtScrollListener(new RecyclerViewExt.OnExtScrollListener() {
    @Override
    public void onScrollUp() {
        //This adapter extends RecyclerViewExtAdapter
        adapter.setDefaultFooterView();
	
	//set your footer layout
	//dapter.setCustomFooterView(YOUR_FOOTER_LAYOUT);
    }
    @Override
    public void onLastItemVisible(int lastPosition) {
        //Execute pull up to refresh
	... ...
    }
});

... ...

//remove footer vieww;
adapter.removeFooterView();
```
That's it!

License
--------
```
    Copyright 2017 Cong Hao
    
    Licensed under the GNU GENERAL PUBLIC LICENSE Version 3.
    Everyone is permitted to copy and distribute verbatim copies 
    of this license document, but changing it is not allowed.
    
    http://www.gnu.org/licenses/gpl-3.0.html
```
