package com.funnywolf.hollowkit.view.scroll.behavior

import android.view.MotionEvent
import android.view.View
import android.widget.Space
import android.widget.FrameLayout.LayoutParams
import androidx.core.view.ViewCompat
import com.funnywolf.hollowkit.utils.isUnder

/**
 * 底部浮层的 [NestedScrollBehavior]
 *
 * @author https://github.com/funnywolfdadada
 * @since 2020/5/17
 */
class BottomSheetBehavior(
    /**
     * 浮层的内容视图
     */
    contentView: View,
    /**
     * 初始位置，最低高度 [POSITION_MIN]、中间高度 [POSITION_MID] 或最大高度 [POSITION_MAX]
     */
    private val initPosition: Int,
    /**
     * 内容视图的最低显示高度
     */
    private val minHeight: Int,
    /**
     * 内容视图中间停留的显示高度，默认等于最低高度
     */
    private val midHeight: Int = minHeight
): NestedScrollBehavior {
    override val scrollVertical: Boolean = true
    override val prevView: View? = Space(contentView.context).also {
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        lp.topMargin = minHeight
        it.layoutParams = lp
    }

    override val prevScrollTarget: NestedScrollTarget? = null
    override val midView: View = contentView
    override val midScrollTarget: NestedScrollTarget? = null
    override val nextView: View? = null
    override val nextScrollTarget: NestedScrollTarget? = null

    private var midScroll = 0
    private var firstLayout = true

    override fun afterLayout(v: BehavioralScrollView) {
        midScroll = v.minScroll + midHeight - minHeight
        if (firstLayout) {
            firstLayout = false
            v.scrollTo(
                v.scrollX,
                when (initPosition) {
                    POSITION_MIN -> v.minScroll
                    POSITION_MAX -> v.maxScroll
                    else -> midScroll
                }
            )
        }
    }

    override fun handleDispatchTouchEvent(
        v: BehavioralScrollView,
        e: MotionEvent
    ): Boolean? {
        if ((e.action == MotionEvent.ACTION_CANCEL || e.action == MotionEvent.ACTION_UP)
            && v.scrollY != 0
            && v.lastScrollDir != 0) {
            v.smoothScrollTo(
                if (v.scrollY > midScroll) {
                    if (v.lastScrollDir > 0) {
                        v.maxScroll
                    } else {
                        midScroll
                    }
                } else {
                    if (v.lastScrollDir > 0) {
                        midScroll
                    } else {
                        v.minScroll
                    }
                }
            )
            return true
        }
        return super.handleDispatchTouchEvent(v, e)
    }

    override fun handleInterceptTouchEvent(
        v: BehavioralScrollView,
        e: MotionEvent
    ): Boolean? {
        return if (prevView?.isUnder(e.rawX, e.rawY) != true) {
            null
        } else {
            false
        }
    }

    override fun handleTouchEvent(v: BehavioralScrollView, e: MotionEvent): Boolean? {
        return if (prevView?.isUnder(e.rawX, e.rawY) != true) {
            null
        } else {
            false
        }
    }

    override fun scrollSelfFirst(
        v: BehavioralScrollView,
        scroll: Int,
        @ViewCompat.NestedScrollType type: Int
    ): Boolean {
        return v.scrollY != 0
    }

    override fun handleScrollSelf(
        v: BehavioralScrollView,
        scroll: Int,
        @ViewCompat.NestedScrollType type: Int
    ): Boolean {
        return v.isFling
    }

    companion object {
        const val POSITION_MIN = 1
        const val POSITION_MID = 2
        const val POSITION_MAX = 3
    }
}