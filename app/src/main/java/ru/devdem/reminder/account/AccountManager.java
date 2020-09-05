package ru.devdem.reminder.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import ru.devdem.reminder.controllers.NetworkController;
import ru.devdem.reminder.controllers.ObjectsController.User;
import ru.devdem.reminder.R;

public class AccountManager {

    private Context mContext;
    private SharedPreferences mLocalData;
    private User mAccount;
    private NetworkController mNetworkController;

    private AccountManager(Context context) {
        mContext = context;
        mLocalData = mContext.getSharedPreferences("account", Context.MODE_PRIVATE);
        mNetworkController = NetworkController.get();
        loadAccount();
        reloadAccount();
    }

    private void saveData() {
        SharedPreferences.Editor editor = mLocalData.edit();
        editor.putInt("id", mAccount.getId());
        editor.putString("names", mAccount.getNames());
        editor.putString("login", mAccount.getLogin());
        editor.putInt("imageId", mAccount.getImageId());
        editor.putString("email", mAccount.getEmail());
        /* TODO:
        editor.putString("", mAccount.isPro());
        editor.putString("", mAccount.getPermission());*/
        editor.putString("token", mAccount.getToken());
        editor.putString("groupId", mAccount.getGroupId());
        editor.apply();

    }

    private void reloadAccount() {
        Response.Listener<String> listener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.isNull("error")) {
                    if (!jsonResponse.isNull("response")) {
                        try {
                            JSONObject jsonObjectResponse = jsonResponse.getJSONObject("response");
                            JSONObject jsonUserInfo = jsonObjectResponse.getJSONObject("user_info");
                            mAccount.setId(jsonUserInfo.getInt("id"));
                            mAccount.setNames(jsonUserInfo.getString("names"));
                            mAccount.setEmail(jsonUserInfo.getString("email"));
                            mAccount.setLogin(jsonUserInfo.getString("login"));
                            mAccount.setGroupId(jsonUserInfo.getString("groupId"));
                            mAccount.setPro(jsonUserInfo.getString("pro").equals("Yes"));
                            //mAccount.setUrlImage(jsonUserInfo.getString("urlImage"));
                            mAccount.setToken(jsonUserInfo.getString("token"));
                            saveData();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Неудалось получить информацию о пользователе.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "Что-то пошло не так..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    JSONObject jsonError = jsonResponse.getJSONObject("error");
                    Toast.makeText(mContext, jsonError.getInt("code")+" "+jsonError.getString("text"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(mContext, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> Toast.makeText(mContext, R.string.errorNetwork, Toast.LENGTH_SHORT).show();
        mNetworkController.Login(mContext, mAccount.getLogin(), mAccount.getToken(), listener, errorListener);
    }

    private User getAccount() {
        return mAccount;
    }

    private void loadAccount() {
        mAccount = new User();
        int id = mLocalData.getInt("id", 0);
        String name = mLocalData.getString("names", "null");
        String login = mLocalData.getString("login", "null");
        int imageId = mLocalData.getInt("imageId", 0);
        String email = mLocalData.getString("email", "null");
        /* TODO:
        boolean isPro = mLocalData.getBoolean("pro", false);
        int permission = mLocalData.getInt("permission", 0);*/
        String token = mLocalData.getString("token", "null");
        String groupId = mLocalData.getString("group", "0");
        mAccount.setId(id);
        mAccount.setNames(name);
        mAccount.setLogin(login);
        mAccount.setImageId(imageId);
        mAccount.setEmail(email);
        /*TODO:
        mAccount.setPro(isPro);
        mAccount.setPermission(permission);*/
        mAccount.setToken(token);
        mAccount.setGroupId(groupId);
    }

    public static AccountManager get(Context context) {
        return new AccountManager(context);
    }

}
