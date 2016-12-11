package com.toxicant.hua.zuimeitag;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hua on 2016.12.11.
 */
public class TAGRecyclerView extends RecyclerView implements View.OnTouchListener {
    private int mOffsetIndex;//偏移量，显示的第一个view的序号 [0,n)
    private int mSelectIndex;//当前选择的真实序号 [0,n)
    private SwitchListener mSwitchListener;

    interface SwitchListener{
        void onSwitch(int realIndex);
    }
    public TAGRecyclerView(Context context) {
        super(context);
        init();
    }

    public TAGRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TAGRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init(){
        mOffsetIndex=0;
        mSelectIndex=0;
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        float x=event.getX();
        final int index= (int) (x/(this.getWidth()/7));
        //Log.i("index","index->"+index);
        switch (action){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                    final int th=this.getHeight()/7;
                    //view突出
                    for (int i=0;i<7;i++){
                        int t=Math.abs(index-i);
                       // Log.i("突出","点击->"+index+"当前相距->"+t+"第一个view:"+count);
                        this.getChildAt(i).setTranslationY(t*th);
                    }
                break;
            case MotionEvent.ACTION_UP:
                //记录位置
                RecyclerView.LayoutManager lm= this.getLayoutManager();
                if (lm instanceof MyLayoutManager){
                    mOffsetIndex = ((MyLayoutManager) lm).firstView;
                    mSelectIndex=mOffsetIndex+index;
                    ((MyLayoutManager) lm).mSelectIndex=mSelectIndex;
                    //Log.i("up","抬起相对位置-"+index+"偏移量-"+mOffsetIndex+"选择的真实序号-"+mSelectIndex);
                }
                if (mSwitchListener!=null){
                    mSwitchListener.onSwitch(mSelectIndex);
                }
                boolean isFirstAnim=true;
                //下落动画
                for (int i=0;i<7;i++){
                    if (i==index){
                        continue;
                    }
                    View temp=this.getChildAt(i);
                    ObjectAnimator anim=ObjectAnimator//
                            .ofFloat(temp, "TranslationY", temp.getTranslationY(),this.getHeight()/7*6)//
                            .setDuration(500);
                            anim.start();
                    if (isFirstAnim){
                        anim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                //位移动画
                                scrollItem(index);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        isFirstAnim=false;
                    }

                }

                break;
        }
        return true;
    }

    /**
     * 把这个item放到中间,是相对序号
     * @param index 对应屏幕上的第几个[0,n)
     */
    private void scrollItem(int index){
        int dx=0;
        final int tw=TAGRecyclerView.this.getWidth()/7;
        if (index<3){
            dx=-tw*(3-index);
        }else if(index>3){
            dx=tw*(index-3);
        }
        TAGRecyclerView.this.smoothScrollBy(dx,0);
    }

    /**
     * 切换到下一个item
     */
    public boolean selectNextItem(){
        if (mSelectIndex>=TAGRecyclerView.this.getAdapter().getItemCount()-1){//已经是最后一个
            return false;
        }
        //当前选择的item下落
        final RecyclerView.LayoutManager lm= this.getLayoutManager();
        if (lm instanceof MyLayoutManager) {
            mOffsetIndex = ((MyLayoutManager) lm).firstView;
            View view=this.getChildAt(mSelectIndex-mOffsetIndex);
            final ObjectAnimator anim=ObjectAnimator.ofFloat(view, "TranslationY", view.getTranslationY(),this.getHeight()/7*6)
                    .setDuration(200);
                   anim.start();
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //下一个item升起
                    mSelectIndex+=1;
                    ((MyLayoutManager) lm).mSelectIndex=mSelectIndex;
                    View view=TAGRecyclerView.this.getChildAt(mSelectIndex-mOffsetIndex);
                    ObjectAnimator animR=ObjectAnimator.ofFloat(view, "TranslationY", view.getTranslationY(),0)
                            .setDuration(200);
                    animR.start();
                    animR.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            scrollItem(mSelectIndex-mOffsetIndex);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return true;
    }

    /**
     * 切换到上一个item
     */
    public boolean selectPreviousItem(){
        if (mSelectIndex<1){//已经是第一个
            return false;
        }
        //当前选择的item下落
        final RecyclerView.LayoutManager lm= this.getLayoutManager();
        if (lm instanceof MyLayoutManager) {
            mOffsetIndex = ((MyLayoutManager) lm).firstView;
            View view=this.getChildAt(mSelectIndex-mOffsetIndex);
            final ObjectAnimator anim=ObjectAnimator.ofFloat(view, "TranslationY", view.getTranslationY(),this.getHeight()/7*6)
                    .setDuration(200);
            anim.start();
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //上一个item升起
                    mSelectIndex-=1;
                    ((MyLayoutManager) lm).mSelectIndex=mSelectIndex;
                    View view=TAGRecyclerView.this.getChildAt(mSelectIndex-mOffsetIndex);
                    ObjectAnimator animR=ObjectAnimator.ofFloat(view, "TranslationY", view.getTranslationY(),0)
                            .setDuration(200);
                    animR.start();
                    animR.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            scrollItem(mSelectIndex-mOffsetIndex);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return true;

    }

    /**
     * 当前选择的item真实序号
     * @return
     */
    public int getSelectIndex(){
        return mSelectIndex;
    }
    public void setSwitchListener(SwitchListener listener){
        this.mSwitchListener=listener;
    }
}
