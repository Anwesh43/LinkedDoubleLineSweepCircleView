package com.anwesh.uiprojects.linkeddoublecircsweepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.doublelinecircsweepview.DoubleLineCircSweepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : DoubleLineCircSweepView = DoubleLineCircSweepView.create(this)
        fullScreen()
        view.addAnimationListener({createToast("animation is ${it} is complete")}, {createToast("animation number ${it} is reset")})
    }

    private fun createToast(txt : String) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}
