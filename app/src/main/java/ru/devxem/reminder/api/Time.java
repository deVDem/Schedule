package ru.devxem.reminder.api;

public class Time {



    public static String getRemain(int hour1, int hour2, int min1, int min2, int sec1, int sec2) {
        // (24 - hour) +":"+ (60 - min)+":"+(60-sec)
        // Algoritm: hour1-hour2:min1-min2:sec1-sec2

        int hour = hour1-hour2;
        int min = min1-min2;
        int sec = sec1-sec2;

        String answer = "";

        if(hour==0 || hour==24) answer="00:";
        if(min==0 || min == 60) answer=answer+"00:";
        if(sec==0 || sec == 60) answer=answer+"00";

        return "null";
    }
}
