package ru.devdem.reminder;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DownloadActivity extends AppCompatActivity {

    private String mUrlPath;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private int downloadId;
    private NotificationUtils mNotificationUtils;

    public static Intent newIntent(Context context, String url) {
        Intent intent = new Intent(context, DownloadActivity.class);
        intent.putExtra("url", url);
        return intent;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mNotificationUtils = new NotificationUtils(this);
                Update();
            } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permision denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrlPath = getIntent().getStringExtra("url");
        View view = View.inflate(this, R.layout.activity_update, null);
        setContentView(view);
        mProgressBar = view.findViewById(R.id.progressBar);
        mTextView = view.findViewById(R.id.detail_download_text);
        mProgressBar.setProgress(0);
        mTextView.setText(R.string.preparing_to_download);
        if (ContextCompat.checkSelfPermission(DownloadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mNotificationUtils = new NotificationUtils(this);
            Update();
        } else {
            ActivityCompat.requestPermissions(DownloadActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
        }
    }

    @Override
    protected void onDestroy() {
        PRDownloader.cancel(downloadId);
        mNotificationUtils.getManager().cancel(101);
        super.onDestroy();
    }

    private void Update() {
        NotificationManager notificationManager = mNotificationUtils.getManager();
        notificationManager.cancel(102);
        String filename = "schedule.apk";
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        PRDownloader.initialize(getApplicationContext());
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(false)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
        downloadId = PRDownloader.download(mUrlPath, path, filename)
                .build()
                .setOnProgressListener(progress -> {
                    mProgressBar.setMax((int) progress.totalBytes);
                    mProgressBar.setProgress((int) progress.currentBytes);
                    float proc = (float) progress.currentBytes / (float) progress.totalBytes * 100;
                    float mbCurrent = (float) progress.currentBytes / 1048576;
                    float mbTotal = (float) progress.totalBytes / 1048576;
                    DecimalFormat format = new DecimalFormat("##.00", DecimalFormatSymbols.getInstance(Locale.getDefault()));
                    String string = format.format(proc) + "% | " + format.format(mbCurrent) + "/" + format.format(mbTotal) + " MB";
                    mTextView.setText(string);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationCompat.Builder nb = mNotificationUtils.getDownloadChannelNotification(progress);
                        nb.setVibrate(null);
                        Notification notification = nb.build();
                        notificationManager.notify(101, notification);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        notificationManager.cancel(101);
                        File toInstall = new File(path, filename);
                        Intent intent;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri apkUri = FileProvider.getUriForFile(DownloadActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", toInstall);
                            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            intent.setData(apkUri);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            Uri apkUri = Uri.fromFile(toInstall);
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Error error) {
                        notificationManager.cancel(101);
                        Toast.makeText(DownloadActivity.this, R.string.errorNetwork, Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}
