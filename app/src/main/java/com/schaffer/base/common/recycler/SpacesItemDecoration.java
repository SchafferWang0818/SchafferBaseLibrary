package com.schaffer.base.common.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by AndroidSchaffer on 2017/8/31.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    private int space_left;
    private int space_right;
    private int space_top;
    private int space_bottom;

    boolean useSingleSpace = false;

    public SpacesItemDecoration(int space) {
        this.space = space;
        useSingleSpace = true;
    }

    public SpacesItemDecoration(int space_left, int space_right, int space_top, int space_bottom) {
        this.space_left = space_left;
        this.space_right = space_right;
        this.space_top = space_top;
        this.space_bottom = space_bottom;
        useSingleSpace = false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        if (useSingleSpace) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
        } else {
            outRect.left = space_left;
            outRect.right = space_right;
            if (parent.getChildPosition(view) == 0) {
                outRect.bottom = space_bottom;
                outRect.top = 0;
            }
            if (parent.getChildPosition(view) == parent.getChildCount() - 1) {
                outRect.top = space_top;
                outRect.bottom = 0;
            }
        }


//        if (parent.getChildPosition(view) == 0)
//            outRect.top = space;
    }
}