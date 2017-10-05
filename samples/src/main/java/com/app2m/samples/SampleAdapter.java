package com.app2m.samples;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app2m.samples.BR;
import com.app2m.samples.R;

import java.util.List;

/**
 * Created by CongHao on 2017/10/2.
 * E-mail: hao.cong@app2m.com
 */

public class SampleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    public static final int TYPE_FOOTER = 1;
    private final List<ItemVM> mData;
    private int footerLayoutId;
    private Context mContext;

    public SampleAdapter(Context context, List<ItemVM> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.sample_item, parent, false);
            //添加监听器
            binding.setVariable(BR.adapter, this);
            ItemViewHolder holder = new ItemViewHolder(binding.getRoot());
            holder.setBinding(binding);
            return holder;
        } else if(viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(footerLayoutId, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position < mData.size() && holder instanceof ItemViewHolder) {
            ((ItemViewHolder)holder).binding.setVariable(BR.item, mData.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(footerLayoutId > 0) {
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        } else {
            return TYPE_ITEM;
        }
    }

    public void setDefaultFooterView() {
        setCustomFooterView(R.layout.sample_footer);
    }

    public void setCustomFooterView(@LayoutRes int footerLayoutId) {
        if(this.footerLayoutId != footerLayoutId) {
            this.footerLayoutId = footerLayoutId;
            if(!mData.isEmpty()) {
                notifyItemInserted(getItemCount());
            }
        }
    }



    public void removeFooterView() {
        if(footerLayoutId > 0) {
            footerLayoutId = 0;
            notifyItemRemoved(getItemCount());
        }
    }

    @Override
    public int getItemCount() {
        if(footerLayoutId > 0) {
            return mData.isEmpty() ? 0 : mData.size() + 1;
        } else {
            return mData.size();
        }
    }

    public int getRealItemCount() {
        return mData.size();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder, holder.getLayoutPosition());
        }
    }
    private boolean isStaggeredGridLayout(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return true;
        }
        return false;
    }
    protected void handleLayoutIfStaggeredGridLayout(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            p.setFullSpan(true);
        } else {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            p.setFullSpan(false);
        }
    }

    public void onItemClick(View view, ItemVM itemVM) {
        if(onItemClickListener != null) {
            onItemClickListener.onItemClick(view, itemVM);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view, ItemVM itemVM);
    }
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        public ItemViewHolder(View itemView) {
            super(itemView);
        }
        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }
        public ViewDataBinding getBinding() {
            return this.binding;
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View view) {
            super(view);
        }
    }
}
