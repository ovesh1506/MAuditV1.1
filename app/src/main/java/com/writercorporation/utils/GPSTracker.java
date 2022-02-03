package com.writercorporation.utils;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.os.Bundle;
import android.os.IBinder;

public class GPSTracker extends IntentService implements LocationListener {

	private Context context=null;
	boolean isGPSenabled = false, isNetworkEnabled = false, canGetLocation = false;
	double latitude, longitude;
	Location location;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
	protected LocationManager locationManager;


    public GPSTracker()
    {
        super("GPS");
    }
	public GPSTracker(Context context) {
		super("GPS");
		this.context = context;
		getLocation();
	}

	public boolean isCanGetLocation() {
		return canGetLocation;
	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSenabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSenabled && !isNetworkEnabled) {
				showSettingAlert();
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {

					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSenabled) {
					if (location == null) {

						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
		}

		return location;
	}

	/*public void stopUsingGPS() {
		if (locationManager != null) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

				return;
			}
			locationManager.removeUpdates(GPSTracker.this);
		}
	    }*/


	public void showSettingAlert()
	{
		final AlertDialog.Builder alerDialog=new AlertDialog.Builder(context);
		alerDialog.setTitle("GPS Setting");
		alerDialog.setMessage("GPS is not enabled. Do you want to go setting menu?");
		alerDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(intent);
			}
		});
		alerDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alerDialog.create().cancel();
			}
		});
		AlertDialog showalertmsg = alerDialog.create();
		showalertmsg.show();
	}
	
	public double getLatitude() {
		if(location!=null)
		return latitude;
		
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		latitude = location.getLatitude();
		longitude=location.getLongitude();
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

	@Override
	protected void onHandleIntent(Intent intent) {

	}

}
