package com.tiger.sgvideo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MediaStorageFragment extends Fragment{
    private static final String TAG = MediaStorageFragment.class.getSimpleName();
    int mNum; //页号


    private ListView listView;//new
    private List<String> storelist=new ArrayList<String>();
    private String   udiskPath="/mnt/udisk";
    private MainActivity mActivity;

    public static MediaStorageFragment newInstance(int num) {
    	MediaStorageFragment fragment= new MediaStorageFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        fragment.setArguments(args);
        return fragment;
    }
  
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "++Oncreate++");
        super.onCreate(savedInstanceState);
        //这里我只是简单的用num区别标签，其实具体应用中可以使用真实的fragment对象来作为叶片
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }
    
    /**为Fragment加载布局时调用**/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.e(TAG, "++OncreateView++");
        View view = inflater.inflate(R.layout.playback_list_layout, null);
       mActivity = (MainActivity)getActivity();
        listView = (ListView)view.findViewById(R.id.main_list);
        listView.setOnItemClickListener(mOnItemClickListener);
        listView.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,storelist));
        storelist.add("文件列表:");
        storelist.add("硬盘");
        storelist.add("SD卡");
        storelist.add("U盘");


        return view;
    }
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            //   VideoTabActivity activity = (VideoTabActivity)getActivity();
            //   activity.play(arg2);
//            Log.e("+++++","arg2:"+arg2);//第几个
            Log.e("storelist",storelist.get(arg2));
//            MainActivity activity=(MainActivity) getActivity();
//            activity.play(mediaFilelist.get(arg2));
//            setlistPosion(arg2);
            if(arg2==3)
                mActivity.updatePlaylist(MainActivity.udiskPath);
            else if(arg2==1)
                mActivity.updatePlaylist(MainActivity.externalStoragePath);
            mActivity.switchToPage(2);
        }
    };
}
