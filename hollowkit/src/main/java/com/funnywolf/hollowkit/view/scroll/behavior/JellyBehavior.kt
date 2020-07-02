package com.funnywolf.hollowkit.view.scroll.behavior

import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat

/**
 * 弹性滚动
 *
 * @author https://github.com/funnywolfdadada
 * @since 2020/6/13
 */
class JellyBehavior(
    /**
     * 当前的可滚动方向
     */
    override val scrollVertical: Boolean,
    /**
     * 内容视图
     */
    override val midView: View,
    /**
     * 内容的前一个视图，水平方向在左边，垂直方向在上边
     */
    override val prevView: View? = null,
    /**
     * 内容的后一个视图，水平方向在右边，垂直方向在下边
     */
    override val nextView: View? = null
) : NestedScrollBehavior {

    private fun selfScrolled(v: BehavioralScrollView) = if (scrollVertical) {
        v.scrollY != 0
    } else {
        v.scrollX != 0
    }

    override fun handleDispatchTouchEvent(
        v: BehavioralScrollView,
        e: MotionEvent
    ): Boolean? {
        if (selfScrolled(v) && (e.action == MotionEvent.ACTION_CANCEL || e.action == MotionEvent.ACTION_UP)) {
            v.smoothScrollTo(0)
            return true
        }
        return super.handleDispatchTouchEvent(v, e)
    }

    override fun scrollSelfFirst(v: BehavioralScrollView, scroll: Int, type: Int): Boolean {
        return selfScrolled(v)
    }

    override fun handleScrollSelf(v: BehavioralScrollView, scroll: Int, type: Int): Boolean? {
        return when {
            type == ViewCompat.TYPE_NON_TOUCH && v.state != NestedScrollState.ANIMATION -> false
            type == ViewCompat.TYPE_TOUCH -> {
                if (scrollVertical) {
                    val s = if ((v.scrollY < 0 && scroll < 0) || (v.scrollY > 0 && scroll > 0)) {
                        scroll / 2
                    } else {
                        scroll
                    }
                    v.scrollBy(0, s)
                } else {
                    val s = if ((v.scrollX < 0 && scroll < 0) || (v.scrollX > 0 && scroll > 0)) {
                        scroll / 2
                    } else {
                        scroll
                    }
                    v.scrollBy(s, 0)
                }
                true
            }
            else -> null
        }
    }

}
