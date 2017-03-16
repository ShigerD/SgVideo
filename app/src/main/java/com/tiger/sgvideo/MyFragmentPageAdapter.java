package com.tiger.sgvideo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 自定义fragment适配器
 * @author ZHF
 *
 */
public class MyFragmentPageAdapter extends FragmentPagerAdapter {

    public MyFragmentPageAdapter(FragmentManager fm) {
		// TODO Auto-generated constructor stub
    	super(fm);
	}
	@Override
    public int getCount() {
        return 3;
    }
    @Override
    public Fragment getItem(int position) {
    	
        switch (position) {
         	case 0:
                return MediaStorageFragment.newInstance(position);
         		//return MediaPlaylistFragment.newInstance(position);       		
            case 1:
                return MediaPlaybackFragment.newInstance(position);
            case 2:
                return MediaPlaylistFragment.newInstance(position);

            default:
                return null;
            }
    }
    @Override
    public float getPageWidth(int position) {
        if (position == 2) {
            return super.getPageWidth(position) / 3;
        } else {
            return super.getPageWidth(position);
        }
    }
}