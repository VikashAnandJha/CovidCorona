package com.vaj.covidcorona;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Config {

    public static String APP_VERSION="1.0";
    public static String BASE_URL="https://api.covid19india.org/";
    public static String APP_PKG_ID="com.vaj.covidcorona";
    public static String BASE_URL1="http://vikashanandjha.com/apps/covidcorona/";
    public static void storeData(Context ct, String key, String value)
    {
        SharedPreferences sharedpreferences;
        sharedpreferences = ct.getSharedPreferences("userdata", Context.MODE_PRIVATE);


        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(key, value+"");

        editor.commit();


    }

    public static void checkUpdateAndDisplayAlert(final Context ct) {
        RequestQueue queue = Volley.newRequestQueue(ct);
        String url = Config.BASE_URL1 + "checkUpdate.php?version="+Config.APP_VERSION;
        Log.d("requesting",url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ct);

                        builder.setMessage("A New And updated Version Of This app is Available on Google Playstore. Update your App Now!");
                        builder.setIcon(R.drawable.covidcoronalogo);
                        builder.setTitle("New Update Found!!");
                        builder.setPositiveButton("UPDATE APP", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+Config.APP_PKG_ID));
                                ct.startActivity(intent);
                            }
                        });

                        builder.setCancelable(false);
                        if(response.contains("update"))
                            builder.show();




                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

    }
    public static String getLoginHash(Context ctx)
    {
        return Config.getData(ctx,"login_hash");
    }

    public static String getData(Context ct,String key)
    {
        SharedPreferences sharedpreferences;
        sharedpreferences = ct.getSharedPreferences("userdata", Context.MODE_PRIVATE);
        String uid =  sharedpreferences.getString(key,"");
        return uid;

    }
}
