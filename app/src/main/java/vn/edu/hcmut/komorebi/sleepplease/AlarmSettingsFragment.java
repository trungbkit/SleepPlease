package vn.edu.hcmut.komorebi.sleepplease;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.preference.*;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ndtho8205 on 07/01/2017.
 */

public class AlarmSettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String TAG = AlarmSettingsFragment.class.getName();

    private final int RINGTONE_FILE_PICKER_ID = 1;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_alarm_item);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen  preferenceScreen  = getPreferenceScreen();
        int               count             = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < count; ++i) {
            Preference preference = preferenceScreen.getPreference(i);
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null) {
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }

    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int            prefIndex      = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (preference instanceof EditTextPreference) {
            preference.setSummary(value);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        Preference filePicker = findPreference(getString(R.string.pref_ringtone_file_key));
        filePicker.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "onPreferenceClick: ringtone");
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Choose Sound File"), 1);
                return true;
            }
        });

        Preference record = findPreference(getString(R.string.pref_record_key));
        record.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //TODO: record <code></code>
                Toast.makeText(getContext(), "Record new audio", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && requestCode == RINGTONE_FILE_PICKER_ID) {
            Log.d(TAG, "onActivityResult: ringtone_file_picker" );
            Uri    uri      = data.getData();
            String filePath = uri.getPath();
            String fileName = getFileName(uri);

            Log.d(TAG, "onActivityResult: " + filePath + "\t" + fileName);
            SharedPreferences        preferences = getPreferenceScreen().getSharedPreferences();
            SharedPreferences.Editor editor      = preferences.edit();

            editor.putString(getString(R.string.pref_ringtone_file_key), filePath);
            editor.putString(getString(R.string.pref_ringtone_file_name_key), fileName);
            editor.apply();
        }
    }

    public String getFileName(Uri uri) {
        Context context  = getContext();
        String  fileName = null;
        String  scheme   = uri.getScheme();

        if (scheme.equals("file")) {
            fileName = uri.getLastPathSegment();
        } else if (scheme.equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        return fileName;
    }
}
