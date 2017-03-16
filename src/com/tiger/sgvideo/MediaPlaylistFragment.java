package com.tiger.sgvideo;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 用于创建Fragment对象，作为ViewPager的叶片

 *
 */
@SuppressLint("DefaultLocale")
public class MediaPlaylistFragment extends Fragment {
    private final String TAG = MediaPlaylistFragment.class.getSimpleName();
    private ListView listView;//new
    public  List<String> mediaFilelist = new ArrayList<String>();//new
    public  int listPosion=0;
    private Boolean isFisrtStart=true;
    int mNum; //页号
    public int getlistPosion(){
        return  this.listPosion;
    }
    public  void setlistPosion(int listPosion){
        this.listPosion=listPosion;
    }
    public List<String> getMediaList(){
        return this.mediaFilelist;
    }



    public static MediaPlaylistFragment newInstance(int num) {
        MediaPlaylistFragment fragment = new MediaPlaylistFragment();
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
        View view = inflater.inflate(R.layout.playback_list_layout, container, false);
        listView = (ListView)view.findViewById(R.id.main_list);
        listView.setOnItemClickListener(mOnItemClickListener);


            // listView.setAdapter(new ArrayAdapter<String>(getContext(),R.id.,mediaFilelist));
            // mediaFilelist.addAll(getfileDir("/sdcard"));
           // new ListFileTask("/storage", mediaFilelist).execute("Hello");

//            mediaFilelist.add("歌曲列表:");//报错

//            mediaFilelist.clear();
//            new ListFileTask(MainActivity.externalStoragePath, mediaFilelist).execute("Hello");
        updatePlaylist(MainActivity.externalStoragePath);
           //listView.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,mediaFilelist));
            return view;
    }

    public void updatePlaylist(String filepath){
        try{
//            if(filepath){
//                Log.e("filepath.isEmpty()","filepath.isEmpty()");
//                Toast.makeText(getContext(),filepath+"为空",Toast.LENGTH_SHORT).show();
//                return;
//
//            }
            mediaFilelist.clear();
            new ListFileTask(filepath, mediaFilelist).execute("Hello");
            Toast.makeText(getActivity(),filepath,Toast.LENGTH_SHORT).show();
//            Toast.makeText(MainActivity.this,externalStoragePath,Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex){
            Log.e("filepath.isEmpty()","filepath.isEmpty()");
            Toast.makeText(getActivity(),filepath+"不存在或没有视频",Toast.LENGTH_SHORT).show();
        }

    }
    //input: absolutePath  eg:  /sdcard
    public List<String> getfileDir(String Path ) {

        File file=new File(Path);
//        List<String> temFilelist = new ArrayList<String>();

        File[] subFile=file.listFiles();
        String name = file.getName().toLowerCase();
        String type = name.substring(name.lastIndexOf(".") + 1);
        for (File f : subFile) {
            // temFilelist.add(f.getPath());

            if(f.isDirectory())
            {
                getfileDir(f.getPath());
            }
            else{//是一个wenjian
                name = f.getName().toLowerCase();
                type = name.substring(name.lastIndexOf(".") + 1);
                //temFilelist.add("FILE:"+f.getName());//
                //temFilelist.add("type:"+type);

                if(type.equals("mp4")) {
                     mediaFilelist.add(f.getPath());
                    // temFilelist.add(f.getPath());
//                    Log.e("----------",f.getPath());
                }
                else  if(type.equals("avi")) {
                    mediaFilelist.add(f.getPath());
//                     temFilelist.add(f.getPath());
//                    Log.e("----------",f.getPath());
                }

            }
        }
        return mediaFilelist;
       // return temFilelist;
    }
    //
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
         //   VideoTabActivity activity = (VideoTabActivity)getActivity();
         //   activity.play(arg2);
//            Log.e("+++++","arg2:"+arg2);//第几个
            Log.e("+++++","filepath"+mediaFilelist.get(arg2));
            MainActivity activity=(MainActivity) getActivity();
            activity.play(mediaFilelist.get(arg2));
            setlistPosion(arg2);


        }
    };


    private class ListFileTask extends AsyncTask<String, Integer, List<String>> {

        private List<String> newGetfileDir(String Path ) {

            File file=new File(Path);

            if(file.list()==null)
                return mediaFilelist;
            File[] subFile=file.listFiles();
            String name = "";
            String type = "";
            for (File f : subFile) {
                // temFilelist.add(f.getPath());
                if(f.isDirectory())
                {
                    getfileDir(f.getPath());
                }
                else{//是一个wenjian
                    name = f.getName().toLowerCase();
                    type = name.substring(name.lastIndexOf(".") + 1);
                    if(type.equals("mp4")) {
                        mediaFilelist.add(f.getPath());
                        Log.e("#########",f.getPath());
                        i++;
                        publishProgress(i);
                    }
                    else  if(type.equals("avi")) {
                        mediaFilelist.add(f.getPath());
                        i++;
                        Log.e("#########",f.getPath());
                        publishProgress(i);
                    }
                }
            }
            return mediaFilelist;

        }
        private int i=0;

        private String filepath_as;

        public ListFileTask(String filepath,List<String> listArry){
            this.filepath_as=filepath;
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            Log.e("+++++","doInback");
            //if(!mediaFilelist.isEmpty())
            //mediaFilelist.clear();
            mediaFilelist=(newGetfileDir(filepath_as)) ;
            return mediaFilelist;
            //listArry=(getfileDir(listArry,filepath_as)) ;
            //return listArry;
        }

        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            listView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,mediaFilelist));
            Log.e("#########",values[0].toString());
//
            MainActivity activity0=(MainActivity)getActivity();
            if(isFisrtStart)
            {
                activity0.hideLoading();
                activity0.play(mediaFilelist.get(0));
                isFisrtStart=false;
            }
            Log.e("+++++","onProgressUpdate");
            //super.Integer(values);
        }

        @Override
        protected void onPreExecute() {

            Log.e("+++++","onPreExrcute");
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(List<String> result) {
            Log.e("+++++","onPostExecute");
            listView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, mediaFilelist));
//            mediaFilelist=result;
        }
    }

}
