package br.cin.ufpe.soundtracker;

import in.ubee.api.Ubee;
import in.ubee.api.UbeeOptions;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import br.cin.ufpe.soundtracker.utils.Configurations;

public class StartActivity extends Activity {
    
    private static final long SPLASH_SCREEN_DURATION = TimeUnit.SECONDS.toMillis(3);
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.start_activity_layout);

        this.inLocoMediaInitialization();
        
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            public void run() {
                startNextActivity();
            }
        }, SPLASH_SCREEN_DURATION);
        
        View contentView = findViewById(android.R.id.content);
        contentView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startNextActivity();                
            }
        });
    }
    
    private void inLocoMediaInitialization() {
        UbeeOptions options = UbeeOptions.getInstance(this);
        options.setLogEnabled(true);
        options.setMapsKey(Configurations.APP_ID, Configurations.APP_SECRET);
        Ubee.init(this, options);
    }
    
    private void startNextActivity() {
        mTimer.cancel();
        startActivity(new Intent(StartActivity.this, MapActivity.class));
        finish();
    }
}
