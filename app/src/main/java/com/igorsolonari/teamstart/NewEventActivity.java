package com.igorsolonari.teamstart;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class NewEventActivity extends BaseActivity implements View.OnClickListener,
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    final Calendar c = Calendar.getInstance();
    private EditText eventName;
    private Button eventDate;
    private Button eventTime;
    private EditText email1;
    private EditText lastEmail;
    private TextView saveEvent;
    ProgressDialog pDialog;
    ConstraintLayout mainLayout;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);

        mainLayout = (ConstraintLayout)findViewById(R.id.emailList);

        eventName = findViewById(R.id.eventName);
        saveEvent = findViewById(R.id.save);
        saveEvent.setOnClickListener(this);
        eventDate = findViewById(R.id.eventDate);
        eventDate.setOnClickListener(this);
        int month = c.get(Calendar.MONTH) + 1;
        eventDate.setText(c.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + c.get(Calendar.YEAR));

        eventTime = findViewById(R.id.eventTime);
        eventTime.setOnClickListener(this);
        eventTime.setText(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));

        email1 = findViewById(R.id.email1);
        lastEmail = email1;


        fab = (FloatingActionButton) findViewById(R.id.plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addView();
            }
        });


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.eventDate:
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "DatePickerFragment");
                break;
            case R.id.eventTime:
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "TimePickerFragment");
                break;
            case R.id.save:
                //TODO: send event to server
                JSONArray participants = new JSONArray();
                for (int i = 0; i < mainLayout.getChildCount(); i++) {
                    if (mainLayout.getChildAt(i) instanceof EditText) {
                        EditText emailView = (EditText) mainLayout.getChildAt(i);
                        String email = emailView.getText().toString();
                        participants.put(email);
                    }
                }
                JSONObject postData = new JSONObject();
                try {
                    postData.put("name", eventName.getText().toString());
                    postData.put("date", eventDate.getText().toString() + "T" +
                            eventTime.getText().toString());
                    postData.put("emails", participants);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Write time to time widget
        eventTime.setText(hourOfDay + ":" + minute);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Write date to date widget
        month++;
        eventDate.setText(day + "-" + month + "-" + year);
    }

    public void addView() {
        //TODO: add animation (delayed transition), clone first set and add second
        ConstraintSet set = new ConstraintSet();
        EditText nextEmail = new EditText(this);
        nextEmail.setId(View.generateViewId());
        nextEmail.setHint("add participant");
        nextEmail.setEms(15);
        nextEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        nextEmail.setLayoutParams(params);
        mainLayout.addView(nextEmail,0);
        set.clone(mainLayout);
        //connect next email to last one
        set.connect(nextEmail.getId(), ConstraintSet.TOP, lastEmail.getId(), ConstraintSet.BOTTOM, 8);
        set.connect(nextEmail.getId(), ConstraintSet.LEFT, mainLayout.getId(), ConstraintSet.LEFT, 8);
        set.connect(nextEmail.getId(), ConstraintSet.RIGHT, mainLayout.getId(), ConstraintSet.RIGHT, 8);
        set.connect(fab.getId(), ConstraintSet.TOP, nextEmail.getId(), ConstraintSet.BOTTOM, 8);
        set.applyTo(mainLayout);
        lastEmail = nextEmail;
    }

    public static class TimePickerFragment extends DialogFragment {

        private Activity mActivity;
        private TimePickerDialog.OnTimeSetListener mListener;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;
            // This error will remind you to implement an OnTimeSetListener
            //   in your Activity if you forget
            try {
                mListener = (TimePickerDialog.OnTimeSetListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnTimeSetListener");
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), mListener, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
    }

    public static class DatePickerFragment extends DialogFragment {

        private Activity mActivity;
        private DatePickerDialog.OnDateSetListener mListener;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;
            // This error will remind you to implement an OnTimeSetListener
            //   in your Activity if you forget
            try {
                mListener = (DatePickerDialog.OnDateSetListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnDateSetListener");
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), mListener, year, month, day);
        }
    }

    private class SaveEvent extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog
            pDialog = new ProgressDialog(NewEventActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL("http", "178.168.41.217", 57349, "addevent").openConnection();

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(params[0]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}
