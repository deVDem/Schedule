package ru.devdem.reminder.controllers;

import android.util.Log;

import java.net.Socket;
import java.util.Arrays;

import ru.devdem.reminder.BuildConfig;

public class HelperConnection {
    private static final String TAG = "HelperConnection";
    private Socket mSocket;
    private static final String mHost = "schedule.devdem.ru";
    private int mPort;

    public HelperConnection() {
        mPort = BuildConfig.DEBUG ? 3480 : 3475;
    }

    public void openConnection() throws Exception {
        closeConnection();
        try {
            mSocket = new Socket(mHost, mPort);
        } catch (Exception e) {
            throw new Exception("Error in open socket: "+ Arrays.toString(e.getStackTrace()));
        }
    }

    public void closeConnection() {
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (Exception e) {
                Log.e(TAG, "Error in closing socket:"
                        + Arrays.toString(e.getStackTrace()));
            } finally {
                mSocket = null;
            }
        }
        mSocket = null;
    }

    public void sendData(byte[] data) throws Exception {
        if (mSocket == null || mSocket.isClosed()) {
            throw new Exception("Error while sending data. " +
                    "Socket is not created or opened");
        }
        try {
            mSocket.getOutputStream().write(data);
            mSocket.getOutputStream().flush();
        } catch (Exception e) {
            throw new Exception("Error while sending data : "
                    + e.getMessage());
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        closeConnection();
    }
}
