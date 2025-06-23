package fancyboard.fonts.keyboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.scale

fun Context.vibrate(duration: Long = 40) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(duration)
    }
}

fun resizeBitmapToMax720(src: Bitmap): Bitmap {
    val maxDim = 720
    val width = src.width
    val height = src.height

    if (width <= maxDim && height <= maxDim) return src // No resizing needed

    val scale = if (width > height) {
        maxDim.toFloat() / width
    } else {
        maxDim.toFloat() / height
    }

    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    return src.scale(newWidth, newHeight)
}


fun resizeAndSaveImage(context: Context, uri: Uri, outputFileName: String): File? {
    return try {
        // Load bitmap from URI
        val inputStream = context.contentResolver.openInputStream(uri)
        val original = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (original == null) return null

        // Resize bitmap to max 720px
        val resized = resizeBitmapToMax720(original)

        // Create images directory if it doesn't exist
        val imagesDir = File(context.filesDir, "images")
        if (!imagesDir.exists()) imagesDir.mkdirs()

        // Create output file
        val outputFile = File(imagesDir, outputFileName)

        // Save resized bitmap to file
        FileOutputStream(outputFile).use { out ->
            resized.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }

        outputFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
