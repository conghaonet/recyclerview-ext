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
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.app2m.samples.R;
import com.app2m.samples.databinding.ActivityRecyclerViewExtBinding;
import com.app2m.widget.recyclerview.RecyclerViewExt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclerViewExtActivity extends AppCompatActivity {
    private ActivityRecyclerViewExtBinding mBinding;
    private static final int ROWS_LIMIT = 5;
    private static final int GRID_SPAN_COUNT = 3;
    private MyExtAdapter mAdapter;
    private final List<ItemVM> mData = new ArrayList<>();
    private boolean mIsLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_recycler_view_ext);
        mBinding.setActivity(this);
        mAdapter = new MyExtAdapter(mData);
        mAdapter.setOnItemClickListener(new MyExtAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ItemVM itemVM) {
                Toast.makeText(RecyclerViewExtActivity.this, "Clicked item: " + itemVM.getStr(), Toast.LENGTH_SHORT).show();
            }
        });
        mBinding.layoutRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RecyclerView.LayoutManager layoutManager = null;
                switch (checkedId) {
                    case R.id.linearLayoutRadio:
                        layoutManager = new LinearLayoutManager(RecyclerViewExtActivity.this, LinearLayoutManager.VERTICAL, false);
                        break;
                    case R.id.gridLayoutRadio:
                        layoutManager = new GridLayoutManager(RecyclerViewExtActivity.this, GRID_SPAN_COUNT);
                        ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                if(MyExtAdapter.TYPE_FOOTER == mAdapter.getItemViewType(position)) {
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
        mBinding.recyclerView.setOnExtScrollListener(new RecyclerViewExt.OnExtScrollListener() {
            @Override
            public void onScrollUp() {
                mAdapter.setDefaultFooterView();
                mAdapter.setCustomFooterView(R.layout.sample_footer_b);
            }
            @Override
            public void onLastItemVisible(int lastPosition) {
                loadData(mData.size());
            }
        });
        mBinding.swipeRefreshLayout.setColorSchemeResources(SampleConstant.SWIPE_REFRESHING_COLORS);
        mBinding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(0);
            }
        });
        mBinding.swipeRefreshLayout.setRefreshing(true);
        loadData(0);
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
