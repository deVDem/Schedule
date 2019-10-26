package ru.devxem.reminder.api;

import java.util.ArrayList;
import java.util.List;

public class Days {
    private static List<String> days = new ArrayList<>();

    public static String getDay(int id) {
        days.add("Воскресенье");
        days.add("Понедельник");
        days.add("Вторник");
        days.add("Среда");
        days.add("Четверг");
        days.add("Пятница");
        days.add("Суббота");
        return days.get(id);
    }
}
