package com.deorabanna1925.postory.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.deorabanna1925.postory.databinding.FragmentPostBinding
import com.deorabanna1925.postory.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

//        binding.data.text = arguments?.getString("data")

        binding.aboutUs.setOnClickListener {
            Toast.makeText(requireActivity(),"",Toast.LENGTH_SHORT).show();
        }

        return binding.root
    }

}