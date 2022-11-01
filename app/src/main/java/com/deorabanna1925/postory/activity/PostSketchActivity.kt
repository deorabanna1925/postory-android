package com.deorabanna1925.postory.activity

import android.app.Activity
import android.content.ContentValues
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.red
import com.deorabanna1925.postory.databinding.ActivityPostSketchBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.IntBuffer


class PostSketchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostSketchBinding
    private lateinit var downloads: String

    private var bitmap: Bitmap? = null
    private var resultUri: Uri? = null
    private var activity: Activity? = null
    private var placeHolderImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostSketchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.title = "Choose Filter"

        System.loadLibrary("NativeImageProcessor")

        activity = this
        initUIWidgets()

        val data = intent.getStringExtra("imagePath")
        if(data!=null){
            resultUri = Uri.parse(data)
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(data))
            placeHolderImageView!!.setImageBitmap(bitmap)
        }

        binding.sketchButton.setOnClickListener {
            val bmp = Changetosketch(bitmap!!)
            placeHolderImageView!!.setImageBitmap(bmp)
            bitmap = bmp
        }

        binding.resetButton.setOnClickListener {
            val data = intent.getStringExtra("imagePath")
            if(data!=null){
                resultUri = Uri.parse(data)
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(data))
                placeHolderImageView!!.setImageBitmap(bitmap)
            }
        }

        binding.printButton.setOnClickListener {
            printCurrent()
        }

        binding.pngButton.setOnClickListener {
            transparentBackground(bitmap!!)
        }

    }

    private fun transparentBackground(bitmap: Bitmap) {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        for (i in pixels.indices) {
            val red = pixels[i] shr 16 and 0xff
            val green = pixels[i] shr 8 and 0xff
            val blue = pixels[i] and 0xff
            if (red > 230 && green > 230 && blue > 230) {
                pixels[i] = Color.TRANSPARENT
            }
        }
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        placeHolderImageView!!.setImageBitmap(result)
        saveMediaToStorageAsPNG(result)
        bitmap.recycle()
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

                // Content resolver will process the content values
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


    private fun initUIWidgets() {
        placeHolderImageView = binding.placeHolderImageview
    }

    fun Changetosketch(bmp: Bitmap?): Bitmap? {
        var Copy: Bitmap?
        var Invert: Bitmap?
        val Result: Bitmap?
        Copy = bmp
        Copy = toGrayscale(Copy!!)
        Invert = createInvertedBitmap(Copy!!)
        Invert = blurRenderScript(Invert!!, 25f)
        Result = ColorDodgeBlend(Invert!!, Copy)
        return Result
    }

    fun blurRenderScript(sentBitmap: Bitmap, radius: Float): Bitmap? {
        var bitmap = sentBitmap
        bitmap = sentBitmap.copy(sentBitmap.config, true)
        val renderScript = RenderScript.create(this)
        val input = Allocation.createFromBitmap(renderScript, sentBitmap)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap)
        return bitmap
    }

    fun toGrayscale(bmpOriginal: Bitmap): Bitmap? {
        val width: Int
        val height: Int
        height = bmpOriginal.height
        width = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bmpOriginal, 0f, 0f, paint)
        return bmpGrayscale
    }

    fun createInvertedBitmap(src: Bitmap): Bitmap? {
        val colorMatrix_Inverted = ColorMatrix(
            floatArrayOf(
                -1f,
                0f,
                0f,
                0f,
                255f,
                0f,
                -1f,
                0f,
                0f,
                255f,
                0f,
                0f,
                -1f,
                0f,
                255f,
                0f,
                0f,
                0f,
                1f,
                0f
            )
        )
        val ColorFilter_Sepia: ColorFilter = ColorMatrixColorFilter(
            colorMatrix_Inverted
        )
        val bitmap = Bitmap.createBitmap(
            src.width, src.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.colorFilter = ColorFilter_Sepia
        canvas.drawBitmap(src, 0f, 0f, paint)
        return bitmap
    }

    fun ColorDodgeBlend(source: Bitmap, layer: Bitmap): Bitmap? {
        val base = source.copy(Bitmap.Config.ARGB_8888, true)
        val blend = layer.copy(Bitmap.Config.ARGB_8888, false)
        val buffBase: IntBuffer = IntBuffer.allocate(base.width * base.height)
        base.copyPixelsToBuffer(buffBase)
        buffBase.rewind()
        val buffBlend: IntBuffer = IntBuffer.allocate(blend.width * blend.height)
        blend.copyPixelsToBuffer(buffBlend)
        buffBlend.rewind()
        val buffOut: IntBuffer = IntBuffer.allocate(base.width * base.height)
        buffOut.rewind()
        while (buffOut.position() < buffOut.limit()) {
            val filterInt: Int = buffBlend.get()
            val srcInt: Int = buffBase.get()
            val redValueFilter = Color.red(filterInt)
            val greenValueFilter = Color.green(filterInt)
            val blueValueFilter = Color.blue(filterInt)
            val redValueSrc = Color.red(srcInt)
            val greenValueSrc = Color.green(srcInt)
            val blueValueSrc = Color.blue(srcInt)
            val redValueFinal = colordodge(redValueFilter, redValueSrc)
            val greenValueFinal = colordodge(greenValueFilter, greenValueSrc)
            val blueValueFinal = colordodge(blueValueFilter, blueValueSrc)
            val pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal)
            buffOut.put(pixel)
        }
        buffOut.rewind()
        base.copyPixelsFromBuffer(buffOut)
        blend.recycle()
        return base
    }

    private fun colordodge(in1: Int, in2: Int): Int {
        val image = in2.toFloat()
        val mask = in1.toFloat()
        return (if (image == 255f) image else Math.min(
            255f,
            (mask.toLong() shl 8) / (255 - image)
        )).toInt()
    }

    private fun saveMediaToStorageAsPNG(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.png"
        var fos: FileOutputStream? = null
        var downloads = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let {
                    resolver.openOutputStream(it)
                } as FileOutputStream?
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }
            val image = File(imagesDir, filename)
            downloads = (Uri.fromFile(image)).toString()
            fos = FileOutputStream(image)
        }

        fos?.use {
            // Finally writing the bitmap to the output stream that we opened
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }

    }

}
