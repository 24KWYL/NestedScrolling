package com.wyl.nestedscrolling

import android.content.Context
import android.support.v4.view.NestedScrollingChild2
import android.support.v4.view.NestedScrollingChildHelper
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.webkit.WebView
import android.widget.Scroller

class NestedScrollingWebView(context: Context?, attrs: AttributeSet?) : WebView(context, attrs), NestedScrollingChild2 {

    private val mScrollConsumed = IntArray(2)
    private var mLastY: Int = 0
    private var mFirstY: Int = 0
    private var mIsSelfFling: Boolean = false
    private var mHasFling: Boolean = false
    private val TOUCH_SLOP: Int by lazy { ViewConfiguration.get(context).scaledTouchSlop }
    private val mMaximumVelocity: Int by lazy { ViewConfiguration.get(context).scaledMaximumFlingVelocity }
    private var mWebViewContentHeight: Int = 0
    private val DENSITY: Float by lazy { context!!.resources.displayMetrics.density }
    private var mMaxScrollY: Int = 0

    private val mChildHelper: NestedScrollingChildHelper by lazy {
        NestedScrollingChildHelper(this)
    }
    private val mScroller: Scroller by lazy { Scroller(context) }
    private var mVelocityTracker: VelocityTracker? = null

    init {
        isNestedScrollingEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mWebViewContentHeight = 0
                mLastY = event.rawY.toInt()
                mFirstY = mLastY
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                }
                initOrResetVelocityTracker()
                mIsSelfFling = false
                mHasFling = false
                mMaxScrollY = getWebViewContentHeight() - height
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                initVelocityTrackerIfNotExists()
                mVelocityTracker?.addMovement(event)
                var y: Int = event.rawY.toInt()
                var dy: Int = y - mLastY
                mLastY = y
                parent.requestDisallowInterceptTouchEvent(true)
                if (!dispatchNestedPreScroll(0, -dy, mScrollConsumed, null)) {
                    scrollBy(0, -dy)
                }
                if (Math.abs(mFirstY - y) > TOUCH_SLOP) {
                    event.action = MotionEvent.ACTION_CANCEL
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isParentResetScroll() && mVelocityTracker != null) {
                    mVelocityTracker?.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                    var yVelocity = -mVelocityTracker?.yVelocity?.toInt()!!
                    recycleVelocityTracker()
                    mIsSelfFling = true
                    flingScroll(0, yVelocity)
                }
            }
        }
        super.onTouchEvent(event)
        return true
    }

    private fun canScrollDown(): Boolean {
        val range: Int = getWebViewContentHeight() - height
        if (range <= 0) {
            return false
        }
        val offset = scrollY
        return offset < range - TOUCH_SLOP
    }

    override fun flingScroll(vx: Int, vy: Int) {
        mScroller.fling(0, scrollY, 0, vy, 0, 0, Int.MIN_VALUE, Int.MAX_VALUE)
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        recycleVelocityTracker()
        stopScroll()
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            var currY: Int = mScroller.currY
            if (!mIsSelfFling) {
                scrollTo(0, currY)
                invalidate()
                return
            }
            if (isWebViewCanScroll()) {
                scrollTo(0, currY)
                invalidate()
            }
            if (!mHasFling && mScroller.startX < currY && !canScrollDown() &&
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL) && dispatchNestedPreFling(0F, mScroller.currVelocity)
            ) {
                mHasFling = true
                dispatchNestedFling(0F, mScroller.currVelocity, false)
            }
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        var mY = y
        if (mY < 0) {
            mY = 0
        }
        if (mMaxScrollY != 0 && mY > mMaxScrollY) {
            mY = mMaxScrollY
        }
        if (isParentResetScroll()) {
            super.scrollTo(x, mY)
        }
    }

    private fun scrollToBottom() {
        var y: Int = getWebViewContentHeight()
        super.scrollTo(0, y - height)
    }

    private fun initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        } else {
            mVelocityTracker!!.clear()
        }
    }

    private fun initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    private fun isParentResetScroll(): Boolean {
        if (parent is NestedScrollingContainer) {
            return (parent as NestedScrollingContainer).scrollY == 0
        }
        return true
    }

    private fun stopScroll() {
        if (!mScroller.isFinished) {
            mScroller.abortAnimation()
        }
    }

    private fun getWebViewContentHeight(): Int {
        if (mWebViewContentHeight == 0) {
            mWebViewContentHeight = (contentHeight * DENSITY).toInt()
        }
        return mWebViewContentHeight
    }

    private fun isWebViewCanScroll(): Boolean {
        return getWebViewContentHeight() > height
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return mChildHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        return mChildHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return mChildHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }
}