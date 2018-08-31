package com.anwesh.uiprojects.doublelinecircsweepview

/**
 * Created by anweshmishra on 01/09/18.
 */

import android.app.Activity
import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.content.Context
import android.graphics.Color

val nodes : Int = 5

fun Canvas.drawDLCSNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / nodes
    val r : Float = gap / 4
    val index : Int = i % 2
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = Color.parseColor("#1E88E5")
    val sc : Float = Math.min(0.5f, Math.max(0f, scale - 0.5f)) * 2
    save()
    translate(w/2, i * gap)
    for (j in 0..1) {
        save()
        translate(-gap * (1 - 2 * j), 0f)
        drawLine(0f, 0f, 0f, gap * scale, paint)
        restore()
    }
    save()
    translate(-gap * (1 - 2 * index), gap / 2)
    drawArc(RectF(-r, -r, r, r), -90f, 360f * sc, true, paint)
    restore()
    restore()
}

class DoubleLineCircSweepView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += 0.05f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class DLSCNode(var i : Int, val state : State = State()) {

        var prev : DLSCNode? = null
        var next : DLSCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = DLSCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawDLCSNode(i, state.scale, paint)
            prev?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : DLSCNode {
            var curr : DLSCNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class DoubleLineCircSweep(var i : Int) {
        private var curr : DLSCNode = DLSCNode(0)
        private var dir : Int = 1

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }
    }

    data class Renderer(var view : DoubleLineCircSweepView) {

        private val animator : Animator = Animator(view)
        private val dlsc : DoubleLineCircSweep = DoubleLineCircSweep(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            dlsc.draw(canvas, paint)
            animator.animate {
                dlsc.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            dlsc.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : DoubleLineCircSweepView {
            val view : DoubleLineCircSweepView = DoubleLineCircSweepView(activity)
            activity.setContentView(view)
            return view
        }
    }
}