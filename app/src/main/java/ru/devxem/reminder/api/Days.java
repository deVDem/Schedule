package ru.devxem.reminder.api;

import java.util.ArrayList;
import java.util.List;

public class Days {
    private List<String> days = new ArrayList<>();

    public String getDay(int id) {
        days.add("Понедельник");
        days.add("Вторник");
        days.add("Среда");
        days.add("Четверг");
        days.add("Пятница");
        days.add("Суббота");
        days.add("Воскресенье");
        return days.get(id);
    }
}
