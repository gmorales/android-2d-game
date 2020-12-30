package com.moralesbatovski.android2dgame

import android.graphics.Bitmap
import android.graphics.Canvas

/**
 * @author Gustavo Morales
 */
class Explosion(
    private val gameSurface: GameSurface,
    image: Bitmap,
    x: Int,
    y: Int
) : GameObject(
    image = image,
    rowCount = ROW_COUNT,
    colCount = COL_COUNT,
    x = x,
    y = y
) {

    private var rowIndex = 0
    private var colIndex = -1

    var isFinish = false
        private set

    fun update() {
        colIndex++

        // Play sound explosion.wav.
        if (colIndex == 0 && rowIndex == 0) {
            gameSurface.playSoundExplosion()
        }

        if (colIndex >= colCount) {
            colIndex = 0
            rowIndex++
            if (rowIndex >= rowCount) {
                isFinish = true
            }
        }
    }

    fun draw(canvas: Canvas) {
        if (!isFinish) {
            val bitmap = createSubImageAt(rowIndex, colIndex)
            canvas.drawBitmap(bitmap, x.toFloat(), y.toFloat(), null)
        }
    }

    companion object {
        const val ROW_COUNT = 5
        const val COL_COUNT = 5
    }
}
