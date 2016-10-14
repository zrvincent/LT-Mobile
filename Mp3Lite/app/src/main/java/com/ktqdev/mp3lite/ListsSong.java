package com.ktqdev.mp3lite;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ListsSong extends ListActivity {

    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    ImageView btnBack;
    int totalSong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists_song);



        btnBack = (ImageView) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

        SongProvider plm = new SongProvider();
        // get all songs from sdcard
        this.songsList = plm.getPlayList();

        // looping through playlist
        for (int i = 0; i < songsList.size(); i++) {
            // creating new HashMap
            HashMap<String, String> song = songsList.get(i);

            // adding HashList to ArrayList
            songsListData.add(song);
            totalSong++;
        }
        // Adding menuItems to ListView
        ListAdapter adapter = new SimpleAdapter(this, songsListData,
                R.layout.item_song, new String[] { "songTitle" }, new int[] {
                R.id.songTitle });
        setListAdapter(adapter);


        // selecting single ListView item
        ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting listitem index
                int songIndex = position;
                // Starting new intent
                Intent in = new Intent(ListsSong.this, MainActivity.class);
                // Sending songIndex to MainActivity
                in.putExtra("songIndex", songIndex);
                in.putExtra("totalSong",totalSong);
                setResult(100, in);
                // Closing ListsSong
                finish();
            }
        });


    }
}
