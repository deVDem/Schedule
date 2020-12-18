package ru.devdem.reminder.controllers;

import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ru.devdem.reminder.object.User;

public class ObjectsController {


    public static User parseUser(JSONObject jsonUser) {
        User user = new User();
        try {
            if (!jsonUser.isNull("id")) user.setId(jsonUser.getInt("id"));
            if (!jsonUser.isNull("names")) user.setNames(jsonUser.getString("names"));
            if (!jsonUser.isNull("email")) user.setEmail(jsonUser.getString("email"));
            if (!jsonUser.isNull("login")) user.setLogin(jsonUser.getString("login"));
            if (!jsonUser.isNull("imageId")) user.setImageId(jsonUser.getInt("imageId"));
            if (!jsonUser.isNull("pro")) user.setPro(jsonUser.getString("pro").equals("Yes"));
            if (!jsonUser.isNull("token")) user.setToken(jsonUser.getString("token"));
            if (!jsonUser.isNull("groupId")) user.setGroupId(jsonUser.getString("groupId"));
        } catch (Exception e) {
            e.printStackTrace();
            user = null;
        }
        return user;
    }



    public static User getLocalUserInfo(SharedPreferences settings) {
        User user = new User();
        user.setId(settings.getInt("user_id", 0));
        user.setNames(settings.getString("name", "error"));
        user.setEmail(settings.getString("email", "error"));
        user.setLogin(settings.getString("login", "error"));
        user.setPro(settings.getBoolean("pro", false));
        user.setImageId(settings.getInt("imageId", 0));
        user.setToken(settings.getString("token", "error"));
        user.setGroupId(settings.getString("group", "error"));
        return user;
    }




}
