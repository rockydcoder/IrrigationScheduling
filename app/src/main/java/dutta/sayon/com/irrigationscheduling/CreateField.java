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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
import java.util.Calendar;
import java.util.Date;

import static dutta.sayon.com.irrigationscheduling.URLEndPoints.*;


public class CreateField extends ActionBarActivity implements View.OnClickListener {
    Spinner spLocation, spCrop, spSoil, spIrrigation;
    EditText etFieldName, etArea, etCapacity, etPWP, etMAD, etPumpPower, etPumpCapacity;
    Button bCreateField;
    TextView tvDate;
    int year, month, day;
    final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();

    Calendar calendar;
    Context context = this;
    final static String FIELD_NAME_TAG = "FIELD_NAME";
    static final int CREATE_FIELD_REQUEST = 1;  // The request code

    static String PASSPHRASE;
    final static String SHARED_PREF_PASSPHRASE = "PASSPHRASE";
    final static String SHARED_PREF_FILE_NAME = "dutta.sayon.com.irrigationscheduling.SHARED_PREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_field);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        PASSPHRASE = sharedPreferences.getString(SHARED_PREF_PASSPHRASE, null);

        spLocation = (Spinner) findViewById(R.id.sp_location);
        spCrop = (Spinner) findViewById(R.id.sp_crop);
        spSoil = (Spinner) findViewById(R.id.sp_soil);
        spIrrigation = (Spinner) findViewById(R.id.sp_irrigation);

        etFieldName = (EditText) findViewById(R.id.et_field_name);
        etArea = (EditText) findViewById(R.id.et_area);
        etCapacity = (EditText) findViewById(R.id.et_field_capacity);
        etPWP = (EditText) findViewById(R.id.et_pwp);
        etMAD = (EditText) findViewById(R.id.et_mad);
        etPumpPower = (EditText) findViewById(R.id.et_pump);
        etPumpCapacity = (EditText) findViewById(R.id.et_capacity);

        tvDate = (TextView) findViewById(R.id.tv_date);
        tvDate.setOnClickListener(this);

        bCreateField = (Button) findViewById(R.id.b_create_field);
        bCreateField.setOnClickListener(this);

        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocation.setAdapter(locationAdapter);

        ArrayAdapter<CharSequence> cropAdapter = ArrayAdapter.createFromResource(this, R.array.crop, android.R.layout.simple_spinner_item);
        cropAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCrop.setAdapter(cropAdapter);

        ArrayAdapter<CharSequence> soilAdapter = ArrayAdapter.createFromResource(this, R.array.soil, android.R.layout.simple_spinner_item);
        soilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSoil.setAdapter(soilAdapter);

        ArrayAdapter<CharSequence> irrigationAdapter = ArrayAdapter.createFromResource(this, R.array.irrigation, android.R.layout.simple_spinner_item);
        irrigationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spIrrigation.setAdapter(irrigationAdapter);

        // Get today's date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month, day);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_field, menu);
        return true;
    }

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

    @Override
    protected void onStop() {
        super.onStop();
        finish();
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

            case R.id.b_create_field:
                String name = etFieldName.getText().toString().trim();
                String location = spLocation.getSelectedItem().toString();
                String area = etArea.getText().toString().trim();
                String crop = spCrop.getSelectedItem().toString();
                String date = tvDate.getText().toString();
                String soil = spSoil.getSelectedItem().toString();
                String pwp = null, mad = null, fieldCap = null;
                if (isEmpty(etCapacity)) {
                    switch (spSoil.getSelectedItemPosition()) {
                        case 0:
                            fieldCap = "10";
                            break;

                        case 1:
                            fieldCap = "12";
                            break;

                        case 2:
                            fieldCap = "18";
                            break;

                        case 3:
                            fieldCap = "27";
                            break;

                        case 4:
                            fieldCap = "28";
                            break;

                        case 5:
                            fieldCap = "36";
                            break;

                        case 6:
                            fieldCap = "31";
                            break;

                        case 7:
                            fieldCap = "30";
                            break;

                        case 8:
                            fieldCap = "36";
                            break;

                        case 9:
                            fieldCap = "38";
                            break;

                        case 10:
                            fieldCap = "41";
                            break;

                        case 11:
                            fieldCap = "42";
                            break;

                    }
                }

                else
                    fieldCap = etCapacity.getText().toString().trim();

                if (isEmpty(etPWP)) {
                    switch(spSoil.getSelectedItemPosition()) {
                        case 0:
                            pwp = "5";
                            break;

                        case 1:
                            pwp = "5";
                            break;

                        case 2:
                            pwp = "8";
                            break;

                        case 3:
                            pwp = "17";
                            break;

                        case 4:
                            pwp = "14";
                            break;

                        case 5:
                            pwp = "25";
                            break;

                        case 6:
                            pwp = "11";
                            break;

                        case 7:
                            pwp = "6";
                            break;

                        case 8:
                            pwp = "22";
                            break;

                        case 9:
                            pwp = "22";
                            break;

                        case 10:
                            pwp = "27";
                            break;

                        case 11:
                            pwp = "30";
                            break;
                    }
                }

                else
                    pwp = etPWP.getText().toString().trim();

                if (isEmpty(etMAD))
                    mad = "50";
                else
                    mad = etMAD.getText().toString().trim();

                String hp = etPumpPower.getText().toString().trim();
                String pumpCap = etPumpCapacity.getText().toString().trim();
                String irrigation = spIrrigation.getSelectedItem().toString();
                Date date2 = new Date();
                String timeStamp = String.valueOf(date2.getTime());

                final JsonObjectRequest secondRequest = new JsonObjectRequest(Request.Method.GET,
                        getSecondURLRequest(name, timeStamp, "sowing", crop, date),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("Second response", response.getString("status"));
                                    Intent intent = new Intent(CreateField.this, FieldList.class);
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

                Log.d("First response url", getFirstURLRequest(name, location, area, mad, pwp, fieldCap, pumpCap, hp, irrigation, soil));
                JsonObjectRequest firstRequest = new JsonObjectRequest(Request.Method.GET,
                        getFirstURLRequest(name, location, area, mad, pwp, fieldCap, pumpCap, hp, irrigation, soil),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String status =response.getString("status");
                                    Log.d("First response", status);
                                    if (status.equals("true"))
                                        requestQueue.add(secondRequest);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError)
                            Toast.makeText(context, "No Network Connection", Toast.LENGTH_SHORT).show();

                        else if (error instanceof AuthFailureError)
                            Toast.makeText(context, "Authentication Failure Error", Toast.LENGTH_SHORT).show();

                        else if (error instanceof ServerError)
                            Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show();

                        else if (error instanceof NetworkError)
                            Toast.makeText(context, "No Network Connection", Toast.LENGTH_SHORT).show();

                        else if (error instanceof ParseError)
                            Toast.makeText(context, "Parsing Error", Toast.LENGTH_SHORT).show();
                    }
                });



                requestQueue.add(firstRequest);
                Log.d("Second request url", getSecondURLRequest(name, timeStamp, "sowing", crop, date));


//                requestQueue.add(secondRequest);

                break;

        }

    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private String getFirstURLRequest(String fieldName, String location, String area, String mad, String pwp, String fieldCap, String pumpCap, String hp, String irrigaion, String soil) {
        String url =
                URL_CREATE_FIELD +
                URL_FIELD_NAME +
                URL_CHAR_EQUAL +
                fieldName +
                URL_CHAR_AMPERSAND +
                URL_LOCATION +
                URL_CHAR_EQUAL +
                location +
                URL_CHAR_AMPERSAND +
                URL_FIELD_AREA +
                URL_CHAR_EQUAL +
                area +
                URL_CHAR_AMPERSAND +
                URL_PASSPHRASE +
                URL_CHAR_EQUAL +
                PASSPHRASE +
                URL_CHAR_AMPERSAND +
                URL_MAD +
                URL_CHAR_EQUAL +
                mad +
                URL_CHAR_AMPERSAND +
                URL_PWP +
                URL_CHAR_EQUAL +
                pwp +
                URL_CHAR_AMPERSAND +
                URL_FIELD_CAPACITY +
                URL_CHAR_EQUAL +
                fieldCap +
                URL_CHAR_AMPERSAND +
                URL_PUMP_CAPACITY +
                URL_CHAR_EQUAL +
                pumpCap +
                URL_CHAR_AMPERSAND +
                URL_PUMP_POWER +
                URL_CHAR_EQUAL +
                hp +
                URL_CHAR_AMPERSAND +
                URL_IRRIGATION_TYPE +
                URL_CHAR_EQUAL +
                irrigaion +
                URL_CHAR_AMPERSAND +
                URL_SOIL_TYPE +
                URL_CHAR_EQUAL +
                soil;

        return url;
    }

    private String getSecondURLRequest(String fieldName, String activityTime, String activityType, String activityCrop, String creationTime) {
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
}
