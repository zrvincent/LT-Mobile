package com.ktqdev.mp3lite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by PhamKhanh on 9/30/2016.
 */
public class SongProvider {
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    // Constructor
    public SongProvider() {

    }

    /**
     * Function to read all mp3 files from sdcard and store the details in
     * ArrayList
     * */
    public ArrayList<HashMap<String, String>> getPlayList() {
        // load file .mp3 trong 1 folder sdcard
        //thay the "/Music1/B" voi path cua ban
        /*
        File musicFolder = new File("/sdcard/Music/");
        System.out.println("error: "+musicFolder);

        if (musicFolder.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : musicFolder.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());
                // Adding each song to SongList
                songsList.add(song);
            }
        }
        else{
            {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", "No song");
                song.put("songPath", "");
                // Adding each song to SongList
                songsList.add(song);
            }
        }


        File[] musicFolder = Environment.getExternalStorageDirectory().listFiles();
        for (File singleFile : musicFolder){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                songsList.addAll(getPlayList());

            }
            else{
                if(singleFile.getName().endsWith(".mp3")){
                    HashMap<String, String> song = new HashMap<String, String>();
                    song.put("songTitle", singleFile.getName().substring(0, (singleFile.getName().length() - 4)));
                    song.put("songPath", singleFile.getPath());
                    // Adding each song to SongList
                    songsList.add(song);
                }
            }
        }*/
        // return songs list array
        ArrayList<File> listTemp = findSong(Environment.getExternalStorageDirectory());
        for (int i = 0; i < listTemp.size(); i++){
            HashMap<String, String> song = new HashMap<String, String>();
            song.put("songTitle", listTemp.get(i).getName().toString().replace(".mp3",""));
            song.put("songPath", listTemp.get(i).getPath().toString());
            // Adding each song to SongList
            songsList.add(song);
        }
        return songsList;
    }

    public ArrayList<File> findSong(File root){
        ArrayList<File> listTemp = new ArrayList<>();
        File[] musicFolder = root.listFiles();
        for (File singleFile : musicFolder){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                listTemp.addAll(findSong(singleFile));

            }
            else{
                if(singleFile.getName().endsWith(".mp3")){

                    listTemp.add(singleFile);
                }
            }
        }
        return listTemp;
    }

    /**
     * The class is used to filter files which are having .mp3 extension
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
