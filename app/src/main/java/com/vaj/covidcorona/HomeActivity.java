package com.vaj.covidcorona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {

    TextView tvCnf,tvDeltaCnf,tvActive,tvDeaths,tvDeltaDeaths,tvLastUpdate;

    int cnfCases=0,deltaCnfCases=0,activeCases,deathCases,deltaDeathCases;

    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        tvCnf=findViewById(R.id.tvCnf);
        tvDeltaCnf=findViewById(R.id.tvDeltaCnf);
        tvActive=findViewById(R.id.tvActive);
        tvDeaths=findViewById(R.id.tvDeath);
        tvDeltaDeaths=findViewById(R.id.tvDeltaDeath);
        tvLastUpdate=findViewById(R.id.tvLastUpdatedTime);


Config.storeData(this,"lastdata",0+"");

        loadData(this);


        findViewById(R.id.refreshBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadData(HomeActivity.this);
            }
        });





        Config.checkUpdateAndDisplayAlert(this);



        Intent notificationIntent = new Intent( this, NotiReciever. class ) ;

        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        long futureInMillis = SystemClock. elapsedRealtime () + 5 ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;
      //  alarmManager.setRepeating(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent); ;

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                10*1000, pendingIntent);




    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alert";
            String description = "Display Latest Data";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Alert", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void showNotification() {
        builder = new NotificationCompat.Builder(this, "Alert")
                .setSmallIcon(R.drawable.covidcoronalogo)
                .setContentTitle("CovidCorona Alert")
                .setContentText("Total:"+cnfCases+"[+"+deltaCnfCases+"] | ACTIVE:"+activeCases+" | Death:"+deathCases+"[+"+deltaDeathCases+"]")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
    }


    void loadData(Context ctx)
    {

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.mainArea).setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url =Config.BASE_URL+"/data.json";


        Log.d("requesting",url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try{
                            findViewById(R.id.mainArea).setVisibility(View.VISIBLE);

                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            JSONObject jObject = new JSONObject(response);


                            String data=jObject.getString("statewise");

                            JSONArray jArray = jObject.getJSONArray("statewise");
                            JSONObject oneObject = jArray.getJSONObject(0);


                            cnfCases=Integer.parseInt(oneObject.getString("confirmed")+"");
                            deltaCnfCases=Integer.parseInt(oneObject.getString("deltaconfirmed"));

                            tvCnf.setText(cnfCases+"");
                            tvDeltaCnf.setText("+"+deltaCnfCases+"");


                            activeCases=Integer.parseInt(oneObject.getString("active")+"");


                            String lastUpdate=oneObject.getString("lastupdatedtime")+"";

                            tvLastUpdate.setText("Last Updated On: "+lastUpdate+"");

                            tvActive.setText(activeCases+"");


                            deathCases=Integer.parseInt(oneObject.getString("deaths")+"");
                            deltaDeathCases=Integer.parseInt(oneObject.getString("deltadeaths")+"");
                            tvDeltaDeaths.setText("+"+deltaDeathCases+"");
                            tvDeaths.setText(deathCases+"");
                            tvDeltaDeaths.setText("+"+deltaDeathCases+"");




                            Log.d("coviddata",deathCases+" -->");

                            if(cnfCases>0){
                                int latestdata=(cnfCases+deathCases);
                                int lastdata=Integer.parseInt(Config.getData(HomeActivity.this,"lastdata"));
                                if(latestdata>lastdata)
                                {
                                    Toast.makeText(HomeActivity.this, "Latest:"+latestdata+" last:"+lastdata, Toast.LENGTH_SHORT).show();
                                    showNotification();
                                    Config.storeData(HomeActivity.this,"lastdata",latestdata+"");
                                }

                            }



                        }catch (Exception e)
                        {

                        }


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
