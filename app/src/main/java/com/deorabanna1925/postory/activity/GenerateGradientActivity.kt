package com.deorabanna1925.postory.activity

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deorabanna1925.postory.databinding.ActivityGenerateGradientBinding
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.content.FileProvider
import java.io.OutputStream


class GenerateGradientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGenerateGradientBinding
    private lateinit var colors: ArrayList<String>
    private lateinit var downloads: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateGradientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Generate Gradient"
        colors = ArrayList()

        binding.minusButton.setOnClickListener {
            val value = Integer.parseInt(binding.numberOfColors.text.toString())
            if (value <= 2) {
                Toast.makeText(this, "There must be minimum 2 colors", Toast.LENGTH_SHORT).show()
            } else {
                val newValue = value - 1
                binding.numberOfColors.text = newValue.toString()
            }
        }

        binding.plusButton.setOnClickListener {
            val value = Integer.parseInt(binding.numberOfColors.text.toString())
            if (value >= 6) {
                Toast.makeText(this, "There must be maximum 6 colors", Toast.LENGTH_SHORT).show()
            } else {
                val newValue = value + 1
                binding.numberOfColors.text = newValue.toString()
            }
        }

        binding.generate.setOnClickListener {
            colors.clear()
            val value = Integer.parseInt(binding.numberOfColors.text.toString())
            for(i in 0 until value){
                val random = Random()
                val nextInt: Int = random.nextInt(0xffffff + 1)
                val colorCode = String.format("#%06x", nextInt)
                colors.add(colorCode)
            }
            showGradient()
        }


    }

    private fun showGradient() {
        val array = IntArray(colors.size)
        for (i in 0 until colors.size) {
            array[i] = Color.parseColor(colors[i])
        }
        val gd = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, array)
        gd.cornerRadius = 0f
        binding.image.background = gd

        binding.rotate.setOnClickListener {
            when (gd.orientation) {
                GradientDrawable.Orientation.LEFT_RIGHT -> gd.orientation = GradientDrawable.Orientation.BL_TR
                GradientDrawable.Orientation.BL_TR -> gd.orientation = GradientDrawable.Orientation.BOTTOM_TOP
                GradientDrawable.Orientation.BOTTOM_TOP -> gd.orientation = GradientDrawable.Orientation.BR_TL
                GradientDrawable.Orientation.BR_TL -> gd.orientation = GradientDrawable.Orientation.RIGHT_LEFT
                GradientDrawable.Orientation.RIGHT_LEFT -> gd.orientation = GradientDrawable.Orientation.TR_BL
                GradientDrawable.Orientation.TR_BL -> gd.orientation = GradientDrawable.Orientation.TOP_BOTTOM
                GradientDrawable.Orientation.TOP_BOTTOM -> gd.orientation = GradientDrawable.Orientation.TL_BR
                GradientDrawable.Orientation.TL_BR -> gd.orientation = GradientDrawable.Orientation.LEFT_RIGHT
            }
            gd.cornerRadius = 0f
            binding.image.background = gd
        }

        binding.print.setOnClickListener {
            val bitmap = getScreenShotFromView(binding.image)
            if (bitmap != null) {
                saveMediaToStorage(bitmap)
            }
        }

    }

    private fun getScreenShotFromView(v: View): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            // inflate screenshot object
            // with Bitmap.createBitmap it
            // requires three parameters
            // width and height of the view and
            // the background color
            screenshot =
                Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        // return the bitmap
        return screenshot
    }

    // this method saves the image to gallery
    private fun saveMediaToStorage(bitmap: Bitmap) {
        // Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        // Output stream
        var fos: OutputStream? = null

        // For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // getting the contentResolver
            this.contentResolver?.also { resolver ->

                // Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    // putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                // Inserting the contentValues to
                // contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                downloads = imageUri.toString()
                // Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            // These for devices running on android < Q
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            downloads = (Uri.fromFile(image)).toString()
            fos = FileOutputStream(image)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/jpeg"
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(downloads))
        share.putExtra(Intent.EXTRA_TEXT, "Hey Friends,\nCheck out this gradient from findgradient.")
        startActivity(Intent.createChooser(share, "Share Image"))
    }
}