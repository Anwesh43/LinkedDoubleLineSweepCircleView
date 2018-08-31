package com.anwesh.uiprojects.linkeddoublecircsweepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.doublelinecircsweepview.DoubleLineCircSweepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DoubleLineCircSweepView.create(this)
    }
}
