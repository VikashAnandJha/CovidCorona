package com.vaj.covidcorona;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        TextView textViewVersion=(TextView)findViewById(R.id.tvVersion) ;
        textViewVersion.setText("Version "+Config.APP_VERSION);


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {


                        startActivity(new Intent(MainActivity.this,HomeActivity.class));

                        finish();

                    }
                }
                , 2000);
    }
}
