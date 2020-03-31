package ru.devdem.reminder;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.volley.Response;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.devdem.reminder.ObjectsController.User;

public class PurchaseActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private BillingClient billingClient;
    private Context mContext;
    private Activity mActivity;
    private NetworkController mNetworkController;
    private User mUser;

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_purchase, null);
        setContentView(view);
        mNetworkController = NetworkController.get();
        Button btnMonth = view.findViewById(R.id.btnMonth);
        Button btnYear = view.findViewById(R.id.btnYear);
        ScrollView scrollView = view.findViewById(R.id.scrollView);
        FrameLayout profileFrame = view.findViewById(R.id.profileFrame);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow);
        toolbar.getNavigationIcon().setTint(getColor(R.color.text_color));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        Space space = view.findViewById(R.id.markerY);
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (space.getY() <= scrollY) toolbar.setTitle(R.string.pro_status);
            else toolbar.setTitle("");
        });
        mUser = ObjectsController.getLocalUserInfo(getSharedPreferences("settings", MODE_PRIVATE));
        mContext = this;
        mActivity = this;
        billingClient = BillingClient.newBuilder(this).setListener(this).enablePendingPurchases().build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    List<String> skuList = new ArrayList<>();
                    skuList.add("disablead_year");
                    skuList.add("upgradeaccounttopro");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
                    billingClient.querySkuDetailsAsync(params.build(),
                            (billingResult1, skuDetailsList) -> {
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                                    for (SkuDetails skuDetails : skuDetailsList) {
                                        String sku = skuDetails.getSku();
                                        String text = skuDetails.getPrice();
                                        if ("upgradeaccounttopro".equals(sku)) {
                                            text = text + " / " + getString(R.string.month);
                                            btnMonth.setText(text);
                                            btnMonth.setOnClickListener(v -> {
                                                // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                        .setSkuDetails(skuDetails)
                                                        .build();
                                                billingClient.launchBillingFlow(mActivity, flowParams);
                                            });
                                        } else if ("disablead_year".equals(sku)) {
                                            text = text + " / " + getString(R.string.year);
                                            btnYear.setText(text);
                                            btnYear.setOnClickListener(v -> {
                                                // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                        .setSkuDetails(skuDetails)
                                                        .build();
                                                billingClient.launchBillingFlow(mActivity, flowParams);
                                            });
                                        }
                                    }
                                }
                            });
                } else {
                    Toast.makeText(mContext, R.string.errorNetwork, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(mContext, R.string.errorNetwork, Toast.LENGTH_LONG).show();
                finish();
            }
        });
        acknowledgePurchaseResponseListener = billingResult -> {

        };
        updateUI(profileFrame, mUser);
    }

    private void updateUI(FrameLayout profileFrame, User user) {
        String name = user.getName();
        String login = "@" + user.getLogin();
        String email = user.getEmail();
        String[] permissions = getResources().getStringArray(R.array.permissions);
        String permission = permissions[user.getPermission()];
        String urlImage = user.getUrlImage();
        View profileCard = View.inflate(mContext, R.layout.group_info_user_view_full, null);
        TextView textName = profileCard.findViewById(R.id.profileName);
        TextView textLogin = profileCard.findViewById(R.id.profileLogin);
        TextView textEmail = profileCard.findViewById(R.id.profileEmail);
        TextView textPermission = profileCard.findViewById(R.id.profilePermission);
        ImageView imagePro = profileCard.findViewById(R.id.proImage);
        CircleImageView imageProfile = profileCard.findViewById(R.id.profileImage);
        CardView cardView = profileCard.findViewById(R.id.card_view);
        textName.setText(name);
        textEmail.setText(email);
        textPermission.setText(permission);
        textLogin.setText(login);
        imagePro.setVisibility(View.VISIBLE);
        Button goBtn = profileCard.findViewById(R.id.goProBtn);
        goBtn.setVisibility(View.GONE);
        if (urlImage != null && urlImage.length() > 1)
            Picasso.get().load(urlImage).into(new Target() {
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
                        runOnUiThread(() -> {
                            imageProfile.setImageBitmap(scaled);
                            imageProfile.setBorderColor(textColor);
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
        profileFrame.addView(profileCard);
    }

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // todo: Grant entitlement to the user.

            Response.Listener<String> listener = response -> {
                try {
                    Log.d("responseee", "handlePurchase: " + response);
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                    switch (status) {
                        case "ORDERED":
                            if (!purchase.isAcknowledged()) {
                                AcknowledgePurchaseParams acknowledgePurchaseParams =
                                        AcknowledgePurchaseParams.newBuilder()
                                                .setPurchaseToken(purchase.getPurchaseToken())
                                                .build();
                                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                            }
                            finish();
                            break;
                        case "EXPIRY_ORDER":
                            break;
                        case "error":
                            Toast.makeText(mContext, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (Exception e) {
                    Log.e("PurchaseActivity", "handlePurchase: ", e);
                }
            };
            Response.ErrorListener errorListener = error -> {
                Toast.makeText(mContext, R.string.unknown_error, Toast.LENGTH_SHORT).show();
            };

            mNetworkController.checkSubs(mContext, listener, errorListener, BuildConfig.APPLICATION_ID, purchase.getSku(), purchase.getPurchaseToken(), String.valueOf(mUser.getId()));

        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            for (Purchase purchase : list) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(mContext, R.string.you_canceled_your_purchase, Toast.LENGTH_SHORT).show();
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Toast.makeText(mContext, R.string.you_have_already_purchased, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, R.string.error_payment, Toast.LENGTH_SHORT).show();
        }
    }
}
