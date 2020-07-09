package com.example.qtalk

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.qtalk.fragment.AllContactsFragment
import com.example.qtalk.fragment.StarredContactsFragment

class ContactPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {


    override fun getItem(position: Int): Fragment {

        if (position == 0) {
            return AllContactsFragment()
        } else {
            return StarredContactsFragment()
        }

    }

    override fun getCount(): Int {
        return 2
    }
}