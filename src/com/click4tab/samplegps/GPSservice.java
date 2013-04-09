package com.click4tab.samplegps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GPSservice extends Service {
	private static final String TAG = "BOOMBOOMTESTGPS";
	private LocationManager mLocationManager = null;
	private static final int LOCATION_INTERVAL = 1000;
	private static final float LOCATION_DISTANCE = 10f;
	Context context;
	Location mLastLocation;
	private AlarmManagerBroadcastReceiver alarm;

	private class LocationListener implements android.location.LocationListener {

		public LocationListener(String provider) {
			Log.e(TAG, "LocationListener " + provider);
			mLastLocation = new Location(provider);
		}

		@Override
		public void onLocationChanged(Location location) {
			// Log.e(TAG, "onLocationChanged: " + location);
			mLastLocation.set(location);
			//
			// double lat = (double) (mLastLocation.getLatitude());
			// double lng = (double) (mLastLocation.getLongitude());
			// Time now = new Time();
			// now.setToNow();
			// if (!(lat == 0.0 && lng == 0.0)) {
			//
			// double endLat = 26.9011666;
			// double endLong = 75.8257483;
			// float[] results = new float[3];
			// Location.distanceBetween(lat, lng, endLat, endLong, results);
			//
			// String notif = " dist " + results[0] + " Lat "
			// + SamplGPS.round(lat) + " Long " + SamplGPS.round(lng)
			// + " at " + now.hour + ":" + now.minute + ":"
			// + now.second;
			// // pupdateView(notif);
			// // SamplGPS.generateNotification(context, notif);
			// // ---------------------------------------------------
			// // %%%%%%% uncommend below 3 lines
			// // SharedPreferences pref =
			// // context.getSharedPreferences("notif",
			// // MODE_WORLD_WRITEABLE);
			// // String s = pref.getString("notif", "");
			// // pref.edit().putString("notif", s + "\n" + notif).commit();
			//
			// }
			// // SamplGPS.generateNotification(context, notif);

		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.e(TAG, "onProviderDisabled: " + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.e(TAG, "onProviderEnabled: " + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.e(TAG, "onStatusChanged: " + provider);
		}
	}

	LocationListener[] mLocationListeners = new LocationListener[] {
			new LocationListener(LocationManager.GPS_PROVIDER),
			new LocationListener(LocationManager.NETWORK_PROVIDER) };

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		super.onStartCommand(intent, flags, startId);
		context = this;
		// alarm = new AlarmManagerBroadcastReceiver();
		// startRepeatingTimer(null);
		initializeLocationManager();
		// try {
		// mLocationManager.requestLocationUpdates(
		// LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
		// LOCATION_DISTANCE, mLocationListeners[1]);
		// } catch (java.lang.SecurityException ex) {
		// Log.i(TAG, "fail to request location update, ignore", ex);
		// } catch (IllegalArgumentException ex) {
		// Log.d(TAG, "network provider does not exist, " + ex.getMessage());
		// }
		try {
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, LOCATION_INTERVAL,
					LOCATION_DISTANCE, mLocationListeners[0]);
		} catch (java.lang.SecurityException ex) {
			Log.i(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, "gps provider does not exist " + ex.getMessage());
		}

		double lat = (double) (mLastLocation.getLatitude());
		double lng = (double) (mLastLocation.getLongitude());
		Time now = new Time();
		now.setToNow();
		if (!(lat == 0.0 && lng == 0.0)) {

			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(this, Locale.getDefault());
			String address = "";
			try {
				addresses = geocoder.getFromLocation(SamplGPS.round(lat),
						SamplGPS.round(lng), 1);
				address = addresses.get(0).getAddressLine(0);
				Log.e("address", address);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

			// String city = addresses.get(0).getAddressLine(1);
			// String country = addresses.get(0).getAddressLine(2);
			// final int level;

			final String notif = "Lat " + SamplGPS.round(lat) + " Long "
					+ SamplGPS.round(lng) + " at " + now.hour + ":"
					+ now.minute + ":" + now.second + "\n address: " + address;
			String bat = "";
			BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context arg0, Intent intent) {
					// TODO Auto-generated method stub
					int level = intent.getIntExtra("level", 0);
					String bat = notif + " bat " + level;
				}
			};
			// notif = notif + bat;
			// @Override
			// public void onCreate(Bundle icicle) {
			// super.onCreate(icicle);
			// setContentView(R.layout.main);
			// contentTxt = (TextView) this.findViewById(R.id.monospaceTxt);
			this.registerReceiver(mBatInfoReceiver, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
			// }

			// updateView(notif);

			SharedPreferences pref = context.getSharedPreferences("notif",
					MODE_WORLD_WRITEABLE);
			String s = pref.getString("notif", "");
			SamplGPS.generateNotification(context, notif);
			if (bat.length() == 0) {
				pref.edit().putString("notif", s + " \n \n" + notif).commit();
			} else {
				pref.edit().putString("notif", s + " \n \n" + bat).commit();
			}

		}

		return START_NOT_STICKY;
	}

	public void startRepeatingTimer(View view) {
		Context context = this.getApplicationContext();
		if (alarm != null) {
			alarm.SetAlarm(context);
		} else {
			// Toast.makeText(context, "Alarm is null",
			// Toast.LENGTH_SHORT).show();
			Log.d("repeating alarm", "alarm null");
		}
	}

	public void cancelRepeatingTimer(View view) {
		Context context = this.getApplicationContext();
		if (alarm != null) {
			alarm.CancelAlarm(context);
		} else {
			Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
	}

	public void onetimeTimer(View view) {
		Context context = this.getApplicationContext();
		if (alarm != null) {
			alarm.setOnetimeTimer(context);
		} else {
			Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onCreate() {
		Log.e(TAG, "onCreate");
		// initializeLocationManager();
		// // try {
		// // mLocationManager.requestLocationUpdates(
		// // LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
		// // LOCATION_DISTANCE, mLocationListeners[1]);
		// // } catch (java.lang.SecurityException ex) {
		// // Log.i(TAG, "fail to request location update, ignore", ex);
		// // } catch (IllegalArgumentException ex) {
		// // Log.d(TAG, "network provider does not exist, " + ex.getMessage());
		// // }
		// try {
		// mLocationManager.requestLocationUpdates(
		// LocationManager.GPS_PROVIDER, LOCATION_INTERVAL,
		// LOCATION_DISTANCE, mLocationListeners[0]);
		// } catch (java.lang.SecurityException ex) {
		// Log.i(TAG, "fail to request location update, ignore", ex);
		// } catch (IllegalArgumentException ex) {
		// Log.d(TAG, "gps provider does not exist " + ex.getMessage());
		// }
		//
		// final Handler handler = new Handler();
		// Runnable runnable = new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		// double lat = (double) (mLastLocation.getLatitude());
		// double lng = (double) (mLastLocation.getLongitude());
		// Time now = new Time();
		// now.setToNow();
		// if (!(lat == 0.0 && lng == 0.0)) {
		// String notif = "Lat " + SamplGPS.round(lat) + " Long "
		// + SamplGPS.round(lng) + " at " + now.hour + ":"
		// + now.minute + ":" + now.second;
		// // updateView(notif);
		//
		// SharedPreferences pref = context.getSharedPreferences(
		// "notif", MODE_WORLD_WRITEABLE);
		// String s = pref.getString("notif", "");
		// SamplGPS.generateNotification(context, notif);
		//
		// pref.edit().putString("notif", s + " \n " + notif).commit();
		//
		// }
		//
		// handler.postDelayed(this, 5000);
		//
		// }
		// };
		// handler.postDelayed(runnable, 5000);

	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		Log.e("Low memory",
				"Low memory now, may be stop the service, shud be back up soon");
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
		// if (mLocationManager != null) {
		// for (int i = 0; i < mLocationListeners.length; i++) {
		// try {
		// mLocationManager.removeUpdates(mLocationListeners[i]);
		// } catch (Exception ex) {
		// Log.i(TAG, "fail to remove location listners, ignore", ex);
		// }
		// }
		// }

	}

	private void initializeLocationManager() {
		Log.e(TAG, "initializeLocationManager");
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
			Log.d("called", "service was called here");
		}
	}
}