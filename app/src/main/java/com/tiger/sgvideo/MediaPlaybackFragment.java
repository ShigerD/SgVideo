package com.tiger.sgvideo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于创建Fragment对象，作为ViewPager的叶片
 * @author ZHF
 *
 */
public class MediaPlaybackFragment extends Fragment implements View.OnClickListener,
        MediaPlayer.OnCompletionListener, View.OnTouchListener ,
        MediaPlayer.OnPreparedListener{
	private static final String TAG =MediaPlaybackFragment.class.getSimpleName();

	//private VideoView video1;
	private CustomVideoView mVideoView;
    private View layout_Loding;
    private View layout_toolbar;
    private boolean isfullmode=false;
    //seekbar
    private SeekBar mSeekBar;
    private static final int MSG_UPDATE_PROGRESS = 0x01;
    private static final int MSG_NOTIFY_POSITION = 0x02;
    private static final int MSG_FADE_OUT        = 0x03;
    private static final int MSG_BRAKE_ON        = 0x04;
    private static final int MSG_BRAKE_OFF       = 0x05;
    private static final int MSG_NOTIFY_MEDIA_INFO = 0x06;
    private static final int MSG_UPDATE_PLAY_STATE = 0x07;
    private boolean mDragging = false;
    public static final int DEFAULT_TIMEOUT       = 5000;
    private TextView mCurrentTime ;
    private TextView mTotalTime;

   //public String mediaPlayPath="/storage/哦呢.mp4";vechiel
    public String mediaPlayPath="storage/sdcard0/哦呢.mp4";   //storage/sdcard0/哦呢.mp4
    private MainActivity mActivity;
    private  List<String> mediaFilelist = new ArrayList<String>();//new
    private   int listPosion=0;
    private Boolean isFisrtStart=true;
    private ImageView mPlayPause;

    int mNum; //页号
    public static MediaPlaybackFragment newInstance(int num) {

        MediaPlaybackFragment fragment = new MediaPlaybackFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.e(TAG, " Oncreate++");
        super.onCreate(savedInstanceState);
        //这里我只是简单的用num区别标签，其实具体应用中可以使用真实的fragment对象来作为叶片
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        mActivity = (MainActivity)getActivity();
        Log.e(TAG, "++End Oncreate++");
    }
    /*
     * 
     */
    private void setupView(View view){
        view.findViewById(R.id.img_previous).setOnClickListener(this);
        view.findViewById(R.id.img_next).setOnClickListener(this);
        view.findViewById(R.id.img_dir).setOnClickListener(this);
        view.findViewById(R.id.img_stop).setOnClickListener(this);
        view.findViewById(R.id.img_play).setOnClickListener(this);
        mPlayPause = (ImageView)view.findViewById(R.id.img_play);
        mSeekBar = (SeekBar)view.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChanged);
        mCurrentTime = (TextView)view.findViewById(R.id.tv_startTime);
        mTotalTime = (TextView)view.findViewById(R.id.tv_totalTime);

        layout_Loding= view.findViewById(R.id.video_loading);
        layout_toolbar=view.findViewById(R.id.media_controller_bar);

        mVideoView=(CustomVideoView)view.findViewById(R.id.videoView1);
//                mVideoView.setVideoPath(mediaPlayPath); //vechiel
        mVideoView.setScreenFull(true);
        mVideoView.requestFocus();
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnTouchListener(this);
        mVideoView.setOnPreparedListener(this);


    }

    /**
     *
     */
    public void showBars(int timeout) {
        Message msg = mHandler.obtainMessage(MSG_FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(MSG_FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    //显示进度bar
//        if (!mShowing) {
//            mShowing = true;
//            mMediaControlBar.setVisibility(View.VISIBLE);
//            mActivity.quitFullScreen();
//
//            if (mShowControlBarAnimation == null) {
//                mShowControlBarAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.media_control_bar_up);
//            }
//
//            mMediaControlBar.startAnimation(mShowControlBarAnimation);
//        }
    }
    private SeekBar.OnSeekBarChangeListener mSeekBarChanged = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int arg1, boolean fromuser) {
            if(mVideoView.getDuration() <= 0){
                seekBar.setProgress(0);
                return;
            }
            if(!fromuser){
                return;
            }
            showBars(DEFAULT_TIMEOUT);
            long newpostion = seekBar.getProgress();
            mVideoView.seekTo((int)newpostion*1000);
            mCurrentTime.setText(makeTimeString( newpostion));
            Log.i(TAG, "newposition = "+newpostion);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            showBars(DEFAULT_TIMEOUT);
            if(mVideoView.getDuration() <= 0){
                return;
            }
            mDragging = true;
            mHandler.removeMessages(MSG_UPDATE_PROGRESS);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            showBars(DEFAULT_TIMEOUT);
            if (mVideoView.getDuration() <= 0) {
                seekBar.setProgress(0);
                return;
            }
            mDragging = false;
            mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);
        }
    };
    private int setProgress(){
        if (mVideoView == null || mDragging) {
            return 0;
        }
        int position = mVideoView.getCurrentPosition();
        int duration = mVideoView.getDuration();
        if (mSeekBar != null){
            if (duration >= 0) {
                long pos = position/1000 ;
                mSeekBar.setProgress((int)pos);
            } else {
                mSeekBar.setProgress(0);
            }
        }

        if (mCurrentTime != null) {
            mCurrentTime.setText(makeTimeString(position/1000));
        }

        return position;
    }
    private void updatePlayBtnState() {
        if(mVideoView.isPlaying()) {
            mPlayPause.setBackgroundResource(R.drawable.mc_play);
        } else {
            mPlayPause.setBackgroundResource(R.drawable.mc_pause);
        }
    }
    private Handler mHandler = new Handler(){
        /****/
        public void handleMessage(Message msg) {
            int pos;
            switch(msg.what){
                case MSG_UPDATE_PROGRESS:
                    pos = setProgress();
                    msg = obtainMessage(MSG_UPDATE_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                    break;
                case MSG_NOTIFY_POSITION:
//                    notifyPosition();
                    break;
                case MSG_BRAKE_ON:
//                    mBrakeView.setVisibility(View.GONE);
                    showBars(DEFAULT_TIMEOUT);
                    break;
                case MSG_BRAKE_OFF:
//                    mBrakeView.setVisibility(View.VISIBLE);
//                    mActivity.quitFullScreen();
                    exitFullScreen();
                    break;
                case MSG_FADE_OUT:
//                    if (mCurrentPage == 1) {
//                        hideBars();
//                    }
                    break;
                case MSG_NOTIFY_MEDIA_INFO:
                    if (mVideoView.isPlaying()) {
//                        notifyMediaInfo();
                    }
                    sendEmptyMessageDelayed(MSG_NOTIFY_MEDIA_INFO, 1000);
                    break;

                default:
                    break;
            }
        };

    };
    /*

     */
    public void hideLoading(){
        if(layout_Loding.getVisibility()==View.VISIBLE)
            layout_Loding.setVisibility(View.GONE);
    }
    /**为Fragment加载布局时调用**/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	Log.e(TAG, " ++OncreateView++");                                                                                                                                                                                                                                                                                       
        View view = inflater.inflate(R.layout.media_play_layout, container, false);
        setupView(view);

        Log.e(TAG, " ++End OncreateView++");
        return view;
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "=onCompletion");
        playNext();//自动播放下一首
    }
    /***
     *
     */
    public  void doPlayNew(String videoPath)
    {
        fullScreen();
        mVideoView.setVideoPath(videoPath);
        mVideoView.start();

    }
    public  void stop(){
        mVideoView.stopPlayback();
    }
    public  void pause(){
        mVideoView.pause();
    }

    public  void playNext(){
        mediaFilelist=mActivity.getPlaylist();
        if(mediaFilelist.isEmpty()) return;
        listPosion=mActivity.getlistPosion();
//        Log.e(TAG,"size:"+mediaFilelist.size());
        listPosion++;
        listPosion=listPosion%(mediaFilelist.size());
        mActivity.setlistPosion(listPosion);
        doPlayNew(mediaFilelist.get(listPosion));

    }

    public void playPrevious(){
        mediaFilelist=mActivity.getPlaylist();
        listPosion=mActivity.getlistPosion();
//        Log.e(TAG,"size:"+mediaFilelist.size());
        if(listPosion>0)
        listPosion--;
        listPosion=listPosion%(mediaFilelist.size());
        mActivity.setlistPosion(listPosion);
        doPlayNew(mediaFilelist.get(listPosion));
    }
    /**
     *
     *
     */
    private void fullScreen(){
        layout_toolbar.setVisibility(View.INVISIBLE);
        mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    private void exitFullScreen(){
        layout_toolbar.setVisibility(View.VISIBLE);
        mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    private void hideToolbar(){
        if(layout_toolbar.getVisibility()==View.VISIBLE){
            fullScreen();//
        }
        else
        {
           exitFullScreen();
        }
    }
    @Override
    public void onClick(View view) {
//        Log.e(TAG,"=====onclick"+view.getId());
        switch(view.getId()){
            case R.id.img_full:
//                boolean fullMode = mStoreManager.getBoolean(StoreManager.KEY_SCREEN_MODE, false);
//                if (fullMode) {
//                    mRatio.setBackgroundResource(R.drawable.mc_full);
//                } else {
//                    mRatio.setBackgroundResource(R.drawable.mc_notfull);
//                }
//                fullMode = !fullMode;
//                mVideoView.setScreenFull(fullMode);
//                mStoreManager.putBoolean(StoreManager.KEY_SCREEN_MODE, fullMode);
                Log.e("img_full","img_full");

                break;
            case R.id.img_stop:
                stop();
                break;
            case R.id.img_previous:
                playPrevious();
                break;
            case R.id.img_play:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                }
                else {
                    mVideoView.start();
                }
                updatePlayBtnState();
                break ;
            case R.id.img_next:
                playNext();
                break ;
            case R.id.img_dir:
                mActivity.switchToPage(0);
                break ;
            default :
                break ;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        hideToolbar();
        return false;
    }

    private String makeTimeString( long secs) {
        String result="--:--";
        String[] sTimeArgs = new String[5];

        long[] timeArgs =new long[5];
        timeArgs[0] = secs / 60;//minue
        timeArgs[1] = secs % 60;//second
        if(timeArgs[0]<10) sTimeArgs[0]="0"+String.valueOf(timeArgs[0]);
        else sTimeArgs[0]=String.valueOf(timeArgs[0]);
        if(timeArgs[1]<10) sTimeArgs[1]="0"+String.valueOf(timeArgs[1]);
        else sTimeArgs[1]=String.valueOf(timeArgs[1]);

        result=sTimeArgs[0]+":"+sTimeArgs[1];
        return result;
    }
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(TAG, "=onPrepared");
        int maxValue = mediaPlayer.getDuration() / 1000;
        Log.e("duration_video",String.valueOf(maxValue));//秒
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
        mHandler.sendEmptyMessage(MSG_UPDATE_PROGRESS);

        if (layout_Loding.getVisibility() == View.VISIBLE) {
            layout_Loding.setVisibility(View.GONE);
        }

        if(maxValue > 0) {
            // reset progress bar;
            mSeekBar.setProgress(0);
            mSeekBar.setMax(maxValue);
            mTotalTime.setText(makeTimeString(maxValue));
        }
    }

    /*

    */

}