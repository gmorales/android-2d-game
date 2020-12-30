package com.moralesbatovski.android2dgame

import android.graphics.Bitmap
import android.graphics.Canvas
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * @author Gustavo Morales
 */
class Character(
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
    // Row index of Image are being used.
    private var rowUsing = ROW_LEFT_TO_RIGHT
    private var colUsing = 0
    private var leftToRights: Array<Bitmap?> = arrayOfNulls(colCount)
    private var rightToLefts: Array<Bitmap?> = arrayOfNulls(colCount)
    private var topToBottoms: Array<Bitmap?> = arrayOfNulls(colCount)
    private var bottomToTops: Array<Bitmap?> = arrayOfNulls(colCount)
    private var movingVectorX = 10
    private var movingVectorY = 5
    private var lastDrawNanoTime: Long = -1

    init {
        for (col in 0 until colCount) {
            topToBottoms[col] = createSubImageAt(ROW_TOP_TO_BOTTOM, col) // 3
            rightToLefts[col] = createSubImageAt(ROW_RIGHT_TO_LEFT, col) // 3
            leftToRights[col] = createSubImageAt(ROW_LEFT_TO_RIGHT, col) // 3
            bottomToTops[col] = createSubImageAt(ROW_BOTTOM_TO_TOP, col) // 3
        }
    }

    val moveBitmaps: Array<Bitmap?>
        get() = when (rowUsing) {
            ROW_BOTTOM_TO_TOP -> bottomToTops
            ROW_LEFT_TO_RIGHT -> leftToRights
            ROW_RIGHT_TO_LEFT -> rightToLefts
            else -> topToBottoms
        }

    val currentMoveBitmap: Bitmap?
        get() {
            val bitmaps = moveBitmaps
            return bitmaps[colUsing]
        }

    fun update() {
        colUsing++
        if (colUsing >= colCount) {
            colUsing = 0
        }
        // Current time in nanoseconds
        val now = System.nanoTime()

        // Never once did draw.
        if (lastDrawNanoTime == -1L) {
            lastDrawNanoTime = now
        }
        // Change nanoseconds to milliseconds (1 millisecond = 1000000 nanoseconds).
        val deltaTime = ((now - lastDrawNanoTime) / 1000000).toInt()

        // Distance moves
        val distance = VELOCITY * deltaTime
        val movingVectorLength =
            sqrt((movingVectorX * movingVectorX + movingVectorY * movingVectorY).toDouble())

        // Calculate the new position of the game character.
        x += (distance * movingVectorX / movingVectorLength).toInt()
        y += (distance * movingVectorY / movingVectorLength).toInt()

        // When the game's character touches the edge of the screen, then change direction
        if (x < 0) {
            x = 0
            movingVectorX = -movingVectorX
        } else if (x > gameSurface.width - width) {
            x = gameSurface.width - width
            movingVectorX = -movingVectorX
        }
        if (y < 0) {
            y = 0
            movingVectorY = -movingVectorY
        } else if (y > gameSurface.height - height) {
            y = gameSurface.height - height
            movingVectorY = -movingVectorY
        }

        // rowUsing
        rowUsing = if (movingVectorX > 0) {
            when {
                movingVectorY > 0 && abs(movingVectorX) < abs(movingVectorY) -> ROW_TOP_TO_BOTTOM
                movingVectorY < 0 && abs(movingVectorX) < abs(movingVectorY) -> ROW_BOTTOM_TO_TOP
                else -> ROW_LEFT_TO_RIGHT
            }
        } else {
            when {
                movingVectorY > 0 && abs(movingVectorX) < abs(movingVectorY) -> ROW_TOP_TO_BOTTOM
                movingVectorY < 0 && abs(movingVectorX) < abs(movingVectorY) -> ROW_BOTTOM_TO_TOP
                else -> ROW_RIGHT_TO_LEFT
            }
        }
    }

    fun draw(canvas: Canvas) {
        val bitmap = currentMoveBitmap
        canvas.drawBitmap(bitmap!!, x.toFloat(), y.toFloat(), null)
        // Last draw time.
        lastDrawNanoTime = System.nanoTime()
    }

    fun setMovingVector(movingVectorX: Int, movingVectorY: Int) {
        this.movingVectorX = movingVectorX
        this.movingVectorY = movingVectorY
    }

    companion object {
        private const val ROW_TOP_TO_BOTTOM = 0
        private const val ROW_RIGHT_TO_LEFT = 1
        private const val ROW_LEFT_TO_RIGHT = 2
        private const val ROW_BOTTOM_TO_TOP = 3

        // Velocity of game character (pixel/millisecond)
        const val VELOCITY = 0.1f

        const val ROW_COUNT = 4
        const val COL_COUNT = 3
    }
}
