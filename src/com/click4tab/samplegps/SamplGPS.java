package com.click4tab.samplegps;

import java.math.BigDecimal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class SamplGPS extends Activity implements LocationListener {
	private LocationManager locationManager;
	private String provider;
	TextView logsView;
	StringBuilder logString;
	Location l;
	ScrollView scroll;
	Button startButton, stopButton;
	Context context;
	public static String s;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_sample_gps);
		startButton = (Button) findViewById(R.id.button1);
		stopButton = (Button) findViewById(R.id.button2);
		scroll = (ScrollView) findViewById(R.id.scrollView1);
		logsView = (TextView) findViewById(R.id.textView1);

		try {
			logsView.setText(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logsView.setText("he");
		}

		logString = new StringBuilder();
		buttonCLickListeners();
		Log.e("check", "starting notification");
		generateNotification(context, "app start");

		final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
		} else {

			getUpdatesFromGPS();
		}

	}

	private void buttonCLickListeners() {
		// TODO Auto-generated method stub
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("Activity", "Starting service");
				Intent intent = new Intent(context, GPSservice.class);
				context.startService(intent);
			}
		});
		stopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("Activity", "Stopping service");
				Intent intent = new Intent(context, GPSservice.class);

				stopService(intent);
			}
		});
	}

	private void getUpdatesFromGPS() {
		// TODO Auto // Get the location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 1, this);
		// Define the criteria how to select the locatioin provider -> use
		// default
		// Criteria criteria = new Criteria();
		// // criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// provider = locationManager.getBestProvider(criteria, true);
		// Location location = locationManager.getLastKnownLocation(provider);
		l = new Location(LocationManager.GPS_PROVIDER);
		// Initialize the location fields
		if (l != null) {

			// System.out.println("Provider " + provider +
			// " has been selected.");
			Time now = new Time();
			now.setToNow();

			updateView("Provider " + provider + " has been selected at "
					+ now.hour + ":" + now.minute + ":" + now.second);

			// getLocationExplicitly();

			final Handler handler = new Handler();
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					getLocationExplicitly();
					handler.postDelayed(this, 5000);
				}
			};
			handler.postDelayed(runnable, 5000);

			// onLocationChanged(l);
		}

	}

	protected void getLocationExplicitly() {
		// TODO Auto-generated method stub
		double lat = (double) (l.getLatitude());
		double lng = (double) (l.getLongitude());
		Time now = new Time();
		now.setToNow();
		if (!(lat == 0.0 && lng == 0.0)) {
			String notif = "Lat " + round(lat) + " Long " + round(lng) + " at "
					+ now.hour + ":" + now.minute + ":" + now.second;
			updateView(notif);
			generateNotification(context, notif);
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
		Log.e("in activity", string);
		generateNotification(context, string);
	}

	// public void updateView(String string, Context c) {
	// // TODO Auto-generated method stub
	// logString.append("\n " + string);
	// logsView.setText(logString);
	// scroll.fullScroll(View.FOCUS_DOWN);
	// Log.e("in activity", string);
	// }

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

	public static void generateNotification(Context context, String message) {
		// Log.e("this msg is received", message);
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, SamplGPS.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		// notification.defaults |= Notification.DEFAULT_SOUND;

		// notification.sound = Uri.parse("android.resource://" +
		// context.getPackageName() + "your_sound_file_name.mp3");

		// Vibrate if vibrate is enabled
		// notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);

	}

}
