package com.wyl.nestedscrolling

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val dataList: ArrayList<Data> by lazy {
        var datas: ArrayList<Data> = ArrayList()
        for (i in 0..20) {
            datas.add(Data())
        }
        datas
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nestedScrollingWebView.loadUrl("https://github.com/24KWYL")
        initData()
    }

    private fun initData() {
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv.adapter = RecyclerViewAdapter(this, dataList)
    }
}
