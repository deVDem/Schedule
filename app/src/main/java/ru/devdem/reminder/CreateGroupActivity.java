package ru.devdem.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import ru.devdem.reminder.ObjectsController.User;

public class CreateGroupActivity extends AppCompatActivity {
    private static final String TAG = "CreateGroupActivity";
    private MaterialEditText mEtName;
    private MaterialEditText mEtCity;
    private MaterialEditText mEtBuilding;
    private MaterialEditText mEtDescription;
    private FloatingActionButton mActionButton;
    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEtName.getText().toString().length() >= 2 && mEtName.getText().toString().length() <= 255 &&
                    mEtCity.getText().toString().length() >= 2 && mEtCity.getText().toString().length() <= 32 &&
                    mEtBuilding.getText().toString().length() >= 4 && mEtBuilding.getText().toString().length() <= 255 &&
                    mEtDescription.getText().toString().length() <= 255) {
                mActionButton.setEnabled(true);
                mActionButton.show();
            } else {
                mActionButton.setEnabled(false);
                mActionButton.hide();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private Space mSpace;
    private NetworkController mNetworkController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkController = NetworkController.get();
        View v = View.inflate(this, R.layout.activity_create_group, null);
        setContentView(v);
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_arrow);
        toolbar.getNavigationIcon().setTint(getColor(R.color.text_bold_color));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v1 -> onBackPressed());
        mEtName = v.findViewById(R.id.etGroupName);
        mEtCity = v.findViewById(R.id.etGroupCity);
        mEtBuilding = v.findViewById(R.id.etGroupBuilding);
        mEtDescription = v.findViewById(R.id.etGroupDescription);
        mActionButton = v.findViewById(R.id.buttonDone);
        ScrollView scrollView = v.findViewById(R.id.scrollView);
        mSpace = v.findViewById(R.id.markerY);
        scrollView.setOnScrollChangeListener((v12, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            //TODO: сделать анимацию появления и изчезания
            if (scrollY >= Math.round(mSpace.getY())) {
                toolbar.setTitle(getString(R.string.group_creation));
            } else toolbar.setTitle("");
        });
        mActionButton.setOnClickListener(v1 -> createGroup());
        mEtName.addTextChangedListener(mTextWatcher);
        mEtCity.addTextChangedListener(mTextWatcher);
        mEtBuilding.addTextChangedListener(mTextWatcher);
        mEtDescription.addTextChangedListener(mTextWatcher);
        mActionButton.hide();
    }

    void createGroup() {
        User user = ObjectsController.getLocalUserInfo(getSharedPreferences("settings", MODE_PRIVATE));
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.group_creation).setMessage(R.string.wait).setCancelable(false).create();
        dialog.show();
        Response.Listener<String> listener = response -> {
            Log.d(TAG, "createGroup: " + response);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                String status = jsonResponse.getString("status");
                switch (status) {
                    case "OK":
                        Toast.makeText(this, R.string.success_created_group, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, GroupListActivity.class).putExtra("group_id", jsonResponse.getInt("group_id")).putExtra("join", true));
                        overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                        finish();
                        break;
                    case "NO_USER":
                        dialog.hide();
                        Toast.makeText(this, getString(R.string.unknown_error) + ": " + getString(R.string.your_user_does_not_exit), Toast.LENGTH_SHORT).show();
                        break;
                    case "USER_HAVE_GROUP":
                        dialog.hide();
                        Toast.makeText(this, getString(R.string.error) + ": " + getString(R.string.you_already_created_group), Toast.LENGTH_SHORT).show();
                        break;
                    case "GROUP_EXIST":
                        dialog.hide();
                        Toast.makeText(this, getString(R.string.error) + ": " + getString(R.string.group_already_exist), Toast.LENGTH_SHORT).show();
                        break;
                    case "DATA_NOT_READY":
                        dialog.hide();
                        Toast.makeText(this, getString(R.string.error) + ": " + getString(R.string.enter_data_correct), Toast.LENGTH_SHORT).show();
                        break;
                    case "USER_IN_GROUP":
                        dialog.hide();
                        Toast.makeText(this, getString(R.string.unknown_error) + ": " + getString(R.string.you_in_group), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "createGroup: ", e);
            }
        };

        Response.ErrorListener errorListener = error -> {
            Log.e(TAG, "createGroup: ", error);
            dialog.hide();
            Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        };

        mNetworkController.addGroup(this, listener, errorListener, user.getToken(), mEtName.getText().toString(), mEtCity.getText().toString(), mEtBuilding.getText().toString(), mEtDescription.getText().toString(), null);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HelloActivity.class));
        overridePendingTransition(R.anim.transition_in_back, R.anim.transition_out_back);
        finish();
    }
}
