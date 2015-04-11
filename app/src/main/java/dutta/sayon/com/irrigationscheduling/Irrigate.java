package dutta.sayon.com.irrigationscheduling;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;


import static dutta.sayon.com.irrigationscheduling.URLEndPoints.*;


public class Irrigate extends ActionBarActivity implements View.OnClickListener {
    TextView tvDate;
    EditText etHours, etMinutes;
    Button bSubmit;
    int year, month, day;
    Calendar calendar;
    Context context = this;
    final static String SHARED_PREF_FILE_NAME = "dutta.sayon.com.irrigationscheduling.SHARED_PREF";
    final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
    static String PASSPHRASE;
    final static String SHARED_PREF_PASSPHRASE = "PASSPHRASE";
    final static String FIELD_NAME_TAG = "FIELD_NAME";
    static String currentField = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigate);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        PASSPHRASE = sharedPreferences.getString(SHARED_PREF_PASSPHRASE, null);
        currentField = getIntent().getStringExtra(FIELD_NAME_TAG);

        tvDate = (TextView) findViewById(R.id.tv_date);
        etHours = (EditText) findViewById(R.id.et_hours);
        etMinutes = (EditText) findViewById(R.id.et_minutes);
        bSubmit = (Button) findViewById(R.id.b_submit);
        bSubmit.setOnClickListener(this);
        tvDate.setOnClickListener(this);

        // Get today's date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month, day);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_irrigate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_date:
                showDialog(999);
                break;

            case R.id.b_submit:
                double hours = Double.parseDouble(etHours.getText().toString().trim());
                double minutes = Double.parseDouble(etMinutes.getText().toString().trim());
                hours = hours + minutes/60;
                String date = tvDate.getText().toString().trim();
                Date date2 = new Date();
                String timeStamp = String.valueOf(date2.getTime());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                        getIrrigateURLRequest(currentField, timeStamp, "irrigation", String.valueOf(hours), date),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("status= ", response.getString("status"));
                                    Intent intent = new Intent(Irrigate.this, FieldList.class);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                requestQueue.add(request);
                break;


        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private String getIrrigateURLRequest(String fieldName, String activityTime, String activityType, String activityCrop, String creationTime) {
        String url =
                        URL_FEED_ACTIVITY +
                        URL_PASSPHRASE +
                        URL_CHAR_EQUAL +
                        PASSPHRASE +
                        URL_CHAR_AMPERSAND +
                        URL_FIELD_NAME +
                        URL_CHAR_EQUAL +
                        fieldName +
                        URL_CHAR_AMPERSAND +
                        URL_ACTIVITY_TIME +
                        URL_CHAR_EQUAL +
                        activityTime +
                        URL_CHAR_AMPERSAND +
                        URL_ACTIVITY_TYPE +
                        URL_CHAR_EQUAL +
                        activityType +
                        URL_CHAR_AMPERSAND +
                        URL_ACTIVITY_CROP +
                        URL_CHAR_EQUAL +
                        activityCrop +
                        URL_CHAR_AMPERSAND +
                        URL_CREATION_TIME +
                        URL_CHAR_EQUAL +
                        creationTime;

        return url;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == 999) {
            return new DatePickerDialog(this, myDatePickerListener, year, month, day);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener myDatePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            showDate(year, monthOfYear, dayOfMonth);
        }
    };

    private void showDate(int year, int month, int day) {
        month += 1;
        String strMonth, strDay;
        if(month < 10)
            strMonth = "0" + month;

        else
            strMonth = month + "";

        if(day < 10)
            strDay = "0" + day;

        else
            strDay = day + "";

        tvDate.setText(new StringBuilder().append(strDay).append("/")
                .append(strMonth).append("/").append(year));
    }
}
