package com.example.weather;

import static com.example.weather.Constants.PERMISSION_ID;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weather.api.APIClient;
import com.example.weather.models.Main;
import com.example.weather.models.Sys;
import com.example.weather.models.Weather;
import com.example.weather.models.WeatherModel;
import com.example.weather.viewmodel.MainActivityViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ImageView wicon;
    TextView temp;
    TextView citydetails, sunrise_time,sunset_time;
    public static String city="Curitiba";
    TextView wind;
    FusedLocationProviderClient mFusedLocationClient;
    ProgressBar progressBar;
    public  double lot,lon;

    MainActivityViewModel mainActivityViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=findViewById(R.id.progressBar);
        if (!checkPermissions()) {
            requestPermissions();
        }
        wicon=findViewById(R.id.iv_weather_condition);
        sunset_time=findViewById(R.id.tv_sunset_time);
        sunrise_time =findViewById(R.id.tv_sunrise_time);
        mainActivityViewModel=new ViewModelProvider(this).get(MainActivityViewModel.class);
        wind=findViewById(R.id.tv_humidity_label);
        citydetails=findViewById(R.id.tv_city_country);
        temp=findViewById(R.id.tv_temperature);
        mFusedLocationClient = new FusedLocationProviderClient(this);
        getCity();
        if(city.isEmpty())
        {

            Toast.makeText(getApplicationContext(),"Check your Intenet Connection and Try Again",Toast.LENGTH_SHORT).show();
        }
        getData();
        LiveData<String>tempLiveData=mainActivityViewModel.getTemp();
        LiveData<String>addressLiveData=mainActivityViewModel.getAddressLiveData();
        LiveData<Integer>sunRiseLiveData=mainActivityViewModel.getSunRiseLiveData();
        LiveData<Integer>sunSetLiveData=mainActivityViewModel.getSunSetLiveData();
        addressLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                citydetails.setText(s);
            }
        });
        sunSetLiveData.observe(this, new Observer<Integer>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(Integer integer) {
                Instant instant = Instant.ofEpochSecond( integer );
                String sr=""+instant;
                sr=sr.substring(11);
                sr=sr.substring(0,sr.length()-1)+"PM";
                sunset_time.setText(sr);
            }
        });
        sunRiseLiveData.observe(this, new Observer<Integer>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(Integer integer) {
                Instant instant = Instant.ofEpochSecond( integer );
                String sr=""+instant;
                sr=sr.substring(11);
                sr=sr.substring(0,sr.length()-1)+"AM";
                sunrise_time.setText(sr);
            }
        });
        tempLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                temp.setText(s);
            }
        });
        LiveData<String>imageLiveData=mainActivityViewModel.getImageLiveData();
        imageLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String imageUrl=Constants.Image_Url+s+".png";
                URL myURL;
                try {
                    myURL = new URL(imageUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if(!s.isEmpty())
                {
                    Picasso.with(getBaseContext()).load(imageUrl).into(wicon);

                }
            }
        });

    }
public void getCity()
{
    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        return;
    }
    if(haveNetworkConnection()) {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    Geocoder geocoder;
                    List<Address> addresses = null;
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    lon=location.getLongitude();
                    lot=location.getLatitude();
                    String address = addresses.get(0).getAddressLine(0);
                    //city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    mainActivityViewModel.setAddress(address);

                }
            }
        });
    }
    else
    {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
    }
}
    private void getData() {
        Call<WeatherModel> weatherCall= APIClient.getWeatherClient().getCityData(lot,lon, Constants.APIKEY);
        weatherCall.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(Call<WeatherModel> call, Response<WeatherModel> response) {
                WeatherModel model=response.body();
                Main mainModel=model.getMain();
                Sys sysModel= model.getSys();
                Weather weather=model.getWeather().get(0);
                String t=""+mainModel.getTempMax();
                t=t.substring(0,4);
                mainActivityViewModel.setTemp(t);
                mainActivityViewModel.setSunrise(sysModel.getSunrise());
                mainActivityViewModel.setSunSet(sysModel.getSunset());
                mainActivityViewModel.setImage(weather.getIcon());
            }

            @Override
            public void onFailure(Call<WeatherModel> call, Throwable t) {

            }


        });
    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}