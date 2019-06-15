package com.wyl.nestedscrolling

import android.content.Context
import android.support.v4.view.NestedScrollingChild2
import android.util.AttributeSet
import android.webkit.WebView

class NestedScrollingWebView(context: Context?, attrs: AttributeSet?) : WebView(context, attrs),NestedScrollingChild2{
    override fun startNestedScroll(p0: Int, p1: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dispatchNestedPreScroll(p0: Int, p1: Int, p2: IntArray?, p3: IntArray?, p4: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stopNestedScroll(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasNestedScrollingParent(p0: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun dispatchNestedScroll(p0: Int, p1: Int, p2: Int, p3: Int, p4: IntArray?, p5: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}