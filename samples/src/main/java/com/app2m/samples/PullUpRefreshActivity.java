package com.app2m.samples;

import android.databinding.DataBindingUtil;
import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.app2m.samples.R;
import com.app2m.samples.databinding.SampleActivityPullUpRefreshBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PullUpRefreshActivity extends AppCompatActivity {
    private static final String TAG = PullUpRefreshActivity.class.getName();
    private SampleActivityPullUpRefreshBinding mBinding;
    private static final int ROWS_LIMIT = 15;
    private static final int GRID_SPAN_COUNT = 3;
    private SampleAdapter mAdapter;
    private final List<ItemVM> mData = new ArrayList<>();
    private boolean mIsLoading;
    private float mActionDownY;
    private boolean mIsControlledOnScrollStateChanged;
    private int mAmountOfVerticalScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.sample_activity_pull_up_refresh);
        mBinding.setActivity(this);
        mAdapter = new SampleAdapter(this, mData);
        mAdapter.setOnItemClickListener(new SampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ItemVM itemVM) {
                Toast.makeText(PullUpRefreshActivity.this, "Clicked item: " + itemVM.getStr(), Toast.LENGTH_SHORT).show();
            }
        });
        mBinding.layoutRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RecyclerView.LayoutManager layoutManager = null;
                switch (checkedId) {
                    case R.id.linearLayoutRadio:
                        layoutManager = new LinearLayoutManager(PullUpRefreshActivity.this, LinearLayoutManager.VERTICAL, false);
                        break;
                    case R.id.gridLayoutRadio:
                        layoutManager = new GridLayoutManager(PullUpRefreshActivity.this, GRID_SPAN_COUNT);
                        ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                if(SampleAdapter.TYPE_FOOTER == mAdapter.getItemViewType(position)) {
                                    return GRID_SPAN_COUNT;
                                } else {
                                    return 1;
                                }
                            }
                        });
                        break;
                    case R.id.staggeredGridLayoutRadio:
                        layoutManager = new StaggeredGridLayoutManager(GRID_SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                        break;
                    default:
                        break;
                }
                if(layoutManager != null) {
                    mBinding.recyclerView.setLayoutManager(layoutManager);
                }
            }
        });
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(mAmountOfVerticalScroll > 0) {
                    mAdapter.setDefaultFooterView();
                }
                if(RecyclerView.SCROLL_STATE_IDLE == newState && mIsControlledOnScrollStateChanged && mAmountOfVerticalScroll > 0) {
                    executePullUp();
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mAmountOfVerticalScroll = dy;
                if(dy != 0) {
                    mIsControlledOnScrollStateChanged = true;
                } else {
                    mIsControlledOnScrollStateChanged = false;
                }
            }
        });
        mBinding.recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!mIsControlledOnScrollStateChanged) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            mActionDownY = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mActionDownY <= 0) {
                                mActionDownY = event.getY();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            mAmountOfVerticalScroll = (int)(mActionDownY - event.getY());
                            if (mAmountOfVerticalScroll > 0) { //向上滑动
                                mAdapter.setDefaultFooterView();
                                executePullUp();
                            }
                            mActionDownY = -1;
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        mBinding.swipeRefreshLayout.setColorSchemeResources(SampleConstant.SWIPE_REFRESHING_COLORS);
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!mIsLoading) {
                    loadData(0);
                }
            }
        });
        mBinding.swipeRefreshLayout.setRefreshing(true);
        loadData(0);
    }
    //找到数组中的最大值
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    private void executePullUp() {
        RecyclerView.LayoutManager layoutManager = mBinding.recyclerView.getLayoutManager();
        int lastPosition = -1;
        if (layoutManager instanceof LinearLayoutManager) {
            lastPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if(mBinding.recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
            //得到这个数组后再取到数组中position值最大的那个就是最后显示的position值了
            int[] lastPositions = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(lastPositions);
            lastPosition = findMax(lastPositions);
        }

        if(!mIsLoading && (!mIsControlledOnScrollStateChanged || lastPosition + 1 == mAdapter.getItemCount())) {
            loadData(mData.size());
        }
    }
    private void loadData(final int offset) {
        if(mIsLoading) {
            return;
        }
        mIsLoading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int indexTo = offset ==0 ? ROWS_LIMIT : mData.size()+ROWS_LIMIT;
                    if(indexTo > SampleConstant.TESTING_ARRAY.length) indexTo = SampleConstant.TESTING_ARRAY.length;
                    final String[] result = Arrays.copyOfRange(SampleConstant.TESTING_ARRAY, offset, indexTo);
                    Thread.sleep(1500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setViewModel(offset, Arrays.asList(result));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private void setViewModel(final int offset, List<String> list) {
        if (offset == 0) {
            mData.clear();
        }
        if(offset == 0 && list.isEmpty()) {
            Toast.makeText(this, "There is nothing.", Toast.LENGTH_SHORT).show();
            mAdapter.notifyDataSetChanged();
        } else if(offset > 0 && list.isEmpty()) {
            Toast.makeText(this, "No more data.", Toast.LENGTH_SHORT).show();
            mAdapter.removeFooterView();
        } else {
            for(String str: list) {
                ItemVM itemVM = new ItemVM(str);
                mData.add(itemVM);
            }
            mAdapter.removeFooterView();
            mAdapter.notifyDataSetChanged();
        }
        if(mBinding.swipeRefreshLayout.isRefreshing()) {
            mBinding.swipeRefreshLayout.setRefreshing(false);
        }
        mIsLoading = false;
    }
}
