package com.click4tab.samplegps;

import java.math.BigDecimal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.LocationListener;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

public class GPSstatusReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.e("GPS", "Location change detected");

		final LocationManager manager = (LocationManager) arg0
				.getSystemService(Context.LOCATION_SERVICE);

		manager.addGpsStatusListener(new Listener() {

			@Override
			public void onGpsStatusChanged(int event) {
				// TODO Auto-generated method stub
				Log.e("GPS", "event: " + event);

				switch (event) {
				case GpsStatus.GPS_EVENT_STARTED:
					Log.e("GPS", "STARTED");
					SamplGPS.generateNotification(arg0, "gps started");
					break;
				case GpsStatus.GPS_EVENT_STOPPED:
					Log.e("GPS", "STOPPED");
					SamplGPS.generateNotification(arg0, "gps stopped");
					break;

				case GpsStatus.GPS_EVENT_FIRST_FIX:
					Log.e("GPS", "first fix ");
					Log.e("time", ""
							+ manager.getGpsStatus(null).getTimeToFirstFix());
					SamplGPS.generateNotification(arg0, "time "
							+ manager.getGpsStatus(null).getTimeToFirstFix());
					break;

				default:
					break;
				}
			}

			private void updateView(String string) {
				// TODO Auto-generated method stub
				Log.e("Current status", string);
			}
		});

	}

}
