package com.shirleyhe.aitfinalproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shirleyhe.aitfinalproject.fragment.ItemDetailsFragment;
import com.shirleyhe.aitfinalproject.fragment.SearchHistoryFragment;

/**
 * Created by shirleyhe on 12/11/16.
 */
public class EbayPagerAdapter extends FragmentPagerAdapter {


    public EbayPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ItemDetailsFragment();
            case 1:
                return new SearchHistoryFragment();
            default:
                return new ItemDetailsFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "ItemDetails";
            case 1:
                return "Search";
            default:
                return "ItemDetails";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
