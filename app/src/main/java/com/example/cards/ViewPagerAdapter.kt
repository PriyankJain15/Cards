package com.example.cards

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fm:FragmentManager,lifecycle: Lifecycle): FragmentStateAdapter(fm,lifecycle) {

    fun getPageTitle(position: Int): CharSequence? {
        if(position==0){
            return "CREATE"
        }else{
            return "JOIN"
        }
    }

    override fun getItemCount(): Int  = 2

    override fun createFragment(position: Int): Fragment {
        if(position==0){
            return CreateFragment()
        }else{
            return JoinFragment()
        }
    }

}