package com.example.a5whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ImageView backgroundImageView;
    TextView nameOfCityTextView , windSpeedTextView , tempTextView , pressureTextView , mainTextView ,descriptionTextView ;
    EditText cityEditText;
    Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        DownloadPicture downloadPicture = new DownloadPicture();
        try {
            Bitmap background = downloadPicture.execute("https://i.dlpng.com/static/png/52429_preview.png").get();
            backgroundImageView.setImageBitmap(background);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void init(){
        backgroundImageView = findViewById(R.id.backgroundImageView);
        nameOfCityTextView = findViewById(R.id.nameCityTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);
        tempTextView = findViewById(R.id.tempTextView);
        pressureTextView = findViewById(R.id.pressureTextView);
        mainTextView = findViewById(R.id.mainTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        searchBtn = findViewById(R.id.whatWeatherBtn);
        cityEditText = findViewById(R.id.cityEditText);
    }

    public void searchBtnOnClicked(View view){
        String city = cityEditText.getText().toString();
        if (city != "" & city != null){
            DownloadTask task = new DownloadTask();
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=5e3edd8f7cacc3383ef67ef1d0754a42");
        }
    }


    public class DownloadTask extends AsyncTask<String ,Void ,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection connection  = null;
            try{
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                String nameOfCity = object.getString("name");
                nameOfCityTextView.setText("City Name : " +"\n" + nameOfCity);

                //wind JSON Object
                String wind = object.getString("wind");
                JSONObject windObj = new JSONObject(wind);
                String wind_speed = windObj.getString("speed");
                windSpeedTextView.setText("Wind Speed : " +"\n" + wind_speed);

                //main JSON Object
                String main = object.getString("main");
                JSONObject mainObj = new JSONObject(main);
                String temp = mainObj.getString("temp");

                tempTextView.setText("Temp : " + "\n" + temp + " 'F");
                String pressure = mainObj.getString("pressure");
                pressureTextView.setText("Pressure : " + "\n" + pressure);

                //weather JSON Object
                String weather = object.getString("weather");
                JSONArray array = new JSONArray(weather);

                for (int i = 0; i < array.length() ; i++) {
                    JSONObject jsonPart = array.getJSONObject(i);
                    String objectPartMain = jsonPart.getString("main");
                    mainTextView.setText("Main : " + "\n" + objectPartMain);
                    String objectPartDescription = jsonPart.getString("description");
                    descriptionTextView.setText("Description : " + "\n" + objectPartDescription);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

