package com.example.ravikant.mapexp;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GpsTrackerNew extends Service implements LocationListener{

	 private final Context mContext;
	 
	    // flag for GPS status
	    boolean isGPSEnabled = false;
	 
	    // flag for network status
	    boolean isNetworkEnabled = false;
	 
	    boolean canGetLocation = false;
	 
	    Location location; // location
	    double latitude; // latitude
	    double longitude; // longitude
	    Geocoder geocoder;
	    
	    // The minimum distance to change Updates in meters
	    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
	 
	    // The minimum time between updates in milliseconds
	    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	 
	    // Declaring a Location Manager
	    protected LocationManager locationManager;
	    
	    public GpsTrackerNew(Context context) {
	        this.mContext = context;
	        geocoder = new Geocoder(context, Locale.getDefault());
	        getLocation();
	    }
	    
	    public Location getLocation() {
	        try {
	            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
	            	 
	            // getting GPS status
	            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	 
	            // getting network status
	            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	 
	            if (!isGPSEnabled && !isNetworkEnabled) {
	                // no network provider is enabled
	            } else {
	                this.canGetLocation = true;
	                // First get location from Network Provider
	                if (isNetworkEnabled) {
						try {

							locationManager.requestLocationUpdates(
									LocationManager.NETWORK_PROVIDER,
									MIN_TIME_BW_UPDATES,
									MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
							if (locationManager != null) {
								location = locationManager
										.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
								if (location != null) {
									latitude = location.getLatitude();
									longitude = location.getLongitude();
								}
							}
						}
						catch (SecurityException ex){
							ex.printStackTrace();
						}
	                }
	                // if GPS Enabled get lat/long using GPS Services
	                if (isGPSEnabled) {
	                    if (location == null) {
							try {
								locationManager.requestLocationUpdates(
										LocationManager.GPS_PROVIDER,
										MIN_TIME_BW_UPDATES,
										MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
								if (locationManager != null) {
									location = locationManager
											.getLastKnownLocation(LocationManager.GPS_PROVIDER);
									if (location != null) {
										latitude = location.getLatitude();
										longitude = location.getLongitude();
									}
								}
							}
							catch (SecurityException ex){
								ex.printStackTrace();
							}
	                    }
	                }
	            }
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	 
	        return location;
	    }
	    
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public void stopUsingGPS(){
        if(locationManager != null){
			try {

				locationManager.removeUpdates(GpsTrackerNew.this);
			}catch (SecurityException ex){
				ex.printStackTrace();
			}
        }       
    }
    
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }
    
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }
	
	public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }
     
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }
    
    public String getLocationName(Double lat,Double lang){
    	
    	String address = "";
    	// return name
    	try {
    		List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
			address = addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return address;
    }
}
