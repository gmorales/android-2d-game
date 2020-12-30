package com.moralesbatovski.android2dgame

import android.graphics.Canvas
import android.view.SurfaceHolder

/**
 * @author Gustavo Morales
 */
class GameThread(
    private val gameSurface: GameSurface,
    private val surfaceHolder: SurfaceHolder
) : Thread() {

    private var running = false

    override fun run() {
        var startTime = System.nanoTime()
        while (running) {
            var canvas: Canvas? = null
            try {
                // Get Canvas from Holder and lock it.
                canvas = surfaceHolder.lockCanvas()

                // Synchronized
                synchronized(canvas) {
                    gameSurface.update()
                    gameSurface.draw(canvas)
                }
            } catch (e: Exception) {
                // Do nothing.
            } finally {
                if (canvas != null) {
                    // Unlock Canvas.
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
            val now = System.nanoTime()
            // Interval to redraw game
            // (Change nanoseconds to milliseconds)
            var waitTime = (now - startTime) / 1000000
            if (waitTime < 10) {
                waitTime = 10 // Millisecond.
            }
            print(" Wait Time=$waitTime")
            try {
                // Sleep.
                sleep(waitTime)
            } catch (e: InterruptedException) {
            }
            startTime = System.nanoTime()
            print(".")
        }
    }

    fun setRunning(running: Boolean) {
        this.running = running
    }
}
