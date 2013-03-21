package com.click4tab.samplegps;

import java.math.BigDecimal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class SamplGPS extends Activity implements LocationListener {
	private LocationManager locationManager;
	private String provider;
	TextView logsView;
	StringBuilder logString;
	Location l;
	ScrollView scroll;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample_gps);
		scroll = (ScrollView) findViewById(R.id.scrollView1);
		logsView = (TextView) findViewById(R.id.textView1);
		logString = new StringBuilder();

		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
		} else {

			getUpdatesFromGPS();
		}

	}

	private void getUpdatesFromGPS() {
		// TODO Auto // Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 1, this);
		// Define the criteria how to select the locatioin provider -> use
		// default
		Criteria criteria = new Criteria();
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);
		provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);
		l = new Location(LocationManager.GPS_PROVIDER);
		// Initialize the location fields
		if (l != null) {

			// System.out.println("Provider " + provider +
			// " has been selected.");
			Time now = new Time();
			now.setToNow();
			updateView("Provider " + provider + " has been selected at "
					+ now.hour + ":" + now.minute + ":" + now.second);
			onLocationChanged(l);
		}
		// else {
		// // latituteField.setText("Location not available");
		// // longitudeField.setText("Location not available");
		// Time now = new Time();
		// now.setToNow();
		// updateView("Location not available at " + now.toString());
		// }

		// final Handler handler = new Handler();
		//
		// Runnable runnable = new Runnable() {
		// @Override
		// public void run() {
		// /* do what you need to do */
		// getLocationExplicitly();
		// /* and here comes the "trick" */
		// handler.postDelayed(this, 5000);
		// }
		// };
		// handler.postDelayed(runnable, 5000);

	}

	protected void getLocationExplicitly() {
		// TODO Auto-generated method stub
		double lat = (double) (l.getLatitude());
		double lng = (double) (l.getLongitude());
		Time now = new Time();
		now.setToNow();
		if (!(lat == 0.0 && lng == 0.0)) {

			updateView("Lat " + round(lat) + " Long " + round(lng) + " at "
					+ now.hour + ":" + now.minute + ":" + now.second);
		}

	}

	public static double round(double unrounded) {
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(7, BigDecimal.ROUND_HALF_UP);
		return rounded.doubleValue();
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(
									@SuppressWarnings("unused") final DialogInterface dialog,
									@SuppressWarnings("unused") final int id) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

								getUpdatesFromGPS();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void updateView(String string) {
		// TODO Auto-generated method stub
		logString.append("\n " + string);
		logsView.setText(logString);
		scroll.fullScroll(View.FOCUS_DOWN);
	}

	// /* Request updates at startup */
	// @Override
	// protected void onResume() {
	// super.onResume();
	// locationManager.requestLocationUpdates(provider, 1000, 1, this);
	// }

	// /* Remove the locationlistener updates when Activity is paused */
	// @Override
	// protected void onPause() {
	// super.onPause();
	// locationManager.removeUpdates(this);
	// }

	@Override
	public void onLocationChanged(Location location) {
		double lat = (double) (location.getLatitude());
		double lng = (double) (location.getLongitude());
		Time now = new Time();
		now.setToNow();
		updateView("Lat " + round(lat) + " Long " + round(lng) + " at "
				+ now.hour + ":" + now.minute + ":" + now.second);
		// latituteField.setText(String.valueOf(lat));
		// longitudeField.setText(String.valueOf(lng));
	}

	public void onProviderDisabled(String provider) {
		updateView(provider + " disabled");
	}

	@Override
	public void onProviderEnabled(String provider) {
		updateView(provider + " enabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
