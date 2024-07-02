package Model

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.whiskerhigh.R

class Cat(
    resources: Resources,
    private val desiredWidth: Int,
    private val desiredHeight: Int
) {
    var height = 100
    var width = 0
    var x: Float = 400f                                     // Initial horizontal position
    var y: Float = ScreenSize.SCREEN_HEIGHT / 2f            // Position the cat to the middle of the screen
    var velocityY: Float = 50f                               // Initial vertical velocity
    var velocityX: Float = 400f                              // Initial horizontal velocity
    var gravity: Float = 0.5f                               // Gravity force applied to the cat
    var jumpStrength: Float = -12f                          // Strength of the jump
    val catBitmap: Bitmap                                   // Bitmap for the cat image

    init {
        // Load the cat image from resources
        val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.blackkitty)

        // Resize the bitmap to the desired dimensions
        catBitmap = Bitmap.createScaledBitmap(
            originalBitmap,
            desiredWidth,
            desiredHeight,
            true                                       // Use filtering for smooth scaling
        )
    }

    fun update() {
        // Apply gravity to the vertical velocity
        velocityY += gravity

        // Update the cat's vertical position based on its velocity
        y += velocityY

        x += velocityX
    }

    fun jump() {
        // Set the vertical velocity to the jump strength
        velocityY = jumpStrength
    }
}
