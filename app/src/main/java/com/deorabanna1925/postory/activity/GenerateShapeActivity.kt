package com.deorabanna1925.postory.activity

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import com.deorabanna1925.postory.databinding.ActivityGenerateShapeBinding
import com.yalantis.ucrop.UCrop
import android.R
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import java.io.*
import java.util.*


class GenerateShapeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGenerateShapeBinding
    private lateinit var downloads: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateShapeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Generate Shape"

        binding.foreground.setOnClickListener {
            openForegroundPhoto()
        }

        binding.foreground.performClick()

    }

    private var foregroundResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent = result.data!!
                val destinationFileName: String =
                    System.currentTimeMillis().toString() + "." + getImageExtension(data.data!!)
                binding.image.setImageURI(data.data)
                binding.image.setBackgroundColor(Color.parseColor("#ffffff"))

                binding.tint.setOnClickListener {
                    val random = Random()
                    val nextInt: Int = random.nextInt(0xffffff + 1)
                    val colorCode = String.format("#%06x", nextInt)
                    binding.image.setColorFilter(Color.parseColor(colorCode))
                }
                binding.background.setOnClickListener {
                    val random = Random()
                    val nextInt: Int = random.nextInt(0xffffff + 1)
                    val colorCode = String.format("#%06x", nextInt)
                    binding.image.setBackgroundColor(Color.parseColor(colorCode))
                }
                binding.whiteTint.setOnClickListener {
                    binding.image.setColorFilter(Color.parseColor("#ffffff"))
                }
                binding.blackTint.setOnClickListener {
                    binding.image.setColorFilter(Color.parseColor("#000000"))
                }
                binding.whiteBackground.setOnClickListener {
                    binding.image.setBackgroundColor(Color.parseColor("#ffffff"))
                }
                binding.blackBackground.setOnClickListener {
                    binding.image.setBackgroundColor(Color.parseColor("#000000"))
                }
                binding.print.setOnClickListener {
                    printCurrent()
                }

            }
        }

    private fun openForegroundPhoto() {

        val mimeTypes = arrayOf("image/png")
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            .setType("image/*")
            .putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        foregroundResultLauncher.launch(pickPhoto)
    }

    private fun getImageExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun printCurrent() {
        val bitmap = getScreenShotFromView(binding.finalPrint)
        if (bitmap != null) {
            saveMediaToStorage(bitmap)
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

/*        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/jpeg"
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(downloads))
        share.putExtra(Intent.EXTRA_TEXT, "Hey Friends,\nCheck out this gradient from findgradient.")
        startActivity(Intent.createChooser(share, "Share Image"))*/
    }

}