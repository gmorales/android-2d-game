package com.moralesbatovski.android2dgame

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set fullscreen
        // TODO: replace deprecated param
        // window.insetsController?.hide(WindowInsets.Type.statusBars())
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Set No Title
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(GameSurface(this@GameActivity))
    }
}
