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
import android.widget.Spinner;
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


public class EditField extends ActionBarActivity implements View.OnClickListener {

    Spinner spCrop;
    TextView tvDate;
    Button bEditField;
    int year, month, day;
    Calendar calendar;
    Context context = this;
    static String currentField = null;
    final static String FIELD_NAME_TAG = "FIELD_NAME";
    final static String SHARED_PREF_FILE_NAME = "dutta.sayon.com.irrigationscheduling.SHARED_PREF";
    final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
    static String PASSPHRASE;
    final static String SHARED_PREF_PASSPHRASE = "PASSPHRASE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = this.getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        PASSPHRASE = sharedPreferences.getString(SHARED_PREF_PASSPHRASE, null);
        currentField = getIntent().getStringExtra(FIELD_NAME_TAG);

        setContentView(R.layout.activity_edit_field);
        spCrop = (Spinner) findViewById(R.id.sp_crop);
        bEditField = (Button) findViewById(R.id.b_edit_field);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvDate.setOnClickListener(this);

        ArrayAdapter<CharSequence> cropAdapter = ArrayAdapter.createFromResource(this, R.array.crop, android.R.layout.simple_spinner_item);
        cropAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCrop.setAdapter(cropAdapter);

        // Get today's date
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month, day);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_field, menu);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_date:
                showDialog(999);
                break;

            case R.id.b_edit_field:
                Log.d("edit field", "entered");
                String crop = spCrop.getSelectedItem().toString();
                String date = tvDate.getText().toString().trim();
                Date date2 = new Date();
                String timeStamp = String.valueOf(date2.getTime());
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                        getEditFieldURLRequest(currentField, timeStamp, "sowing", crop, date),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.d("edit field", response.getString("status"));
                                    Intent intent = new Intent(EditField.this, FieldList.class);
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.getMessage());
                    }
                });

                requestQueue.add(request);
                break;
        }
    }

    private String getEditFieldURLRequest(String fieldName, String activityTime, String activityType, String activityCrop, String creationTime) {
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
