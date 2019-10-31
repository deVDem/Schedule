package ru.devxem.reminder.api;

public class Time {


    public static String getRemain(int hour1, int hour2, int min1, int min2, int sec1, int sec2) {
        // (24 - hour) +":"+ (60 - min)+":"+(60-sec)
        // Algoritm: hour1-hour2:min1-min2:sec1-sec2

        int hour = hour1-hour2;
        int min = min1-min2;
        int sec = sec1-sec2;

        String answer;
        if(min<0) { min=60+min; hour--; }
        if(sec<0) { sec=60+sec; min--; }
        if(min == 60) {
            hour++;
        }
        if(sec == 60) {
            min++;
        }
        if(hour==24) hour=23;
        if(hour==0) answer="0:";
        else {
            answer=hour+":";
        }
        if(min==0) answer=answer+"00:";

        else {
            if(min<10) answer=answer+"0"+min;
            else answer=answer+min;
            answer=answer+":";
        }
        if(sec==0 || sec == 60) answer=answer+"00";
        else {
            if(sec<10) answer=answer+"0"+sec;
            else answer=answer+sec;
        }
        return answer;
    }
}
