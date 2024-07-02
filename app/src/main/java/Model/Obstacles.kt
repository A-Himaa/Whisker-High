package Model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

enum class ObstacleType {
    POT, TREE
}

data class Obstacle(
    var x: Float,                            // Horizontal position of the obstacle
    var y: Float,                            // Vertical position of the obstacle
    val width: Int,                          // Width of the obstacle
    var height: Int,                         // Height of the obstacle
    val speed: Float= 20f,                        // Speed of the obstacle
    val bitmap: Bitmap,                      // Bitmap representing the obstacle
    val obstacleType: String                 // Type of the obstacle (POT or TREE)
  )
{

    fun move() {
        x -= speed                           // Move the obstacle from right to left
    }

    fun isOffScreen(): Boolean {
        return x + width < 0                 // Check if the obstacle is off-screen
    }

    fun getRect(): Rect {
        // Return the rectangle representing the obstacle's bounds
        return Rect(x.toInt(), y.toInt(), x.toInt() + width, y.toInt() + height)
    }

    fun reset(newX: Float, newY: Float, newHeight: Int) {
        // Reset obstacle position and height
        x = newX
        y = newY
        height = newHeight
    }


}
