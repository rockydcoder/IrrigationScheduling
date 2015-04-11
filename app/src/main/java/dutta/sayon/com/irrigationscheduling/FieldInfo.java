package dutta.sayon.com.irrigationscheduling;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static dutta.sayon.com.irrigationscheduling.URLEndPoints.*;


public class FieldInfo extends ActionBarActivity {
    final static String FIELD_NAME_TAG = "FIELD_NAME";
    TextView tvFieldName, tvPPT, tvProgress;
    Button bEdit;
    ProgressBar progressBar;
    final RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
    final static String SHARED_PREF_PASSPHRASE = "PASSPHRASE";
    private SharedPreferences sharedPreferences;
    static String PASSPHRASE = null;
    final static String SHARED_PREF_FILE_NAME = "dutta.sayon.com.irrigationscheduling.SHARED_PREF";
    String currentField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentField = getIntent().getStringExtra(FIELD_NAME_TAG);
        sharedPreferences = this.getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        PASSPHRASE = sharedPreferences.getString(SHARED_PREF_PASSPHRASE, null);
        setContentView(R.layout.activity_field_info);

        tvFieldName = (TextView) findViewById(R.id.textView4);
        tvPPT = (TextView) findViewById(R.id.tv_ppt);
        tvProgress = (TextView) findViewById(R.id.textView7);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Log.d("Field info url", getURLRequest());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getURLRequest(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("FieldInfo", "Entered respoinse");
                            String ppt = response.getString("precipCM");
                            String pani_percent = response.getString("pani_percent");
                            double percent = Double.parseDouble(pani_percent);
                            if (percent > 100.0)
                                percent = 100.0;
                            progressBar.setProgress((int) percent);
                            tvPPT.setText(ppt);
                            tvProgress.setText(String.valueOf((int) percent));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("FieldInfo", error.getMessage());
                    }
                });

        requestQueue.add(request);
        bEdit = (Button) findViewById(R.id.b_edit);
        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FieldInfo.this, EditField.class);
                intent.putExtra(FIELD_NAME_TAG, currentField);
                startActivity(intent);
            }
        });

        tvFieldName.setText(currentField);
    }

    private String getURLRequest() {
        String url =
                URL_WHAT_TO_DO +
                URL_PASSPHRASE +
                URL_CHAR_EQUAL +
                PASSPHRASE +
                URL_CHAR_AMPERSAND +
                URL_FIELD_NAME +
                URL_CHAR_EQUAL +
                currentField;

        Log.d("Field info url", url);
        return url;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_field_info, menu);
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
}
