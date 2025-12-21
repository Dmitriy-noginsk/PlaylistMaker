package com.example.playlistmaker.presentation.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!
    private var tabMediator: TabLayoutMediator? = null
    private var selectedTab: Int = 0

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            selectedTab = position
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLibrary) { v, insets ->
            val status = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val nav = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val extraTop = resources.getDimensionPixelSize(R.dimen.content_top_margin)
            v.updatePadding(top = status.top + extraTop, bottom = nav.bottom)
            insets
        }

        binding.viewPager.adapter = LibraryViewPagerAdapter(this)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.media_tab_favorites)
                else -> getString(R.string.media_tab_playlists)
            }
        }.also { it.attach() }

        selectedTab = savedInstanceState?.getInt(KEY_SELECTED_TAB) ?: 0
        binding.viewPager.setCurrentItem(selectedTab, false)

        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_TAB, selectedTab)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)

        tabMediator?.detach()
        tabMediator = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val KEY_SELECTED_TAB = "selected_tab"
    }
}