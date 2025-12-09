package com.example.gpsapp;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherActivity extends AppCompatActivity {

    private static final String API_KEY = "376dd7798c97056e8513f06d987597b1";

    private TextView locationText;
    private TextView updateTime;
    private TextView weatherCondition;
    private TextView mainTemperature;
    private TextView minTemp;
    private TextView maxTemp;
    private TextView sunriseTime;
    private TextView sunsetTime;
    private TextView windSpeed;
    private TextView pressure;
    private TextView humidity;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);

        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        locationText = findViewById(R.id.locationText);
        updateTime = findViewById(R.id.updateTime);
        weatherCondition = findViewById(R.id.weatherCondition);
        mainTemperature = findViewById(R.id.mainTemperature);
        minTemp = findViewById(R.id.minTemp);
        maxTemp = findViewById(R.id.maxTemp);
        sunriseTime = findViewById(R.id.sunriseTime);
        sunsetTime = findViewById(R.id.sunsetTime);
        windSpeed = findViewById(R.id.windSpeed);
        pressure = findViewById(R.id.pressure);
        humidity = findViewById(R.id.humidity);

        fetchWeatherData();
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void fetchWeatherData() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String urlString = "https://api.openweathermap.org/data/2.5/weather?lat="
                        + latitude + "&lon=" + longitude + "&appid=" + API_KEY + "&units=metric";

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    parseWeatherData(response.toString());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Błąd pobierania danych: " + responseCode, Toast.LENGTH_SHORT).show());
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Błąd: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void parseWeatherData(String jsonData) {
        try {
            JSONObject json = new JSONObject(jsonData);

            String cityName = json.getString("name");
            JSONObject sys = json.getJSONObject("sys");
            String country = sys.getString("country");

            JSONObject main = json.getJSONObject("main");
            double temp = main.getDouble("temp");
            double tempMin = main.getDouble("temp_min");
            double tempMax = main.getDouble("temp_max");
            int hum = main.getInt("humidity");
            int press = main.getInt("pressure");

            JSONObject wind = json.getJSONObject("wind");
            double windSpd = wind.getDouble("speed");

            JSONArray weatherArray = json.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String description = weather.getString("description");

            long sunriseUnix = sys.getLong("sunrise");
            long sunsetUnix = sys.getLong("sunset");

            long dt = json.getLong("dt");

            runOnUiThread(() -> {
                locationText.setText(cityName + ", " + country);

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
                updateTime.setText("Updated at: " + sdf.format(new Date(dt * 1000)));

                weatherCondition.setText(description.toUpperCase());
                mainTemperature.setText(String.format(Locale.getDefault(), "%.2f°C", temp));
                minTemp.setText(String.format(Locale.getDefault(), "Min Temp: %.2f°C", tempMin));
                maxTemp.setText(String.format(Locale.getDefault(), "Max Temp: %.2f°C", tempMax));

                SimpleDateFormat timeSdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                sunriseTime.setText(timeSdf.format(new Date(sunriseUnix * 1000)));
                sunsetTime.setText(timeSdf.format(new Date(sunsetUnix * 1000)));

                windSpeed.setText(String.format(Locale.getDefault(), "%.2f", windSpd));
                pressure.setText(String.valueOf(press));
                humidity.setText(String.valueOf(hum));
            });

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Błąd parsowania danych", Toast.LENGTH_SHORT).show());
        }
    }
}