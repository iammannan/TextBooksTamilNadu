package mannan.textbookstamilnadu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    public static int splashtime=1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent go_to_main_act = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(go_to_main_act);
                finish();
            }
        }, splashtime);
		
    }

	}
