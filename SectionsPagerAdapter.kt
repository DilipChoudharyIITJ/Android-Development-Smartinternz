package com.tenalis.mart.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tenalis.mart.fragments.*

class SectionsPagerAdapter(lifecycle: Lifecycle, fm: FragmentManager) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        // Show 7 total pages.
        return 7
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> FoodFragment()
            2 -> GroceryFragment()
            3 -> BeverageFragment()
            4 -> DetergentsFragment()
            5 -> KitchenHomeCleaningFragment()
            6 -> OtherFragment()
            else -> HomeFragment()
        }
    }
}
