package com.moralesbatovski.android2dgame

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * @author Gustavo Morales
 */
class GameSurface(
    context: Context
) : SurfaceView(context), SurfaceHolder.Callback {

    private lateinit var gameThread: GameThread
    private lateinit var soundPool: SoundPool

    private var characterList: MutableList<Character> = mutableListOf()
    private var explosionList: MutableList<Explosion> = mutableListOf()
    private var soundIdExplosion = 0
    private var soundIdBackground = 0
    private var soundPoolLoaded = false

    init {
        // Make Game Surface focusable so it can handle events.
        isFocusable = true

        // Set callback.
        holder.addCallback(this)

        initSoundPool()
    }

    // Implements method of SurfaceHolder.Callback
    override fun surfaceCreated(holder: SurfaceHolder) {
        val firstCharacterBitmap = BitmapFactory.decodeResource(resources, R.drawable.chibi1)
        val secondCharacterBitmap = BitmapFactory.decodeResource(resources, R.drawable.chibi2)

        characterList = arrayListOf(
            Character(this, firstCharacterBitmap, 100, 50),
            Character(this, secondCharacterBitmap, 400, 850)
        )

        gameThread = GameThread(this, holder)
        gameThread.setRunning(true)
        gameThread.start()
    }

    // Implements method of SurfaceHolder.Callback
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    // Implements method of SurfaceHolder.Callback
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                gameThread.setRunning(false)

                // Parent thread must wait until the end of GameThread.
                gameThread.join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            retry = true
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        for (character in characterList) {
            character.draw(canvas)
        }
        for (explosion in explosionList) {
            explosion.draw(canvas)
        }
    }

    fun update() {
        for (character in characterList) {
            character.update()
        }

        for (explosion in explosionList) {
            explosion.update()
        }

        val iterator = explosionList.iterator()
        while (iterator.hasNext()) {
            val explosion = iterator.next()
            if (explosion.isFinish) {
                iterator.remove()
                continue
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x.toInt()
            val y = event.y.toInt()

            // Character explosion
            val characterIterator = characterList.iterator()
            while (characterIterator.hasNext()) {
                val character = characterIterator.next()
                if (character.x < x && x < character.x + character.width && character.y < y && y < character.y + character.height) {
                    // Remove the current element from the iterator and the list.
                    characterIterator.remove()
                    // Create Explosion object.
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.explosion)
                    explosionList = arrayListOf(
                        Explosion(this, bitmap, character.x, character.y)
                    )
                }
            }

            // Character movement
            for (character in characterList) {
                val movingVectorX: Int = x - character.x
                val movingVectorY: Int = y - character.y
                character.setMovingVector(movingVectorX, movingVectorY)
            }
            return true
        }
        return false
    }

    private fun initSoundPool() {
        // With Android API >= 21.
        soundPool = if (Build.VERSION.SDK_INT >= 21) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val builder = SoundPool.Builder()
            builder.setAudioAttributes(audioAttributes).setMaxStreams(MAX_STREAMS)
            builder.build()
        } else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0)
        }

        // When SoundPool load complete.
        soundPool.setOnLoadCompleteListener { _, _, _ ->
            soundPoolLoaded = true

            // Playing background sound.
            playSoundBackground()
        }

        // Load the sound background.mp3 into SoundPool
        soundIdBackground = soundPool.load(this.context, R.raw.background, 1)

        // Load the sound explosion.wav into SoundPool
        soundIdExplosion = soundPool.load(this.context, R.raw.explosion, 1)
    }

    private fun playSoundBackground() {
        if (soundPoolLoaded) {
            val leftVolume = 0.8f
            val rightVolume = 0.8f
            // Play sound background.mp3
            soundPool.play(soundIdBackground, leftVolume, rightVolume, 1, -1, 1f)
        }
    }

    fun playSoundExplosion() {
        if (soundPoolLoaded) {
            val leftVolume = 0.8f
            val rightVolume = 0.8f
            // Play sound explosion.wav
            soundPool.play(soundIdExplosion, leftVolume, rightVolume, 1, 0, 1f)
        }
    }

    companion object {
        private const val MAX_STREAMS = 100
    }
}
