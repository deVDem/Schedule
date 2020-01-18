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
            "-Оптимизация\n\n" +
            "Косметические исправления:\n" +
            "-Исправлен цвет текста в нижнем уведомлении(Snackbar)\n" +
            "-Исправлено для некоторых устройств фон для нижнего меню\n" +
            "-Добавлено крутой и забавный экран информации об обновлении :)\n" +
            "\n\n\nНовости по разработке:\n" +
            "Отныне все обновления будут выходить раз в неделю, приблизительно в воскресенье(за исключением" +
            "исправления критических ошибок).\n" +
            "Как только будут реализованы все функции, которые я хочу реализовать приложение выйдет из бета-теста\n" +
            "\n\n\nХорошей вам недели! <3";
    private int i;
    private int ANIM_DURATION = 470 * 2;
    private LinearLayout mHarderLayout;
    private RelativeLayout mMOTDLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_motd, null);
        setContentView(view);
        mHarderLayout = view.findViewById(R.id.harderbetterfasterstronger);
        mMOTDLayout = view.findViewById(R.id.motdlayout);
        TextView textView = view.findViewById(R.id.motdText);
        textView.setText(mTextMOTD);
        Button ok = view.findViewById(R.id.buttonOK);
        ok.setOnClickListener(v -> {
            overridePendingTransition(R.anim.transition_out, R.anim.transition_in);
            finish();
        });
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.harderbetterfasterstronger);
        mediaPlayer.setLooping(false);
        View[] views = {view.findViewById(R.id.harder), view.findViewById(R.id.better),
                view.findViewById(R.id.faster), view.findViewById(R.id.stronger)};
        mediaPlayer.start();
        new CountDownTimer(ANIM_DURATION * 4, ANIM_DURATION) {

            @Override
            public void onTick(long millisUntilFinished) {
                YoYo.with(Techniques.FadeIn)
                        .onStart(animator -> views[i].setVisibility(View.VISIBLE))
                        .duration(400)
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .playOn(views[i]);
                i++;
            }

            @Override
            public void onFinish() {
                mediaPlayer.stop();
                mMOTDLayout.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Landing)
                        .duration(400)
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .playOn(mMOTDLayout);
                YoYo.with(Techniques.TakingOff)
                        .duration(400)
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .onEnd(animator -> mHarderLayout.setVisibility(View.GONE))
                        .playOn(mHarderLayout);
            }
        }.start();
    }
}
