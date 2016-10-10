package com.ktqdev.mp3lite;

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
        File musicFolder = new File(Environment.getExternalStorageDirectory().toString() + "/song");

        if (musicFolder.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : musicFolder.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put(
                        "songTitle",
                        file.getName().substring(0,
                                (file.getName().length() - 4)));
                song.put("songPath", file.getPath());
                // Adding each song to SongList
                songsList.add(song);
            }
        }
        // return songs list array
        return songsList;
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
