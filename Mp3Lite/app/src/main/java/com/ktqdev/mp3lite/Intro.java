package com.ktqdev.mp3lite;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Intro extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        int secs = 3;
        final Intent i = new Intent(this,MainActivity.class);
        Utilities.delay(secs, new Utilities.DelayCallback() {
            @Override
            public void afterDelay() {
                startActivity(i);
                finish();
            }
        });
    }
}
