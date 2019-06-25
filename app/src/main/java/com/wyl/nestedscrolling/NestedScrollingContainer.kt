package com.wyl.nestedscrolling

import android.content.Context
import android.support.v4.view.NestedScrollingParent2
import android.support.v4.view.NestedScrollingParentHelper
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Scroller


/**
 * 嵌套父布局
 */
class NestedScrollingContainer(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs),
    NestedScrollingParent2 {

    private var mIsBeingDragged: Boolean = false//开始拖拽

    private var mLastY: Float = 0F
    private var mLastMotionY: Float = 0F

    private var nestedScrollView: NestedScrollView? = null//scrollView
    private var nestedScrollingWebView: NestedScrollingWebView? = null//webView
    private var iv: ImageView? = null//ImageView
    private var ll: LinearLayout? = null//LinearLayout
    private var rv: RecyclerView? = null//RecyclerView

    private lateinit var viewList: ArrayList<View>

    override fun onFinishInflate() {
        super.onFinishInflate()
        nestedScrollView = findViewById(R.id.nestedScrollingView)
        nestedScrollingWebView = findViewById(R.id.nestedScrollingWebView)
        iv = findViewById(R.id.iv)
        ll = findViewById(R.id.ll)
        rv = findViewById(R.id.rv)
        viewList = ArrayList()
        viewList.add(nestedScrollView!!)
        viewList.add(nestedScrollingWebView!!)
        viewList.add(rv!!)
    }

    private val mScroller: Scroller by lazy { Scroller(getContext()) }//控制滑动Scroller

    private val mParentHelper: NestedScrollingParentHelper by lazy {
        NestedScrollingParentHelper(this)
    }//嵌套滚动Helper

    private val mVelocityTracker: VelocityTracker by lazy {
        VelocityTracker.obtain()
    }//速度监听器

    private val TOUCH_SLOP: Int by lazy {
        ViewConfiguration.get(getContext()).scaledDoubleTapSlop
    }//最小响应滑动位移

    private val mMaximumVelocity: Int by lazy {
        ViewConfiguration.get(getContext()).scaledMaximumFlingVelocity
    }//最大惯性速度

    private var mInnerScrollHeight: Int = 0//布局可滑动最大距离

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        for (i in 0 until childCount) {
            var view: View = getChildAt(i)
            mInnerScrollHeight += view.measuredHeight
        }
        mInnerScrollHeight -= measuredHeight
    }

    override fun computeScroll() {

    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val action = ev?.action ?: return false
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_MOVE -> {
                //判断滑动的是否是不可以滑动的View，截获MOVE事件
                val y: Float = ev.y
                val yDiff = Math.abs(y - mLastMotionY)
                var isMoveNestedView = isMoveNestedView(ev.rawX, ev.rawY)
                if (yDiff > TOUCH_SLOP && !isMoveNestedView) {
                    mIsBeingDragged = true
                    mLastY = y
                }
            }
            MotionEvent.ACTION_DOWN -> {
                mLastMotionY = ev.y
                mIsBeingDragged = false
            }
            MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
            }
        }
        return mIsBeingDragged
    }

    /**
     * 判断当前手指move的View类型
     */
    private fun isMoveNestedView(x: Float, y: Float): Boolean {
        for (view in viewList) {
            if (view.visibility == View.GONE) {
                continue
            }
            var location = IntArray(2)
            view.getLocationOnScreen(location)
            var left: Int = location[0]
            var top: Int = location[1]
            var right: Int = left + view.measuredWidth
            var bottom: Int = top + view.measuredHeight
            if (y >= top && y <= bottom && x >= left && x <= right) {
                return true
            }
        }
        return false
    }

    /****** NestedScrollingParent2 BEGIN ******/

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun getNestedScrollAxes(): Int {
        return mParentHelper.nestedScrollAxes
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        mParentHelper.onStopNestedScroll(target)
    }


    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {

    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        pdxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return false
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }


}