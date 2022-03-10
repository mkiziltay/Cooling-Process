package com.kodlab.simple_alarm_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.log;
import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity {
    TimePicker pickTime;
    TextView textView, textView2;
    Button button;
    Spinner spinIt;
    EditText degree;

    int sHour,sMinute,addMin,addHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pickTime = findViewById(R.id.picktime);
        textView = findViewById(R.id.box);
        textView2= findViewById(R.id.alarmTime);
        button = findViewById(R.id.button);
        spinIt = findViewById(R.id.spinIt);
        degree = findViewById(R.id.degree);

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.process, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinIt.setAdapter(adapter);
        spinIt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
                if (spinIt.getSelectedItem().equals("cooling") && Integer.valueOf(degree.getText().toString())>65){//eğer sıcaklık 65 üstündeyse 10 dk sonra alarm verecek
                    addMin = setMinval(degree.getText().toString()); button.setEnabled(true);
                    //spinIt.setSelection(1);
                    Toast.makeText(getApplicationContext(),spinIt.getSelectedItem()+" "+sHour+":"+sMinute,Toast.LENGTH_SHORT).show();
                }else if (spinIt.getSelectedItem().equals("cooling") && Integer.valueOf(degree.getText().toString())<=39 && Integer.valueOf(degree.getText().toString())>9){ //Set selected item 3th item and add 60 sec
                    addHour=1; button.setEnabled(true);
                }else if (spinIt.getSelectedItem().equals("cooling") && Integer.valueOf(degree.getText().toString())<=60 && Integer.valueOf(degree.getText().toString())>=46){//Set selected item 3th item and add 60 sec
                    addMin =setMinval(degree.getText().toString());; button.setEnabled(true);
                    //if ((pickTime.getMinute()+addMin)>=60){sHour+=1;sMinute=(pickTime.getMinute()+addMin)-60;addMin=0;}
                }else if (spinIt.getSelectedItem().equals("heating")){
                    //spinIt.setSelection(2);
                }else {
                    //spinIt.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinIt.setSelection(2); // set selected item 3th position item
            }
        });

        // added temp codes if not selected time
        if (sHour==0 || sMinute==0){
            sHour=pickTime.getHour(); sMinute=pickTime.getMinute(); textView.setText("Current Time :" +sHour+":"+sMinute);
            spinIt.setEnabled(false); button.setEnabled(false);
        }
        degree.setOnFocusChangeListener(new View.OnFocusChangeListener() {// when degree is not setted; process will be inactive
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                spinIt.setEnabled(true); textView2.setText("Set Process !!!"); textView2.setTextColor(Color.rgb(255,0,0));
            }
        });


        pickTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                sHour = hourOfDay;
                sMinute = minute;
                textView.setText("Time :" +sHour+":"+sMinute+" "+"Added :"+addMin+"mn:"+addHour+"hr:");
            }
        });
    button.setOnClickListener(v -> setTimer(v));
    }

    public void setTimer(View v){
        AlarmManager manageAlarm= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date date = new Date();
        Calendar cal_Alarm = Calendar.getInstance();
        Calendar cal_Now = Calendar.getInstance();

        cal_Now.setTime(date);
        cal_Alarm.setTime(date);

        //show selected time
        textView.setText("Time :" +sHour+":"+sMinute+" "+"Added :"+addMin+"mn:"+addHour+"hr:");
        if ((pickTime.getMinute()+addMin)>=60){sHour+=1;sMinute=(pickTime.getMinute()+addMin)-60;addMin=0;}
        textView2.setText("Next Alarm Time :" + (sHour+addHour)+":"+(sMinute+addMin)); textView2.setTextColor(Color.rgb(0,171,255));

        cal_Alarm.set(Calendar.HOUR_OF_DAY,sHour+addHour);
        cal_Alarm.set(Calendar.MINUTE,sMinute+addMin);
        cal_Alarm.set(Calendar.SECOND,0);

        if (cal_Alarm.before(cal_Now)){
            cal_Alarm.add(Calendar.DATE,1);
        }

        Intent intent = new Intent(this,AlarmBroadcastReceiver.class);

        PendingIntent pIntent = PendingIntent.getBroadcast(this,24444,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        manageAlarm.set(AlarmManager.RTC_WAKEUP, cal_Alarm.getTimeInMillis(), pIntent);
    }

    public  int setMinval(String t){
        final double time;
        if(Integer.parseInt(degree.getText().toString())>65){
            time=((Integer.parseInt(t))-45)*2*log(2)-20;
        }else if (Integer.parseInt(degree.getText().toString())<45){
           time=((Integer.parseInt(t))-45)*2*log(2);
        }else{
            time=((Integer.parseInt(t))-45)*4*log(2);
        }
        int val=(int)(round(time)); //round vlue to upper integer
        return val;
    }
}