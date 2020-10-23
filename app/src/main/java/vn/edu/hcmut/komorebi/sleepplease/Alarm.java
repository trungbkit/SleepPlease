package vn.edu.hcmut.komorebi.sleepplease;

/**
 * Created by trungbkit on 07/01/2017.
 */

public class Alarm {
    public int hour;
    public int minute;
    public boolean isActived;
    public Alarm(int hour, int minute, boolean isActived) {
        this.hour = hour;
        this.minute = minute;
        this.isActived = isActived;
    }
    @Override
    public String toString() {
        String aHouse = hour < 10 ? "0" + String.valueOf(hour) : String.valueOf(hour);
        String aMinute =  minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute);
        return aHouse + ":" + aMinute;
    }
    public void toggle() {
        isActived = !isActived;
    }
    public boolean isEqualTo(Alarm anAlarm) {
        return this.hour == anAlarm.hour && this.minute == anAlarm.minute && this.isActived == anAlarm.isActived;
    }

}
