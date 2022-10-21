package com.example.weatherapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText cityField;
    private Button mainButton;
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityField = findViewById(R.id.cityField);
        mainButton = findViewById(R.id.mainButton);
        info = findViewById(R.id.info);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cityField.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.alert_text, Toast.LENGTH_LONG).show();
                } else {
                    String city = cityField.getText().toString();
                    String APIkey = "7f3563e887db9b7aac17c1e054f767b6";
                    String URL = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + APIkey + "&units=metric&lang=ru";
                    new GetData().execute(URL);
                }
            }
        });
    }

    private class GetData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            info.setText("Загружаем...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer str = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    str.append(line).append("\n");
                }

                return str.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject json = new JSONObject(result);
                String output = "Температура: " + json.getJSONObject("main").getDouble("temp") + " °C\n" +
                        "Ощущается как: " + json.getJSONObject("main").getDouble("feels_like") + " °C\n" +
                        "Ветер: " + json.getJSONObject("wind").getDouble("speed") + " м/c";
                info.setText(output);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}