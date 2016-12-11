package com.toxicant.hua.zuimeitag;

import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hua on 2016.12.10.
 */
public class MyLayoutManager extends RecyclerView.LayoutManager {
    public int mSelectIndex=0;
    final int mPadding=3;
    int allWidth=0;
    int offsetScroll=0;
    int firstView=0;//第一个可视view序号
    boolean firstLayout=true;//每次开始布局
    //保存所有的Item的上下左右的偏移量信息
    private SparseArray<Rect> allItemFrames = new SparseArray<>();
    //记录Item是否出现过屏幕且还没有回收。true表示出现过屏幕上，并且还没被回收
    private SparseBooleanArray hasAttachedItems = new SparseBooleanArray();
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int offsetX=mPadding;
        for(int i=0;i<getItemCount();i++){
            View view=recycler.getViewForPosition(i);
           // addView(view);
            measureChildWithMargins(view,0,0);
             int width = getDecoratedMeasuredWidth(view);
            int height = getDecoratedMeasuredHeight(view);
//            layoutDecorated(view,offsetX+mPadding,mPadding,offsetX+width-mPadding,height);

            Rect frame = allItemFrames.get(i);
            if (frame == null) {
                frame = new Rect();
            }
            frame.set(offsetX,mPadding,offsetX+width,height);
            // 将当前的Item的Rect边界数据保存
            allItemFrames.put(i, frame);
            // 由于已经调用了detachAndScrapAttachedViews，因此需要将当前的Item设置为未出现过
            hasAttachedItems.put(i, false);
            offsetX+=(width+mPadding*2);

        }
        allWidth=Math.max(getWidth()/7*getItemCount(),getWidth());
        recyclerAndFillItems(recycler,state);
    }

    @Override
    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        final int widthSpec = getChildMeasureSpec(getWidth(), getWidthMode(),
                0, getWidth()/7-2*mPadding,
                canScrollHorizontally());
        final int heightSpec = getChildMeasureSpec(getHeight(), getHeightMode(),
                0, lp.height,
                canScrollVertically());
            child.measure(widthSpec, heightSpec);

    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       final int position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return MyLayoutManager.this.computeScrollVectorForPosition(position);
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        final int firstChildPos = getPosition(getChildAt(0));
        final int direction = targetPosition < firstChildPos? -1 : 1;

        return new PointF(direction, 0);

    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //先detach掉所有的子View
        detachAndScrapAttachedViews(recycler);
        //实际要滑动的距离
        int travel = dx;
        if (offsetScroll+dx<0){//滑动到最左边
            travel=-offsetScroll;
        }else if(offsetScroll+dx>allWidth-getWidth()){//最右边
            travel=allWidth-getWidth()-offsetScroll;
        }
        offsetScroll+=travel;
        offsetChildrenHorizontal(-travel);
        recyclerAndFillItems(recycler, state);
        return travel;
    }

    void recyclerAndFillItems(RecyclerView.Recycler recycler, RecyclerView.State state){
        if (state.isPreLayout()) { // 跳过preLayout，preLayout主要用于支持动画
            return;
        }

        // 当前scroll offset状态下的显示区域
        Rect displayFrame = new Rect(offsetScroll,0, getWidth()+offsetScroll, getHeight());
       // Log.e("display","left->"+displayFrame.left+"right->"+displayFrame.right);
        //回收
        Rect childFrame = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            childFrame.left = getDecoratedLeft(child);
            childFrame.top = getDecoratedTop(child);
            childFrame.right = getDecoratedRight(child);
            childFrame.bottom = getDecoratedBottom(child);
           // Log.e("回收判断","left->"+childFrame.left+"right->"+childFrame.right);
            //如果Item没有在显示区域，就说明需要回收
            if (!Rect.intersects(displayFrame, childFrame)) {
                //回收掉滑出屏幕的View
                removeAndRecycleView(child, recycler);
            }
        }
        //显示
        firstLayout=true;
        for (int i = 0; i < getItemCount(); i++) {
            if (Rect.intersects(displayFrame, allItemFrames.get(i))) {
               // Log.e("layout","显示==>"+i);
                if(firstLayout){
                    firstView=i;
                    firstLayout=false;
                }
                View scrap = recycler.getViewForPosition(i);
                measureChildWithMargins(scrap, 0, 0);
                int height = getDecoratedMeasuredHeight(scrap);
                if (i!=mSelectIndex){
                    scrap.setTranslationY(height/7*6);
                }
                addView(scrap);
                Rect frame = allItemFrames.get(i);
              //  Log.e("显示判断","left->"+frame.left+"right->"+frame.right);
                //将这个item布局出来
                layoutDecorated(scrap,
                        frame.left-offsetScroll,
                        frame.top,
                        frame.right-offsetScroll,
                        frame.bottom);

            }
        }

    }
}

