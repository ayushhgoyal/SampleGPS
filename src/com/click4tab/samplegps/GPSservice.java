package com.click4tab.samplegps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GPSservice extends Service {
	private static final String TAG = "BOOMBOOMTESTGPS";
	private LocationManager mLocationManager = null;
	private LocationManager networkLocationManager = null;
	private static final int LOCATION_INTERVAL = 1000;
	private static final float LOCATION_DISTANCE = 10f;
	Context context;
	Location mLastLocation;
	Location networkLastLocation;
	private AlarmManagerBroadcastReceiver alarm;
	private static final long POINT_RADIUS = 100; // in Meters
	private static final long PROX_ALERT_EXPIRATION = -1;

	private class nwLocationListener implements
			android.location.LocationListener {

		public nwLocationListener(String provider) {
			Log.e(TAG, "LocationListener " + provider);
			networkLastLocation = new Location(LocationManager.NETWORK_PROVIDER);
			// networkLastLocation = new
			// Location(LocationManager.NETWORK_PROVIDER);
		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			networkLastLocation.set(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	}

	private class gpsLocationListener implements
			android.location.LocationListener {

		public gpsLocationListener(String provider) {
			Log.e(TAG, "LocationListener " + provider);
			mLastLocation = new Location(provider);
			// networkLastLocation = new
			// Location(LocationManager.NETWORK_PROVIDER);
		}

		@Override
		public void onLocationChanged(Location location) {
			// Log.e(TAG, "onLocationChanged: " + location);
			mLastLocation.set(location);
			//
			double lat = (double) (mLastLocation.getLatitude());
			double lng = (double) (mLastLocation.getLongitude());
			Log.e("CHECK", "lat " + lat + " long " + lng);
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

	gpsLocationListener[] mLocationListeners = new gpsLocationListener[] {
			new gpsLocationListener(LocationManager.GPS_PROVIDER),
			new gpsLocationListener(LocationManager.NETWORK_PROVIDER) };

	nwLocationListener[] nwLocationsListeners = new nwLocationListener[] {
			new nwLocationListener(LocationManager.GPS_PROVIDER),
			new nwLocationListener(LocationManager.NETWORK_PROVIDER) };

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

			// if (mLocationManager
			// .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			// this criteria part is new
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setCostAllowed(false);
			String providerName = mLocationManager.getBestProvider(criteria,
					true);

			int i;
			if (providerName.equals(LocationManager.GPS_PROVIDER)) {
				i = 0;
			} else {
				i = 1;
			}

			mLocationManager
					.requestLocationUpdates(providerName, LOCATION_INTERVAL,
							LOCATION_DISTANCE, mLocationListeners[i]);

			// mLocationManager.requestLocationUpdates(
			// LocationManager.GPS_PROVIDER, LOCATION_INTERVAL,
			// LOCATION_DISTANCE, mLocationListeners[0]);
			SamplGPS.LocationProviderString = providerName;
			// }
			// else if (networkLocationManager
			// .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// networkLocationManager.requestLocationUpdates(
			// LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
			// LOCATION_DISTANCE, nwLocationsListeners[1]);
			// SamplGPS.LocationProviderString = "Network provider";
			// }

		} catch (java.lang.SecurityException ex) {
			Log.i(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.d(TAG, "gps provider does not exist " + ex.getMessage());
		}

		double lat = (double) (mLastLocation.getLatitude());
		double lng = (double) (mLastLocation.getLongitude());
		Time now = new Time();
		now.setToNow();

		// double nwLat = (double) (networkLastLocation.getLatitude());
		// double nwLong = (double) (networkLastLocation).getLongitude();
		//
		// Log.e("both latitudes", lat + "\n" + nwLat);
		// Log.e("both long", lng + "\n" + nwLong);
		// krishna mandir
		// 26.899779, 75.824874

		// Janta colony circle
		// 26.905153,75.835431
		// office circle
		// 26.901539,75.828251
		// govind marg
		// 26.897676,75.824062
		// paranathe wala
		// 26.901702,75.827835

		// juice 26.90362,75.821569
		double startLat = 26.901702;
		double startLong = 75.827835;
		double endLat = lat; // this is current locations
		double endLong = lng;
		float[] results = new float[3];
		Location.distanceBetween(startLat, startLong, endLat, endLong, results);
		Log.e("DIstance", "distance " + results[0] + "");
		SamplGPS.DISTANCE_STRING = results[0] + "";

		// // ----calculating distance between gps and network
		//
		// float[] results2 = new float[3];
		// Location.distanceBetween(nwLat, nwLong, endLat, endLong, results2);
		// Log.e("accuracy ", "difference " + results2[0] + "");
		// SamplGPS.GPS_NETWORK_DISTANCE = results2[0] + "";
		//
		// // end calculating gps-netowrk

		Toast.makeText(context, "provider  " + SamplGPS.LocationProviderString,
				Toast.LENGTH_SHORT).show();
		SamplGPS.updateStatus();
		// // addding proximity alert here
		//
		// Intent proxIntent = new Intent("com.click4tab.samplegps");
		// PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0,
		// proxIntent, 0);
		// 26.901439,75.82835 POI
		// krishna mandir 26.8998,75.824877
		// office 26.901826,75.825923
		// mLocationManager.addProximityAlert(26.8998, // the latitude of the
		// // central point of the
		// // alert region
		// 75.824877, // the longitude of the central point of the alert
		// // region
		// POINT_RADIUS, // the radius of the central point of the alert
		// // region, in meters
		// PROX_ALERT_EXPIRATION, // time for this proximity alert, in
		// // milliseconds, or -1 to indicate no
		// // expiration
		// proximityIntent // will be used to generate an Intent to fire
		// // when entry to or exit from the alert region
		// // is detected
		// );
		//
		// IntentFilter filter = new IntentFilter("com.click4tab.samplegps");
		// registerReceiver(new ProximityIntentReceiver(), filter);
		//
		// // and proximity alert

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
			// notif = notif + bat;
			// @Override
			// public void onCreate(Bundle icicle) {
			// super.onCreate(icicle);
			// setContentView(R.layout.main);
			// contentTxt = (TextView) this.findViewById(R.id.monospaceTxt);
			// }

			// updateView(notif);

			// updateMapView(context, lat, lng);
			// SamplGPS.generateNotification(context, notif);

		}

		return START_NOT_STICKY;
	}

	private void updateMapView(Context context2, double lat, double lng) {
		// TODO Auto-generated method stub
		// GoogleMap map = ((MapFragment) getFragmentManager().context2
		// .findFragmentById(R.id.map)).getMap();
		final LatLng HAMBURG = new LatLng(lat, lng);
		Marker hamburg = SamplGPS.map.addMarker(new MarkerOptions().position(
				HAMBURG).title("Hamburg"));

		// .icon(BitmapDescriptorFactory
		// .fromResource(R.drawable.ic_launcher)));

		// Move the camera instantly to hamburg with a zoom of 15.
		SamplGPS.map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 18));
		// Zoom in, animating the camera.
		SamplGPS.map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
		SamplGPS.map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
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
			networkLocationManager = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
			Log.d("called", "service was called here");
		}
	}
}