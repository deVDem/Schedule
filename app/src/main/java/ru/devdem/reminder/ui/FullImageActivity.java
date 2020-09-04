package ru.devdem.reminder.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import ru.devdem.reminder.R;

public class FullImageActivity extends AppCompatActivity {

    private static String ARG_PATH = "ru.devdem.reminder.pathurl";

    public static Intent newInstance(Activity activity, String urlImage) {
        Intent intent = new Intent(activity, FullImageActivity.class);
        intent.putExtra(ARG_PATH, urlImage);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        ImageView imageView = findViewById(R.id.imageFull);
        String urlImage = getIntent().getStringExtra(ARG_PATH);
        Picasso.get().load(urlImage).placeholder(R.drawable.cat).error(R.drawable.cat_error).into(imageView);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
