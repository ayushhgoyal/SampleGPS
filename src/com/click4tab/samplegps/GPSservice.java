package com.click4tab.samplegps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.renderscript.Sampler;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class GPSservice extends Service {
	public static int i;
	Location location;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		Log.e("Service", "Service started");
		SamplGPS.generateNotification(this, "service start");
		i = 1;
		myCron();
		return Service.START_STICKY;
	}

	public void myCron() {

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 5000, 1, new LocationListener() {
		//
		// @Override
		// public void onStatusChanged(String provider, int status,
		// Bundle extras) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onProviderEnabled(String provider) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onProviderDisabled(String provider) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onLocationChanged(Location location) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// Define the criteria how to select the locatioin provider -> use
		// default
		// Criteria criteria = new Criteria();
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// provider = locationManager.getBestProvider(criteria, true);

		location = new Location(LocationManager.GPS_PROVIDER);
		final Handler handler = new Handler();
		final Runnable runnable = new Runnable() {

			@Override
			public void run() {
				if (i == 1) {
					// SamplGPS.generateNotification(getBaseContext(),
					// " service");
					performRepetitiveTask();
					handler.postDelayed(this, 10000);
				}
				// TODO Auto-generated method stub

			}
		};
		handler.postDelayed(runnable, 10000);
		// ms
	}

	private void performRepetitiveTask() {
		// TODO Auto-generated method stub
		Time now = new Time();
		now.setToNow();
		Log.e("scheduled task", "called " + now.hour + ":" + now.minute + ":"
				+ now.second);
		// SamplGPS.generateNotification(getBaseContext(), "yo");
		double lat = (double) (location.getLatitude());
		double lng = (double) (location.getLongitude());
		// Time now = new Time();
		// now.setToNow();
		// if (!(lat == 0.0 && lng == 0.0)) {

		// updateView("Lat " + SamplGPS.round(lat) + " Long "
		// + SamplGPS.round(lng) + " at " + now.hour + ":"
		// + now.minute + ":" + now.second);

		// Toast.makeText(
		// getBaseContext(),
		// "Lat " + SamplGPS.round(lat) + " Long "
		// + SamplGPS.round(lng) + " at " + now.hour + ":"
		// + now.minute + ":" + now.second, Toast.LENGTH_SHORT)
		// .show();
		SamplGPS.generateNotification(this, "Lat " + SamplGPS.round(lat)
				+ " Long " + SamplGPS.round(lng) + " at " + now.hour + ":"
				+ now.minute + ":" + now.second);

	}

	// }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		i = 0;
		Log.v("Service", "Service stopped");

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
