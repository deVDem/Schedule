package ru.devdem.reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.devdem.reminder.ObjectsController.User;


public class ProfileFragment extends Fragment {
    private Context mContext;
    private SharedPreferences mSettings;
    private String[] permissions;
    private MainActivity mMainActivity;
    private NetworkController networkController;
    private FrameLayout mProfileFrame;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.fragment_profile, null);
        mContext = Objects.requireNonNull(getContext());
        mMainActivity = (MainActivity) Objects.requireNonNull(getActivity());
        networkController = NetworkController.get();
        permissions = getResources().getStringArray(R.array.permissions);
        mSettings = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        mProfileFrame = v.findViewById(R.id.profileFrame);
        setHasOptionsMenu(true);
        updateUI();
        Button mDetailButton = v.findViewById(R.id.buttonDetailGroup);
        mDetailButton.setOnClickListener(view -> {
            mMainActivity.startActivity(GroupInfoActivity.getAIntent(mContext, 1, false));
            mMainActivity.overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
        });
        Button mLeaveButton = v.findViewById(R.id.buttonLeaveGroup);
        mLeaveButton.setOnClickListener(v1 -> {
            Toast.makeText(mContext, R.string.loading, Toast.LENGTH_LONG).show();
            v1.setEnabled(false);
            Response.Listener<String> listener = response -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    String error = jsonResponse.getString("error");
                    if (status.equals("error")) {
                        if (error.equals("WRONG_TOKEN")) {
                            mMainActivity.checkAccount();
                            mContext.getSharedPreferences("jsondata", Context.MODE_PRIVATE).edit().clear().apply();
                        }
                        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                    } else if (status.equals("JOINED")) {
                        mMainActivity.checkAccount();
                        mContext.getSharedPreferences("jsondata", Context.MODE_PRIVATE).edit().clear().apply();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    v1.setEnabled(true);
                }
            };
            Response.ErrorListener errorListener = error -> {
                v1.setEnabled(true);
                Toast.makeText(mContext, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            };
            networkController.joinToGroup(mContext, listener, errorListener, "0", mSettings.getString("token", "null"));
        });
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reload:
                mMainActivity.checkAccount();
                updateUI();
                return true;
            case R.id.menu_edit:
                startActivityForResult(new Intent(mContext, EditProfileActivity.class), 228);
                Objects.requireNonNull(getActivity()).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    private void updateUI() {
        User user = ObjectsController.getLocalUserInfo(mSettings);
        String name = user.getName();
        String login = "@" + user.getLogin();
        String email = user.getEmail();
        String permission = permissions[user.getPermission()];
        String urlImage = user.getUrlImage();
        View profileCard = View.inflate(mContext, R.layout.group_info_user_view_full, null);
        TextView textName = profileCard.findViewById(R.id.profileName);
        TextView textLogin = profileCard.findViewById(R.id.profileLogin);
        TextView textEmail = profileCard.findViewById(R.id.profileEmail);
        TextView textPermission = profileCard.findViewById(R.id.profilePermission);
        ImageView imagePro = profileCard.findViewById(R.id.proImage);
        CircleImageView imageProfile = profileCard.findViewById(R.id.profileImage);
        Button goProBtn = profileCard.findViewById(R.id.goProBtn);
        goProBtn.setOnClickListener(v -> {
            startActivity(new Intent(mMainActivity, PurchaseActivity.class));
        });
        CardView cardView = profileCard.findViewById(R.id.card_view);
        textName.setText(name);
        textEmail.setText(email);
        textPermission.setText(permission);
        textLogin.setText(login);
        imagePro.setVisibility(user.isPro() ? View.VISIBLE : View.GONE);
        goProBtn.setVisibility(user.isPro() ? View.GONE : View.VISIBLE);
        if (urlImage.length() > 1) Picasso.get().load(urlImage).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(null, () -> {
                    int width = 250;
                    int height = Math.round((float) width / bitmap.getWidth() * bitmap.getHeight());
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, height, true);
                    Bitmap preparePixel = Bitmap.createScaledBitmap(scaled, 1, 1, true);
                    int color = preparePixel.getPixel(0, 0);
                    int rez = 0xFFF - color + 0xFF000000;
                    float[] hsv = new float[3];
                    Color.colorToHSV(rez, hsv);
                    hsv[0] = hsv[0] + 180;
                    int cardColor = Color.HSVToColor(hsv);
                    int textColor = -1 * cardColor + 0xFF000000;
                    mMainActivity.runOnUiThread(() -> {
                        imageProfile.setImageBitmap(scaled);
                        imageProfile.setBorderColor(textColor);
                        if (user.isPro()) {
                            int[][] states = new int[][]{
                                    new int[]{android.R.attr.state_enabled}
                            };
                            int[] colors = new int[]{
                                    cardColor
                            };
                            textName.setTextColor(textColor);
                            textLogin.setTextColor(textColor);
                            textEmail.setTextColor(textColor);
                            textPermission.setTextColor(textColor);
                            cardView.setBackgroundTintList(new ColorStateList(states, colors));
                            imagePro.setColorFilter(textColor);
                        }
                        new CountDownTimer(2000, 16) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                float alpha = (10000f - millisUntilFinished) / 10000f;
                                imageProfile.setAlpha(alpha);
                            }

                            @Override
                            public void onFinish() {
                                imageProfile.setAlpha(1.0f);
                            }
                        }.start();
                    });
                }, "Card background").start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        mProfileFrame.addView(profileCard);
    }
}
