package ru.devxem.reminder.api;

class URLs {

    static String getGroups() {
        return "https://api.devdem.ru/schedule/groups.php";
    }

    static String getInfos() {
        return "https://api.devdem.ru/schedule/info.php";
    }

    static String getLess() {
        return "https://api.devdem.ru/schedule/timings.php";
    }

    static String getNotes() {
        return "https://api.devdem.ru/schedule/notification.php";
    }

    static String getErr() {
        return "https://api.devdem.ru/schedule/senderr.php";
    }
}
