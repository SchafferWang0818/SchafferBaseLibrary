package com.schaffer.base.common.recycler;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by AndroidSchaffer on 2017/9/21.
 */

public abstract class OnRecyclerScrollListener extends RecyclerView.OnScrollListener {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFRESH = 1;
    private static final int STATE_LOAD_MORE = 2;

    private static final int TYPE_LINEARMANAGER = 0;
    private static final int TYPE_GRIDMANAGER = 1;
    private static final int TYPE_STAGGEREDMANAGER = 2;

    private static final int ORIENTATION_VERTICAL = 0;
    private static final int ORIENTATION_HORIZONTAL = 1;

    private int recyclerState = STATE_NORMAL;
    private int recyclerOrientation = ORIENTATION_VERTICAL;
    private int recyclerManagerType = TYPE_LINEARMANAGER;
    private boolean scrollOrientation;

    public void setRecyclerState(int recyclerState) {
        this.recyclerState = recyclerState;
    }

    public void setRecyclerOrientation(int recyclerOrientation) {
        this.recyclerOrientation = recyclerOrientation;
    }

    public void setRecyclerManagerType(int recyclerManagerType) {
        this.recyclerManagerType = recyclerManagerType;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (recyclerOrientation == ORIENTATION_VERTICAL) {
                //vertical
                if (recyclerManagerType == TYPE_LINEARMANAGER) {
                    if (isLinearBottom(recyclerView)) {
                        onLoadMore(recyclerView);
                    }
                } else if (recyclerManagerType == TYPE_GRIDMANAGER) {
                    if (isGridBottom(recyclerView)) {
                        onLoadMore(recyclerView);
                    }
                } else if (recyclerManagerType == TYPE_STAGGEREDMANAGER) {
                    if (isStaggeredBottom(recyclerView)) {
                        onLoadMore(recyclerView);
                    }
                }

            } else {
                //horizontal
                if (recyclerManagerType == TYPE_LINEARMANAGER) {
                    if (isLinearRight(recyclerView)) {
                        onLoadMore(recyclerView);
                    }
                } else if (recyclerManagerType == TYPE_GRIDMANAGER) {
                    if (isGridRight(recyclerView)) {
                        onLoadMore(recyclerView);
                    }
                } else if (recyclerManagerType == TYPE_STAGGEREDMANAGER) {
                    onLoadMore(recyclerView);
                }
            }

        }

    }

    abstract void onLoadMore(RecyclerView recyclerView);

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        scrollOrientation = dy > 0;
    }


    public boolean isLinearBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        return scrollOrientation&&visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && !recyclerView.canScrollVertically(1);
    }

    public boolean isLinearRight(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        return scrollOrientation&&visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && !recyclerView.canScrollHorizontally(1);
    }

    public boolean isGridBottom(RecyclerView recyclerView) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        return scrollOrientation&&visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && !recyclerView.canScrollVertically(1);
    }

    public boolean isGridRight(RecyclerView recyclerView) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        return scrollOrientation&&visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && !recyclerView.canScrollHorizontally(1);
    }

    public boolean isStaggeredBottom(RecyclerView recyclerView) {
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
        int lastVisibleItemPosition = getMaxElem(layoutManager.findLastCompletelyVisibleItemPositions(new int[layoutManager.getSpanCount()]));
        int totalItemCount = layoutManager.getItemCount();
        return scrollOrientation&&lastVisibleItemPosition == (totalItemCount - 1) && !recyclerView.canScrollVertically(1);
    }

    private int getMaxElem(int[] arr) {
        int size = arr.length;
        int maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            if (arr[i] > maxVal)
                maxVal = arr[i];
        }
        return maxVal;
    }


}
