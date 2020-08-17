package ru.devdem.reminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONObject;

import ru.devdem.reminder.ObjectsController.User;

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
        mAccount.getId();
        mAccount.getName();
        mAccount.getLogin();
        mAccount.getUrlImage();
        mAccount.getEmail();
        mAccount.isPro();
        mAccount.getPermission();
        mAccount.getToken();
        mAccount.getGroupId();
        editor.apply();

    }

    private void reloadAccount() {
        Response.Listener<String> listener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean ok = jsonResponse.getBoolean("ok");
                if (ok) {
                    boolean password_ok = jsonResponse.getBoolean("password_ok");
                    if (password_ok) {
                        try {
                            JSONObject jsonUserInfo = jsonResponse.getJSONObject("user_info");
                            mAccount.setId(jsonUserInfo.getInt("id"));
                            mAccount.setName(jsonUserInfo.getString("name"));
                            mAccount.setEmail(jsonUserInfo.getString("email"));
                            mAccount.setLogin(jsonUserInfo.getString("login"));
                            mAccount.setGroupId(jsonUserInfo.getString("groups"));
                            mAccount.setPro(jsonUserInfo.getString("pro").equals("Yes"));
                            mAccount.setUrlImage(jsonUserInfo.getString("urlImage"));
                            mAccount.setPermission(jsonUserInfo.getInt("permission"));
                            mAccount.setToken(jsonUserInfo.getString("token"));
                            mAccount.setPassword(jsonUserInfo.getString("password"));
                            saveData();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Неудалось получить информацию о пользователе.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "Wrong password.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Wrong login or email.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(mContext, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        };
        Response.ErrorListener errorListener = error -> Toast.makeText(mContext, R.string.errorNetwork, Toast.LENGTH_SHORT).show();
        mNetworkController.Login(mContext, mAccount.getLogin(), mAccount.getPassword(), listener, errorListener);
    }

    private User getAccount() {
        return mAccount;
    }

    private void loadAccount() {
        mAccount = new User();
        int id = mLocalData.getInt("id", 0);
        String name = mLocalData.getString("name", "null");
        String login = mLocalData.getString("login", "null");
        String urlImage = mLocalData.getString("urlImage", "null");
        String email = mLocalData.getString("email", "null");
        boolean isPro = mLocalData.getBoolean("pro", false);
        int permission = mLocalData.getInt("permission", 0);
        String token = mLocalData.getString("token", "null");
        String groupId = mLocalData.getString("group", "0");
        mAccount.setId(id);
        mAccount.setName(name);
        mAccount.setLogin(login);
        mAccount.setUrlImage(urlImage);
        mAccount.setEmail(email);
        mAccount.setPro(isPro);
        mAccount.setPermission(permission);
        mAccount.setToken(token);
        mAccount.setGroupId(groupId);
    }

    public static AccountManager get(Context context) {
        return new AccountManager(context);
    }

}
