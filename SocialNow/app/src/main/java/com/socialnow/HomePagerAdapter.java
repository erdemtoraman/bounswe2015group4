package com.socialnow;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.socialnow.HomeScreen.EventFrag;
import com.socialnow.HomeScreen.GroupFrag;

/**
 * Created by mugekurtipek on 03/01/16.
 */
public class HomePagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public HomePagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                //TODO my events fragment
                EventFrag tab1 = new EventFrag ();
                return tab1;
            case 1:
                //TODO my groups fragment
                GroupFrag tab2 = new GroupFrag ();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}