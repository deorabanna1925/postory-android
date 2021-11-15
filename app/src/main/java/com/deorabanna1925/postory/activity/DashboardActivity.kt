package com.deorabanna1925.postory.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.deorabanna1925.postory.R
import com.deorabanna1925.postory.databinding.ActivityDashboardBinding
import com.deorabanna1925.postory.fragment.PostFragment
import com.deorabanna1925.postory.fragment.SettingsFragment
import com.deorabanna1925.postory.fragment.StoryFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDashboardBinding
    private lateinit var actionBar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupDefaultHomeScreen()
        setupBottomNavigation()

    }

    private fun setupDefaultHomeScreen() {
        val bundle = Bundle()
        bundle.putString("data", "Post Fragment")
        val post = PostFragment()
        post.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, post)
            .commit()
        binding.bottomNavigation.selectedItemId = R.id.bottom_nav_home
        actionBar.title = "Post"
    }

    private fun setupActionBar() {
        actionBar = supportActionBar!!
        actionBar.title = "Post"
    }

    @SuppressLint("NonConstantResourceId")
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val bundle = Bundle()
            bundle.clear()
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.bottom_nav_home -> {
                    selectedFragment = PostFragment()
                    bundle.putString("data", "Post Fragment")
                    selectedFragment.setArguments(bundle)
                    actionBar.title = "Post"
                }
                R.id.bottom_nav_explore -> {
                    selectedFragment = StoryFragment()
                    bundle.putString("data", "Story Fragment")
                    selectedFragment.setArguments(bundle)
                    actionBar.title = "Story"
                }
                R.id.bottom_nav_settings -> {
                    selectedFragment = SettingsFragment()
                    bundle.putString("data", "Settings Fragment")
                    selectedFragment.setArguments(bundle)
                    actionBar.title = "Settings"
                }
            }
            assert(selectedFragment != null)
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                selectedFragment!!
            ).commit()
            true
        }
    }

}