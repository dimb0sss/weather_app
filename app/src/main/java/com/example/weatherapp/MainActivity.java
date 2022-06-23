package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.BufferOverflowException;

public class MainActivity extends AppCompatActivity {

    private final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=a62780fc4eac4a9665774ea5be8af895&units=metric&lang=ru";
    private final String CITIES_URL = "https://htmlweb.ru/geo/api.php?city_name=$s&json";

    public EditText editTextCity;
    private TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewWeather = findViewById(R.id.textViewWeather);
        editTextCity = findViewById(R.id.editTextCity);




    }


    public void onClickShowWeather(View view) {
        String city = editTextCity.getText().toString().trim();
        if (!city.isEmpty()) {
            DownloadWeatherTask task = new DownloadWeatherTask();
            String url = String.format(WEATHER_URL, city);
            task.execute(url);
        }
    }

    private class DownloadWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream =urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line !=null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

            }
            textViewWeather.setText("Город не найден");
            return "";

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String feelsTemp = jsonObject.getJSONObject("main").getString("feels_like");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String wind = jsonObject.getJSONObject("wind").getString("speed");

                String weather = String.format("%s\nТемпература: %s\nОщущаемая температура: %s\nНа улице: %s\n\nВетер: %s м.с.", city, temp,feelsTemp, description,wind);

                textViewWeather.setText(weather);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}