package com.app2m.widget.recyclerview;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by CongHao on 2017/10/4.
 * E-mail: hao.cong@app2m.com
 */

public abstract class RecyclerViewExtAdapter<VH extends ViewHolder> extends Adapter {
    public static final int TYPE_FOOTER = -2147483648;
    private int footerLayoutId;

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(footerLayoutId, parent, false);
            return new FooterViewHolder(view);
        } else {
            return onCreateRealViewHolder(parent, viewType);
        }
    }

    public abstract ViewHolder onCreateRealViewHolder(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(ViewHolder holder, int position) {
        if(position < getRealItemCount()) {
            onBindRealViewHolder((VH)holder, position);
        } else if(footerLayoutId > 0 && position + 1 == getItemCount()) {
            onBindFooterViewHolder(holder, position);
        }
    }
    public abstract void onBindRealViewHolder(VH holder, int position);

    protected void onBindFooterViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public final int getItemCount() {
        if(footerLayoutId > 0) {
            return getRealItemCount() == 0 ? 0 : getRealItemCount() + 1;
        } else {
            return getRealItemCount();
        }
    }
    @Override
    public int getItemViewType(int position) {
        if(footerLayoutId > 0) {
            if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                return getRealItemViewType(position);
            }
        } else {
            return getRealItemViewType(position);
        }
    }
    public abstract int getRealItemCount();
    public abstract int getRealItemViewType(int position);

    public void setDefaultFooterView() {
        setCustomFooterView(R.layout.app2m_ext_recycler_view_default_footer);
    }

    public void setCustomFooterView(@LayoutRes int footerLayoutId) {
        if(this.footerLayoutId != footerLayoutId) {
            if(getItemCount() == getRealItemCount() + 1) {
                this.footerLayoutId = footerLayoutId;
                notifyItemChanged(getItemCount()-1);
            } else if(getRealItemCount() > 0) {
                this.footerLayoutId = footerLayoutId;
                notifyItemInserted(getItemCount() - 1);
            }
        }
    }
    public void removeFooterView() {
        if(footerLayoutId > 0) {
            footerLayoutId = 0;
            notifyItemRemoved(getItemCount());
        }
    }
    public boolean hasFooterView() {
        return footerLayoutId > 0;
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder, holder.getLayoutPosition());
        }
    }
    private boolean isStaggeredGridLayout(ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return true;
        }
        return false;
    }
    protected void handleLayoutIfStaggeredGridLayout(ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FOOTER) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            p.setFullSpan(true);
        } else {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            p.setFullSpan(false);
        }
    }
    static class FooterViewHolder extends ViewHolder {
        public FooterViewHolder(View view) {
            super(view);
        }
    }
}
