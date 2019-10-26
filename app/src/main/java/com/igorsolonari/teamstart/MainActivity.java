package com.igorsolonari.teamstart;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    protected String[] myDataset;
    private static final int DATASET_COUNT = 60;
    private String TAG = "main_activity";
    private ArrayList<Event> eventList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewEventActivity.class));
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        new GetEvents().execute();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.sign_out) {
            // Firebase sign out
            mAuth.signOut();

            // Google sign out
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        }
                    });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private class GetEvents extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /* Showing progress dialog
            pDialog = new ProgressDialog(Maggy.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
            */
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String json = null;

            try {
                InputStream is = MainActivity.this.getAssets().open("test_list.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
                Log.d(TAG, "opened file");
            } catch (IOException e) {
                Log.e(TAG, "cant open file: ", e);
            }

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);

                    // Getting JSON Array node
                    JSONArray events = jsonObj.getJSONArray("events");

                    for(int i=0; i < events.length(); i++) {

                        //Get event object
                        JSONObject eventObject = events.getJSONObject(i);
                        //Create new event
                        Event event = new Event();
                        //Set name of the event
                        event.setName(eventObject.getString("name"));
                        //Parse date string to Date object
                        String dateString = eventObject.getString("date");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm",
                                getApplicationContext().getResources().getConfiguration().locale);
                        Date date = dateFormat.parse(dateString);
                        //Set date to event
                        event.setDate(date);

                        //Create a arraylist of participants
                        ArrayList<Participant> partList = new ArrayList<>();
                        //Get participants array
                        JSONArray participants = eventObject.getJSONArray("participants");
                        //Get all participants of the event
                        for(int j=0; j < participants.length(); j++) {
                            JSONObject participant = participants.getJSONObject(j);
                            String email = participant.getString("email");
                            int status = participant.getInt("status");
                            Participant part = new Participant(email, status);
                            partList.add(part);
                        }
                        event.addParticipants(partList);
                        eventList.add(event);
                    }

                    Log.d(TAG, "List created: " + events.toString());

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "Couldn't get json file.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            /* Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            */
            // specify an adapter (see also next example)
            RecyclerView.Adapter mAdapter = new MyAdapter(eventList);
            mRecyclerView.setAdapter(mAdapter);
            Log.d(TAG, "Adapter set");
        }
    }
}
