package UI

import Thread.PlayThread
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class PlayView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var playThread: PlayThread? = null

    init {
        holder.addCallback(this)                       // Set up the SurfaceHolder callback
        isFocusable = true                                     // Ensure the view can receive focus
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (playThread == null) {
            playThread = PlayThread(context, holder, resources)        // Create and start the thread
            playThread!!.start()

        } else if (!playThread!!.isRunning) {
            playThread!!.isRunning = true
            playThread!!.start()                                               // Restart the thread if needed
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        playThread?.isRunning = false                                         // Stop the thread
        try {
            playThread?.join()                                                // Wait for the thread to finish
        }
        catch (e: InterruptedException) {
            Log.e("PlayView", "Thread interruption", e)             // Handle the exception
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("PlayView", "Touch event received")                  // Log to check if the event is received

        if (event.action == MotionEvent.ACTION_DOWN) {
            if (playThread?.isRunning == false) {                           // If the game is over
                playThread?.restartGame()                                   // Restart the game
                return true                                                 // Indicates the event was handled
            }

            playThread?.jump()                                             // Trigger a jump if the game is running
            return true
        }

        return super.onTouchEvent(event)                                  // Default handling for other events
    }

    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {

    }
}
