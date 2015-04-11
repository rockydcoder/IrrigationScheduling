package dutta.sayon.com.irrigationscheduling;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Random;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;
import me.tatarka.support.os.PersistableBundle;


public class LogIn extends ActionBarActivity implements View.OnClickListener {

    private static final int JOB_ID = 100;
    private EditText etName, etSurname;
    private Button bCreate;
    private Context context = this;
    final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
    final static String ALL_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    public static String PASSPHRASE;
    final static String SHARED_PREF_PASSPHRASE = "PASSPHRASE";
    final static String SHARED_PREF_HAS_LOG_IN = "LOG_IN_STATUS";
    private SharedPreferences sharedPreferences;
    final static String SHARED_PREF_FILE_NAME = "dutta.sayon.com.irrigationscheduling.SHARED_PREF";
    private JobScheduler mJobScheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mJobScheduler = JobScheduler.getInstance(context);
//        constructJob();

        etName = (EditText) findViewById(R.id.et_name);
        etSurname = (EditText) findViewById(R.id.et_surname);
        sharedPreferences = this.getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(SHARED_PREF_HAS_LOG_IN, false))
            changeActivity();

        bCreate = (Button) findViewById(R.id.b_create);
        bCreate.setOnClickListener(this);

//        String message = "Date: 11/04/2015 \nHours to irrigate: 4 \nProjected rainfall for \n next 4 days (cm): 4.4";
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.leaf_small)
//                .setContentTitle("Irrigation Scheduling")
//                .setAutoCancel(true)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
//
//
//
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//// mId allows you to update the notification later on.
//        mNotificationManager.notify(0, mBuilder.build());
    }

    private void constructJob() {
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, NotificationService.class));
        PersistableBundle persistableBundle = new PersistableBundle();
        builder.setPeriodic(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true);

        mJobScheduler.schedule(builder.build());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
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
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.b_create:

                if (isEmpty(etName))
                    Toast.makeText(context, "Name field cannot be empty!!", Toast.LENGTH_SHORT).show();

                else if (isEmpty(etSurname))
                    Toast.makeText(context, "SurName field cannot be empty!!", Toast.LENGTH_SHORT).show();

                else {


                    String name = etName.getText().toString().trim();
                    String surname = etSurname.getText().toString().trim();

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                           requestURL(name, surname),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {
                                        String status = response.getString("status");
                                        if(status.equals("true")) {
                                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString(SHARED_PREF_PASSPHRASE, PASSPHRASE);
                                            editor.commit();
                                            editor.putBoolean(SHARED_PREF_HAS_LOG_IN, true);
                                            editor.commit();
                                            Intent intent = new Intent(LogIn.this, FieldList.class);
                                            startActivity(intent);
                                        }

                                        else
                                            Toast.makeText(context, "Try again!", Toast.LENGTH_SHORT).show();


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Login", error.getMessage());

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





                break;
        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private String requestURL(String name, String surname) {
        String url = "http://irr.quesky.com/signup.php?first_name=" + name + "&last_name=" + surname + "&passphrase=";
        PASSPHRASE = generatePassPhrase();
        url = url + PASSPHRASE;

        return url;
    }

    private String generatePassPhrase() {
        int length = 10;
        Random rng = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = ALL_CHARACTERS.charAt(rng.nextInt(ALL_CHARACTERS.length()));
        }
        Log.d("Passphrase", new String(text));
        return new String(text);
    }

    private void changeActivity() {
        Intent intent = new Intent(LogIn.this, FieldList.class);
        startActivity(intent);
    }
}
