package com.wyl.nestedscrolling

import android.content.Context
import android.support.v4.view.NestedScrollingParent2
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class NestedScrollingContainer(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs), NestedScrollingParent2 {
    override fun onNestedPreScroll(p0: View, p1: Int, p2: Int, p3: IntArray, p4: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStopNestedScroll(p0: View, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartNestedScroll(p0: View, p1: View, p2: Int, p3: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNestedScrollAccepted(p0: View, p1: View, p2: Int, p3: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNestedScroll(p0: View, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}