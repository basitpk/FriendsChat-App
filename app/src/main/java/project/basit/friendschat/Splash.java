package project.basit.friendschat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import javax.net.ssl.HandshakeCompletedListener;

import static java.lang.Thread.sleep;

public class Splash extends AppCompatActivity {

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferences = PreferenceManager.getDefaultSharedPreferences(Splash.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = preferences.edit();
                Boolean b = preferences.getBoolean("Visited", false);
                if(!b)
                {
                    editor.putBoolean("Visited",true);
                    editor.commit();
                }
                Intent intent=new Intent(Splash.this,MainActivity.class);
              Splash.this.startActivity(intent);
                Splash.this.finish();
            }
        },2000);

    }
}
