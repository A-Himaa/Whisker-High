package com.example.whiskerhigh

import Model.ScreenSize
import UI.playgame
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ScreenSize.getScreenSize(this)

//  Play Button
        val Button1 = findViewById<ImageButton>(R.id.imageButton2)

        Button1.setOnClickListener{
            val iplaygame = Intent(this@MainActivity, playgame::class.java)
            startActivity(iplaygame)
            finish()
            Log.d(TAG, "Button play clicked")
        }


    }
}