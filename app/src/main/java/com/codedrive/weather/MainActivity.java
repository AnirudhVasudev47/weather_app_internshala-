package com.codedrive.weather;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextInputEditText mobile, name, dob, address1, address2, pinCode;
    AutoCompleteTextView genderText;
    TextView state, district;
    Button checkButton, registerButton;
    TextInputLayout gender;
    ArrayList<String> genderList;
    ArrayAdapter<String> genderAdapter;


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (checkFields()){
                checkButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Register");


        mobile = findViewById(R.id.mobileText);
        name = findViewById(R.id.fullNameText);
        dob = findViewById(R.id.dobText);
        address1 = findViewById(R.id.addressLineOneText);
        address2 = findViewById(R.id.addressLineTwoText);
        pinCode = findViewById(R.id.pinCodeText);
        gender = findViewById(R.id.gender);
        genderText = findViewById(R.id.genderText);
        checkButton = findViewById(R.id.checkButton);
        registerButton = findViewById(R.id.registerButton);
        state = findViewById(R.id.stateTextView);
        district = findViewById(R.id.districtTextView);

        genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");

        genderAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, genderList);
        genderText.setAdapter(genderAdapter);
        genderText.setThreshold(1);

        mobile.addTextChangedListener(textWatcher);
        name.addTextChangedListener(textWatcher);
        dob.addTextChangedListener(textWatcher);
        address1.addTextChangedListener(textWatcher);
        address2.addTextChangedListener(textWatcher);
        genderText.addTextChangedListener(textWatcher);
        pinCode.addTextChangedListener(textWatcher);

    }

    public void handleCalender(View view) {
        final Calendar calendar = Calendar.getInstance();
        final int Year = calendar.get(Calendar.YEAR);
        final int Month = calendar.get(Calendar.MONTH);
        final int Day = calendar.get(Calendar.DATE);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                month += 1;
                String selectedDate;

                selectedDate = date + " - " + month + " - " + year;

                dob.setText(selectedDate);

            }
        }, Year, Month, Day);
        datePickerDialog.show();

    }

    public void checkClicked(View view) {

        DownloadTask task = new DownloadTask();
        task.execute(BuildConfig.POSTAL_PIN_LINK + pinCode.getText().toString());

    }

    public void registerClicked(View view) {

        Intent intent = new Intent(this, WeatherActivity.class);
        startActivity(intent);

    }

    public boolean checkFields() {
        if (name.getText().length() == 0) {
            checkButton.setEnabled(false);
            return false;
        }
        if (mobile.getText().length() != 10) {
            checkButton.setEnabled(false);
            return false;
        }
        if (genderText.getText().length() == 0) {
            checkButton.setEnabled(false);
            return false;
        }
        if (dob.getText().length() == 0) {
            checkButton.setEnabled(false);
            return false;
        }
        if (address1.getText().length() < 3) {
            checkButton.setEnabled(false);
            return false;
        }
        return pinCode.getText().length() == 6;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

             } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONArray jsonArray = new JSONArray(s);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                Log.i(TAG, "onPostExecute: Json object: " + jsonObject + "\n");

                JSONArray postArray = jsonObject.getJSONArray("PostOffice");
                JSONObject postObject = postArray.getJSONObject(0);
                Log.i(TAG, "onPostExecute: Post object: " + postObject.get("District") + ", " + postObject.get("State") + "\n");

                state.setText("State: " + postObject.get("State"));
                district.setText("District: " + postObject.get("District"));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}