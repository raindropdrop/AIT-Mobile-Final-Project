package com.shirleyhe.aitfinalproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shirleyhe.aitfinalproject.fragment.ItemDetailsFragment;
import com.shirleyhe.aitfinalproject.fragment.ScanFragment;
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
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment =  new ScanFragment();
                break;
            case 1:
                fragment =  new ItemDetailsFragment();
                break;
            case 2:
                fragment =  new SearchHistoryFragment();
                break;
            default:
                fragment = new ScanFragment();
                break;
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Scan";
            case 1:
                return "ItemDetails";
            case 2:
                return "Search";
            default:
                return "Scan";
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
