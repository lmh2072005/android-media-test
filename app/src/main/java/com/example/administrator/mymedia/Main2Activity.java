package com.example.administrator.mymedia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    TextView listTitle;
    ImageView listImage;
    ImageButton btnBack;
    ListView listView;
    ArrayList mediaList;
    NetWrokReceiver myReceiver;
    NetworkInfo activeInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listTitle = (TextView) findViewById(R.id.media_list_title);
        listImage = (ImageView) findViewById(R.id.media_list_image);

        btnBack = (ImageButton) findViewById(R.id.btn_back);

        listView = (ListView) findViewById(R.id.lv_media_list);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        listTitle.setText(bundle.getString("itemText"));
        listImage.setImageResource(bundle.getInt("itemImage"));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mediaList = new ArrayList();
        mediaList.add(new BeanData("6929994", "Besame Mucho", "王晰").map);
        mediaList.add(new BeanData("1034671", "Wait For You", "Ina").map);
        mediaList.add(new BeanData("6882123", "从你的全世界路过", "牛奶@咖啡").map);
        mediaList.add(new BeanData("6929992", "爱的箴言", "张信哲").map);
        mediaList.add(new BeanData("6929987", "苦海", "黄致列").map);
        mediaList.add(new BeanData("6896346", "遗忘之前", "徐佳莹").map);
        mediaList.add(new BeanData("6929991", "友情岁月", "李克勤").map);
        mediaList.add(new BeanData("6906502", "一起走过的日子", "李克勤").map);
        SimpleAdapter adept = new SimpleAdapter(
                this,
                mediaList,
                R.layout.media_list_item,
                new String[]{"itemTitle","itemSinger"},
                new int[]{R.id.media_list_item_title, R.id.media_list_item_singer}
        );
        listView.setAdapter(adept);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (activeInfo == null){
                    Toast.makeText(Main2Activity.this, "请连接网络", Toast.LENGTH_SHORT).show();
                    return;
                }
                ListView listView1 = (ListView) adapterView;  //包裹的listview
                HashMap<String,String> map = (HashMap<String,String>) listView1.getItemAtPosition(position);  //当前项的值
                String title = map.get("itemTitle");
                String itemId = map.get("itemId");
                Intent intent = new Intent(Main2Activity.this, MediaPlayActivity.class);
                intent.putExtra("itemId",itemId);
                intent.putExtra("itemTitle",title);
                intent.putExtra("itemSinger",map.get("itemSinger"));
                intent.putExtra("position",position);
                intent.putStringArrayListExtra("mediaList", (ArrayList<String>) mediaList);
                startActivity(intent);
                Toast.makeText(Main2Activity.this, "标题：" + title + ";\n itemId：" + itemId, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        myReceiver = new NetWrokReceiver();
        this.registerReceiver(myReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class BeanData{
        HashMap<String,String> map = new HashMap<String,String>();
        public BeanData(String id, String title, String singer){
            map.put("itemId", id);
            map.put("itemTitle", title);
            map.put("itemSinger", singer);
        }
    }

    public class NetWrokReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            //Toast.makeText(context, intent.getAction(), 1).show();
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            activeInfo = manager.getActiveNetworkInfo(); //如果无网络连接activeInfo为null
            if (activeInfo == null){
                Toast.makeText(context, "请连接网络", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "mobileInfo:"+mobileInfo.isConnected()+"\n"+"wifiInfo:"+wifiInfo.isConnected()+"\n"+"activeInfo:"+activeInfo.getTypeName(), Toast.LENGTH_LONG).show();
            }
        }

    }
}
