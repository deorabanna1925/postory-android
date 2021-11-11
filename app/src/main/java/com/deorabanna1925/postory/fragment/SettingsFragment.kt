package com.deorabanna1925.postory.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.deorabanna1925.postory.activity.DashboardActivity
import com.deorabanna1925.postory.activity.GenerateGradientActivity
import com.deorabanna1925.postory.activity.GenerateShapeActivity
import com.deorabanna1925.postory.activity.GenerateSolidActivity
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

        binding.generateShape.setOnClickListener {
            val intent = Intent(requireActivity(), GenerateShapeActivity::class.java)
            startActivity(intent)
        }

        binding.generateSolid.setOnClickListener {
            val intent = Intent(requireActivity(), GenerateSolidActivity::class.java)
            startActivity(intent)
        }

        binding.generateGradient.setOnClickListener {
            val intent = Intent(requireActivity(), GenerateGradientActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

}