package com.schaffer.base.common.recycler;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.schaffer.base.common.base.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by AndroidSchaffer on 2017/9/19.
 */

public abstract class HeaderFooterRecyclerAdapter<T> extends BaseRecyclerAdapter<T> {

    protected View headerView;
    protected View footerView;
    protected int headerResId;
    protected int footerResId;
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_FOOTER = 2;

    public HeaderFooterRecyclerAdapter(Context context, List<T> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public View getHeaderView() {
        return headerView;
    }

    private void setHeaderView(View headerView, @LayoutRes int resId) {
        if (resId <= 0 || headerView == null) {
            try {
                throw new RuntimeException("headerView can not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.headerView = headerView;
        headerResId = resId;
//      notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public void setHeaderView(@LayoutRes int resId) {
        setHeaderView(View.inflate(mContext, resId, null), resId);
    }

    public View getFooterView() {
        return footerView;
    }

    private void setFooterView(View footerView, @LayoutRes int resId) {
        if (resId <= 0 || footerView == null) {
            try {
                throw new RuntimeException("footerView can not be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.footerView = footerView;
        footerResId = resId;
        notifyItemInserted(getItemCount() - 2);
//        notifyDataSetChanged();
    }

    public void setFooterView(@LayoutRes int resId) {
        setFooterView(View.inflate(mContext, resId, null), resId);
    }

    public void removeHeaderView() {
        if (headerResId <= 0 && headerView == null) return;
        headerResId = 0;
        headerView = null;
//        notifyItemRemoved(0);
        notifyDataSetChanged();
    }

    public void removeFooterView() {
        if (footerResId <= 0 && footerView == null) return;
        footerResId = 0;
        footerView = null;
//        notifyItemRemoved(0);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (headerView == null && footerView == null) {
            return TYPE_NORMAL;
        }
        if (headerView != null && position == 0) {
            return TYPE_HEADER;
        } else if (footerView != null && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (headerView == null && footerView == null) {
            return mDatas.size();
        } else if (headerView == null && footerView != null) {
            return mDatas.size() + 1;
        } else if (headerView != null && footerView == null) {
            return mDatas.size() + 1;
        } else {
            return mDatas.size() + 2;
        }
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (headerView != null && viewType == TYPE_HEADER) {
            return RecyclerViewHolder.get(mContext, parent, headerResId);
        }
        if (footerView != null && viewType == TYPE_FOOTER) {
            return RecyclerViewHolder.get(mContext, parent, footerResId);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if (headerView != null && getItemViewType(position) == TYPE_HEADER) {
            convertHeader(holder);
            return;
        }
        if (footerView != null && getItemViewType(position) == TYPE_FOOTER) {
            convertFooter(holder);
            return;
        }
        super.onBindViewHolder(holder, position-1);
    }

    public abstract void convertHeader(RecyclerViewHolder holder);

    public abstract void convertFooter(RecyclerViewHolder holder);


    /**
     * Grid设置头部显示
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * 瀑布流
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams && (getItemViewType(holder.getLayoutPosition()) != TYPE_NORMAL)) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }
}
