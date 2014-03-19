package com.example.tomafotoitexico;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

@SuppressWarnings("deprecation")
public class CameraSettings extends PreferenceActivity 
	implements OnSharedPreferenceChangeListener {
	public static final String KEY_CAMERA_NUM = "camera_number";
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ListPreference cameraList = (ListPreference) findPreference(KEY_CAMERA_NUM);
        int totalCameras = Camera.getNumberOfCameras();
        String defaultCam = (totalCameras > 0) ? "0" : null; 
        CharSequence[] entries = new CharSequence[totalCameras];
        CharSequence[] values = new CharSequence[totalCameras];
        for (int i = 0; i < totalCameras; i++) {
        	entries[i] = "Cámara " + Integer.toString(i+1);
        	values[i] = Integer.toString(i);
        }
        cameraList.setEntryValues(values);
        cameraList.setEntries(entries);
        cameraList.setDefaultValue(defaultCam);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        defaultCam = prefs.getString(KEY_CAMERA_NUM, defaultCam);        
        
        CharSequence summary = "";        
        if (defaultCam != null)
        	summary = Integer.toString(Integer.parseInt(defaultCam) + 1);
        
        cameraList.setSummary(summary);              
        
    }
	
	@Override
	protected void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_CAMERA_NUM)) {
            Preference connectionPref = findPreference(key);       
            String newValue = sharedPreferences.getString(key, "");
            String summary = Integer.toString(Integer.parseInt(newValue) + 1);
            connectionPref.setSummary(summary);
        }
    }


}
