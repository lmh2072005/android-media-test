package com.example.administrator.mymedia;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.home_tab_layout);
        viewPager = (ViewPager) findViewById(R.id.home_view_pager);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetViewPagerHeight(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        List tabList = new ArrayList<>();
        tabList.add("推荐");
        tabList.add("排行");
        tabList.add("分类");
        tabList.add("歌手");
        tabList.add("MV");

        for(Object tabItem : tabList){
            tabLayout.addTab(tabLayout.newTab().setText(tabItem.toString()));
        }
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        List fragmentList = new ArrayList();

        Fragment f0 = new Fragment_tj();  //推荐
        Fragment f1 = new Fragment_ph();  //排行
        Fragment f2 = new Fragment_fl(); //分类
        Fragment f3 = new Fragment_gs(); //歌手
        Fragment f4 = new Fragment_mv(); //MV

        fragmentList.add(f0);
        fragmentList.add(f1);
        fragmentList.add(f2);
        fragmentList.add(f3);
        fragmentList.add(f4);

        TabFragmentAdept fragmentAdept = new TabFragmentAdept(getSupportFragmentManager(), fragmentList, tabList, this);
        viewPager.setAdapter(fragmentAdept);  //给ViewPager设置适配器
        tabLayout.setupWithViewPager(viewPager);//将TabLayout和ViewPager关联起来。

        tabLayout.setTabsFromPagerAdapter(fragmentAdept);//给Tabs设置适配器, 默认调用fragmentAdept.getPageTitle(i);

    }
    //重新设置viewPager高度
    public void resetViewPagerHeight(int position) {
        /*View child = viewPager.getChildAt(position);
        if (child != null) {
            child.measure(0, 0);
            int h = child.getMeasuredHeight();
            viewPager.setMinimumHeight(h);
        }*/
    }
    public class TabFragmentAdept extends FragmentPagerAdapter {
        private List<Fragment> mFragments;
        private List<String> mTitles;
        private Context context;
        public TabFragmentAdept(android.support.v4.app.FragmentManager supportFragmentManager, List<Fragment> fragmentList, List<String> tabList, Context context) {
            super(supportFragmentManager);
            mFragments = fragmentList;
            mTitles = tabList;
            this.context = context;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);  //style1
        }
    }

    private long lastBackTime = 0;
    private long curBackTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            curBackTime = System.currentTimeMillis();
            if(curBackTime - lastBackTime > 2*1000){
                Toast.makeText(this,"再按一次退出", Toast.LENGTH_SHORT).show();
                lastBackTime = curBackTime;
            }else{
                intent = new Intent(this, MediaService.class);
                stopService(intent);
                MainActivity.this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
