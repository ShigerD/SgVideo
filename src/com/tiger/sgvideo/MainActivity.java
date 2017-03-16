package com.tiger.sgvideo;

//import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
	
	private static final String TAG =MainActivity.class.getSimpleName();
	
    private ViewPager mViewPager;
//    private MyFragmentPageAdapter mAdapter;
    private FragmentPagerAdapter mAdapter2;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    public MediaStorageFragment mMediaStorageFragment = new MediaStorageFragment();
    private MediaPlaybackFragment mMediaPlaybackFragment = new MediaPlaybackFragment();
    private MediaPlaylistFragment mMediaPlaylistFragment = new MediaPlaylistFragment();


    public static  String externalStoragePath= Environment.getExternalStorageDirectory().getPath();
    public static  String udiskPath="/mnt/udisk/udisk1";

//    private int postion = 0;
//    private MediaPlayer mp= new MediaPlayer();
	//private VideoView video1;
		
	//File file=new File("/storage/Aegean_Sea.mp4");
	//public void h
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.e(TAG, "++Oncreate++");
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          //      WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager)findViewById(R.id.viewpager);


        Toast.makeText(MainActivity.this,externalStoragePath,Toast.LENGTH_SHORT).show();

        FragmentManager fm = getSupportFragmentManager();
        //初始化自定义适配器
        mAdapter2 =  new MyFragmentPageAdapter(fm);
        //绑定自定义适配器
        mFragments.add(mMediaStorageFragment);
        mFragments.add(mMediaPlaybackFragment);
        mFragments.add(mMediaPlaylistFragment);
        mAdapter2 = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }

            @Override
            public float getPageWidth(int position) {
                if (position == 0)
                    return super.getPageWidth(position) / 3;
                else if (position == 2) {
                    return super.getPageWidth(position) / 3;
                } else {
                    return super.getPageWidth(position);
                }
            }
        };
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapter2);
        mViewPager.setCurrentItem(1, true);
        Log.e(TAG, "++End Oncreate++");
    }


    public void play(String mediaPath) {
        mMediaPlaybackFragment.doPlayNew(mediaPath);
    }

    public void hideLoading()
    {
        mMediaPlaybackFragment.hideLoading();
    }

    /**
     *
     * @return
     */
    public List<String> getPlaylist(){
      return   mMediaPlaylistFragment.mediaFilelist;
    }
    public int getlistPosion(){
        return  mMediaPlaylistFragment.listPosion;
    }
    public  void setlistPosion(int listPosion){
        mMediaPlaylistFragment.setlistPosion(listPosion);
    }
    public void updatePlaylist(String filepath){
         mMediaPlaylistFragment.updatePlaylist(filepath);
    }
    public void switchToPage(int page) {
        mViewPager.setCurrentItem(page, true);
    }
}
