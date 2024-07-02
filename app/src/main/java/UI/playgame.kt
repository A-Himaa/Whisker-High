package UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whiskerhigh.R

class playgame : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playView = PlayView(this)
        setContentView(playView)

    }
}