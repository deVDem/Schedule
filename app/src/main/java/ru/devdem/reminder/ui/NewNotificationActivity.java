package ru.devdem.reminder.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import ru.devdem.reminder.controllers.NetworkController;
import ru.devdem.reminder.controllers.ObjectsController;
import ru.devdem.reminder.object.User;
import ru.devdem.reminder.R;

public class NewNotificationActivity extends AppCompatActivity {

    private SharedPreferences mSettings;
    private NetworkController mNetworkController;
    private static final String TAG = "NewNotificationActivity";
    FloatingActionButton mDoneButton;
    private RelativeLayout mLoadingLayout;
    private MaterialEditText mEditHeader;
    private MaterialEditText mEditMessage;
    private FloatingActionButton mAddPhotoButton;
    private Dialog mDialogChoose;
    private Dialog mDialogBack;
    private Bitmap mBitmap;
    private LinearLayout mImageLayout;
    private ImageView mImageView;
    private int REQUEST_ID = 1006;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] chooses = new String[]{getString(R.string.photoFromCamera), getString(R.string.imagefromgallery)};
        mSettings = getSharedPreferences("settings", MODE_PRIVATE);
        mNetworkController = NetworkController.get();
        setTheme(R.style.EditProfile);
        setTitle(getString(R.string.new_notification));
        View view = View.inflate(this, R.layout.activity_new_notification, null);
        setContentView(view);
        mLoadingLayout = view.findViewById(R.id.loadingLayout);
        mEditHeader = view.findViewById(R.id.etTitle);
        mEditMessage = view.findViewById(R.id.etText);
        mDoneButton = view.findViewById(R.id.floatingActionButton);
        mAddPhotoButton = view.findViewById(R.id.buttonAddPhoto);
        mImageLayout = view.findViewById(R.id.imageLayout);
        mImageView = view.findViewById(R.id.imageNotification);
        ImageButton deleteButton = view.findViewById(R.id.deleteImageBtn);
        deleteButton.setOnClickListener(v -> deleteImg());
        mDialogChoose = new AlertDialog.Builder(this).setItems(chooses, (dialog, which) -> getImage(which)).create();
        mDialogBack = new AlertDialog.Builder(this)
                .setTitle(R.string.sure_exit)
                .setMessage(R.string.message_will_delete)
                .setPositiveButton(R.string.stay, (dialog, which) -> dialog.cancel())
                .setNegativeButton(R.string.exit, ((dialog, which) -> exit(false)))
                .create();
        mAddPhotoButton.setOnClickListener(v -> mDialogChoose.show());
        mDoneButton.setOnClickListener(v -> {
            if (Objects.requireNonNull(mEditMessage.getText()).toString().length() >= 6 && Objects.requireNonNull(mEditHeader.getText()).toString().length() >= 6) {
                mLoadingLayout.setVisibility(View.VISIBLE);
                send();
            } else
                Toast.makeText(this, getString(R.string.enter_data_correct), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBackPressed() {
        mDialogBack.show();
    }

    private void showHide(View view, View relativeView, int[] size, boolean show) {
        view.setVisibility(View.VISIBLE);
        Animator animator;
        if (show)
            animator = ViewAnimationUtils.createCircularReveal(view, Math.round(relativeView.getX()), Math.round(relativeView.getY()), 0, Math.max(size[0], size[1]));
        else
            animator = ViewAnimationUtils.createCircularReveal(view, Math.round(relativeView.getX()), Math.round(relativeView.getY()), Math.max(size[0], size[1]), 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();
        if (!show)
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });
    }

    void send() {
        mDoneButton.hide();
        mAddPhotoButton.hide();
        mEditHeader.setEnabled(false);
        mEditMessage.setEnabled(false);
        showHide(mLoadingLayout, mDoneButton, new int[]{mEditHeader.getWidth() * 2, mEditHeader.getHeight() * 2}, true);
        User user = ObjectsController.getLocalUserInfo(mSettings);
        String image = null;
        if (mBitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
            byte[] imgBytes = outputStream.toByteArray();
            image = Base64.encodeToString(imgBytes, Base64.DEFAULT);
        }
        Response.Listener<String> listener = response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").equals("ok")) {
                    Toast.makeText(this, getString(R.string.sended), Toast.LENGTH_SHORT).show();
                    exit(true);
                } else {
                    Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                    mDoneButton.show();
                    mAddPhotoButton.show();
                    mEditHeader.setEnabled(true);
                    mEditMessage.setEnabled(true);
                    showHide(mLoadingLayout, mDoneButton, new int[]{mEditHeader.getWidth() * 2, mEditHeader.getHeight() * 2}, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                mDoneButton.show();
                mAddPhotoButton.show();
                mEditHeader.setEnabled(true);
                mEditMessage.setEnabled(true);
                showHide(mLoadingLayout, mDoneButton, new int[]{mEditHeader.getWidth() * 2, mEditHeader.getHeight() * 2}, false);
            }
        };
        Response.ErrorListener errorListener = error -> {
            Toast.makeText(this, getString(R.string.unknown_error) + ": " + error.getMessage(), Toast.LENGTH_LONG).show();
            mDoneButton.show();
            mAddPhotoButton.show();
            mEditHeader.setEnabled(true);
            mEditMessage.setEnabled(true);
            showHide(mLoadingLayout, mDoneButton, new int[]{mEditHeader.getWidth() * 2, mEditHeader.getHeight() * 2}, false);
        };
        mNetworkController.addNotification(this, listener, errorListener, user.getToken(), user.getGroupId(), Objects.requireNonNull(mEditHeader.getText()).toString(), mEditMessage.getText().toString(), image);
    }

    void deleteImg() {
        mBitmap = null;
        mImageLayout.setVisibility(View.GONE);
        Toast.makeText(this, getString(R.string.deleted), Toast.LENGTH_SHORT).show();
        mAddPhotoButton.show();
    }

    void getImage(int where) {
        Intent intent = new Intent();
        Log.d(TAG, "getImage: " + where);
        switch (where) {
            case 0:
                Toast.makeText(this, getString(R.string.temporarily_not_available), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, REQUEST_ID);
                overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
                break;
            default:
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                InputStream imageStream = null;
                if (path != null) {
                    imageStream = getContentResolver().openInputStream(path);
                mBitmap = BitmapFactory.decodeStream(imageStream);
                mImageView.setImageBitmap(mBitmap);
                mImageLayout.setVisibility(View.VISIBLE);
                mAddPhotoButton.hide();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                mAddPhotoButton.show();
            }
        }
    }

    private void exit(boolean ok) {
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        if (ok) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
