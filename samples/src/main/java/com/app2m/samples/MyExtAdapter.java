package com.app2m.samples;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app2m.samples.BR;
import com.app2m.samples.R;
import com.app2m.widget.recyclerview.RecyclerViewExtAdapter;

import java.util.List;

/**
 * Created by CongHao on 2017/10/4.
 * E-mail: hao.cong@app2m.com
 */

public class MyExtAdapter extends RecyclerViewExtAdapter<MyExtAdapter.ItemViewHolder> {
    private final List<ItemVM> mData;

    public MyExtAdapter(List<ItemVM> data) {
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateRealViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ext_adapter_item, parent, false);
        //添加监听器
        binding.setVariable(BR.adapter, this);
        ItemViewHolder holder = new ItemViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindRealViewHolder(ItemViewHolder holder, int position) {
        holder.binding.setVariable(BR.item, mData.get(position));
    }

    @Override
    public int getRealItemCount() {
        return mData.size();
    }

    @Override
    public int getRealItemViewType(int position) {
        return 0;
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

}
