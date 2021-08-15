package com.codedrive.weather;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    TextInputEditText city;
    Button resultButton;
    TextView tempCentigrade, tempFahrenheit, latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        city = findViewById(R.id.cityText);
        resultButton = findViewById(R.id.registerButton);
        tempCentigrade = findViewById(R.id.tempCentigrade);
        tempFahrenheit = findViewById(R.id.tempFahrenheit);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        resultButton = findViewById(R.id.registerButton);



    }

    public void showResult(View view) {

        DownloadTask task = new DownloadTask();
        task.execute(BuildConfig.WEATHER_API_LINK + city.getText().toString() +"&aqi=no");

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

                JSONObject jsonObject = new JSONObject(s);
                Log.i(TAG, "onPostExecute: Post object: " + jsonObject.getJSONObject("location").get("lat") + ", " + jsonObject.getJSONObject("location").get("lon") + "\n");
                tempCentigrade.setText("Temperature Centigrade: " + jsonObject.getJSONObject("current").get("temp_c"));
                tempFahrenheit.setText("Temperature Fahrenheit: " + jsonObject.getJSONObject("current").get("temp_f"));
                latitude.setText("Latitude: " + jsonObject.getJSONObject("location").get("lat"));
                longitude.setText("Longitude: " + jsonObject.getJSONObject("location").get("lon"));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}