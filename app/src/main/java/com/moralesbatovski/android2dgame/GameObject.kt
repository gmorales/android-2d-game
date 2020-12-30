package com.moralesbatovski.android2dgame

import android.graphics.Bitmap

/**
 * @author Gustavo Morales
 */
abstract class GameObject(
    protected var image: Bitmap,
    protected val rowCount: Int,
    protected val colCount: Int,
    var x: Int,
    var y: Int
) {
    protected val WIDTH: Int
    protected val HEIGHT: Int
    val width: Int
    val height: Int

    init {
        WIDTH = image.width
        HEIGHT = image.height
        width = WIDTH / colCount
        height = HEIGHT / rowCount
    }

    protected fun createSubImageAt(row: Int, col: Int): Bitmap {
        // createBitmap(bitmap, x, y, width, height).
        return Bitmap.createBitmap(image, col * width, row * height, width, height)
    }
}
