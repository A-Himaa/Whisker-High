package Thread

import Model.BackgroundImage
import Model.Cat
import Model.Obstacle
import Model.ScreenSize
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import android.view.MotionEvent
import com.example.whiskerhigh.GameOver
import com.example.whiskerhigh.R
import kotlin.random.Random

class PlayThread(

    private val context: Context,
    private val holder: SurfaceHolder,
    private val resources: Resources) : Thread() {
    private val tag = "PlayThread"
    var isRunning = true
    private val FPS = (1000.0 / 60.0).toInt()                                                // Frame rate for 60 FPS
    private val backgroundImage = BackgroundImage()                                          // Variable for BackgroundImage model
    private val cat: Cat = Cat(resources, 600, 600)                  // Cat initialization

    private val obstacles = mutableListOf<Obstacle>()                                        // Obstacle List
    private val baseFrequency = 7000L                                                        // Time between obstacle generation (in ms)
    private val randomDelay = Random.nextLong(1000, 2000L)                        // Randomized additional delay
    private val obstacleFrequency = baseFrequency + randomDelay                              // Total frequency with randomness
    private var obstacleHandler: Handler? = null                                             // Handler to manage obstacle generation

    private var score = 0                                                                    // Total score
    private var elapsedTime = 0f                                                             // Total elapsed time in seconds
    private var timeSinceLastScoreUpdate = 0f                                                // Time since the last score update

    private val gravity = 15f                                                                // Gravity force
    private val jumpForce = -70f                                                             // Jump force for screen touches
    private var velocity = 50f                                                               // Velocity of the cat
    val topMargin = 50f
    val bottomMargin = 300f



    init {
        obstacleHandler = Handler(Looper.getMainLooper())
        createObstacle()                                                                    // Create the first obstacle as soon as the game starts
        startObstacleGeneration()                                                           // Start generating other obstacles
    }

    //Function to randomly generate obstacles
    private fun createObstacle() {
        val obstacleX = ScreenSize.SCREEN_WIDTH.toFloat()                                  // Start at the right edge

        // Randomly determine if the obstacle is a tree or a hanging pot
        val obstacleType = if (Random.nextBoolean()) "tree" else "pot"

        val randomBitmap = if (obstacleType == "tree") {
            BitmapFactory.decodeResource(resources, R.drawable.tree)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.hangingpot)
        }

        if (randomBitmap == null) {
            Log.e(tag, "Failed to load bitmap for obstacle")
            return                                                                       // Early exit to avoid creating an invalid obstacle
        }

        // Adjust obstacle creation based on type
        val obstacleHeight = if (obstacleType == "tree") {
            (ScreenSize.SCREEN_HEIGHT / 3)
        } else {
            (ScreenSize.SCREEN_HEIGHT / 3)
        }

        val obstacleY = if (obstacleType == "tree") {
            ScreenSize.SCREEN_HEIGHT - obstacleHeight.toFloat()                         // Start from bottom(Trees)
        }
        else {
            0f                                                                         // Start from top(Hanging pots)
        }

        val speed = 10f                                                                // Speed for obstacles

        val newObstacle = Obstacle(
            obstacleX,
            obstacleY,
            randomBitmap.width,
            obstacleHeight,
            speed,
            randomBitmap,
            obstacleType                                                              // Pass the obstacleType to the constructor
        )

        obstacles.add(newObstacle)                                                   // Add to the list of obstacles
        Log.d(tag, "Obstacle created at position ($obstacleX, $obstacleY) with type $obstacleType")
    }

    private fun startObstacleGeneration() {
        obstacleHandler?.postDelayed({
            createObstacle()
            startObstacleGeneration()                                               // Schedule when to generate next obstacle
        }, obstacleFrequency)                                                       // Use the randomized frequency
    }


    override fun run() {
        val startTime = System.currentTimeMillis()                                         // Get the starting time

        while (isRunning) {
            var canvas: Canvas? = null

            try {
                canvas = holder.lockCanvas()                                             // Lock the canvas

                if (canvas != null) {
                    synchronized(holder) {
                        val deltaTime = (System.currentTimeMillis() - startTime) / 1000f
                        update(deltaTime)                                               // Pass the delta time to update method
                        render(canvas)                                                  // Render all objects
                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)                                 // Unlock the canvas
                }
            }
        }
    }

    //Function to detect collision
    private fun checkCollision(cat: Cat, obstacle: Obstacle): Boolean {

        // Check if the bounding boxes of the cat and obstacle overlap
        val catRect = Rect(
            cat.x.toInt(),
            cat.y.toInt(),
            (cat.x + cat.width +30).toInt(),
            (cat.y + cat.height+30).toInt()
        )

        val obstacleRect = Rect(
            obstacle.x.toInt(),
            obstacle.y.toInt(),
            (obstacle.x + obstacle.width).toInt(),
            (obstacle.y + obstacle.height).toInt()
        )

        return catRect.intersect(obstacleRect)
    }



    private fun update(deltaTime: Float) {

        // Update velocity and cat position
        velocity += gravity
        cat.y += velocity
//        cat.y = cat.y.coerceIn(0f, ScreenSize.SCREEN_HEIGHT.toFloat())

        val catBottom = cat.y + cat.height
        val screenHeight = ScreenSize.SCREEN_HEIGHT.toFloat()
        cat.y = cat.y.coerceIn(topMargin, screenHeight - bottomMargin - cat.height)




        // Increment elapsed time
        elapsedTime += deltaTime

        // Increment score every second
        timeSinceLastScoreUpdate += deltaTime
        if (timeSinceLastScoreUpdate >= 60) {
            score +=  1                                                              // Increment the score by 1 every second
            timeSinceLastScoreUpdate = 0f                                             // Reset the timer for the next score increment
        }

        // Check for collisions
        val hasCollided = obstacles.any { checkCollision(cat, it) }
        if (hasCollided) {
            isRunning = false                                                         // Stop the game

            // Start GameOver activity
            val intent = Intent(context, GameOver::class.java)
            intent.putExtra("FINAL_SCORE", score)
            context.startActivity(intent)
        }

        // Move obstacles and remove those off-screen
        obstacles.forEach { it.move() }
        obstacles.removeAll { it.isOffScreen() }

        // Debugging logs
        Log.d(tag, "Cat position: ${cat.y}")
        Log.d(tag, "Score: $score")

        obstacles.forEach { obstacle ->
            Log.d(tag, "Obstacle position: (${obstacle.x}, ${obstacle.y})")
        }
    }


    private fun render(canvas: Canvas) {

        // Draw the background
        val bitmapImage = ScaleResize(
            BitmapFactory.decodeResource(resources, R.drawable.run_cloudysky)
        )
        backgroundImage.x -= 15
        if (backgroundImage.x < -bitmapImage.width) {
            backgroundImage.x = 0
        }

        canvas.drawBitmap(
            bitmapImage,
            backgroundImage.x.toFloat(),
            backgroundImage.y.toFloat(),
            null
        )

        canvas.drawBitmap(
            bitmapImage,
            (backgroundImage.x + bitmapImage.width).toFloat(),
            backgroundImage.y.toFloat(),
            null
        )

        // Draw the cat
        canvas.drawBitmap(
            cat.catBitmap,
            cat.x,
            cat.y,
            null
        )

        // Draw the obstacles
        obstacles.forEach { obstacle ->
            canvas.drawBitmap(
                obstacle.bitmap,
                obstacle.x,
                obstacle.y,
                null
            )
        }


        // Draw the score
        val paint = Paint()
        paint.color = Color.DKGRAY
        paint.textSize = 80f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val scoreYPosition = topMargin + paint.textSize
        canvas.drawText("Score: $score", 10f, scoreYPosition, paint)            // Adjust position and style

    }


    private fun ScaleResize(bitmap: Bitmap): Bitmap {
        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val scaleWidth = (ScreenSize.SCREEN_HEIGHT * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, scaleWidth, ScreenSize.SCREEN_HEIGHT, false)
    }

    fun jump() {
        if (cat.y > 0) {                                                         // Constrain the jump to prevent out-of-bounds movement
            velocity = jumpForce                                                 // Apply jump force to the velocity
        }
    }

    fun onTouch(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            jump() // Trigger the jump action
            return true
        }
        return false
    }




    fun restartGame() {
        cat.y = 600f                                                            // Reset cat's position
        velocity = 50f                                                           // Reset velocity
        obstacles.clear()                                                       // Clear existing obstacles
        score = 0                                                               // Reset score
        elapsedTime = 0f                                                        // Reset elapsed time
        timeSinceLastScoreUpdate = 0f
        isRunning = true                                                        // Resume the game loop
    }


}
