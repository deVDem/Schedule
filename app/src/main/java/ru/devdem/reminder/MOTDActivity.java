package ru.devdem.reminder;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class MOTDActivity extends AppCompatActivity {

    public String mTextMOTD = "Новое обновление " + BuildConfig.VERSION_NAME + "!" +
            "\n\n\nДобавлено в обновлении:" +
            "-Срочное обновление: исправлена ошибка в спике уроков" +
            "\n\n\nХорошей вам недели и спасибо за тестирование и использование приложения! <3";
    private int i;
    private int ANIM_DURATION = 470 * 2;
    private LinearLayout mHarderLayout;
    private RelativeLayout mMOTDLayout;
    private TextView mTextView;
    private MediaPlayer mediaPlayer;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();
        if (mCountDownTimer != null) mCountDownTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getSharedPreferences("settings", MODE_PRIVATE).edit().putInt("version", BuildConfig.VERSION_CODE).apply();
        overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_motd, null);
        setContentView(view);
        mHarderLayout = view.findViewById(R.id.harderbetterfasterstronger);
        mMOTDLayout = view.findViewById(R.id.motdlayout);
        mTextView = view.findViewById(R.id.textView5);
        TextView textView = view.findViewById(R.id.motdText);
        textView.setText(mTextMOTD);
        Button ok = view.findViewById(R.id.buttonOK);
        ok.setOnClickListener(v -> onBackPressed());
        mediaPlayer = MediaPlayer.create(this, R.raw.harderbetterfasterstronger);
        mediaPlayer.setLooping(false);
        View[] views = {view.findViewById(R.id.harder), view.findViewById(R.id.better),
                view.findViewById(R.id.faster), view.findViewById(R.id.stronger)};
        mediaPlayer.start();
        mCountDownTimer = new CountDownTimer(ANIM_DURATION * 4 + ANIM_DURATION / 2, ANIM_DURATION) {

            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    YoYo.with(Techniques.FadeIn)
                            .onStart(animator -> views[i].setVisibility(View.VISIBLE))
                            .duration(400)
                            .interpolate(new AccelerateDecelerateInterpolator())
                            .playOn(views[i]);
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                mediaPlayer.stop();
                mTextView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceIn)
                        .duration(650)
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .onEnd(animator -> {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mMOTDLayout.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.Landing)
                                    .duration(400)
                                    .interpolate(new AccelerateDecelerateInterpolator())
                                    .playOn(mMOTDLayout);
                            YoYo.with(Techniques.TakingOff)
                                    .duration(400)
                                    .interpolate(new AccelerateDecelerateInterpolator())
                                    .onEnd(animator2 -> mHarderLayout.setVisibility(View.GONE))
                                    .playOn(mHarderLayout);
                        })
                        .playOn(mTextView);
            }
        }.start();
    }
}
