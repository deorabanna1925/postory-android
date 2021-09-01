package com.deorabanna1925.postory.fragment

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.deorabanna1925.postory.databinding.FragmentStoryBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class StoryFragment : Fragment() {

    private lateinit var binding: FragmentStoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStoryBinding.inflate(inflater, container, false)

//        binding.data.text = arguments?.getString("data")

        binding.selectNew.setOnClickListener {
            openPickPhoto()
        }

        return binding.root
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent = result.data!!
                val destinationFileName: String =
                    System.currentTimeMillis().toString() + "." + getImageExtension(data.data!!)

                val options: UCrop.Options = UCrop.Options();
                options.setBrightnessEnabled(false)
                options.setSaturationEnabled(false)
                options.setSharpnessEnabled(false)
                options.setContrastEnabled(false)

                UCrop.of(data.data!!, Uri.fromFile(File(requireActivity().cacheDir, destinationFileName)))
                    .withOptions(options)
                    .withAspectRatio(9f, 16f)
                    .start(requireActivity(),this)
            }
        }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            handleCropResult(data)
        }
    }

    private fun handleCropResult(data: Intent?) {
        val resultUri = UCrop.getOutput(data!!)
        if (resultUri != null) {
            binding.image.setImageURI(resultUri)
        }
    }

    private fun openPickPhoto() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher.launch(pickPhoto)
    }

    private fun getImageExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

}