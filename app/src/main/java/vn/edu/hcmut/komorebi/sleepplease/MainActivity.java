package vn.edu.hcmut.komorebi.sleepplease;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerDataAdapter.RecyclerDataAdapterOnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private Button btnAddAlarm;
    private RecyclerView listAlarm;
    public static List<Alarm> listOfAlarm;
    private TimePickerDialog timePickerDialog;
    private AlertDialog.Builder builder_delete;

    private Switch clickedSwitch;
    private int clickedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddAlarm = (Button) findViewById(R.id.add_alarm);
        listAlarm = (RecyclerView) findViewById(R.id.list_alarm);

        timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hour = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
                Alarm newAlarm = new Alarm(hourOfDay, minute, true);
                listOfAlarm.add(newAlarm);

                SharedPreferences        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor      = preferences.edit();

                editor.putString(getString(R.string.pref_time_key), newAlarm.toString());
                editor.apply();

                update();
                Toast.makeText(getApplicationContext(), hour, Toast.LENGTH_SHORT).show();
            }
        }, new Time(System.currentTimeMillis()).getHours(), new Time(System.currentTimeMillis()).getMinutes(), false);
        listOfAlarm = new ArrayList<Alarm>();

        builder_delete = new AlertDialog.Builder(this);
        builder_delete.setTitle("Delete");
        builder_delete.setIcon(android.R.drawable.ic_delete);
        builder_delete.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listAlarm.setLayoutManager(layoutManager);
        listAlarm.setHasFixedSize(true);

        btnAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.updateTime(new Time(System.currentTimeMillis()).getHours(), new Time(System.currentTimeMillis()).getMinutes());
                timePickerDialog.show();
            }
        });

        setupSharedPreferences();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Time time = new Time(System.currentTimeMillis());
                    boolean run = false;
                    for (Alarm anAlarm: listOfAlarm) {
                        run = false;
                        Alarm current_alarm = new Alarm(time.getHours(),time.getMinutes(),true);
                        if (anAlarm.isEqualTo(current_alarm)) {
                            run = true;
                            startActivity(new Intent(getBaseContext(), AlertActivity.class));
                            break;
                        }

                    }
                    if (run) break;
                }
            }
        }).start();
    }


    private void optimize(List<Alarm> listAlarm){
        for (int i = 0; i < listAlarm.size() - 1; i++){
            for(int j = i+1; j < listAlarm.size(); j++){
                Alarm alarm_i = listAlarm.get(i);
                Alarm alarm_j = listAlarm.get(j);
                if((alarm_i.hour > alarm_j.hour) || ((alarm_i.hour == alarm_j.hour) &&
                        alarm_i.minute > alarm_j.minute)){
                    listAlarm.set(i, new Alarm(alarm_j.hour, alarm_j.minute, alarm_j.isActived));
                    listAlarm.set(j, new Alarm(alarm_i.hour, alarm_i.minute, alarm_i.isActived));
                }
            }
        }
        for (int i = 0; i < listAlarm.size() - 1; i++){
            for(int j = i + 1; j < listAlarm.size(); j++){
                if(isEqual(listAlarm.get(i), listAlarm.get(j))) listAlarm.remove(j);
            }
        }
    }
    private boolean isEqual(Alarm x, Alarm y){
        if(x.hour == y.hour && x.minute == y.minute) return true;
        return false;
    }

    private void update() {
        optimize(listOfAlarm);
        List<String> lstAlarm = new ArrayList<String>();
        for (Alarm item : listOfAlarm) {
            lstAlarm.add(item.toString());
        }
        listAlarm.setAdapter(new RecyclerDataAdapter(this, this));
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.contains(getString(R.string.pref_time_key))) {
            String[] time = sharedPreferences.getString(getString(R.string.pref_time_key), "00:00").split(":");
            boolean isActived = sharedPreferences.getBoolean(getString(R.string.pref_alarm_status_key), true);


            Alarm alarm = new Alarm(Integer.parseInt(time[0]), Integer.parseInt(time[1]), isActived);
            listOfAlarm.add(alarm);
            update();
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    //code for Menu Options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting_option:
                try {
                    //go to Setting Activity
                } catch (Exception e) {
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position, Switch clickedSwitch) {
        int clickedViewId = view.getId();
        if (clickedViewId == R.id.switch_alarm) {
            listOfAlarm.get(position).toggle();

            SharedPreferences        preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor      = preferences.edit();

            editor.putBoolean(getString(R.string.pref_alarm_status_key), clickedSwitch.isChecked());
            editor.apply();
        } else {
            Intent intent = new Intent(MainActivity.this, AlarmSettingsActivity.class);
            intent.putExtra(getString(R.string.pref_time_key), listOfAlarm.get(position).toString());
            startActivity(intent);

            this.clickedSwitch = clickedSwitch;
            this.clickedPosition = position;
        }
    }

    @Override
    public boolean onItemLongClick(View view, final int position) {
        builder_delete.setMessage("Do you want to remove " + listOfAlarm.get(position).toString());
        builder_delete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor      = preferences.edit();

                editor.clear();
                editor.apply();


                listOfAlarm.remove(position);
                update();
            }
        });
        AlertDialog alertDialogDelete = builder_delete.create();
        alertDialogDelete.show();
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_alarm_status_key))) {
            if (clickedSwitch != null) {

                listOfAlarm.get(clickedPosition).toggle();
                clickedSwitch.setChecked(sharedPreferences.getBoolean(
                        getString(R.string.pref_alarm_status_key),
                        getResources().getBoolean(R.bool.pref_alarm_status_default))
                );
                clickedSwitch = null;
            }
        } else if (key.equals(getString(R.string.pref_ringtone_file_key))) {
//            mRingtoneTextView.setText(sharedPreferences.getString(
//                    getString(R.string.pref_ringtone_file_key),
//                    "Ringtone"
//            ));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}

//class Adapter for RecyclerView
