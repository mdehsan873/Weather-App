package com.example.weather.api;
import com.example.weather.Constants;
import com.example.weather.models.Weather;
import com.example.weather.models.WeatherModel;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class APIClient {
    public static weatherClient weatherClient=null;

    public static weatherClient getWeatherClient(){
        if (weatherClient==null)
        {
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(Constants.BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            weatherClient=retrofit.create(APIClient.weatherClient.class);
        }


        return weatherClient;
    }

    public interface weatherClient
    {
        @GET("weather")
        Call<WeatherModel> getCityData(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String APIKEY);
    }
    }

