package dutta.sayon.com.irrigationscheduling;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static dutta.sayon.com.irrigationscheduling.URLEndPoints.*;
//import static dutta.sayon.com.irrigationscheduling.LogIn.PASSPHRASE;


public class FieldList extends ActionBarActivity implements View.OnClickListener, CustomAdapter.ViewOnClickListener {

    private Button bCreate;
    private RecyclerView recyclerView;
    private CustomAdapter customAdapter;
    private Context context = this;
    static final int CREATE_FIELD_REQUEST = 1;  // The request code
    final static String FIELD_NAME_TAG = "FIELD_NAME";
    List<String> dataList = new ArrayList<>();
    String currentField;
    Dialog dialog;
    final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
    final static String SHARED_PREF_PASSPHRASE = "PASSPHRASE";
    private SharedPreferences sharedPreferences;
    static String PASSPHRASE = null;
    final static String SHARED_PREF_FILE_NAME = "dutta.sayon.com.irrigationscheduling.SHARED_PREF";
    int year, month, day;
    Calendar calendar;
    int notificationId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_list);
        sharedPreferences = this.getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        PASSPHRASE = sharedPreferences.getString(SHARED_PREF_PASSPHRASE, null);
        Log.d("Passphrase = ", PASSPHRASE);

        // Get today's date
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getURLFieldList(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        Log.d("array", "response");
                        try {
//                            Log.d("try", "entered");
                            JSONArray items = response.getJSONArray("data");
                            Log.d("items length = ", String.valueOf(items.length()));
                            for(int i = 0; i < items.length(); i++) {
//                                Log.d("inside", "forloop");
                                JSONObject current = items.getJSONObject(i);
                                dataList.add(current.getString("field_name"));
                                Log.d("field name", dataList.get(i));
                                customAdapter.notifyDataSetChanged();

                            }
                            getNotification();
                        } catch (JSONException e) {
                            Log.d("try catch", "error");
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

        requestQueue.add(request);

        bCreate = (Button) findViewById(R.id.b_create_field);
        recyclerView = (RecyclerView) findViewById(R.id.fields_list);
        customAdapter = new CustomAdapter(context, getData());
        recyclerView.setAdapter(customAdapter);
        customAdapter.setViewOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        bCreate.setOnClickListener(this);
    }

    private String getURLFieldList() {
        return URL_FIELD_LIST +
                URL_PASSPHRASE +
                URL_CHAR_EQUAL +
                PASSPHRASE;
    }

    private List<String> getData() {


//        Log.d("Inside getData", "now");
        return dataList;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_field_list, menu);
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
        switch(v.getId()) {
            case R.id.b_create_field:

                Intent intent = new Intent(FieldList.this, CreateField.class);
                startActivity(intent);
                break;

            case R.id.b_field_info:
                dialog.dismiss();
                Intent intentFieldInfo = new Intent(FieldList.this, FieldInfo.class);
                intentFieldInfo.putExtra(FIELD_NAME_TAG, currentField);
                startActivity(intentFieldInfo);
                break;

            case R.id.b_irrigate:
                dialog.dismiss();
                Intent intentIrrigate = new Intent(FieldList.this, Irrigate.class);
                intentIrrigate.putExtra(FIELD_NAME_TAG, currentField);
                startActivity(intentIrrigate);
                break;
        }

    }


    @Override
    public void onItemClick(View item, int position) {
        currentField = dataList.get(position);
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);


        Button bFieldInfo, bIrrigate;
        bFieldInfo = (Button) dialog.findViewById(R.id.b_field_info);
        bIrrigate = (Button) dialog.findViewById(R.id.b_irrigate);
        bFieldInfo.setOnClickListener(this);
        bIrrigate.setOnClickListener(this);
        dialog.show();

    }

    private void getNotification() {
        for(int i = 0; i < dataList.size(); i++) {
            final String field = dataList.get(i);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    getURLRequest(field),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String ppt = response.getString("precipCM");
                                String pani_hours = response.getString("pani_hours");
                                String date = "Date: " + day + "/" + month + "/" + year + " ";
                                String hours = "\nHours to irrigate: " + pani_hours + " ";
                                String rainfall = "\nProjected rainfall for \nnext 4 days (cm): " + ppt;
                                createNotification(date + hours + rainfall, field);
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

            requestQueue.add(request);
        }
    }

    private String getURLRequest(String field) {
        String url =
                URL_WHAT_TO_DO +
                        URL_PASSPHRASE +
                        URL_CHAR_EQUAL +
                        PASSPHRASE +
                        URL_CHAR_AMPERSAND +
                        URL_FIELD_NAME +
                        URL_CHAR_EQUAL +
                        field;

//        Log.d("Field info url", url);
        return url;

    }

    private void createNotification(String message, String field) {
//        String message = "Date: 11/04/2015 \nHours to irrigate: 4 \nProjected rainfall for \n next 4 days (cm): 4.4";

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.leaf_small)
                        .setContentTitle(field)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message));



        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(notificationId++, mBuilder.build());
    }
}
