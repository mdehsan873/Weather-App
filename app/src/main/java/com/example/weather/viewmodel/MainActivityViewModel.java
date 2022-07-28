package com.example.weather.viewmodel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weather.Constants;
import com.example.weather.api.APIClient;
import com.example.weather.models.Main;
import com.example.weather.models.WeatherModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityViewModel  extends ViewModel {
   private String temp="";
   private int sunrise=0;
    private int sunset=0;
    private String Address="";
    private String image="";
    MutableLiveData<String>mutableLiveData=new MutableLiveData<>();
    MutableLiveData<Integer>sunRiseLiveData=new MutableLiveData<>();
    MutableLiveData<Integer>sunSetLiveData=new MutableLiveData<>();
    MutableLiveData<String>addressLiveData=new MutableLiveData<>();
    MutableLiveData<String>imageLiveData=new MutableLiveData<>();
    public MutableLiveData<String> getTemp() {
        mutableLiveData.setValue(temp);
        return mutableLiveData;
    }

    public void setTemp(String temp) {
        mutableLiveData.setValue(temp);
    }

    public MutableLiveData<Integer> getSunRiseLiveData() {
        sunRiseLiveData.setValue(sunrise);
        return sunRiseLiveData;
    }

    public void setSunrise(int sunrise) {
        sunRiseLiveData.setValue(sunrise);
    }
    public MutableLiveData<Integer> getSunSetLiveData() {
        sunSetLiveData.setValue(sunset);
        return sunRiseLiveData;
    }

    public void setSunSet(int sunset) {
        sunSetLiveData.setValue(sunset);
    }

    public MutableLiveData<String> getAddressLiveData() {
        addressLiveData.setValue(Address);
        return addressLiveData;
    }

    public void setAddress(String address) {
        addressLiveData.setValue(address);
    }

    public MutableLiveData<String> getImageLiveData() {
        imageLiveData.setValue(image);
        return imageLiveData;
    }

    public void setImage(String image) {
        imageLiveData.setValue(image);

    }
}
