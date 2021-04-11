package axis.axis21.sanchita.axisapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashStart extends AppCompatActivity {

    private TextView animateImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_start);

        TextView presents = findViewById(R.id.presents);
        animateImage = (TextView)findViewById(R.id.main_anim);
        presents.setTranslationX(-3500);

        animateImage.animate().alpha(1).setDuration(2000).setStartDelay(500).start();
        presents.animate().translationXBy(3500).setDuration(800).setStartDelay(1800).start();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashStart.this, Splash.class);
                startActivity(intent);
                finish();
            }
        };

        new Handler(Looper.myLooper()).postDelayed(runnable, 3000);
    }
}