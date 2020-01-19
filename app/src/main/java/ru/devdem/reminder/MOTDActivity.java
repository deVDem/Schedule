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
            "\n\n\nДобавлено в обновлении:\n" +
            "-Более адекватная система загрузки обновлений, автоматическое открытие установщика\n" +
            "-Исправлена ошибка, когда можно было на главном экране нажать элемент любого соседнего экрана\n" +
            "-Исправлена ошибка автообновления списка уроков\n" +
            "-Исправлена ошибка, когда до первого урока сегодняшнего дня писалось, что неделя закончилась\n" +
            "-Исправлена ошибка для устройств на базе Android 7.x при выходе из аккаунта\n" +
            "-Добавлено уведомление!! Однако, порой оно может зависать, но было сделано специальное уведомление для перезапуска\n" +
            "(крч появится - увидите и поймёте)\n" +
            "-Добавлены IME Actions для текстовых полей в логине и регистрации(крч действия по нажатию на Enter на клаве)\n" +
            "-Оптимизация кода, исправление утечек памяти\n\n" +
            "Косметические исправления:\n" +
            "-Исправлен цвет текста в нижнем уведомлении(SnackBar)\n" +
            "-Исправлен для некоторых устройств фон для нижнего меню\n" +
            "-Добавлено крутой и забавный экран информации об обновлении :)\n" +
            "\n\n\nНовости по разработке:\n" +
            "Отныне все обновления будут выходить раз в неделю, приблизительно в воскресенье(за исключением" +
            "исправления критических ошибок).\n" +
            "Как только будут реализованы все функции, которые я хочу реализовать приложение выйдет из бета-теста\n" +
            "\n\n\nХорошей вам недели и спасибо за тестирование и использование приложения! <3";
    private int i;
    private int ANIM_DURATION = 470 * 2;
    private LinearLayout mHarderLayout;
    private RelativeLayout mMOTDLayout;
    private TextView mTextView;

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
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.harderbetterfasterstronger);
        mediaPlayer.setLooping(false);
        View[] views = {view.findViewById(R.id.harder), view.findViewById(R.id.better),
                view.findViewById(R.id.faster), view.findViewById(R.id.stronger)};
        mediaPlayer.start();
        new CountDownTimer(ANIM_DURATION * 4 + ANIM_DURATION / 2, ANIM_DURATION) {

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
