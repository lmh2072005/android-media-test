package com.example.administrator.mymedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Fragment_tj extends Fragment implements Runnable {
    private static final String TAG = "Fragment_tj";
    private ViewPager mBanner;
    private BannerAdapter mBannerAdapter;
    private TextView[] mIndicators;
    private Timer mTimer = new Timer();

    private int mBannerPosition = 0;
    private final int FAKE_BANNER_SIZE = 100;
    private final int DEFAULT_BANNER_SIZE = 5;
    private boolean mIsUserTouched = false;


    private int[] mImagesSrc = {
            R.drawable.banner1,
            R.drawable.banner2,
            R.drawable.banner3,
            R.drawable.banner4,
            R.drawable.banner5
    };

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (!mIsUserTouched) {
                mBannerPosition = (mBannerPosition + 1) % FAKE_BANNER_SIZE;
                getActivity().runOnUiThread((Runnable) getActivity());
                Log.d(TAG, "tname:" + Thread.currentThread().getName());
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_tj, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        //mTimer.schedule(mTimerTask, 5000, 5000);

        setSongNewGv();  //每日新歌

        setSongTjGv(); //歌单推荐
    }

    private void initView() {
        mIndicators = new TextView[] {
                (TextView) getActivity().findViewById(R.id.indicator1),
                (TextView) getActivity().findViewById(R.id.indicator2),
                (TextView) getActivity().findViewById(R.id.indicator3),
                (TextView) getActivity().findViewById(R.id.indicator4),
                (TextView) getActivity().findViewById(R.id.indicator5)
        };
        mBanner = (ViewPager) getActivity().findViewById(R.id.tj_banner_viewPager);
        mBannerAdapter = new BannerAdapter(getActivity());
        mBanner.setAdapter(mBannerAdapter);
        mBanner.setOnPageChangeListener(mBannerAdapter);
        mBanner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN
                        || action == MotionEvent.ACTION_MOVE) {
                    mIsUserTouched = true;
                } else if (action == MotionEvent.ACTION_UP) {
                    mIsUserTouched = false;
                }
                return false;
            }
        });
    }

    private void setIndicator(int position) {
        position %= DEFAULT_BANNER_SIZE;
        for(TextView indicator : mIndicators) {
            indicator.setBackgroundResource(R.drawable.banner_slider_icon_off);
        }
        mIndicators[position].setBackgroundResource(R.drawable.banner_slider_icon_on);
    }

    @Override
    public void run() {
        if (mBannerPosition == FAKE_BANNER_SIZE - 1) {
            mBanner.setCurrentItem(DEFAULT_BANNER_SIZE - 1, false);
        } else {
            mBanner.setCurrentItem(mBannerPosition);
        }
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }

    private class BannerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private LayoutInflater mInflater;

        public BannerAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return FAKE_BANNER_SIZE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position %= DEFAULT_BANNER_SIZE;
            View view = mInflater.inflate(R.layout.banner_slider_item, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.tj_banner_image);
            imageView.setImageResource(mImagesSrc[position]);  //加载本地图片
            final int pos = position;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "click banner item :" + pos, Toast.LENGTH_SHORT).show();
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            int position = mBanner.getCurrentItem();
            Log.d(TAG, "finish update before, position=" + position);
            if (position == 0) {
                position = DEFAULT_BANNER_SIZE;
                mBanner.setCurrentItem(position, false);
            } else if (position == FAKE_BANNER_SIZE - 1) {
                position = DEFAULT_BANNER_SIZE - 1;
                mBanner.setCurrentItem(position, false);
            }
            Log.d(TAG, "finish update after, position=" + position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mBannerPosition = position;
            setIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


    //set song_new gridview
    private void setSongNewGv(){
        TextView gvTitle = (TextView) getActivity().findViewById(R.id.gv_song_type);
        GridView gridView = (GridView) getActivity().findViewById(R.id.gv_song_new);

        gvTitle.setText("每日新歌");

        ArrayList<HashMap<String,Object>> gvData = new ArrayList<HashMap<String, Object>>();
        List newSongIcon = new ArrayList<>();
        newSongIcon.add(R.drawable.new1);
        newSongIcon.add(R.drawable.new2);
        newSongIcon.add(R.drawable.new3);

        List newSongTitle = new ArrayList<>();
        newSongTitle.add("华语");
        newSongTitle.add("欧美");
        newSongTitle.add("日韩");

        for(int i=0;i<newSongIcon.size();i++){
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("ItemImage", newSongIcon.get(i));
            map.put("ItemText", newSongTitle.get(i));
            gvData.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                getActivity(), //content
                gvData,  //data
                R.layout.song_new_item, //gridview item layout
                new String[] {"ItemImage","ItemText"}, //动态数组与ImageItem对应的子项
                new int[] {R.id.gv_song_new_item_image, R.id.gv_song_new_item_text}  //ImageItem的XML文件里面的ImageView,TextView ID
        );
        gridView.setAdapter(simpleAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String,Object> map = (HashMap<String,Object>) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), Main2Activity.class);
                intent.putExtra("itemImage", (Integer) map.get("ItemImage"));
                intent.putExtra("itemText", map.get("ItemText").toString());
                startActivity(intent);
            }
        });
    }

    //歌单推荐
    private void setSongTjGv(){
        TextView gvTitle = (TextView) getActivity().findViewById(R.id.gv_song_tj_type);
        GridView gridView = (GridView) getActivity().findViewById(R.id.gv_song_tj_new);

        gvTitle.setText("歌单推荐");

        ArrayList<HashMap<String,Object>> gvData = new ArrayList<HashMap<String, Object>>();
        List newSongIcon = new ArrayList<>();
        newSongIcon.add(R.drawable.gdtj1);
        newSongIcon.add(R.drawable.gdtj2);
        newSongIcon.add(R.drawable.gdtj3);
        newSongIcon.add(R.drawable.gdtj4);
        newSongIcon.add(R.drawable.gdtj5);
        newSongIcon.add(R.drawable.gdtj6);

        List newSongTitle = new ArrayList<>();
        newSongTitle.add("超好听韩剧主题曲");
        newSongTitle.add("抖腿神曲合集");
        newSongTitle.add("【听歌榜】我们一起陪你走");
        newSongTitle.add("60首麦霸养成攻略");
        newSongTitle.add("90后必听经典歌曲");
        newSongTitle.add("酷我热门单曲");

        for(int i=0;i<newSongIcon.size();i++){
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("ItemImage", newSongIcon.get(i));
            map.put("ItemText", newSongTitle.get(i));
            gvData.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                getActivity(), //content
                gvData,  //data
                R.layout.song_tj_item, //gridview item layout
                new String[] {"ItemImage","ItemText"}, //动态数组与ImageItem对应的子项
                new int[] {R.id.gv_song_tj_item_image, R.id.gv_song_tj_item_text}  //ImageItem的XML文件里面的ImageView,TextView ID
        );
        gridView.setAdapter(simpleAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HashMap<String,Object> map = (HashMap<String,Object>) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), Main2Activity.class);
                intent.putExtra("itemImage", (Integer) map.get("ItemImage"));
                intent.putExtra("itemText", map.get("ItemText").toString());
                startActivity(intent);
            }
        });


    }
}
