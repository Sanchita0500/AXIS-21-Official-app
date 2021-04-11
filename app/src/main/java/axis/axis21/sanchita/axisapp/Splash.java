package axis.axis21.sanchita.axisapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    private ImageView logo;
    private LinearLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        logo = (ImageView)findViewById(R.id.splash_anim_logo);
        loader = (LinearLayout)findViewById(R.id.splash_loader);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, Home.class);
                startActivity(intent);
                finish();
            }
        };

        new Handler(Looper.myLooper()).postDelayed(runnable, 2500);

        animate();
    }

    private void animate() {
        loader.animate().scaleXBy(300).setDuration(2000);
        //logo.animate().rotation(150).setDuration(3000);
    }
}