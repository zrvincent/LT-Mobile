package com.ktqdev.mp3lite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class MainActivity extends AppCompatActivity
        implements OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    private ImageView btnList;
    private ImageView btnPlay;
    private ImageView btnNext;
    private ImageView btnPrev;
    private ImageView btnRepeat;
    private ImageView btnShuffle;
    private ImageView songThumbnail;
    private TextView songTitleLabel;
    private SeekBar songProgressBar;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private MediaPlayer mp;
    private TextView numberSong;

    MediaMetadataRetriever metaRetriver;
    byte[] art;


    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    private SongProvider songProvider;
    private Utilities utils;
    private int currentSongIndex = 0;
    private int totalSong = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;

    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.about:
                Intent i = new Intent(this,About.class);
                startActivity(i);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnList = (ImageView) findViewById(R.id.btnList);
        btnPlay = (ImageView) findViewById(R.id.btnPlay);
        btnNext = (ImageView) findViewById(R.id.btnNext);
        btnPrev = (ImageView) findViewById(R.id.btnPrev);
        btnRepeat = (ImageView) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageView) findViewById(R.id.btnShuffle);

        songThumbnail = (ImageView) findViewById(R.id.songThumbnail);

        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        numberSong = (TextView) findViewById(R.id.numberSong_txt);
        songProgressBar = (SeekBar) findViewById(R.id.seekbarProgress);
        songCurrentDurationLabel = (TextView) findViewById(R.id.currentTime_txt);
        songTotalDurationLabel = (TextView) findViewById(R.id.totalTime_txt);

        // Mediaplayer
        mp = new MediaPlayer();
        songProvider = new SongProvider();
        utils = new Utilities();

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important


        // Getting all songs list
        songsList = songProvider.getPlayList();

        // By default play first song
        SongProvider plm = new SongProvider();
        this.songsList = plm.getPlayList();
        // looping through playlist
        for (int i = 0; i < songsList.size(); i++) {
            totalSong++;
        }
        startApp(0,totalSong);

        btnList.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(MainActivity.this, ListsSong.class);
                startActivityForResult(i, 100);
            }
        });

        btnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // check for already playing
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.play_default);
                        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    // Resume song
                    if(mp!=null){
                        mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.pause_default);
                        Toast.makeText(getApplicationContext(), "Playing", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    // shuffle is on - play a random song
                    Random rand = new Random();
                    currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                    playSong(currentSongIndex,totalSong);
                } else{
                    // no repeat or shuffle ON - play next song
                    if(currentSongIndex < (songsList.size() - 1)){
                        playSong(currentSongIndex + 1,totalSong);
                        currentSongIndex = currentSongIndex + 1;
                    }else{
                        // play first song
                        playSong(0,totalSong);
                        currentSongIndex = 0;
                    }
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    // shuffle is on - play a random song
                    Random rand = new Random();
                    currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                    playSong(currentSongIndex,totalSong);
                } else{
                    // no repeat or shuffle ON - play next song
                    if(currentSongIndex > 0){
                        playSong(currentSongIndex - 1,totalSong);
                        currentSongIndex = currentSongIndex - 1;
                    }else{
                        playSong(songsList.size() - 1,totalSong);
                        currentSongIndex = songsList.size() - 1;
                    }
                }
            }
        });

        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.repeat_default);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.repeat_focused);
                    btnShuffle.setImageResource(R.drawable.shuffle_default);
                }
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.shuffle_default);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.repeat_default);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            currentSongIndex = data.getIntExtra("songIndex", 0);
            totalSong = data.getIntExtra("totalSong",0);

            // play selected song
            playSong(currentSongIndex, totalSong);
        }
    }

    public void playSong(int songIndex, int total) {
        // Play song
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();
            metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(songsList.get(songIndex).get("songPath"));
            art = metaRetriver.getEmbeddedPicture();
            if(art != null)
            {
                InputStream is = new ByteArrayInputStream(metaRetriver.getEmbeddedPicture());
                Bitmap bm = BitmapFactory.decodeStream(is);
                songThumbnail.setImageBitmap(bm);
            }
            else
            {
                songThumbnail.setImageResource(R.drawable.songbg);
            }

            // Displaying Song title
            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);

            numberSong.setText(songIndex + 1 + " of "+total);

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.pause_default);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startApp(int songIndex, int total) {
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();
            mp.pause();
            metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(songsList.get(songIndex).get("songPath"));
            art = metaRetriver.getEmbeddedPicture();
            if(art != null)
            {
                InputStream is = new ByteArrayInputStream(metaRetriver.getEmbeddedPicture());
                Bitmap bm = BitmapFactory.decodeStream(is);
                songThumbnail.setImageBitmap(bm);
            }
            else
            {
                songThumbnail.setImageResource(R.drawable.songbg);
            }

            // Displaying Song title
            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);

            numberSong.setText(songIndex + 1 + " of "+total);

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.play_default);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);
            updateProgressBar();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            //try{
                long totalDuration = mp.getDuration();
                long currentDuration = mp.getCurrentPosition();

                // Displaying Total Duration time
                songTotalDurationLabel.setText("- " + utils.milliSecondsToTimer(totalDuration-currentDuration));
                // Displaying time completed playing
                songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                songProgressBar.setProgress(progress);

                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100);
            //}catch (RuntimeException e){ e.printStackTrace();}
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong(currentSongIndex,totalSong);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex,totalSong);
        } else{
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1,totalSong);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                playSong(0,totalSong);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mp.release();
    }

}
