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
        //Use [RecyclerViewExtAdapter](https://github.com/conghaonet/RecyclerViewExt/blob/master/recycler_view_ext/src/main/java/com/app2m/widget/recyclerview/RecyclerViewExtAdapter.java)
        adapter.setDefaultFooterView();
    }
    @Override
    public void onLastItemVisible(int lastPosition) {
        //Execute pull up to refresh
	... ...
    }
});
```
That's it!
