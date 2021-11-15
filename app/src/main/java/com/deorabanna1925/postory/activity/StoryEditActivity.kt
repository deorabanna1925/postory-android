package com.deorabanna1925.postory.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deorabanna1925.postory.adapter.ThumbnailsAdapter
import com.deorabanna1925.postory.databinding.ActivityPostEditBinding
import com.deorabanna1925.postory.databinding.ActivityStoryEditBinding
import com.deorabanna1925.postory.model.ThumbnailItem
import com.deorabanna1925.postory.utils.ThumbnailCallback
import com.deorabanna1925.postory.utils.ThumbnailsManager
import com.zomato.photofilters.SampleFilters
import com.zomato.photofilters.imageprocessors.Filter


class StoryEditActivity : AppCompatActivity(), ThumbnailCallback {

    private lateinit var binding: ActivityStoryEditBinding

    private var bitmap: Bitmap? = null
    private var resultUri: Uri? = null
    private var activity: Activity? = null
    private var thumbListView: RecyclerView? = null
    private var placeHolderImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryEditBinding.inflate(layoutInflater)
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
            initHorizontalList()
        }

    }

    private fun initUIWidgets() {
        thumbListView = binding.thumbnails
        placeHolderImageView = binding.placeHolderImageview
    }

    private fun initHorizontalList() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.scrollToPosition(0)

        thumbListView!!.layoutManager = layoutManager
        thumbListView!!.setHasFixedSize(true)
        bindDataToAdapter()
    }

    private fun bindDataToAdapter() {
        val handler = Handler(Looper.getMainLooper())
        val r = Runnable {
            listData()
        }
        handler.post(r)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listData() {
        val context: Context = this.application

        val thumbImage = bitmap
        val t1 = ThumbnailItem()
        val t2 = ThumbnailItem()
        val t3 = ThumbnailItem()
        val t4 = ThumbnailItem()
        val t5 = ThumbnailItem()
        val t6 = ThumbnailItem()

        t1.image = thumbImage
        t2.image = thumbImage
        t3.image = thumbImage
        t4.image = thumbImage
        t5.image = thumbImage
        t6.image = thumbImage

        t1.name = "Original"
        t2.name = "Star Lit"
        t3.name = "Blue Mess"
        t4.name = "Awe Struck"
        t5.name = "Lime Stutter"
        t6.name = "Night Whisper"

        ThumbnailsManager.clearThumbs()
        ThumbnailsManager.addThumb(t1) // Original Image
        t2.filter = SampleFilters.getStarLitFilter()
        ThumbnailsManager.addThumb(t2)
        t3.filter = SampleFilters.getBlueMessFilter()
        ThumbnailsManager.addThumb(t3)
        t4.filter = SampleFilters.getAweStruckVibeFilter()
        ThumbnailsManager.addThumb(t4)
        t5.filter = SampleFilters.getLimeStutterFilter()
        ThumbnailsManager.addThumb(t5)
        t6.filter = SampleFilters.getNightWhisperFilter()
        ThumbnailsManager.addThumb(t6)
        val thumbs: List<ThumbnailItem> = ThumbnailsManager.processThumbs(context)
        val adapter = ThumbnailsAdapter(thumbs, activity as ThumbnailCallback?)
        thumbListView!!.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onThumbnailClick(filter: Filter?) {
        bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
        placeHolderImageView!!.setImageBitmap(
            filter!!.processFilter(
                Bitmap.createScaledBitmap(
                    bitmap!!,
                    1080,
                    1920,
                    false
                )
            )
        )
    }

}