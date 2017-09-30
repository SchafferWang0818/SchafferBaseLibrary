package com.schaffer.base.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by AndroidSchaffer on 2017/9/6.
 */

public class DefinedRecyclerView extends RecyclerView {


    public DefinedRecyclerView(Context context) {
        this(context, null);
    }

    public DefinedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefinedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 在RecyclerView存在数据,静止状态并且是最后一项时,是加载数据的最好状态
     * {@link RecyclerView#canScrollVertically(int)}向上滑为1,向下滑为-1.返回true表示可以滑动,返回false表示不可滑动
     *
     * @return 是否处于RecyclerView的底部
     */
    public boolean isBottomVisibleLinear() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int state = getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == SCROLL_STATE_IDLE && !canScrollVertically(1)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 瀑布流.
     * 在RecyclerView存在数据,静止状态并且是最后一项时,是加载数据的最好状态
     * {@link RecyclerView#canScrollVertically(int)}向上滑为1,向下滑为-1.返回true表示可以滑动,返回false表示不可滑动
     *
     * @return 是否处于RecyclerView的底部
     */
    public boolean isBottomVisibleStaggered() {
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
        int lastVisibleItemPosition = getMaxElem(layoutManager.findLastCompletelyVisibleItemPositions(new int[layoutManager.getSpanCount()]));
        int totalItemCount = layoutManager.getItemCount();
        int state = getScrollState();
        if (lastVisibleItemPosition == (totalItemCount - 1) && state == SCROLL_STATE_IDLE && !canScrollVertically(1)) {
            return true;
        } else {
            return false;
        }
    }

    private static int getMaxElem(int[] arr) {
        int size = arr.length;
        int maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            if (arr[i] > maxVal)
                maxVal = arr[i];
        }
        return maxVal;
    }

}
