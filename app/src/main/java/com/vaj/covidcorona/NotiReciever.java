package com.vaj.covidcorona;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;


public class NotiReciever extends BroadcastReceiver {
    int cnfCases=0,deltaCnfCases=0,activeCases,deathCases,deltaDeathCases;

    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;

        loadData(context);


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
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void showNotification() {
        builder = new NotificationCompat.Builder(context, "Alert")
                .setSmallIcon(R.drawable.covidcoronalogo)
                .setContentTitle("CovidCorona Alert")
                .setContentText("Total:"+cnfCases+"[+"+deltaCnfCases+"] | ACTIVE:"+activeCases+" | Death:"+deathCases+"[+"+deltaDeathCases+"]")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        createNotificationChannel();
        notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(1, builder.build());
    }

    void loadData(Context ctx)
    {



        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url =Config.BASE_URL+"/data.json";


        Log.d("requesting",url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try{

                            JSONObject jObject = new JSONObject(response);


                            String data=jObject.getString("statewise");

                            JSONArray jArray = jObject.getJSONArray("statewise");
                            JSONObject oneObject = jArray.getJSONObject(0);


                            cnfCases=Integer.parseInt(oneObject.getString("confirmed")+"");
                            deltaCnfCases=Integer.parseInt(oneObject.getString("deltaconfirmed"));



                            activeCases=Integer.parseInt(oneObject.getString("active")+"");




                            deathCases=Integer.parseInt(oneObject.getString("deaths")+"");
                            deltaDeathCases=Integer.parseInt(oneObject.getString("deltadeaths")+"");



                            Log.d("coviddata",deathCases+" -->");


                            if(cnfCases>0){
                                int latestdata=(cnfCases+deathCases);
                                int lastdata=Integer.parseInt(Config.getData(context,"lastdata"));
                                if(latestdata>lastdata)
                                {

                                    Toast.makeText(context, "Covid19 Data Updated", Toast.LENGTH_SHORT).show();
                                    showNotification();
                                    Config.storeData(context,"lastdata",latestdata+"");
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