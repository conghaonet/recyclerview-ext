# RecyclerViewExt
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
```xml
<com.app2m.widget.recyclerview.RecyclerViewExt
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
```java
recyclerView.setOnExtScrollListener(new RecyclerViewExt.OnExtScrollListener() {
    @Override
    public void onScrollUp() {
        mAdapter.setDefaultFooterView();
    }
    @Override
    public void onLastItemVisible(int lastPosition) {
        loadData(mData.size());
    }
});
```
That's it!
