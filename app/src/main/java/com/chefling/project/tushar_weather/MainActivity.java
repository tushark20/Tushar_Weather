package com.chefling.project.tushar_weather;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView date;
    TextView lowT;
    TextView highT;
    TextView title;
    TextView temp;
    static int ONE_MINUTE = 60*1000;
    static String NO_INTERNET_CONNECTION = "com.android.volley.NoConnectionError:";
    static String CHICAGO_WEATHER = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather." +
            "forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%" +
            "22chicago%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    ArrayList<String> list;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        title = (TextView) findViewById(R.id.textCity);
        date = (TextView) findViewById(R.id.textDate);
        temp = (TextView) findViewById(R.id.textTemp);
        lowT = (TextView) findViewById(R.id.textlTemp);
        highT = (TextView) findViewById(R.id.texthTemp);
        getWeatherUpdates();
    }

    public void getWeatherUpdates()
    {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Update_Weather(activity);
            }
        }, 0, ONE_MINUTE);
    }


    public void Update_Weather(final Activity activity)
    {
        list = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = CHICAGO_WEATHER;
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject myObject = new JSONObject(response);
                            list.add(myObject.getJSONObject("query").getJSONObject("results").getJSONObject("channel").get("title").toString());
                            list.add(myObject.getJSONObject("query").getJSONObject("results").getJSONObject("channel").get("lastBuildDate").toString());
                            list.add(myObject.getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONObject("condition").get("temp").toString());
                            list.add(myObject.getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONArray("forecast").getJSONObject(0).get("low").toString());
                            list.add(myObject.getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONArray("forecast").getJSONObject(0).get("high").toString());
                            values(list);
                        }catch (Exception e)
                        {
                            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                DialogBox(error.toString());
            }
        });
        queue.add(stringRequest);
    }

    public void values(ArrayList<String> list)
    {
        if(list.size()>0)
        {
            title.setText(list.get(0).replace("Yahoo! Weather -", ""));
            date.setText(changeDateForm(list.get(1).replace(" CDT", "")));
            temp.setText(list.get(2)+" \u2109");
            lowT.setText("\u2193"+list.get(3)+(char) 0x00B0 );
            highT.setText("\u2191"+list.get(4)+(char) 0x00B0 );

        }
    }

    public String changeDateForm(String dateStr)
    {
        try {
            SimpleDateFormat srcDf = new SimpleDateFormat("E, dd MMM yyyy hh:mm a");

            // parse the date string into Date object
            Date date = srcDf.parse(dateStr);

            SimpleDateFormat destDf = new SimpleDateFormat("MM/dd, hh:mm a");

            // format the date into another format
            dateStr = destDf.format(date);
        }catch (ParseException e) {
            Log.d("TAG", e.toString());
        }
        return dateStr;
    }
    public void DialogBox(String _message)
    {
        if(_message.contains(NO_INTERNET_CONNECTION))
            _message = "There is no Internet connection";
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        dlgAlert.setTitle("Error:");
        dlgAlert.setMessage(_message);
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        moveTaskToBack(true);
                    }
                });
        dlgAlert.create().show();
    }

}
