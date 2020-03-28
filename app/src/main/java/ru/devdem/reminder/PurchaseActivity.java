package ru.devdem.reminder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PurchaseActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    private static final String TAG = "PurchaseActivity";
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private BillingClient billingClient;
    private Context mContext;
    private Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_purchase, null);
        setContentView(view);
        Button btnMonth = view.findViewById(R.id.btnMonth);
        Button btnYear = view.findViewById(R.id.btnYear);
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
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult,
                                                                 List<SkuDetails> skuDetailsList) {
                                    Log.d(TAG, "onSkuDetailsResponse: " + billingResult.getResponseCode());
                                    Log.d(TAG, "onSkuDetailsResponse: " + Arrays.toString(skuDetailsList.toArray()));
                                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                                        for (SkuDetails skuDetails : skuDetailsList) {
                                            String sku = skuDetails.getSku();
                                            String price = skuDetails.getPrice();
                                            String text = skuDetails.getTitle() + " " + skuDetails.getPrice() + skuDetails.isRewarded();
                                            Log.d(TAG, "onSkuDetailsResponse: " + price);
                                            if ("upgradeaccounttopro".equals(sku)) {
                                                btnMonth.setText(text);
                                                btnMonth.setOnClickListener(v -> {
                                                    // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                            .setSkuDetails(skuDetails)
                                                            .build();
                                                    int responseCode = billingClient.launchBillingFlow(mActivity, flowParams).getResponseCode();
                                                });
                                            } else if ("disablead_year".equals(sku)) {
                                                btnYear.setText(text);
                                                btnYear.setOnClickListener(v -> {
                                                    // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                            .setSkuDetails(skuDetails)
                                                            .build();
                                                    int responseCode = billingClient.launchBillingFlow(mActivity, flowParams).getResponseCode();
                                                });
                                            }
                                        }
                                    }
                                }
                            });
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(mContext, R.string.errorNetwork, Toast.LENGTH_LONG).show();
                finish();
            }
        });
        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

            }
        };
    }

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.
            Toast.makeText(mContext, purchase.getSku(), Toast.LENGTH_LONG).show();

            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            for (Purchase purchase : list) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(mContext, "Вы отменили покупку", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Произошла ошибка при оплате. Попробуйте ещё раз", Toast.LENGTH_SHORT).show();
        }
    }

    public class Product {
        private String mTitle;
        private String mPrice;
        private String mDescription;
    }
}
