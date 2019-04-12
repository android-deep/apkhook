package ma.mhy.apkhook.view;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.view
 * 作者 mahongyin
 * 时间 2019/4/7 17:46
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

public class DZScrollView extends ScrollView {
    private View inner;
    private boolean isCount = false;
    private Rect normal = new Rect();
    private float y;

    public DZScrollView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public void animation() {
        Animation translateAnimation = new TranslateAnimation(0.0F, 0.0F, this.inner.getTop(), this.normal.top);
        translateAnimation.setDuration((long) 200);
        this.inner.startAnimation(translateAnimation);
        this.inner.layout(this.normal.left, this.normal.top, this.normal.right, this.normal.bottom);
        this.normal.setEmpty();
    }

    public void commOnTouchEvent(MotionEvent paramMotionEvent) {
        int i = 0;
        switch (paramMotionEvent.getAction()) {

            case 1:

                if (isNeedAnimation()) {
                    animation();
                    this.isCount = false;
                    return;
                }
                return;
            case 2:
                float f = this.y;
                float y = paramMotionEvent.getY();
                int i2 = (int) (f - y);
                if (this.isCount) {
                    i = i2;
                }
                this.y = y;
        if (isNeedMove()) {
            if (this.normal.isEmpty()) {
                this.normal.set(this.inner.getLeft(), this.inner.getTop(), this.inner.getRight(), this.inner.getBottom());
               }
                this.inner.layout(this.inner.getLeft(), this.inner.getTop() - i / 2, this.inner.getRight(), this.inner.getBottom() - i / 2);
        }
            this.isCount = true;
            return;
            default:
                return;
        }
    }

    public boolean isNeedAnimation() {
//        boolean bool = true;
//        if (this.normal.isEmpty()) {
//        bool = false;
//        }
       // return bool;//
        return !this.normal.isEmpty();
    }

//    public boolean isNeedMove() {
//        boolean bool = false;
//        int i = this.inner.getMeasuredHeight()-getHeight();
//        int k = getScrollY();
//        if ((k != 0) && (k != i )) {
//        bool = true;
//        }
//        return bool;
//    }
    public boolean isNeedMove() {
        int measuredHeight = this.inner.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        return scrollY == 0 || scrollY == measuredHeight;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            this.inner = getChildAt(0);
        }
    }

    @Override
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if (this.inner != null) {
            commOnTouchEvent(paramMotionEvent);
        }
        return super.onTouchEvent(paramMotionEvent);
    }
}

