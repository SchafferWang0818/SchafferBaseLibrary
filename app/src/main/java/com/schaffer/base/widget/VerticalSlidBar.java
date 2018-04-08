package com.schaffer.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.schaffer.base.R;

import java.util.ArrayList;

/**
 * 纵向导航,需要绑定RecyclerView,其Adapter需要实现{@link RecyclerAdapterInterface}
 */

public class VerticalSlidBar extends View {

    private ArrayList<String> allSections = new ArrayList<>();
    private Paint paint;
    private int singleH;
    private int viewW;
    private TextView text_connected;
    private RecyclerView recyclerView_connected;
    private RecyclerView.Adapter adapter;
    private boolean move;
    private boolean isScroll;//使用scrollToPosition方法移动为 true

    public VerticalSlidBar(Context context) {
        this(context, null);
    }

    public VerticalSlidBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalSlidBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化标题
        for (int i = 'A'; i < 'Z' + 1; i++) {
            allSections.add((char) i + "");
        }
        allSections.add("#");
        float fontSize = getResources().getDimension(R.dimen.dimen_14dp);
        //创建paint
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(Paint.Align.CENTER);//x方向以居中的位置确定
        paint.setTextSize(fontSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < allSections.size(); i++) {
            canvas.drawText(allSections.get(i), viewW / 2, singleH * (i + 1), paint);
        }
    }

    public void setTextConnected(TextView connect) {
        this.text_connected = connect;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView_connected;
    }

    public void setRecyclerView(RecyclerView connect) {
        this.recyclerView_connected = connect;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        singleH = h / allSections.size();
        viewW = w;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleMove(event);
                break;
            case MotionEvent.ACTION_MOVE:
                handleMove(event);
                break;
            case MotionEvent.ACTION_UP:
                setBackgroundColor(Color.TRANSPARENT);
                if (text_connected != null) {
                    //隐藏悬浮
                    text_connected.setVisibility(GONE);
                }
                break;
        }
        return true;
    }

    //处理手指按下和移动时字体和列表移动
    private void handleMove(MotionEvent event) {
        //显示字体
        float y = event.getY();
        //找到index
        int index = (int) (y / singleH);
        if (index < 0) {
            index = 0;
        } else if (index > allSections.size() - 1) {
            index = allSections.size() - 1;
        }
        //找到当前要显示的字符串
        String s = allSections.get(index);

        if (text_connected != null) {
            text_connected.setText(s);
            text_connected.setVisibility(VISIBLE);
        }

        if (recyclerView_connected == null) return;
        adapter = recyclerView_connected.getAdapter();
        if (adapter == null) return;
        if (!(adapter instanceof RecyclerAdapterInterface)) return;
        //列表的滑动 找到要滑动到的position
        //判断是否有以当前字母开头的联系人(用容器保存所有联系人的首字母)
        ArrayList<String> sections = ((RecyclerAdapterInterface) adapter).getSections();
        int sectionIndex = -1;
        for (int i = 0; i < sections.size(); i++) {
            if (s.equalsIgnoreCase(sections.get(i))) {
                sectionIndex = i;
                break;
            }
        }
        if (sectionIndex == -1) return;
        //如果有以当前字母开头的联系人  根据字母在容器中的位置找到对应的第一个条目的position
        final int position = ((RecyclerAdapterInterface) adapter).getPositionForSection(sectionIndex);
        //找到第一个可见的条目position
        final RecyclerView.LayoutManager layoutManager = recyclerView_connected.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            final int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

            if (position <= firstVisibleItemPosition) {
                //如果position小于第一个可见的条目位置 往上滑 直接滑动到指定的位置
                recyclerView_connected.scrollToPosition(position);
            } else if (position <= lastVisibleItemPosition) {
                //处理屏幕可见
                View childAt = recyclerView_connected.getChildAt(position - firstVisibleItemPosition);
                //当前条目的top
                int top = childAt.getTop();
                recyclerView_connected.smoothScrollBy(0, top);
            } else {
                //另外 先移动到指定位置  再向上移动当前条目的top
                move = true;
                isScroll = false;
                recyclerView_connected.scrollToPosition(position);
                //如果没有移动到可见的位置  需要判断当前条目是否可见
                recyclerView_connected.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    //smoothScrollToPosition时回调
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                            move = false;
                            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
                            int lastItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (position >= firstItem && position <= lastItem) {
                                View childAt = recyclerView_connected.getChildAt(position - firstItem);
                                //当前条目的top
                                int top = childAt.getTop();
                                recyclerView_connected.smoothScrollBy(0, top);
                            }
                        }
                    }

                    //scrollToPosition  smoothScrollToPosition 手动滑动也会回调
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        //只有当是scrollToPosition再进行移动操作
                        if (move && isScroll) {
                            move = false;
                            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
                            int lastItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (position >= firstItem && position <= lastItem) {
                                View childAt = recyclerView_connected.getChildAt(position - firstItem);
                                //当前条目的top
                                int top = childAt.getTop();
                                recyclerView_connected.smoothScrollBy(0, top);
                            }
                        }

                    }
                });

            }
        }

    }


    interface RecyclerAdapterInterface {

        /**
         * 当前RecyclerView item 首字母集合
         *
         * @return item 首字母集合
         */
        ArrayList<String> getSections();

        /**
         * 根据字母在容器中的位置获取对应的第一条的position
         *
         * @param index
         * @return
         */
        int getPositionForSection(int index);
    }
}
