package com.example.whiskerhigh

import Model.ScreenSize
import UI.playgame
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOver : AppCompatActivity() {
    private val TAG = "gameover"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_over)
        ScreenSize.getScreenSize(this)

        // Get the final score from the intent
        val finalScore = intent.getIntExtra("FINAL_SCORE", 0)

        // Set the final score text
        val finalScoreTextView: TextView = findViewById(R.id.textView)
        finalScoreTextView.text = "Final Score: $finalScore"

        // Play Button
        val playButton = findViewById<ImageButton>(R.id.imageButton)
        playButton.setOnClickListener {
            val playGameIntent = Intent(this@GameOver, playgame::class.java)
            startActivity(playGameIntent)
            finish()
            Log.d(TAG, "Button play clicked")
        }
    }
}
